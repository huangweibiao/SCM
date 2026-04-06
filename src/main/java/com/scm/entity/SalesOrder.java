package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售订单主表实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sales_order")
public class SalesOrder extends BaseEntity {

    /**
     * 销售订单号
     */
    private String soNo;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户电话
     */
    private String customerPhone;

    /**
     * 发货仓库id
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
     * 10待审核 20已审核 30部分发货 40已完成
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
