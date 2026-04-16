package com.scm.repository;

import com.scm.entity.SalesOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 销售订单明细Repository
 */
@Repository
public interface SalesOrderDetailRepository extends JpaRepository<SalesOrderDetail, Long> {
    List<SalesOrderDetail> findBySoId(Long soId);
    void deleteBySoId(Long soId);
}
