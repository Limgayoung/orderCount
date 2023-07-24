package nunu.orderCount.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(name = "reissue response", description = "토큰 reissue 완료 후 재발급한 access token 반환")
@RequiredArgsConstructor
public class ResponseReissueDto {
    @Schema(description = "accessToken",example = "alsjdfl.alskdjf.alksjdf")
    private final String accessToken;
}
