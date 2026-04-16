package com.scm.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入库单明细实体类
 * 对应数据库表：scm_inbound_order_detail
 */
@Entity
@Table(name = "scm_inbound_order_detail")
public class InboundOrderDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inbound_id", nullable = false)
    private Long inboundId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "po_detail_id")
    private Long poDetailId;

    @Column(name = "qty", nullable = false, precision = 18, scale = 2)
    private BigDecimal qty;

    @Column(name = "price", precision = 18, scale = 4)
    private BigDecimal price;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "batch_no", length = 50)
    private String batchNo;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInboundId() {
        return inboundId;
    }

    public void setInboundId(Long inboundId) {
        this.inboundId = inboundId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getPoDetailId() {
        return poDetailId;
    }

    public void setPoDetailId(Long poDetailId) {
        this.poDetailId = poDetailId;
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

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }
}
