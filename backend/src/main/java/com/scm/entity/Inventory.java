package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存实体类
 * 对应数据库表：scm_inventory
 * 包含乐观锁版本号
 */
@Entity
@Table(name = "scm_inventory",
       uniqueConstraints = @UniqueConstraint(columnNames = {"item_id", "warehouse_id", "batch_no"}))
public class Inventory extends BaseEntity {

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "qty", nullable = false, precision = 18, scale = 2)
    private BigDecimal qty = BigDecimal.ZERO;

    @Column(name = "locked_qty", precision = 18, scale = 2)
    private BigDecimal lockedQty = BigDecimal.ZERO;

    @Column(name = "available_qty", precision = 18, scale = 2)
    private BigDecimal availableQty = BigDecimal.ZERO;

    @Column(name = "batch_no", length = 50)
    private String batchNo;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getLockedQty() {
        return lockedQty;
    }

    public void setLockedQty(BigDecimal lockedQty) {
        this.lockedQty = lockedQty;
    }

    public BigDecimal getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(BigDecimal availableQty) {
        this.availableQty = availableQty;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }
}
