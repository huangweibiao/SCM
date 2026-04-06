package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory")
public class Inventory extends BaseEntity {

    /**
     * 物料id
     */
    private Long itemId;

    /**
     * 仓库id
     */
    private Long warehouseId;

    /**
     * 当前库存数量
     */
    private BigDecimal qty;

    /**
     * 锁定数量（销售预占）
     */
    private BigDecimal lockedQty;

    /**
     * 可用库存（qty-locked_qty）
     */
    private BigDecimal availableQty;

    /**
     * 物料批次号
     */
    private String batchNo;

    /**
     * 物料过期日期
     */
    private LocalDate expireDate;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 最后更新时间
     */
    private java.time.LocalDateTime lastUpdateTime;
}
