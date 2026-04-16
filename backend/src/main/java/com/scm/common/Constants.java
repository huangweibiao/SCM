package com.scm.common;

/**
 * 系统常量类
 */
public class Constants {

    /**
     * 订单状态
     */
    public static class OrderStatus {
        public static final Integer DRAFT = 10;          // 草稿
        public static final Integer PENDING = 10;       // 待审核
        public static final Integer APPROVED = 20;      // 已审核
        public static final Integer PARTIAL = 30;       // 部分处理
        public static final Integer COMPLETED = 40;     // 已完成
        public static final Integer CLOSED = 50;        // 已关闭
    }

    /**
     * 入库类型
     */
    public static class InboundType {
        public static final Integer PURCHASE = 10;      // 采购入库
        public static final Integer PRODUCTION = 20;    // 生产入库
        public static final Integer RETURN = 30;        // 退货入库
        public static final Integer TRANSFER = 40;     // 调拨入库
    }

    /**
     * 出库类型
     */
    public static class OutboundType {
        public static final Integer SALES = 10;        // 销售出库
        public static final Integer PRODUCTION = 20;   // 生产领料
        public static final Integer RETURN = 30;      // 退货出库
        public static final Integer TRANSFER = 40;    // 调拨出库
    }

    /**
     * 库存业务类型
     */
    public static class InventoryBusinessType {
        public static final Integer INBOUND = 10;       // 入库
        public static final Integer OUTBOUND = 20;     // 出库
        public static final Integer TRANSFER = 30;    // 调拨
        public static final Integer CHECK = 40;       // 盘点
    }

    /**
     * 物流状态
     */
    public static class LogisticsStatus {
        public static final Integer PENDING = 10;      // 待发货
        public static final Integer SHIPPED = 20;      // 运输中
        public static final Integer RECEIVED = 30;     // 已签收
        public static final Integer REJECTED = 40;     // 拒收
    }

    /**
     * 业务类型（物流关联）
     */
    public static class BusinessType {
        public static final Integer PURCHASE = 10;      // 采购
        public static final Integer SALES = 20;        // 销售
    }

    /**
     * 生产状态
     */
    public static class ProductionStatus {
        public static final Integer PLANNED = 10;      // 计划
        public static final Integer IN_PROGRESS = 20;  // 生产中
        public static final Integer FINISHED = 30;     // 完工
    }

    /**
     * 数据字典类型编码
     */
    public static class DictTypeCode {
        public static final String ORDER_STATUS = "order_status";
        public static final String INBOUND_TYPE = "inbound_type";
        public static final String OUTBOUND_TYPE = "outbound_type";
        public static final String LOGISTICS_STATUS = "logistics_status";
    }

    /**
     * JWT相关
     */
    public static class Jwt {
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String HEADER_NAME = "Authorization";
        public static final Long EXPIRATION = 86400000L; // 24小时
    }

    /**
     * 分页默认配置
     */
    public static class Page {
        public static final Integer DEFAULT_PAGE_NUM = 1;
        public static final Integer DEFAULT_PAGE_SIZE = 10;
    }
}
