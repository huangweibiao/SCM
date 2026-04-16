package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产工单实体类
 * 对应数据库表：scm_production_order
 */
@Entity
@Table(name = "scm_production_order")
public class ProductionOrder extends BaseEntity {

    @Column(name = "mo_no", unique = true, nullable = false, length = 50)
    private String moNo;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "qty", nullable = false, precision = 18, scale = 2)
    private BigDecimal qty;

    @Column(name = "finished_qty", precision = 18, scale = 2)
    private BigDecimal finishedQty = BigDecimal.ZERO;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    @Column(name = "status", nullable = false)
    private Integer status = 10;

    @Column(name = "remark", length = 500)
    private String remark;

    public String getMoNo() {
        return moNo;
    }

    public void setMoNo(String moNo) {
        this.moNo = moNo;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getFinishedQty() {
        return finishedQty;
    }

    public void setFinishedQty(BigDecimal finishedQty) {
        this.finishedQty = finishedQty;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getActualStart() {
        return actualStart;
    }

    public void setActualStart(LocalDateTime actualStart) {
        this.actualStart = actualStart;
    }

    public LocalDateTime getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(LocalDateTime actualEnd) {
        this.actualEnd = actualEnd;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
