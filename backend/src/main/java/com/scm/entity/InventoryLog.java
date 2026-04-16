package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存流水实体类
 * 对应数据库表：scm_inventory_log
 * 记录所有库存变动
 */
@Entity
@Table(name = "scm_inventory_log")
public class InventoryLog extends BaseEntity {

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "before_qty", nullable = false, precision = 18, scale = 2)
    private BigDecimal beforeQty;

    @Column(name = "change_qty", precision = 18, scale = 2)
    private BigDecimal changeQty;

    @Column(name = "after_qty", nullable = false, precision = 18, scale = 2)
    private BigDecimal afterQty;

    @Column(name = "business_type")
    private Integer businessType;

    @Column(name = "business_id")
    private Long businessId;

    @Column(name = "operate_by")
    private Long operateBy;

    @Column(name = "remark", length = 500)
    private String remark;

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

    public BigDecimal getBeforeQty() {
        return beforeQty;
    }

    public void setBeforeQty(BigDecimal beforeQty) {
        this.beforeQty = beforeQty;
    }

    public BigDecimal getChangeQty() {
        return changeQty;
    }

    public void setChangeQty(BigDecimal changeQty) {
        this.changeQty = changeQty;
    }

    public BigDecimal getAfterQty() {
        return afterQty;
    }

    public void setAfterQty(BigDecimal afterQty) {
        this.afterQty = afterQty;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getOperateBy() {
        return operateBy;
    }

    public void setOperateBy(Long operateBy) {
        this.operateBy = operateBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
