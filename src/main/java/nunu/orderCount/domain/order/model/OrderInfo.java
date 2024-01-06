package nunu.orderCount.domain.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "상세 주문 정보", description = "각 옵션 별 조회 시 상세 주문 정보")
@Getter
public class OrderInfo {
    //상품 이름, 상품 이미지, 상품 옵션, 주문 수량, 재고 수량
    @Schema(description = "지그재그 주문 번호", example = "111111111")
    private final String orderNumber;
    @Schema(description = "지그재그 상품 주문 번호", example = "111111111")
    private final String orderItemNumber;
    @Schema(description = "주문 수량", example = "5")
    private final Long orderQuantity;
    @Schema(description = "주문 날짜", example = "2023-12-12 10:10:10")
    private final LocalDateTime orderDateTime;

    @Builder
    public OrderInfo(String orderNumber, String orderItemNumber, Long orderQuantity, LocalDateTime orderDateTime) {
        this.orderNumber = orderNumber;
        this.orderItemNumber = orderItemNumber;
        this.orderQuantity = orderQuantity;
        this.orderDateTime = orderDateTime;
    }
}
