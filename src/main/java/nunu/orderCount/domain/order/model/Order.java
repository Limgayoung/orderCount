package nunu.orderCount.domain.order.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.global.common.BaseEntity;
import nunu.orderCount.infra.zigzag.model.dto.response.ResponseZigzagOrderDto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long orderId;
    @NotNull
    private Long quantity; //주문 수량
    @NotNull
    @Column(unique = true)
    private String orderItemNumber; //상품 주문 번호
    @NotNull
    private String orderNumber; //주문 번호
    @NotNull
    private Long datePaid; //결제 일자
    @NotNull
    private Boolean isDone; //배송 준비 완료 유무

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
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

    public void setOption(Option option) {
        this.option = option;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (!Objects.equals(orderId, order.orderId)) return false;
        if (!Objects.equals(quantity, order.quantity)) return false;
        if (!Objects.equals(orderItemNumber, order.orderItemNumber))
            return false;
        if (!Objects.equals(orderNumber, order.orderNumber)) return false;
        if (!Objects.equals(datePaid, order.datePaid)) return false;
        if (!Objects.equals(option, order.option)) return false;
        return Objects.equals(member, order.member);
    }

    @Override
    public int hashCode() {
        int result = orderId != null ? orderId.hashCode() : 0;
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (orderItemNumber != null ? orderItemNumber.hashCode() : 0);
        result = 31 * result + (orderNumber != null ? orderNumber.hashCode() : 0);
        result = 31 * result + (datePaid != null ? datePaid.hashCode() : 0);
        result = 31 * result + (option != null ? option.hashCode() : 0);
        result = 31 * result + (member != null ? member.hashCode() : 0);
        return result;
    }
}
