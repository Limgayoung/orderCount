package nunu.orderCount.domain.product.repository;

import nunu.orderCount.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByZigzagProductId(String zigzagProductId);

    Optional<Product> findByZigzagProductId(String zigzagProductId);
}
