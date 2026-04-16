package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入库单实体类
 * 对应数据库表：scm_inbound_order
 */
@Entity
@Table(name = "scm_inbound_order")
public class InboundOrder extends BaseEntity {

    @Column(name = "inbound_no", unique = true, nullable = false, length = 50)
    private String inboundNo;

    @Column(name = "po_id")
    private Long poId;

    @Column(name = "mo_id")
    private Long moId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "inbound_type", nullable = false)
    private Integer inboundType;

    @Column(name = "inbound_date", nullable = false)
    private LocalDateTime inboundDate;

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

    public String getInboundNo() {
        return inboundNo;
    }

    public void setInboundNo(String inboundNo) {
        this.inboundNo = inboundNo;
    }

    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
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

    public Integer getInboundType() {
        return inboundType;
    }

    public void setInboundType(Integer inboundType) {
        this.inboundType = inboundType;
    }

    public LocalDateTime getInboundDate() {
        return inboundDate;
    }

    public void setInboundDate(LocalDateTime inboundDate) {
        this.inboundDate = inboundDate;
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
