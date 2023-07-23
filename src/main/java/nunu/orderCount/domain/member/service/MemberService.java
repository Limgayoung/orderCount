package nunu.orderCount.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.exception.LoginFailException;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.dto.request.RequestJoinDto;
import nunu.orderCount.domain.member.model.dto.request.RequestLoginDto;
import nunu.orderCount.domain.member.model.dto.request.RequestReissueDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseLoginDto;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.global.config.jwt.JwtProvider;
import nunu.orderCount.global.config.jwt.JwtToken;
import nunu.orderCount.infra.zigzag.model.dto.request.RequestZigzagLoginDto;
import nunu.orderCount.infra.zigzag.service.ZigzagAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ZigzagAuthService zigzagAuthService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    //join
    public void join(RequestJoinDto dto){
        String zigzagToken = zigzagAuthService.zigzagLogin(new RequestZigzagLoginDto(dto.getEmail(), dto.getPassword()));
        //todo: cookie는 redis에 저장할 것
        //todo: zigzag에 포함된 가게 모두 저장 (store entity 생성 필요) (추후 구현)
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        memberRepository.save(member);
    }

    //login
    public ResponseLoginDto login(RequestLoginDto dto){
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new LoginFailException());

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new LoginFailException("비밀번호가 일치하지 않습니다");
        }
        //todo: 계정 정지 여부 확인 필요
        
        //jwt 토큰 발급
        JwtToken jwtToken = jwtProvider.issue(member.getMemberId(), member.getRole());
        //todo: redis에 refresh token 저장해야 함 (이전 토큰 삭제 필요)

        return new ResponseLoginDto(jwtToken.getAccessToken(), jwtToken.getRefreshToken());
    }

    //jwt reissue
    public String refreshToken(RequestReissueDto dto){
        //refresh token 검증
        jwtProvider.isValidToken(dto.getRefreshToken());
        //access token에서 member id 가져오기
        Long memberId = jwtProvider.getMemberId(dto.getAccessToken());
        //todo: db의 refresh token과 일치하는지 검사 -> redis에서 꺼내야 함
        
        //토큰 생성
        String recreateAccessToken = jwtProvider.recreateAccessToken(dto.getAccessToken());
        //todo: db 업데이트

        return recreateAccessToken;
    }

    //zigzag token 갱신
}
