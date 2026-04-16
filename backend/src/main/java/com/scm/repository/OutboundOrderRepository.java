package com.scm.repository;

import com.scm.entity.OutboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 出库单Repository
 */
@Repository
public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long> {
    Optional<OutboundOrder> findByOutboundNo(String outboundNo);
}
