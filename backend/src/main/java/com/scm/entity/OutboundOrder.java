package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库单实体类
 * 对应数据库表：scm_outbound_order
 */
@Entity
@Table(name = "scm_outbound_order")
public class OutboundOrder extends BaseEntity {

    @Column(name = "outbound_no", unique = true, nullable = false, length = 50)
    private String outboundNo;

    @Column(name = "so_id")
    private Long soId;

    @Column(name = "mo_id")
    private Long moId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "outbound_type", nullable = false)
    private Integer outboundType;

    @Column(name = "outbound_date", nullable = false)
    private LocalDateTime outboundDate;

    @Column(name = "total_qty", precision = 18, scale = 2)
    private BigDecimal totalQty;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false)
    private Integer status = 10;

    @Column(name = "create_by")
    private Long createBy;

    @Column(name = "remark", length = 500)
    private String remark;

    public String getOutboundNo() {
        return outboundNo;
    }

    public void setOutboundNo(String outboundNo) {
        this.outboundNo = outboundNo;
    }

    public Long getSoId() {
        return soId;
    }

    public void setSoId(Long soId) {
        this.soId = soId;
    }

    public Long getMoId() {
        return moId;
    }

    public void setMoId(Long moId) {
        this.moId = moId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getOutboundType() {
        return outboundType;
    }

    public void setOutboundType(Integer outboundType) {
        this.outboundType = outboundType;
    }

    public LocalDateTime getOutboundDate() {
        return outboundDate;
    }

    public void setOutboundDate(LocalDateTime outboundDate) {
        this.outboundDate = outboundDate;
    }

    public BigDecimal getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(BigDecimal totalQty) {
        this.totalQty = totalQty;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
