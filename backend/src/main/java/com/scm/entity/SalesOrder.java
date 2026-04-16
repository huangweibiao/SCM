package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售订单主实体类
 * 对应数据库表：scm_sales_order
 */
@Entity
@Table(name = "scm_sales_order")
public class SalesOrder extends BaseEntity {

    @Column(name = "so_no", unique = true, nullable = false, length = 50)
    private String soNo;

    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

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
    private LocalDateTime auditTime;

    @Column(name = "remark", length = 500)
    private String remark;

    public String getSoNo() {
        return soNo;
    }

    public void setSoNo(String soNo) {
        this.soNo = soNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
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

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
