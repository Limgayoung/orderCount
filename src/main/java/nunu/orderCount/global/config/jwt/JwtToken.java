package nunu.orderCount.global.config.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class JwtToken {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public JwtToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
