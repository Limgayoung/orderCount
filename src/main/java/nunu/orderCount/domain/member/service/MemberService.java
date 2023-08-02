package nunu.orderCount.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.exception.DuplicateEmailException;
import nunu.orderCount.domain.member.exception.InvalidRefreshTokenException;
import nunu.orderCount.domain.member.exception.LoginFailException;
import nunu.orderCount.domain.member.model.Member;
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
import org.springframework.beans.factory.annotation.Value;
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
    private final RedisUtil redisUtil;
    private final String REDIS_REFRESH_TOKEN = "refresh-token:";
    private final String REDIS_ZIGZAG_TOKEN = "zigzag-token:";
    @Value("${jwt.token.refresh-expiration}")
    private Long RFT_EXPIRE_TIME;

    @Value("${webclient.zigzag.expire-duration}")
    private Long ZIGZAG_EXPIRE_TIME;


    //join
    public ResponseJoinDto join(RequestJoinDto dto){
        if(memberRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        String zigzagToken = zigzagAuthService.zigzagLogin(new RequestZigzagLoginDto(dto.getEmail(), dto.getPassword()));
        //todo: zigzag에 포함된 가게 모두 저장 (store entity 생성 필요) (추후 구현)

        Member member = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        ResponseJoinDto responseJoinDto = ResponseJoinDto.of(memberRepository.save(member));
        redisUtil.setData(REDIS_ZIGZAG_TOKEN+responseJoinDto.getMemberId(), zigzagToken, ZIGZAG_EXPIRE_TIME);

        return responseJoinDto;
    }

    //login
    public ResponseLoginDto login(RequestLoginDto dto){
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(()-> new LoginFailException("해당 아이디를 가진 회원이 존재하지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new LoginFailException("비밀번호가 일치하지 않습니다");
        }
        //todo: 계정 정지 여부 확인 필요
        
        //jwt 토큰 발급
        JwtToken jwtToken = jwtProvider.issue(member.getEmail(), member.getRole());
        redisUtil.setData(REDIS_REFRESH_TOKEN+member.getMemberId(), jwtToken.getRefreshToken(), RFT_EXPIRE_TIME);
        return new ResponseLoginDto(member.getMemberId(), jwtToken.getAccessToken(), jwtToken.getRefreshToken());
    }

    //jwt reissue
    public ResponseReissueDto reissueToken(RequestReissueDto dto){
        //refresh token 검증
        jwtProvider.isValidToken(dto.getRefreshToken());
        //access token에서 member id 가져오기
        String memberEmail = jwtProvider.getMemberEmail(dto.getAccessToken());
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(() -> new InvalidRefreshTokenException("존재하지 않는 email입니다."));

        //db 값과 비교
        String dbRefreshToken = redisUtil.getData(REDIS_REFRESH_TOKEN + member.getMemberId());
        if (!dbRefreshToken.equals(dto.getRefreshToken())) {
            throw new InvalidRefreshTokenException("사용자의 refresh token과 일치하지 않습니다");
        }
        //토큰 생성
        String recreateAccessToken = jwtProvider.reissue(dto.getAccessToken());

        return new ResponseReissueDto(recreateAccessToken);
    }

    //zigzag token 갱신
}
