package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 库存流水表实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@TableName("inventory_log")
public class InventoryLog {

    /**
     * 主键
     */
    private Long id;

    /**
     * 租户id（预留）
     */
    private Long tenantId;

    /**
     * 物料id
     */
    private Long itemId;

    /**
     * 仓库id
     */
    private Long warehouseId;

    /**
     * 操作前库存
     */
    private BigDecimal beforeQty;

    /**
     * 库存变动量（+入库/-出库）
     */
    private BigDecimal changeQty;

    /**
     * 操作后库存
     */
    private BigDecimal afterQty;

    /**
     * 业务类型（10入库 20出库 30调拨 40盘点）
     */
    private Integer businessType;

    /**
     * 关联业务单id（入库/出库/订单id）
     */
    private Long businessId;

    /**
     * 操作人id
     */
    private Long operateBy;

    /**
     * 操作时间
     */
    private java.time.LocalDateTime operateTime;

    /**
     * 操作备注
     */
    private String remark;
}
