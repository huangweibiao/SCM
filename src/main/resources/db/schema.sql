-- =============================================
-- SCM供应链管理系统数据库初始化脚本
-- 数据库: scm_db
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_general_ci
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `scm_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `scm_db`;

-- =============================================
-- 1. 物料分类表
-- =============================================
DROP TABLE IF EXISTS `item_category`;
CREATE TABLE `item_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_code` VARCHAR(50) NOT NULL COMMENT '物料分类编码',
  `category_name` VARCHAR(200) NOT NULL COMMENT '物料分类名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '上级分类id，0表示顶级',
  `sort` INT DEFAULT 0 COMMENT '排序号',
  `status` TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_code` (`category_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料分类表';

-- =============================================
-- 2. 物料主表
-- =============================================
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `item_code` VARCHAR(50) NOT NULL COMMENT '物料编码',
  `item_name` VARCHAR(200) NOT NULL COMMENT '物料名称',
  `spec` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位（个/箱/吨）',
  `category_id` BIGINT DEFAULT NULL COMMENT '物料分类id',
  `safety_stock` DECIMAL(18,2) DEFAULT 0.00 COMMENT '安全库存',
  `max_stock` DECIMAL(18,2) DEFAULT 0.00 COMMENT '最高库存',
  `min_stock` DECIMAL(18,2) DEFAULT 0.00 COMMENT '最低库存',
  `status` TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_item_code` (`item_code`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料主表';

-- =============================================
-- 3. 仓库表
-- =============================================
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `warehouse_code` VARCHAR(50) NOT NULL COMMENT '仓库编码',
  `warehouse_name` VARCHAR(200) NOT NULL COMMENT '仓库名称',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '仓库地址',
  `manager` VARCHAR(50) DEFAULT NULL COMMENT '仓库管理员',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `status` TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_warehouse_code` (`warehouse_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- =============================================
-- 4. 供应商表
-- =============================================
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `supplier_code` VARCHAR(50) NOT NULL COMMENT '供应商编码',
  `supplier_name` VARCHAR(200) NOT NULL COMMENT '供应商名称',
  `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '地址',
  `payment_terms` VARCHAR(100) DEFAULT NULL COMMENT '付款条件（账期）',
  `rating` TINYINT DEFAULT 3 COMMENT '供应商评级（1-5星）',
  `status` TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supplier_code` (`supplier_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- =============================================
-- 5. 采购订单主表
-- =============================================
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `po_no` VARCHAR(50) NOT NULL COMMENT '采购订单号',
  `supplier_id` BIGINT NOT NULL COMMENT '供应商id',
  `warehouse_id` BIGINT NOT NULL COMMENT '收货仓库id',
  `order_date` DATE NOT NULL COMMENT '下单日期',
  `total_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '订单总金额（含税）',
  `status` TINYINT DEFAULT 10 COMMENT '10待审核 20已审核 30部分收货 40已完成 50关闭',
  `create_by` BIGINT DEFAULT NULL COMMENT '制单人id',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人id',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_po_no` (`po_no`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_status` (`status`),
  KEY `idx_order_date` (`order_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单主表';

-- =============================================
-- 6. 采购订单明细表
-- =============================================
DROP TABLE IF EXISTS `purchase_order_detail`;
CREATE TABLE `purchase_order_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `po_id` BIGINT NOT NULL COMMENT '采购订单id',
  `item_id` BIGINT NOT NULL COMMENT '物料id',
  `qty` DECIMAL(18,2) NOT NULL COMMENT '采购数量',
  `price` DECIMAL(18,4) DEFAULT 0.0000 COMMENT '不含税单价',
  `tax_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '税率%',
  `tax_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '税额（qty*price*tax_rate/100）',
  `amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '含税金额（qty*price*(1+tax_rate/100)）',
  `received_qty` DECIMAL(18,2) DEFAULT 0.00 COMMENT '已收货数量',
  `remain_qty` DECIMAL(18,2) DEFAULT 0.00 COMMENT '未收货数量（qty-received_qty）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_po_id` (`po_id`),
  KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细表';

-- =============================================
-- 7. 入库单表
-- =============================================
DROP TABLE IF EXISTS `inbound_order`;
CREATE TABLE `inbound_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `inbound_no` VARCHAR(50) NOT NULL COMMENT '入库单号',
  `po_id` BIGINT DEFAULT NULL COMMENT '关联采购订单id（可为空）',
  `mo_id` BIGINT DEFAULT NULL COMMENT '关联生产工单id（可为空）',
  `warehouse_id` BIGINT NOT NULL COMMENT '入库仓库id',
  `inbound_type` TINYINT NOT NULL COMMENT '10采购入库 20生产入库 30退货入库 40调拨入库',
  `inbound_date` DATETIME NOT NULL COMMENT '入库日期',
  `total_qty` DECIMAL(18,2) DEFAULT 0.00 COMMENT '总入库数量',
  `total_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '总入库金额',
  `status` TINYINT DEFAULT 10 COMMENT '10草稿 20已确认',
  `create_by` BIGINT DEFAULT NULL COMMENT '制单人id',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inbound_no` (`inbound_no`),
  KEY `idx_po_id` (`po_id`),
  KEY `idx_mo_id` (`mo_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_inbound_type` (`inbound_type`),
  KEY `idx_status` (`status`),
  KEY `idx_inbound_date` (`inbound_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- =============================================
-- 8. 入库单明细表
-- =============================================
DROP TABLE IF EXISTS `inbound_order_detail`;
CREATE TABLE `inbound_order_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inbound_id` BIGINT NOT NULL COMMENT '入库单id',
  `item_id` BIGINT NOT NULL COMMENT '物料id',
  `po_detail_id` BIGINT DEFAULT NULL COMMENT '关联采购订单明细id',
  `qty` DECIMAL(18,2) NOT NULL COMMENT '入库数量',
  `price` DECIMAL(18,4) DEFAULT 0.0000 COMMENT '入库单价',
  `amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '入库金额',
  `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '物料批次号',
  `expire_date` DATE DEFAULT NULL COMMENT '物料过期日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_inbound_id` (`inbound_id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_po_detail_id` (`po_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单明细表';

-- =============================================
-- 9. 库存表
-- =============================================
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `item_id` BIGINT NOT NULL COMMENT '物料id',
  `warehouse_id` BIGINT NOT NULL COMMENT '仓库id',
  `qty` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '当前库存数量',
  `locked_qty` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '锁定数量（销售预占）',
  `available_qty` DECIMAL(18,2) GENERATED ALWAYS AS (qty-locked_qty) STORED COMMENT '可用库存（qty-locked_qty）',
  `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '物料批次号',
  `expire_date` DATE DEFAULT NULL COMMENT '物料过期日期',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  `last_update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_item_warehouse_batch` (`item_id`, `warehouse_id`, `batch_no`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_available_qty` (`available_qty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- =============================================
-- 10. 库存流水表
-- =============================================
DROP TABLE IF EXISTS `inventory_log`;
CREATE TABLE `inventory_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `item_id` BIGINT NOT NULL COMMENT '物料id',
  `warehouse_id` BIGINT NOT NULL COMMENT '仓库id',
  `before_qty` DECIMAL(18,2) NOT NULL COMMENT '操作前库存',
  `change_qty` DECIMAL(18,2) DEFAULT 0.00 COMMENT '库存变动量（+入库/-出库）',
  `after_qty` DECIMAL(18,2) NOT NULL COMMENT '操作后库存',
  `business_type` TINYINT DEFAULT NULL COMMENT '业务类型（10入库 20出库 30调拨 40盘点）',
  `business_id` BIGINT DEFAULT NULL COMMENT '关联业务单id（入库/出库/订单id）',
  `operate_by` BIGINT DEFAULT NULL COMMENT '操作人id',
  `operate_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '操作备注',
  PRIMARY KEY (`id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_business_id` (`business_id`),
  KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';

-- =============================================
-- 11. 销售订单主表
-- =============================================
DROP TABLE IF EXISTS `sales_order`;
CREATE TABLE `sales_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `so_no` VARCHAR(50) NOT NULL COMMENT '销售订单号',
  `customer_name` VARCHAR(200) NOT NULL COMMENT '客户名称',
  `customer_phone` VARCHAR(20) DEFAULT NULL COMMENT '客户电话',
  `warehouse_id` BIGINT NOT NULL COMMENT '发货仓库id',
  `order_date` DATE DEFAULT (CURRENT_DATE) COMMENT '下单日期',
  `total_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '订单总金额（含税）',
  `status` TINYINT DEFAULT 10 COMMENT '10待审核 20已审核 30部分发货 40已完成',
  `create_by` BIGINT DEFAULT NULL COMMENT '制单人id',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人id',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_so_no` (`so_no`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_status` (`status`),
  KEY `idx_order_date` (`order_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单主表';

-- =============================================
-- 12. 销售订单明细表
-- =============================================
DROP TABLE IF EXISTS `sales_order_detail`;
CREATE TABLE `sales_order_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `so_id` BIGINT NOT NULL COMMENT '销售订单id',
  `item_id` BIGINT NOT NULL COMMENT '物料id',
  `qty` DECIMAL(18,2) NOT NULL COMMENT '销售数量',
  `price` DECIMAL(18,4) DEFAULT 0.0000 COMMENT '不含税单价',
  `tax_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '税率%',
  `tax_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '税额',
  `amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '含税金额',
  `shipped_qty` DECIMAL(18,2) DEFAULT 0.00 COMMENT '已发货数量',
  `remain_qty` DECIMAL(18,2) DEFAULT 0.00 COMMENT '未发货数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_so_id` (`so_id`),
  KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单明细表';

-- =============================================
-- 13. 出库单表
-- =============================================
DROP TABLE IF EXISTS `outbound_order`;
CREATE TABLE `outbound_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `outbound_no` VARCHAR(50) NOT NULL COMMENT '出库单号',
  `so_id` BIGINT DEFAULT NULL COMMENT '关联销售订单id',
  `mo_id` BIGINT DEFAULT NULL COMMENT '关联生产工单id（领料）',
  `warehouse_id` BIGINT NOT NULL COMMENT '出库仓库id',
  `outbound_type` TINYINT NOT NULL COMMENT '10销售出库 20生产领料 30退货出库 40调拨出库',
  `outbound_date` DATETIME NOT NULL COMMENT '出库日期',
  `total_qty` DECIMAL(18,2) DEFAULT 0.00 COMMENT '总出库数量',
  `total_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '总出库金额',
  `status` TINYINT DEFAULT 10 COMMENT '10草稿 20已确认',
  `create_by` BIGINT DEFAULT NULL COMMENT '制单人id',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_outbound_no` (`outbound_no`),
  KEY `idx_so_id` (`so_id`),
  KEY `idx_mo_id` (`mo_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_outbound_type` (`outbound_type`),
  KEY `idx_status` (`status`),
  KEY `idx_outbound_date` (`outbound_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- =============================================
-- 14. 出库单明细表
-- =============================================
DROP TABLE IF EXISTS `outbound_order_detail`;
CREATE TABLE `outbound_order_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `outbound_id` BIGINT NOT NULL COMMENT '出库单id',
  `item_id` BIGINT NOT NULL COMMENT '物料id',
  `so_detail_id` BIGINT DEFAULT NULL COMMENT '关联销售订单明细id',
  `qty` DECIMAL(18,2) NOT NULL COMMENT '出库数量',
  `price` DECIMAL(18,4) DEFAULT 0.0000 COMMENT '出库单价',
  `amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '出库金额',
  `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '物料批次号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_outbound_id` (`outbound_id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_so_detail_id` (`so_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细表';

-- =============================================
-- 15. 生产工单表
-- =============================================
DROP TABLE IF EXISTS `production_order`;
CREATE TABLE `production_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `mo_no` VARCHAR(50) NOT NULL COMMENT '工单号',
  `item_id` BIGINT NOT NULL COMMENT '产成品物料id',
  `qty` DECIMAL(18,2) NOT NULL COMMENT '计划生产数量',
  `finished_qty` DECIMAL(18,2) DEFAULT 0.00 COMMENT '已完工数量',
  `start_date` DATE DEFAULT NULL COMMENT '计划开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '计划结束日期',
  `actual_start` DATETIME DEFAULT NULL COMMENT '实际开始时间',
  `actual_end` DATETIME DEFAULT NULL COMMENT '实际结束时间',
  `status` TINYINT DEFAULT 10 COMMENT '10计划 20生产中 30完工',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mo_no` (`mo_no`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_date` (`start_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产工单表';

-- =============================================
-- 16. 物流订单表
-- =============================================
DROP TABLE IF EXISTS `logistics_order`;
CREATE TABLE `logistics_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户id（预留）',
  `logistics_no` VARCHAR(50) NOT NULL COMMENT '物流单号',
  `business_type` TINYINT NOT NULL COMMENT '关联业务类型（10采购 20销售）',
  `business_id` BIGINT NOT NULL COMMENT '关联业务单id（采购/销售订单id）',
  `courier_name` VARCHAR(100) DEFAULT NULL COMMENT '快递公司名称',
  `courier_no` VARCHAR(50) DEFAULT NULL COMMENT '快递单号',
  `send_address` VARCHAR(500) DEFAULT NULL COMMENT '发货地址',
  `receive_address` VARCHAR(500) NOT NULL COMMENT '收货地址',
  `receive_person` VARCHAR(50) NOT NULL COMMENT '收货人',
  `receive_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `status` TINYINT DEFAULT 10 COMMENT '10待发货 20运输中 30已签收 40拒收',
  `logistics_fee` DECIMAL(18,2) DEFAULT 0.00 COMMENT '物流费用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_logistics_no` (`logistics_no`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_business_id` (`business_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流订单表';

-- =============================================
-- 17. 数据字典类型表
-- =============================================
DROP TABLE IF EXISTS `dict_type`;
CREATE TABLE `dict_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_code` VARCHAR(50) NOT NULL COMMENT '字典类型编码',
  `dict_name` VARCHAR(200) NOT NULL COMMENT '字典类型名称',
  `sort` INT DEFAULT 0 COMMENT '排序号',
  `status` TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_code` (`dict_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典类型表';

-- =============================================
-- 18. 数据字典数据表
-- =============================================
DROP TABLE IF EXISTS `dict_data`;
CREATE TABLE `dict_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_type_id` BIGINT NOT NULL COMMENT '字典类型id',
  `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
  `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值',
  `sort` INT DEFAULT 0 COMMENT '排序号',
  `status` TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type_id` (`dict_type_id`),
  KEY `idx_dict_value` (`dict_value`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典数据表';

-- =============================================
-- 19. 用户表
-- =============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密）',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 20. 角色表
-- =============================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `status` TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- =============================================
-- 21. 用户角色关联表
-- =============================================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户id',
  `role_id` BIGINT NOT NULL COMMENT '角色id',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- =============================================
-- 22. 权限表
-- =============================================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限id，0表示顶级',
  `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
  `permission_type` TINYINT NOT NULL COMMENT '权限类型：1菜单 2按钮 3接口',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
  `component` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
  `sort` INT DEFAULT 0 COMMENT '排序号',
  `status` TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- =============================================
-- 23. 角色权限关联表
-- =============================================
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` BIGINT NOT NULL COMMENT '角色id',
  `permission_id` BIGINT NOT NULL COMMENT '权限id',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- =============================================
-- 24. 操作日志表
-- =============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `module` VARCHAR(50) DEFAULT NULL COMMENT '模块名称',
  `operation` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
  `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
  `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
  `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
  `response_result` TEXT DEFAULT NULL COMMENT '响应结果',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
  `user_id` BIGINT DEFAULT NULL COMMENT '操作人id',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '操作人用户名',
  `status` TINYINT DEFAULT 1 COMMENT '1成功 0失败',
  `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
  `execute_time` BIGINT DEFAULT NULL COMMENT '执行时间(毫秒)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_module` (`module`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =============================================
-- 数据库初始化完成
-- =============================================
