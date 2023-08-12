package nunu.orderCount.domain.order.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "주문 현황 업데이트 dto", description = "주문 현황 업데이트에 필요한 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestOrderUpdateDto {

    @Schema(description = "회원 id", example = "1L")
    private Long memberId;
}
