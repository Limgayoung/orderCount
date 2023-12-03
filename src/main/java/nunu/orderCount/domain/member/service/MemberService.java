package nunu.orderCount.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.exception.*;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.member.model.dto.request.RequestJoinDto;
import nunu.orderCount.domain.member.model.dto.request.RequestLoginDto;
import nunu.orderCount.domain.member.model.dto.request.RequestRefreshZigzagTokenDto;
import nunu.orderCount.domain.member.model.dto.request.RequestReissueDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseJoinDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseLoginDto;
import nunu.orderCount.domain.member.model.dto.response.ResponseRefreshZigzagToken;
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

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ZigzagAuthService zigzagAuthService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    @Value("${spring.redis.key.refresh-token}")
    private String REDIS_REFRESH_TOKEN;
    @Value("${spring.redis.key.zigzag-token}")
    private String REDIS_ZIGZAG_TOKEN;
    @Value("${jwt.token.refresh-expiration}")
    private Long RFT_EXPIRE_TIME;

    @Value("${webclient.zigzag.expire-duration}")
    private Long ZIGZAG_EXPIRE_TIME;

    /**
     * 회원가입
     * @param dto
     * @return ResponseJoinDto
     */
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

    /**
     * 로그인
     * @param dto
     * @return ResponseLoginDto
     */
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

    /**
     * jwt access token 재발급
     * @param dto
     * @return ResponseReissueDto
     */
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

    /**
     * zigzag token 갱신
     * @param requestDto
     * @return ResponseRefreshZigzagToken
     */
    public ResponseRefreshZigzagToken refreshZigzagToken(RequestRefreshZigzagTokenDto requestDto) {
        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(() -> new NotExistMemberException("존재하지 않는 회원 id입니다."));

        String zigzagToken = zigzagAuthService.zigzagLogin(new RequestZigzagLoginDto(member.getEmail(), requestDto.getPassword()));
        if (zigzagToken == null) {
            throw new ZigzagLoginFailException("zigzag에 로그인할 수 없습니다.");
        }
        redisUtil.setData(REDIS_ZIGZAG_TOKEN + member.getMemberId(), zigzagToken, ZIGZAG_EXPIRE_TIME);

        return new ResponseRefreshZigzagToken("done");
    }


    public MemberInfo createMemberInfo(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotExistMemberException("존재하지 않는 회원 id입니다."));
        String zigzagToken = redisUtil.getData(REDIS_ZIGZAG_TOKEN + member.getMemberId());

        //todo: null일 경우 validate
        if (zigzagToken.equals(null)) {
        }

        return new MemberInfo(member.getMemberId(), zigzagToken);
    }
}
