package nunu.orderCount.domain.product.repository;

import nunu.orderCount.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
