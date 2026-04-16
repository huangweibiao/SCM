package com.scm.repository;

import com.scm.entity.OutboundOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 出库单明细Repository
 */
@Repository
public interface OutboundOrderDetailRepository extends JpaRepository<OutboundOrderDetail, Long> {
    List<OutboundOrderDetail> findByOutboundId(Long outboundId);
    void deleteBySoId(Long soId);
}
