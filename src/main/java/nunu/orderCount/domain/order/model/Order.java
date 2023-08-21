package nunu.orderCount.domain.order.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.product.model.Product;
import nunu.orderCount.global.common.BaseEntity;
import nunu.orderCount.infra.zigzag.model.dto.response.ResponseZigzagOrderDto;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "order_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long orderId;
    private Long quantity; //주문 수량
    @Column(unique = true)
    private String orderItemNumber; //상품 주문 번호
    @Column(unique = true)
    private String orderNumber; //주문 번호
    private Long datePaid; //결제 일자
    private Boolean isDone; //배송 준비 완료 유무

    @ManyToOne(fetch = FetchType.LAZY)
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public Order(Long quantity, String orderItemNumber, String orderNumber, Long datePaid, Option option, Member member) {
        this.quantity = quantity;
        this.orderItemNumber = orderItemNumber;
        this.orderNumber = orderNumber;
        this.datePaid = datePaid;
        this.member = member;
        this.option = option;
        this.isDone = false;
    }

    public static Order of(ResponseZigzagOrderDto dto, Member member, Option option) {
        return Order.builder()
                .orderItemNumber(dto.getOrderItemNumber())
                .orderNumber(dto.getOrderNumber())
                .option(option)
                .member(member)
                .quantity(dto.getQuantity())
                .datePaid(dto.getDatePaid())
                .build();
    }

    public void setDone() {
        isDone = true;
    }
}
