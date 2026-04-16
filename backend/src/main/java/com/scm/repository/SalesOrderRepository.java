package com.scm.repository;

import com.scm.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 销售订单Repository
 */
@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    Optional<SalesOrder> findBySoNo(String soNo);
}
