package nunu.orderCount.domain.member.service;

import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.Role;
import nunu.orderCount.domain.member.model.dto.request.RequestJoinDto;
import nunu.orderCount.domain.member.model.dto.request.RequestLoginDto;
import nunu.orderCount.domain.member.model.dto.request.RequestReissueDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseJoinDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseLoginDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseReissueDto;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.global.config.jwt.JwtProvider;
import nunu.orderCount.global.config.jwt.JwtToken;
import nunu.orderCount.global.util.RedisUtil;
import nunu.orderCount.infra.zigzag.model.dto.request.RequestZigzagLoginDto;
import nunu.orderCount.infra.zigzag.service.ZigzagAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ZigzagAuthService zigzagAuthService;
    @Spy
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private MemberService memberService;

    private String email = "email@email.com";
    private String password = "password";
    private String zigzagToken = "zigzagToken";
    private String accessToken = "accessToken";
    private String refreshToken = "refreshToken";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                memberService,
                "ZIGZAG_EXPIRE_TIME",
                10000L
        );
        ReflectionTestUtils.setField(
                memberService,
                "RFT_EXPIRE_TIME",
                10000L
        );
    }

    @DisplayName("회원가입")
    @Test
    void join() {
        //given
        RequestJoinDto dto = new RequestJoinDto(email, password);

        Member testMember = createTestMember(dto.getEmail(), dto.getPassword());

        doReturn(testMember).when(memberRepository).save(any(Member.class));
        doReturn(zigzagToken).when(zigzagAuthService).zigzagLogin(any(RequestZigzagLoginDto.class));
        doNothing().when(redisUtil).setData(anyString(), anyString(), anyLong());

        //when
        ResponseJoinDto responseJoinDto = memberService.join(dto);
        //then
        assertThat(testMember.getEmail()).isEqualTo(responseJoinDto.getEmail());
        assertThat(testMember.getMemberId()).isEqualTo(responseJoinDto.getMemberId());
    }

    @DisplayName("로그인")
    @Test
    void login() {
        //given
        RequestLoginDto requestLoginDto = new RequestLoginDto(email, password);
        Member testMember = createTestMember(requestLoginDto.getEmail(), requestLoginDto.getPassword());

        JwtToken testJwtToken = new JwtToken(accessToken, refreshToken);

        doReturn(Optional.of(testMember)).when(memberRepository).findByEmail(anyString());
        doReturn(testJwtToken).when(jwtProvider).issue(anyString(), any(Role.class));
        doNothing().when(redisUtil).setData(anyString(), anyString(), anyLong());
        doReturn(true).when(passwordEncoder).matches(requestLoginDto.getPassword(), testMember.getPassword());

        //when
        ResponseLoginDto responseLoginDto = memberService.login(requestLoginDto);

        //then
        assertThat(responseLoginDto.getAccessToken()).isNotEmpty();
        assertThat(responseLoginDto.getMemberId()).isEqualTo(testMember.getMemberId());
    }

    @DisplayName("access token 재발급")
    @Test
    void refreshToken() {
        //given
        RequestReissueDto requestReissueDto = new RequestReissueDto(refreshToken, accessToken);
        Member testMember = createTestMember(email, password);
        doReturn(testMember.getEmail()).when(jwtProvider).getMemberEmail(anyString());
        doReturn(Optional.of(testMember)).when(memberRepository).findByEmail(anyString());
        doReturn(refreshToken).when(redisUtil).getData(anyString());
        doReturn("recreatedAccessToken").when(jwtProvider).reissue(anyString());

        //when
        ResponseReissueDto reissueToken = memberService.reissueToken(requestReissueDto);

        //then
        assertThat(reissueToken.getAccessToken()).isNotEqualTo(requestReissueDto.getAccessToken());
    }

    private Member createTestMember(String email, String password){
        Member testMember = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        ReflectionTestUtils.setField(
                testMember,
                "memberId",
                1L,
                Long.class
        );
        return testMember;
    }


}