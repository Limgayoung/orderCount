package nunu.orderCount.domain.order.model;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderDtoInfo {
    @NotNull
    private Long quantity; //주문 수량
    @NotNull
    private String orderItemNumber; //상품 주문 번호
    @NotNull
    private String orderNumber; //주문 번호
    @NotNull
    private Long datePaid; //결제 일자
    @NotNull
    private String productId;
    @NotNull
    private String optionName;

}
