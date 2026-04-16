package com.scm.repository;

import com.scm.entity.LogisticsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 物流订单Repository
 */
@Repository
public interface LogisticsOrderRepository extends JpaRepository<LogisticsOrder, Long> {
    Optional<LogisticsOrder> findByLogisticsNo(String logisticsNo);
}
