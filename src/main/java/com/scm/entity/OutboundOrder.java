package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 出库单实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("outbound_order")
public class OutboundOrder extends BaseEntity {

    /**
     * 出库单号
     */
    private String outboundNo;

    /**
     * 关联销售订单id
     */
    private Long soId;

    /**
     * 关联生产工单id（领料）
     */
    private Long moId;

    /**
     * 出库仓库id
     */
    private Long warehouseId;

    /**
     * 10销售出库 20生产领料 30退货出库 40调拨出库
     */
    private Integer outboundType;

    /**
     * 出库日期
     */
    private java.time.LocalDateTime outboundDate;

    /**
     * 总出库数量
     */
    private BigDecimal totalQty;

    /**
     * 总出库金额
     */
    private BigDecimal totalAmount;

    /**
     * 10草稿 20已确认
     */
    private Integer status;

    /**
     * 制单人id
     */
    private Long createBy;

    /**
     * 备注
     */
    private String remark;
}
