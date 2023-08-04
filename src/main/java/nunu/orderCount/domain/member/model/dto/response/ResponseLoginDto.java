package nunu.orderCount.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(name = "login response", description = "로그인 완료 시 토큰 전달")
@RequiredArgsConstructor
public class ResponseLoginDto {
    @Schema(description = "memberId",example = "1L")
    private final Long memberId;
    @Schema(description = "accessToken",example = "alsjdfl.alskdjf.alksjdf")
    private final String accessToken;
    @Schema(description = "refreshToken",example = "asdfasdf.asdfasdf.asfdasdf")
    private final String refreshToken;
}
