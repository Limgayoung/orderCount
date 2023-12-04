package nunu.orderCount.domain.option.repository;

import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {
    public Optional<Option> findByProductAndName(Product product, String name);

    Boolean existsByProductAndName(Product product, String name);
}
