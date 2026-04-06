package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购订单明细表实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@TableName("purchase_order_detail")
public class PurchaseOrderDetail {

    /**
     * 主键
     */
    private Long id;

    /**
     * 采购订单id
     */
    private Long poId;

    /**
     * 物料id
     */
    private Long itemId;

    /**
     * 采购数量
     */
    private BigDecimal qty;

    /**
     * 不含税单价
     */
    private BigDecimal price;

    /**
     * 税率%
     */
    private BigDecimal taxRate;

    /**
     * 税额（qty*price*taxRate/100）
     */
    private BigDecimal taxAmount;

    /**
     * 含税金额（qty*price*(1+taxRate/100)）
     */
    private BigDecimal amount;

    /**
     * 已收货数量
     */
    private BigDecimal receivedQty;

    /**
     * 未收货数量（qty-received_qty）
     */
    private BigDecimal remainQty;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;
}
