package com.scm.repository;

import com.scm.entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 库存流水Repository
 */
@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    List<InventoryLog> findByItemId(Long itemId);
    List<InventoryLog> findByWarehouseId(Long warehouseId);
    List<InventoryLog> findByBusinessIdAndBusinessType(Long businessId, Integer businessType);
}
