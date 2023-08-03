package nunu.orderCount.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class ResponseRefreshZigzagToken {
    @Schema(description = "완료 여부",example = "done")
    private final String isDone;
}
