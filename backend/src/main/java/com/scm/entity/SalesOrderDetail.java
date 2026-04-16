package com.scm.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售订单明细实体类
 * 对应数据库表：scm_sales_order_detail
 */
@Entity
@Table(name = "scm_sales_order_detail")
public class SalesOrderDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "so_id", nullable = false)
    private Long soId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "qty", nullable = false, precision = 18, scale = 2)
    private BigDecimal qty;

    @Column(name = "price", precision = 18, scale = 4)
    private BigDecimal price;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 18, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "shipped_qty", precision = 18, scale = 2)
    private BigDecimal shippedQty = BigDecimal.ZERO;

    @Column(name = "remain_qty", precision = 18, scale = 2)
    private BigDecimal remainQty;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSoId() {
        return soId;
    }

    public void setSoId(Long soId) {
        this.soId = soId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getShippedQty() {
        return shippedQty;
    }

    public void setShippedQty(BigDecimal shippedQty) {
        this.shippedQty = shippedQty;
    }

    public BigDecimal getRemainQty() {
        return remainQty;
    }

    public void setRemainQty(BigDecimal remainQty) {
        this.remainQty = remainQty;
    }
}
