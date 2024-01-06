package nunu.orderCount.domain.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.option.model.Option;

@Getter
public class OptionOrderInfo {
    @Schema(description = "옵션 이름", example = "색상: 진청 / size : M")
    private final String optionName;
    @Schema(description = "옵션 주문 수량", example = "9")
    private final Long count;
    @Schema(description = "해당 옵션의 가장 오래된 주문 일시", example = "2023-12-12 10:10:10")
    private final LocalDateTime oldestOrderDateTime;
    @Schema(description = "재고 수량", example = "1")
    private final Integer inventoryQuantity;
    private final List<OrderInfo> orderInfos;

    @Builder
    public OptionOrderInfo(String optionName, Long count, LocalDateTime oldestOrderDateTime,
                           Integer inventoryQuantity,
                           List<OrderInfo> orderInfos) {
        this.optionName = optionName;
        this.count = count;
        this.oldestOrderDateTime = oldestOrderDateTime;
        this.inventoryQuantity = inventoryQuantity;
        this.orderInfos = orderInfos;
    }
}
