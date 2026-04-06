package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出库单明细实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@TableName("outbound_order_detail")
public class OutboundOrderDetail {

    /**
     * 主键
     */
    private Long id;

    /**
     * 出库单id
     */
    private Long outboundId;

    /**
     * 物料id
     */
    private Long itemId;

    /**
     * 关联销售订单明细id
     */
    private Long soDetailId;

    /**
     * 出库数量
     */
    private BigDecimal qty;

    /**
     * 出库单价
     */
    private BigDecimal price;

    /**
     * 出库金额
     */
    private BigDecimal amount;

    /**
     * 物料批次号
     */
    private String batchNo;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;
}
