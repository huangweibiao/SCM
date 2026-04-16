package com.scm.repository;

import com.scm.entity.InboundOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 入库单明细Repository
 */
@Repository
public interface InboundOrderDetailRepository extends JpaRepository<InboundOrderDetail, Long> {
    List<InboundOrderDetail> findByInboundId(Long inboundId);
}
