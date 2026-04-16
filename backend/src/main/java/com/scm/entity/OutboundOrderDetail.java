package com.scm.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出库单明细实体类
 * 对应数据库表：scm_outbound_order_detail
 */
@Entity
@Table(name = "scm_outbound_order_detail")
public class OutboundOrderDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outbound_id", nullable = false)
    private Long outboundId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "so_detail_id")
    private Long soDetailId;

    @Column(name = "qty", nullable = false, precision = 18, scale = 2)
    private BigDecimal qty;

    @Column(name = "price", precision = 18, scale = 4)
    private BigDecimal price;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "batch_no", length = 50)
    private String batchNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOutboundId() {
        return outboundId;
    }

    public void setOutboundId(Long outboundId) {
        this.outboundId = outboundId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getSoDetailId() {
        return soDetailId;
    }

    public void setSoDetailId(Long soDetailId) {
        this.soDetailId = soDetailId;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
}
