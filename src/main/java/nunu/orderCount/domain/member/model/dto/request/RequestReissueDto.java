package nunu.orderCount.domain.member.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Schema(name = "토큰 재발급 request dto", description = "토큰 재발급 시 필요한 refresh token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestReissueDto {
    @NotEmpty
    @Schema(description = "refresh token", example = "asdf.asdf.asdf")
    private String refreshToken;

    @NotEmpty
    @Schema(description = "access token", example = "asdf.asdf.asdf")
    private String accessToken;

}
