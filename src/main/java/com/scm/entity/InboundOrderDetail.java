package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入库单明细实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@TableName("inbound_order_detail")
public class InboundOrderDetail {

    /**
     * 主键
     */
    private Long id;

    /**
     * 入库单id
     */
    private Long inboundId;

    /**
     * 物料id
     */
    private Long itemId;

    /**
     * 关联采购订单明细id
     */
    private Long poDetailId;

    /**
     * 入库数量
     */
    private BigDecimal qty;

    /**
     * 入库单价
     */
    private BigDecimal price;

    /**
     * 入库金额
     */
    private BigDecimal amount;

    /**
     * 物料批次号
     */
    private String batchNo;

    /**
     * 物料过期日期
     */
    private LocalDate expireDate;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;
}
