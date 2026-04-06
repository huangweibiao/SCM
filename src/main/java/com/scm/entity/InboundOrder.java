package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入库单实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inbound_order")
public class InboundOrder extends BaseEntity {

    /**
     * 入库单号
     */
    private String inboundNo;

    /**
     * 关联采购订单id（可为空）
     */
    private Long poId;

    /**
     * 关联生产工单id（可为空）
     */
    private Long moId;

    /**
     * 入库仓库id
     */
    private Long warehouseId;

    /**
     * 10采购入库 20生产入库 30退货入库 40调拨入库
     */
    private Integer inboundType;

    /**
     * 入库日期
     */
    private LocalDateTime inboundDate;

    /**
     * 总入库数量
     */
    private BigDecimal totalQty;

    /**
     * 总入库金额
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
