package nunu.orderCount.domain.order.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findTopByMemberOrderByOrderDateTimeDesc(Member member);
    Boolean existsByMemberAndOrderItemNumber(Member member, String orderItemNumber);
    List<Order> findByMemberAndIsDoneFalse(Member member);

    List<Order> findByMemberAndIsDoneFalseAndOrderDateTimeBetween(Member member, LocalDateTime startDate, LocalDateTime endDate);

    Order findTopByMemberAndOptionAndIsDoneIsFalseOrderByOrderDateTime(Member member, Option option);
}
