package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 采购订单主实体类
 * 对应数据库表：scm_purchase_order
 */
@Entity
@Table(name = "scm_purchase_order")
public class PurchaseOrder extends BaseEntity {

    @Column(name = "po_no", unique = true, nullable = false, length = 50)
    private String poNo;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false)
    private Integer status = 10;

    @Column(name = "create_by")
    private Long createBy;

    @Column(name = "audit_by")
    private Long auditBy;

    @Column(name = "audit_time")
    private java.time.LocalDateTime auditTime;

    @Column(name = "remark", length = 500)
    private String remark;

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(Long auditBy) {
        this.auditBy = auditBy;
    }

    public java.time.LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(java.time.LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
