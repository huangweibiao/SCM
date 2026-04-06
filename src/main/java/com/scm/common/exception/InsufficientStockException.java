package com.scm.common.exception;

/**
 * 库存不足异常
 *
 * @author SCM System
 * @since 2026-04-06
 */
public class InsufficientStockException extends BusinessException {

    private Long itemId;
    private Long warehouseId;
    private java.math.BigDecimal requiredQty;
    private java.math.BigDecimal availableQty;

    public InsufficientStockException(Long itemId, Long warehouseId, java.math.BigDecimal requiredQty, java.math.BigDecimal availableQty) {
        super("库存不足");
        this.itemId = itemId;
        this.warehouseId = warehouseId;
        this.requiredQty = requiredQty;
        this.availableQty = availableQty;
    }

    public InsufficientStockException(String message) {
        super(message);
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public java.math.BigDecimal getRequiredQty() {
        return requiredQty;
    }

    public java.math.BigDecimal getAvailableQty() {
        return availableQty;
    }
}
