-- =============================================
-- SCM供应链管理系统初始化数据脚本
-- =============================================

USE `scm_db`;

-- =============================================
-- 数据字典类型初始化
-- =============================================
INSERT INTO `dict_type` (`dict_code`, `dict_name`, `sort`, `status`) VALUES
('order_status', '订单状态', 1, 1),
('inbound_type', '入库类型', 2, 1),
('outbound_type', '出库类型', 3, 1),
('logistics_status', '物流状态', 4, 1),
('production_status', '生产状态', 5, 1),
('item_status', '物料状态', 6, 1),
('warehouse_status', '仓库状态', 7, 1),
('supplier_status', '供应商状态', 8, 1),
('system_status', '系统状态', 9, 1);

-- =============================================
-- 数据字典数据初始化
-- =============================================

-- 订单状态
INSERT INTO `dict_data` (`dict_type_id`, `dict_label`, `dict_value`, `sort`, `status`)
SELECT id, '待审核', '10', 1, 1 FROM dict_type WHERE dict_code = 'order_status'
UNION ALL
SELECT id, '已审核', '20', 2, 1 FROM dict_type WHERE dict_code = 'order_status'
UNION ALL
SELECT id, '部分收货', '30', 3, 1 FROM dict_type WHERE dict_code = 'order_status'
UNION ALL
SELECT id, '已完成', '40', 4, 1 FROM dict_type WHERE dict_code = 'order_status'
UNION ALL
SELECT id, '已关闭', '50', 5, 1 FROM dict_type WHERE dict_code = 'order_status'
UNION ALL
SELECT id, '部分发货', '30', 3, 1 FROM dict_type WHERE dict_code = 'order_status';

-- 入库类型
INSERT INTO `dict_data` (`dict_type_id`, `dict_label`, `dict_value`, `sort`, `status`)
SELECT id, '采购入库', '10', 1, 1 FROM dict_type WHERE dict_code = 'inbound_type'
UNION ALL
SELECT id, '生产入库', '20', 2, 1 FROM dict_type WHERE dict_code = 'inbound_type'
UNION ALL
SELECT id, '退货入库', '30', 3, 1 FROM dict_type WHERE dict_code = 'inbound_type'
UNION ALL
SELECT id, '调拨入库', '40', 4, 1 FROM dict_type WHERE dict_code = 'inbound_type';

-- 出库类型
INSERT INTO `dict_data` (`dict_type_id`, `dict_label`, `dict_value`, `sort`, `status`)
SELECT id, '销售出库', '10', 1, 1 FROM dict_type WHERE dict_code = 'outbound_type'
UNION ALL
SELECT id, '生产领料', '20', 2, 1 FROM dict_type WHERE dict_code = 'outbound_type'
UNION ALL
SELECT id, '退货出库', '30', 3, 1 FROM dict_type WHERE dict_code = 'outbound_type'
UNION ALL
SELECT id, '调拨出库', '40', 4, 1 FROM dict_type WHERE dict_code = 'outbound_type';

-- 物流状态
INSERT INTO `dict_data` (`dict_type_id`, `dict_label`, `dict_value`, `sort`, `status`)
SELECT id, '待发货', '10', 1, 1 FROM dict_type WHERE dict_code = 'logistics_status'
UNION ALL
SELECT id, '运输中', '20', 2, 1 FROM dict_type WHERE dict_code = 'logistics_status'
UNION ALL
SELECT id, '已签收', '30', 3, 1 FROM dict_type WHERE dict_code = 'logistics_status'
UNION ALL
SELECT id, '拒收', '40', 4, 1 FROM dict_type WHERE dict_code = 'logistics_status';

-- 生产状态
INSERT INTO `dict_data` (`dict_type_id`, `dict_label`, `dict_value`, `sort`, `status`)
SELECT id, '计划', '10', 1, 1 FROM dict_type WHERE dict_code = 'production_status'
UNION ALL
SELECT id, '生产中', '20', 2, 1 FROM dict_type WHERE dict_code = 'production_status'
UNION ALL
SELECT id, '完工', '30', 3, 1 FROM dict_type WHERE dict_code = 'production_status';

-- 系统状态
INSERT INTO `dict_data` (`dict_type_id`, `dict_label`, `dict_value`, `sort`, `status`)
SELECT id, '启用', '1', 1, 1 FROM dict_type WHERE dict_code = 'system_status'
UNION ALL
SELECT id, '停用', '0', 2, 1 FROM dict_type WHERE dict_code = 'system_status';

-- =============================================
-- 物料分类初始化
-- =============================================
INSERT INTO `item_category` (`category_code`, `category_name`, `parent_id`, `sort`, `status`) VALUES
('CAT001', '原材料', 0, 1, 1),
('CAT002', '半成品', 0, 2, 1),
('CAT003', '产成品', 0, 3, 1),
('CAT001001', '金属材料', 1, 1, 1),
('CAT001002', '塑料材料', 1, 2, 1),
('CAT001003', '电子元件', 1, 3, 1);

-- =============================================
-- 仓库初始化
-- =============================================
INSERT INTO `warehouse` (`warehouse_code`, `warehouse_name`, `address`, `manager`, `phone`, `status`) VALUES
('WH001', '主仓库', '北京市朝阳区XXX路1号', '张三', '13800138001', 1),
('WH002', '原料仓库', '北京市朝阳区XXX路2号', '李四', '13800138002', 1),
('WH003', '成品仓库', '北京市朝阳区XXX路3号', '王五', '13800138003', 1);

-- =============================================
-- 物料初始化
-- =============================================
INSERT INTO `item` (`tenant_id`, `item_code`, `item_name`, `spec`, `unit`, `category_id`, `safety_stock`, `max_stock`, `min_stock`, `status`) VALUES
(0, 'ITEM001', '钢材', 'Φ20mm', '吨', 4, 10.00, 100.00, 5.00, 1),
(0, 'ITEM002', '塑料颗粒', 'ABS', '吨', 5, 20.00, 200.00, 10.00, 1),
(0, 'ITEM003', '电阻', '10KΩ', '个', 6, 1000.00, 10000.00, 500.00, 1),
(0, 'ITEM004', '半成品A', 'TYPE-A', '个', 2, 50.00, 500.00, 20.00, 1),
(0, 'ITEM005', '产成品X', 'MODEL-X', '台', 3, 10.00, 100.00, 5.00, 1),
(0, 'ITEM006', '产成品Y', 'MODEL-Y', '台', 3, 10.00, 100.00, 5.00, 1);

-- =============================================
-- 供应商初始化
-- =============================================
INSERT INTO `supplier` (`tenant_id`, `supplier_code`, `supplier_name`, `contact_person`, `phone`, `email`, `address`, `payment_terms`, `rating`, `status`) VALUES
(0, 'SUP001', 'XX钢铁有限公司', '赵六', '13900139001', 'zhaoliu@steel.com', '上海市浦东新区XXX路1号', '月结30天', 5, 1),
(0, 'SUP002', 'YY塑料化工有限公司', '钱七', '13900139002', 'qianqi@plastic.com', '上海市浦东新区XXX路2号', '月结45天', 4, 1),
(0, 'SUP003', 'ZZ电子元件有限公司', '孙八', '13900139003', 'sunba@electronics.com', '上海市浦东新区XXX路3号', '月结30天', 5, 1);

-- =============================================
-- 用户初始化
-- =============================================
-- 密码为 admin123 的BCrypt加密结果
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', '13800000001', 'admin@scm.com', 1),
('user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '采购员', '13800000002', 'user001@scm.com', 1),
('user002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '仓库管理员', '13800000003', 'user002@scm.com', 1);

-- =============================================
-- 角色初始化
-- =============================================
INSERT INTO `sys_role` (`role_code`, `role_name`, `description`, `status`) VALUES
('ROLE_ADMIN', '系统管理员', '拥有系统所有权限', 1),
('ROLE_PURCHASE', '采购员', '负责采购相关业务', 1),
('ROLE_WAREHOUSE', '仓库管理员', '负责库存管理', 1),
('ROLE_SALES', '销售员', '负责销售相关业务', 1),
('ROLE_PRODUCTION', '生产管理员', '负责生产管理', 1);

-- =============================================
-- 用户角色关联初始化
-- =============================================
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),  -- admin -> ROLE_ADMIN
(2, 2),  -- user001 -> ROLE_PURCHASE
(3, 3);  -- user002 -> ROLE_WAREHOUSE

-- =============================================
-- 权限初始化
-- =============================================
INSERT INTO `sys_permission` (`parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `component`, `icon`, `sort`, `status`) VALUES
(0, 'supplier', '供应商管理', 1, '/supplier', 'Layout/supplier', 'Shop', 1, 1),
(1, 'supplier:create', '创建供应商', 2, '', '', '', 1, 1),
(1, 'supplier:update', '更新供应商', 2, '', '', '', 2, 1),
(1, 'supplier:delete', '删除供应商', 2, '', '', '', 3, 1),
(1, 'supplier:query', '查询供应商', 2, '', '', '', 4, 1),
(0, 'purchase', '采购管理', 1, '/purchase', 'Layout/purchase', 'ShoppingCart', 2, 1),
(6, 'purchase:create', '创建采购订单', 2, '', '', '', 1, 1),
(6, 'purchase:update', '更新采购订单', 2, '', '', '', 2, 1),
(6, 'purchase:delete', '删除采购订单', 2, '', '', '', 3, 1),
(6, 'purchase:query', '查询采购订单', 2, '', '', '', 4, 1),
(6, 'purchase:audit', '审核采购订单', 2, '', '', '', 5, 1),
(0, 'inventory', '库存管理', 1, '/inventory', 'Layout/inventory', 'Box', 3, 1),
(11, 'inventory:query', '查询库存', 2, '', '', '', 1, 1),
(11, 'inventory:log', '库存流水', 2, '', '', '', 2, 1),
(0, 'sales', '销售管理', 1, '/sales', 'Layout/sales', 'Sell', 4, 1),
(14, 'sales:create', '创建销售订单', 2, '', '', '', 1, 1),
(14, 'sales:update', '更新销售订单', 2, '', '', '', 2, 1),
(14, 'sales:delete', '删除销售订单', 2, '', '', '', 3, 1),
(14, 'sales:query', '查询销售订单', 2, '', '', '', 4, 1),
(14, 'sales:audit', '审核销售订单', 2, '', '', '', 5, 1),
(0, 'production', '生产管理', 1, '/production', 'Layout/production', 'Operation', 5, 1),
(19, 'production:create', '创建生产工单', 2, '', '', '', 1, 1),
(19, 'production:update', '更新生产工单', 2, '', '', '', 2, 1),
(19, 'production:query', '查询生产工单', 2, '', '', '', 3, 1),
(0, 'logistics', '物流管理', 1, '/logistics', 'Layout/logistics', 'Van', 6, 1),
(23, 'logistics:create', '创建物流订单', 2, '', '', '', 1, 1),
(23, 'logistics:update', '更新物流订单', 2, '', '', '', 2, 1),
(23, 'logistics:query', '查询物流订单', 2, '', '', '', 3, 1),
(0, 'report', '报表分析', 1, '/report', 'Layout/report', 'DataAnalysis', 7, 1),
(27, 'report:purchase', '采购报表', 2, '', '', '', 1, 1),
(27, 'report:sales', '销售报表', 2, '', '', '', 2, 1),
(27, 'report:inventory', '库存报表', 2, '', '', '', 3, 1),
(0, 'basic', '基础数据', 1, '/basic', 'Layout/basic', 'Setting', 8, 1),
(31, 'basic:item', '物料管理', 2, '', '', '', 1, 1),
(31, 'basic:warehouse', '仓库管理', 2, '', '', '', 2, 1),
(31, 'basic:dict', '数据字典', 2, '', '', '', 3, 1),
(0, 'system', '系统管理', 1, '/system', 'Layout/system', 'Tools', 9, 1),
(35, 'system:user', '用户管理', 2, '', '', '', 1, 1),
(35, 'system:role', '角色管理', 2, '', '', '', 2, 1),
(35, 'system:permission', '权限管理', 2, '', '', '', 3, 1),
(35, 'system:log', '操作日志', 2, '', '', '', 4, 1);

-- =============================================
-- 角色权限关联初始化 (系统管理员拥有所有权限)
-- =============================================
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM sys_permission;

-- 采购员角色权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),  -- 供应商管理
(2, 6), (2, 7), (2, 8), (2, 9), (2, 10), (2, 11);  -- 采购管理

-- 仓库管理员角色权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(3, 11), (3, 12), (3, 13),  -- 库存管理
(3, 31), (3, 32);  -- 基础数据-物料、仓库

-- 初始化数据完成
