package nunu.orderCount.domain.product.repository;

import nunu.orderCount.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByZigzagProductId(String zigzagProductId);

    Optional<Product> findByZigzagProductId(String zigzagProductId);
}
