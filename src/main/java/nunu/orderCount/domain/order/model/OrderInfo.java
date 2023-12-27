package nunu.orderCount.domain.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Schema(name = "전체 배송준비중 주문 내역 조회", description = "주문 내역 조회에 필요한 정보")
@Getter
//@RequiredArgsConstructor
public class OrderInfo {
    //상품 이름, 상품 이미지, 상품 옵션, 주문 수량, 재고 수량
    @Schema(description = "상품 이름", example = "청바지")
    private final String productName;
    @Schema(description = "상품 이미지 url", example = "https://github.com/Limgayoung/orderCount/issues/42")
    private final String productImageUrl;
    @Schema(description = "옵션 이름", example = "색상: 진청 / size : M")
    private final String optionName;
    @Schema(description = "주문 수량", example = "5")
    private final Long orderQuantity;
    @Schema(description = "재고 수량", example = "1")
    private final Integer inventoryQuantity;
    @Schema(description = "가장 오래된 주문 dateTime", example = "2023-12-20 10:10:10")
    private final LocalDateTime oldestOrderDateTime;

    @Builder
    public OrderInfo(String productName, String productImageUrl, String optionName, Long orderQuantity,
                     Integer inventoryQuantity, LocalDateTime oldestOrderDateTime) {
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.optionName = optionName;
        this.orderQuantity = orderQuantity;
        this.inventoryQuantity = inventoryQuantity;
        this.oldestOrderDateTime = oldestOrderDateTime;
    }
}
