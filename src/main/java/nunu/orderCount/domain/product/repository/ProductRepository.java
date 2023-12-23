package nunu.orderCount.domain.product.repository;

import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByZigzagProductId(String zigzagProductId);
    Boolean existsByZigzagProductIdAndMember(String zigzagProductId, Member member);

    Optional<Product> findByZigzagProductId(String zigzagProductId);
    Optional<Product> findByZigzagProductIdAndMember(String zigzagProductId, Member member);
}
