package nunu.orderCount.domain.order.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductOptionOrderInfo {
    private final String zigzagProductId;
    private final String productName;
    private final List<OptionOrderInfo> optionOrderInfos;

    @Builder
    public ProductOptionOrderInfo(String zigzagProductId, String productName, List<OptionOrderInfo> optionOrderInfos) {
        this.zigzagProductId = zigzagProductId;
        this.productName = productName;
        this.optionOrderInfos = optionOrderInfos;
    }
}
