package nunu.orderCount.domain.order.model;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class OrderDtoInfo {
    @NotNull
    private Long quantity; //주문 수량
    @NotNull
    private String orderItemNumber; //상품 주문 번호
    @NotNull
    private String orderNumber; //주문 번호
    @NotNull
    private LocalDateTime orderDateTime; //주문 일자
    @NotNull
    private String productId;
    @NotNull
    private String optionName;
}
