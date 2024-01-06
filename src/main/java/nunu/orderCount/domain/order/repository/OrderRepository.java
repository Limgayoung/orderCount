package nunu.orderCount.domain.order.repository;

import java.time.LocalDateTime;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.order.model.Order;
import nunu.orderCount.domain.order.model.OrderCountByOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findTopByMemberOrderByOrderDateTimeDesc(Member member);
    Boolean existsByMemberAndOrderItemNumber(Member member, String orderItemNumber);
    List<Order> findByMemberAndIsDoneFalse(Member member);

    @Query(value = "select "
            + "new nunu.orderCount.domain.order.model.OrderCountByOption(o.option, sum(o.quantity))"
            + "from Order o where o.member = :member and o.isDone = false group by o.option")
    List<OrderCountByOption> sumIsDoneFalseOrdersByOption(@Param("member") Member member);

    @Query(value = "select "
            + "new nunu.orderCount.domain.order.model.OrderCountByOption(o.option, sum(o.quantity))"
            + "from Order o "
            + "where o.member = :member and o.isDone = false and o.orderDateTime >= :startDate and o.orderDateTime <= :endDate "
            + "group by o.option")
    List<OrderCountByOption> sumIsDoneFalseOrdersByOptionBetweenDate(@Param("member") Member member,
                                                                     @Param("startDate") LocalDateTime startDate,
                                                                     @Param("endDate") LocalDateTime endDate);

    Order findTopByMemberAndOptionAndIsDoneIsFalseOrderByOrderDateTime(Member member, Option option);
}
