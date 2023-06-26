package nunu.orderCount.debug.repository;

import nunu.orderCount.debug.model.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, Long> {
}
