package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 物料实体类
 * 对应数据库表：scm_item
 */
@Entity
@Table(name = "scm_item")
public class Item extends BaseEntity {

    @Column(name = "item_code", unique = true, nullable = false, length = 50)
    private String itemCode;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "spec", length = 200)
    private String spec;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "safety_stock", precision = 18, scale = 2)
    private BigDecimal safetyStock = BigDecimal.ZERO;

    @Column(name = "max_stock", precision = 18, scale = 2)
    private BigDecimal maxStock = BigDecimal.ZERO;

    @Column(name = "min_stock", precision = 18, scale = 2)
    private BigDecimal minStock = BigDecimal.ZERO;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(BigDecimal safetyStock) {
        this.safetyStock = safetyStock;
    }

    public BigDecimal getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(BigDecimal maxStock) {
        this.maxStock = maxStock;
    }

    public BigDecimal getMinStock() {
        return minStock;
    }

    public void setMinStock(BigDecimal minStock) {
        this.minStock = minStock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
