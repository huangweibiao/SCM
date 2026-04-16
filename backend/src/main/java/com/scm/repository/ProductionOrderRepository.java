package com.scm.repository;

import com.scm.entity.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 生产工单Repository
 */
@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    Optional<ProductionOrder> findByMoNo(String moNo);
}
