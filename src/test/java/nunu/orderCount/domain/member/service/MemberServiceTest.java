package nunu.orderCount.domain.member.service;

import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.exception.DuplicateEmailException;
import nunu.orderCount.domain.member.exception.LoginFailException;
import nunu.orderCount.domain.member.exception.NotExistMemberException;
import nunu.orderCount.domain.member.exception.ZigzagLoginFailException;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.member.model.Role;
import nunu.orderCount.domain.member.model.dto.request.RequestJoinDto;
import nunu.orderCount.domain.member.model.dto.request.RequestLoginDto;
import nunu.orderCount.domain.member.model.dto.request.RequestRefreshZigzagTokenDto;
import nunu.orderCount.domain.member.model.dto.request.RequestReissueDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseJoinDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseLoginDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseRefreshZigzagToken;
import nunu.orderCount.domain.member.model.dto.response.ResponseReissueDto;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.global.config.RedisTestContainers;
import nunu.orderCount.global.config.jwt.JwtProvider;
import nunu.orderCount.global.config.jwt.JwtToken;
import nunu.orderCount.global.util.RedisUtil;
import nunu.orderCount.infra.zigzag.model.dto.request.RequestZigzagLoginDto;
import nunu.orderCount.infra.zigzag.service.ZigzagAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith({MockitoExtension.class, RedisTestContainers.class})
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
        ReflectionTestUtils.setField(
                memberService,
                "REDIS_REFRESH_TOKEN",
                "refresh-token: "
        );
        ReflectionTestUtils.setField(
                memberService,
                "REDIS_ZIGZAG_TOKEN",
                "zigzag-token: "
        );
    }

    @DisplayName("회원가입")
    @Nested
    class join {
        @DisplayName("성공")
        @Test
        void join() {
            //given
            RequestJoinDto dto = new RequestJoinDto(email, password);

            Member testMember = createTestMember(dto.getEmail(), dto.getPassword(), 1L);

            doReturn(testMember).when(memberRepository).save(any(Member.class));
            doReturn(zigzagToken).when(zigzagAuthService).zigzagLogin(any(RequestZigzagLoginDto.class));
            doNothing().when(redisUtil).setData(anyString(), anyString(), anyLong());

            //when
            ResponseJoinDto responseJoinDto = memberService.join(dto);
            //then
            assertThat(testMember.getMemberId()).isEqualTo(responseJoinDto.getMemberId());

            //verify
            verify(memberRepository, times(1)).save(any(Member.class));
        }

        @DisplayName("실패 - 중복 이메일")
        @Test
        void duplicateEmailJoinTest(){
            //given
            RequestJoinDto dto = new RequestJoinDto(email, password);
            Member testMember = createTestMember(dto.getEmail(), dto.getPassword(), 1L);

            doReturn(Optional.empty()).when(memberRepository).findByEmail(dto.getEmail());
            doReturn(testMember).when(memberRepository).save(any(Member.class));
            doReturn(zigzagToken).when(zigzagAuthService).zigzagLogin(any(RequestZigzagLoginDto.class));
            doNothing().when(redisUtil).setData(anyString(), anyString(), anyLong());

            memberService.join(dto);
            doReturn(Optional.of(testMember)).when(memberRepository).findByEmail(dto.getEmail());

            //when, then
            assertThatThrownBy(() -> memberService.join(dto))
                    .isInstanceOf(DuplicateEmailException.class);

            //verify
            verify(memberRepository, times(1)).save(any(Member.class));
        }
    }

    @DisplayName("로그인")
    @Nested
    class login {
        @DisplayName("성공")
        @Test
        void login() {
            //given
            RequestLoginDto requestLoginDto = new RequestLoginDto(email, password);
            Member testMember = createTestMember(requestLoginDto.getEmail(), requestLoginDto.getPassword(), 1L);

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

            //verify
            verify(memberRepository, times(1)).findByEmail(anyString());
        }

        @DisplayName("실패 - 잘못된 이메일")
        @Test
        void notExistEmailLoginTest(){
            //given
            RequestLoginDto requestLoginDto = new RequestLoginDto(email, password);
            Member testMember = createTestMember(requestLoginDto.getEmail(), requestLoginDto.getPassword(), 1L);

            JwtToken testJwtToken = new JwtToken(accessToken, refreshToken);

            doReturn(Optional.empty()).when(memberRepository).findByEmail(anyString());

            //when, then
            assertThatThrownBy(() -> memberService.login(requestLoginDto))
                    .isInstanceOf(LoginFailException.class);

            //verify
            verify(memberRepository, times(1)).findByEmail(anyString());
        }
        @DisplayName("실패 - 잘못된 비밀번호로")
        @Test
        void notMatchPasswordTest(){
            //given
            RequestLoginDto requestLoginDto = new RequestLoginDto(email, password);
            Member testMember = createTestMember(requestLoginDto.getEmail(), requestLoginDto.getPassword(), 1L);

            JwtToken testJwtToken = new JwtToken(accessToken, refreshToken);

            doReturn(Optional.of(testMember)).when(memberRepository).findByEmail(anyString());
            doReturn(false).when(passwordEncoder).matches(requestLoginDto.getPassword(), testMember.getPassword());

            //when, then
            assertThatThrownBy(() -> memberService.login(requestLoginDto))
                    .isInstanceOf(LoginFailException.class);

            //verify
            verify(memberRepository, times(1)).findByEmail(anyString());
        }
    }

    @DisplayName("jwt 토큰 재발급")
    @Nested
    class reissue {
        @DisplayName("성공")
        @Test
        void reissueToken() {
            //given
            RequestReissueDto requestReissueDto = new RequestReissueDto(refreshToken, accessToken);
            Member testMember = createTestMember(email, password, 1L);
            doReturn(testMember.getEmail()).when(jwtProvider).getMemberEmail(anyString());
            doReturn(Optional.of(testMember)).when(memberRepository).findByEmail(anyString());
            doReturn(refreshToken).when(redisUtil).getData(anyString());
            doReturn("recreatedAccessToken").when(jwtProvider).reissue(anyString());

            //when
            ResponseReissueDto reissueToken = memberService.reissueToken(requestReissueDto);

            //then
            assertThat(reissueToken.getAccessToken()).isNotEqualTo(requestReissueDto.getAccessToken());

            //verify
            verify(memberRepository, times(1)).findByEmail(anyString());
            verify(jwtProvider, times(1)).reissue(anyString());
        }
    }

    @DisplayName("zigzag 토큰 갱신")
    @Nested
    class refreshZigzagToken {
        @DisplayName("성공")
        @Test
        void refreshZigzagTokenTest() {
            //given
            RequestRefreshZigzagTokenDto requestDto = new RequestRefreshZigzagTokenDto(1L, "password");
            Member testMember = createTestMember(email, password, 1L);

            doReturn(Optional.of(testMember)).when(memberRepository).findById(anyLong());
            doReturn("update" + zigzagToken).when(zigzagAuthService).zigzagLogin(any(RequestZigzagLoginDto.class));
            doNothing().when(redisUtil).setData(anyString(), anyString(), anyLong());

            //when
            ResponseRefreshZigzagToken responseToken = memberService.refreshZigzagToken(requestDto);

            //then
            assertThat(responseToken.getIsDone()).isEqualTo("done");
        }

        @DisplayName("실패 - 잘못된 member Id")
        @Test
        void invalidMemberIdRefreshZigzagTokenTest(){
            //given
            RequestRefreshZigzagTokenDto requestDto = new RequestRefreshZigzagTokenDto(1L, "password");
            Member testMember = createTestMember(email, password, 1L);

            doReturn(Optional.empty()).when(memberRepository).findById(anyLong());

            //when, then
            assertThatThrownBy(() -> memberService.refreshZigzagToken(requestDto))
                    .isInstanceOf(NotExistMemberException.class);
        }

        @DisplayName("실패 - zigzag login 실패")
        @Test
        void failZigzagLoginTest(){
            //given
            RequestRefreshZigzagTokenDto requestDto = new RequestRefreshZigzagTokenDto(1L, "password");
            Member testMember = createTestMember(email, password, 1L);

            doReturn(Optional.of(testMember)).when(memberRepository).findById(anyLong());
            doReturn(null).when(zigzagAuthService).zigzagLogin(any(RequestZigzagLoginDto.class));

            //when, then
            assertThatThrownBy(() -> memberService.refreshZigzagToken(requestDto))
                    .isInstanceOf(ZigzagLoginFailException.class);
        }
    }

    @Test
    @DisplayName("회원 정보 생성")
    void createMemberInfo(){
        //given
        Member testMember = createTestMember(email, password, 1L);

        doReturn(Optional.of(testMember)).when(memberRepository).findById(anyLong());
        doReturn("update" + zigzagToken).when(redisUtil).getData(anyString());

        MemberInfo memberInfo = memberService.createMemberInfo(1L);

        assertThat(memberInfo.getMember().getMemberId()).isEqualTo(1L);
    }

    private Member createTestMember(String email, String password, Long memberId){
        Member testMember = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        ReflectionTestUtils.setField(
                testMember,
                "memberId",
                memberId,
                Long.class
        );
        return testMember;
    }

}