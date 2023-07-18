package nunu.orderCount.global.config.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.member.model.Role;
import nunu.orderCount.global.auth.CustomAuthentication;
import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.token.secret-key}")
    private String SECRET_KEY;
    @Value("${jwt.token.access-expiration}")
    private Long RFT_EXPIRE_TIME;
    @Value("${jwt.token.refresh-expiration}")
    private Long ACT_EXPIRE_TIME;
    private String AUTHORIZATION = "Authorization";

    //jwt 토큰 발급
    public JwtToken issue(Long id, Role role){ //todo: id 외의 다른 대리키 찾을 것
        return JwtToken.builder()
                .accessToken(createAccessToken(id, role))
                .refreshToken(createRefreshToken())
                .build();
    }

    //토큰 재발급 (refresh token 이용)
    public JwtToken reissue(String accessToken, String refreshToken){
        getTokenInfo(refreshToken); //토큰 만료/유효성 확인
        String recreatedAccessToken = recreateAccessToken(accessToken);

        return JwtToken.builder()
                .accessToken(recreatedAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //access token 생성
    public String createAccessToken(Long id, Role role){
        Claims claims = Jwts.claims();
        claims.put("userId", id);
        claims.put("role", role);
        return Jwts.builder()
                .setSubject("UserInfo")
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (ACT_EXPIRE_TIME)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();

    }
    //refresh token 생성
    public String createRefreshToken(){
        //todo: 이전에 발급했던 refreshToken은 만료시켜야 함
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (RFT_EXPIRE_TIME)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    public String recreateAccessToken(String accessToken){
        Long userId;
        Role role;

        try{
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(accessToken);
            Claims body = claimsJws.getBody();
            userId = Long.parseLong((String) body.get("userId"));
            role = Role.of((String) body.get("role"));
        } catch (ExpiredJwtException e) {
            userId = Long.parseLong((String) e.getClaims().get("userId"));
            role = Role.of((String) e.getClaims().get("role"));
        }
        //todo: 이전 토큰은 사용할 수 있어도 되는가? -> 만료 시간이 30분 정도로 짧지만 따로 삭제를 해줘야 하는건가?
        return createAccessToken(userId, role);
    }

    //토큰에서 Authentication 객체 반환
    public Authentication getAuthentication(String accessToken){
        Jws<Claims> claims = getTokenInfo(accessToken);
        Claims body = claims.getBody();
        Long userId = Long.parseLong((String) body.get("userId"));
        Role role = Role.of((String) body.get("role"));

        //todo: 해당하는 memberId가 존재하는지 확인 필요
        return new CustomAuthentication(userId, role);
    }

    //사용자가 보낸 Authorization 필드에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }
    
    //토큰 유효성 확인, get claims
    private Jws<Claims> getTokenInfo(String token){
        try{
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e){ //유효기간 만료
            throw new BusinessException(ErrorCode.JWT_EXPIRED_ERROR);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e){ //형식/구성/서명 오류
            throw new BusinessException(ErrorCode.JWT_FORMAT_ERROR);
        } catch (JwtException e){ //그 외 jwt 오류
            throw new BusinessException(ErrorCode.JWT_ERROR);
        }
    }

}
