package nunu.orderCount.domain.order.repository;

import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findTopByMemberOrderByOrderDateTimeDesc(Member member);

    Boolean existsByMemberAndOrderItemNumber(Member member, String orderItemNumber);
    List<Order> findByMemberAndIsDoneFalse(Member member);
}
