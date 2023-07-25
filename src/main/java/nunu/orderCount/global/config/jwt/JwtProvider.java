package nunu.orderCount.global.config.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Role;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.global.auth.CustomAuthentication;
import nunu.orderCount.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
    private final byte[] SECRET_KEY;
    private final Long ACT_EXPIRE_TIME;
    private final Long RFT_EXPIRE_TIME;
    public final static String AUTHORIZATION = "Authorization";
    public final MemberRepository memberRepository;

    public JwtProvider(@Value("${jwt.token.secret-key}") String SECRET_KEY,
                       @Value("${jwt.token.access-expiration}") Long ACT_EXPIRE_TIME,
                       @Value("${jwt.token.refresh-expiration}") Long RFT_EXPIRE_TIME,
                       MemberRepository memberRepository) {
        this.SECRET_KEY = SECRET_KEY.getBytes();
        this.ACT_EXPIRE_TIME = ACT_EXPIRE_TIME;
        this.RFT_EXPIRE_TIME = RFT_EXPIRE_TIME;
        this.memberRepository = memberRepository;
    }

    //jwt 토큰 발급
    public JwtToken issue(String email, Role role){
        return JwtToken.builder()
                .accessToken(createAccessToken(email, role))
                .refreshToken(createRefreshToken())
                .build();
    }

    //토큰 재발급 (refresh token 이용)
    public String reissue(String accessToken, String refreshToken){
        isValidToken(refreshToken); //토큰 만료/유효성 확인
        String recreatedAccessToken = recreateAccessToken(accessToken);

        return recreatedAccessToken;
    }

    //access token 생성
    public String createAccessToken(String email, Role role){
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("role", role.getRole());
        return Jwts.builder()
                .setSubject("UserInfo")
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (ACT_EXPIRE_TIME)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

    }
    //refresh token 생성
    public String createRefreshToken(){
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (RFT_EXPIRE_TIME)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String recreateAccessToken(String accessToken){
        String email;
        Role role;

        try{
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(accessToken);
            Claims body = claimsJws.getBody();
            email = String.valueOf(body.get("email"));
            role = Role.of((String) body.get("role"));
        } catch (ExpiredJwtException e) {
            email = String.valueOf(e.getClaims().get("email"));
            role = Role.of((String) e.getClaims().get("role"));
        }

        return createAccessToken(email, role);
    }

    //토큰에서 Authentication 객체 반환
    public Authentication getAuthentication(String accessToken){
        Jws<Claims> claims = getClaims(accessToken);
        Claims body = claims.getBody();
        String email = String.valueOf(body.get("email"));
        Role role = Role.of((String) body.get("role"));

        if(!memberRepository.existsByEmail(email)){
            throw new CustomJwtException(ErrorCode.JWT_ERROR, "유효하지 않은 토큰입니다.");
        }

        return new CustomAuthentication(email, role);
    }

    //사용자가 보낸 Authorization 필드에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }
    
    //토큰 유효성 확인, get claims
    public boolean isValidToken(String token){
        if(token== null) throw new CustomJwtException(ErrorCode.JWT_ERROR, "빈 값입니다.");
        getClaims(token);
        return true;
    }

    public String getMemberEmail(String accessToken){
        String email;
        try{
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(accessToken);
            Claims body = claimsJws.getBody();
            email =String.valueOf(body.get("email"));
        } catch (ExpiredJwtException e) {
            email = String.valueOf(e.getClaims().get("email"));
        }
        return email;
    }

    private Jws<Claims> getClaims(String token){
        try{
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e){ //유효기간 만료
            throw new CustomJwtException(ErrorCode.JWT_EXPIRED_ERROR);
        } catch (JwtException e){ //그 외 jwt 오류
            throw new CustomJwtException(ErrorCode.JWT_ERROR);
        }
    }
}
