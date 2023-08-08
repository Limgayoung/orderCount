package nunu.orderCount.domain.order.repository;

import nunu.orderCount.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
