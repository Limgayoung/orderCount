package nunu.orderCount.domain.order.model;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.global.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Table(name = "orders")
@EqualsAndHashCode
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
    private LocalDateTime orderDateTime; //주문 일자
    @NotNull
    private Boolean isDone; //배송 준비 완료 유무

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    private Member member;

    @Builder
    public Order(Long quantity, String orderItemNumber, String orderNumber, LocalDateTime orderDateTime, Option option, Member member) {
        this.quantity = quantity;
        this.orderItemNumber = orderItemNumber;
        this.orderNumber = orderNumber;
        this.orderDateTime = orderDateTime;
        this.member = member;
        this.option = option;
        this.isDone = false;
    }

    public void setDone() {
        isDone = true;
    }

    public void setOption(Option option) {
        this.option = option;
    }
}
