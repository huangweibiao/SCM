package com.scm.repository;

import com.scm.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 库存Repository
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.itemId = :itemId AND i.warehouseId = :warehouseId AND i.batchNo = :batchNo")
    Optional<Inventory> findByItemIdAndWarehouseIdAndBatchNo(Long itemId, Long warehouseId, String batchNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.itemId = :itemId AND i.warehouseId = :warehouseId AND i.batchNo = :batchNo")
    Optional<Inventory> findByItemIdAndWarehouseIdAndBatchNoForUpdate(Long itemId, Long warehouseId, String batchNo);

    List<Inventory> findByItemId(Long itemId);
    List<Inventory> findByWarehouseId(Long warehouseId);

    /**
     * 乐观锁更新库存锁定数量
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.lockedQty = :newLockedQty, i.availableQty = :newAvailableQty, i.version = i.version + 1 " +
           "WHERE i.itemId = :itemId AND i.warehouseId = :warehouseId AND i.version = :version")
    int updateInventoryLock(Long itemId, Long warehouseId, BigDecimal qty, Integer version,
                             BigDecimal newLockedQty, BigDecimal newAvailableQty);
}
