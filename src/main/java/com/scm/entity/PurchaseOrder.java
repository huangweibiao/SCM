package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 采购订单主表实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_order")
public class PurchaseOrder extends BaseEntity {

    /**
     * 采购订单号
     */
    private String poNo;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 收货仓库id
     */
    private Long warehouseId;

    /**
     * 下单日期
     */
    private LocalDate orderDate;

    /**
     * 订单总金额（含税）
     */
    private BigDecimal totalAmount;

    /**
     * 10待审核 20已审核 30部分收货 40已完成 50关闭
     */
    private Integer status;

    /**
     * 制单人id
     */
    private Long createBy;

    /**
     * 审核人id
     */
    private Long auditBy;

    /**
     * 审核时间
     */
    private java.time.LocalDateTime auditTime;

    /**
     * 备注
     */
    private String remark;
}
