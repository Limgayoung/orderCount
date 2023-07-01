package nunu.orderCount.infra.zigzag.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseZigzagOrderDto {
    private final Long totalOrderCount; //총 주문 수량
    private final String productName; //상품명
    private final String option; //상품 옵션명
    private final Long quantity; //주문 수량
    private final String orderItemNumber; //상품 주문 번호
    private final String orderNumber; //주문 번호
    private final String productId; //상품 번호
    private final Long datePaid; //결제 일자

    @Builder
    public ResponseZigzagOrderDto(Long totalOrderCount, String productName, String option, Long quantity, String orderItemNumber, String orderNumber, String productId, Long datePaid) {
        this.totalOrderCount = totalOrderCount;
        this.productName = productName;
        this.option = option;
        this.quantity = quantity;
        this.orderItemNumber = orderItemNumber;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.datePaid = datePaid;
    }
}