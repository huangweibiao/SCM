package com.scm.repository;

import com.scm.entity.InboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 入库单Repository
 */
@Repository
public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long> {
    Optional<InboundOrder> findByInboundNo(String inboundNo);
}
