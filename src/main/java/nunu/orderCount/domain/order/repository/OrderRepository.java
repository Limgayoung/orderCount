package nunu.orderCount.domain.order.repository;

import java.util.Map;
import nunu.orderCount.domain.member.model.Member;
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
            + "new nunu.orderCount.domain.order.model.OrderCountByOption(o, o.option, sum(o.quantity))"
            + "from Order o where o.member = :member group by o.option")
    List<OrderCountByOption> sumIsDoneFalseOrdersByOption(@Param("member") Member member);
}
