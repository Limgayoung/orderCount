package nunu.orderCount.domain.order.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.product.model.Product;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    private Long orderId;
    private Long quantity; //주문 수량
    private String orderItemNumber; //상품 주문 번호
    private String orderNumber; //주문 번호
    private Long datePaid; //결제 일자
    private Boolean isDone; //배송 준비 완료 유무

    @ManyToOne
    private Product product;
}
