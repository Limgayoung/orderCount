package nunu.orderCount.domain.order.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.order.model.OptionOrderInfo;
import nunu.orderCount.domain.order.model.ProductOptionOrderInfo;

@Schema(name = "전체 배송준비중 주문 내역 조회", description = "주문 내역 조회에 필요한 정보")
@Getter
@RequiredArgsConstructor
public class ResponseFindOrdersByOptionDto {
    @Schema(description = "옵션 목록 개수", example = "30 (상품/옵션이 다른 주문이 30개란뜻)")
    private final Integer optionCount;
    @Schema(description = "각 옵션 정보", example = "")
    private final List<ProductOptionOrderInfo> productOptionOrderInfos;
}
