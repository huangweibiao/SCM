package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售订单明细实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@TableName("sales_order_detail")
public class SalesOrderDetail {

    /**
     * 主键
     */
    private Long id;

    /**
     * 销售订单id
     */
    private Long soId;

    /**
     * 物料id
     */
    private Long itemId;

    /**
     * 销售数量
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
     * 税额
     */
    private BigDecimal taxAmount;

    /**
     * 含税金额
     */
    private BigDecimal amount;

    /**
     * 已发货数量
     */
    private BigDecimal shippedQty;

    /**
     * 未发货数量
     */
    private BigDecimal remainQty;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;
}
