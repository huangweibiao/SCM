package com.scm.common.constant;

/**
 * 业务类型常量
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface BusinessTypeConstant {

    /**
     * 库存流水业务类型
     */
    interface InventoryLogType {
        /**
         * 入库
         */
        Integer INBOUND = 10;

        /**
         * 出库
         */
        Integer OUTBOUND = 20;

        /**
         * 调拨
         */
        Integer TRANSFER = 30;

        /**
         * 盘点
         */
        Integer INVENTORY_CHECK = 40;
    }

    /**
     * 订单类型
     */
    interface OrderType {
        /**
         * 采购订单
         */
        Integer PURCHASE = 1;

        /**
         * 销售订单
         */
        Integer SALES = 2;
    }

    /**
     * 物流业务类型
     */
    interface LogisticsBusinessType {
        /**
         * 采购
         */
        Integer PURCHASE = 10;

        /**
         * 销售
         */
        Integer SALES = 20;
    }

    /**
     * 入库单状态
     */
    interface InboundOrderStatus {
        /**
         * 草稿
         */
        Integer DRAFT = 10;

        /**
         * 已确认
         */
        Integer CONFIRMED = 20;
    }

    /**
     * 出库单状态
     */
    interface OutboundOrderStatus {
        /**
         * 草稿
         */
        Integer DRAFT = 10;

        /**
         * 已确认
         */
        Integer CONFIRMED = 20;
    }
}
