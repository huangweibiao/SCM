package com.scm.repository;

import com.scm.entity.PurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 采购订单明细Repository
 */
@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {
    List<PurchaseOrderDetail> findByPoId(Long poId);
    void deleteByPoId(Long poId);
}
