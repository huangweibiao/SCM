-- ========== 性能优化索引 ==========

-- 1. 商品表索引优化
-- 商品编码唯一索引（已存在）
ALTER TABLE item ADD UNIQUE INDEX uk_item_code (item_code);

-- 商品名称索引
ALTER TABLE item ADD INDEX idx_item_name (item_name);

-- 商品状态索引
ALTER TABLE item ADD INDEX idx_status (status);

-- 2. 仓库表索引优化
-- 仓库编码唯一索引（已存在）
ALTER TABLE warehouse ADD UNIQUE INDEX uk_warehouse_code (warehouse_code);

-- 仓库状态索引
ALTER TABLE warehouse ADD INDEX idx_status (status);

-- 3. 供应商表索引优化
-- 供应商编码唯一索引（已存在）
ALTER TABLE supplier ADD UNIQUE INDEX uk_supplier_code (supplier_code);

-- 供应商名称索引
ALTER TABLE supplier ADD INDEX idx_supplier_name (supplier_name);

-- 供应商状态索引
ALTER TABLE supplier ADD INDEX idx_status (status);

-- 4. 采购订单表索引优化
-- 订单编号唯一索引（已存在）
ALTER TABLE purchase_order ADD UNIQUE INDEX uk_po_no (po_no);

-- 供应商ID索引
ALTER TABLE purchase_order ADD INDEX idx_supplier_id (supplier_id);

-- 订单状态索引
ALTER TABLE purchase_order ADD INDEX idx_status (status);

-- 创建时间索引（用于按时间范围查询）
ALTER TABLE purchase_order ADD INDEX idx_create_time (create_time);

-- 5. 采购订单明细表索引优化
-- 订单ID索引
ALTER TABLE purchase_order_detail ADD INDEX idx_po_id (po_id);

-- 商品ID索引
ALTER TABLE purchase_order_detail ADD INDEX idx_item_id (item_id);

-- 6. 入库单表索引优化
-- 入库单号唯一索引（已存在）
ALTER TABLE inbound_order ADD UNIQUE INDEX uk_inbound_no (inbound_no);

-- 采购订单ID索引
ALTER TABLE inbound_order ADD INDEX idx_po_id (po_id);

-- 仓库ID索引
ALTER TABLE inbound_order ADD INDEX idx_warehouse_id (warehouse_id);

-- 入库类型索引
ALTER TABLE inbound_order ADD INDEX idx_inbound_type (inbound_type);

-- 订单状态索引
ALTER TABLE inbound_order ADD INDEX idx_status (status);

-- 7. 入库单明细表索引优化
-- 入库单ID索引
ALTER TABLE inbound_order_detail ADD INDEX idx_inbound_id (inbound_id);

-- 商品ID索引
ALTER TABLE inbound_order_detail ADD INDEX idx_item_id (item_id);

-- 8. 销售订单表索引优化
-- 订单编号唯一索引（已存在）
ALTER TABLE sales_order ADD UNIQUE INDEX uk_so_no (so_no);

-- 客户ID索引
ALTER TABLE sales_order ADD INDEX idx_customer_id (customer_id);

-- 订单状态索引
ALTER TABLE sales_order ADD INDEX idx_status (status);

-- 创建时间索引
ALTER TABLE sales_order ADD INDEX idx_create_time (create_time);

-- 9. 销售订单明细表索引优化
-- 订单ID索引
ALTER TABLE sales_order_detail ADD INDEX idx_so_id (so_id);

-- 商品ID索引
ALTER TABLE sales_order_detail ADD INDEX idx_item_id (item_id);

-- 10. 出库单表索引优化
-- 出库单号唯一索引（已存在）
ALTER TABLE outbound_order ADD UNIQUE INDEX uk_outbound_no (outbound_no);

-- 销售订单ID索引
ALTER TABLE outbound_order ADD INDEX idx_so_id (so_id);

-- 仓库ID索引
ALTER TABLE outbound_order ADD INDEX idx_warehouse_id (warehouse_id);

-- 出库类型索引
ALTER TABLE outbound_order ADD INDEX idx_outbound_type (outbound_type);

-- 订单状态索引
ALTER TABLE outbound_order ADD INDEX idx_status (status);

-- 11. 出库单明细表索引优化
-- 出库单ID索引
ALTER TABLE outbound_order_detail ADD INDEX idx_outbound_id (outbound_id);

-- 商品ID索引
ALTER TABLE outbound_order_detail ADD INDEX idx_item_id (item_id);

-- 12. 库存表索引优化（核心表，需要重点关注）
-- 商品ID+仓库ID联合唯一索引（已存在）
ALTER TABLE inventory ADD UNIQUE INDEX uk_item_warehouse (item_id, warehouse_id);

-- 仓库ID索引
ALTER TABLE inventory ADD INDEX idx_warehouse_id (warehouse_id);

-- 库存数量索引（用于查询低库存）
ALTER TABLE inventory ADD INDEX idx_qty (qty);

-- 13. 生产订单表索引优化
-- 订单编号唯一索引（已存在）
ALTER TABLE production_order ADD UNIQUE INDEX uk_mo_no (mo_no);

-- 商品ID索引
ALTER TABLE production_order ADD INDEX idx_item_id (item_id);

-- 订单状态索引
ALTER TABLE production_order ADD INDEX idx_status (status);

-- 14. 物流订单表索引优化
-- 订单编号唯一索引（已存在）
ALTER TABLE logistics_order ADD UNIQUE INDEX uk_lg_no (lg_no);

-- 订单状态索引
ALTER TABLE logistics_order ADD INDEX idx_status (status);

-- 创建时间索引
ALTER TABLE logistics_order ADD INDEX idx_create_time (create_time);

-- 15. 库存日志表索引优化
-- 商品ID索引
ALTER TABLE inventory_log ADD INDEX idx_item_id (item_id);

-- 仓库ID索引
ALTER TABLE inventory_log ADD INDEX idx_warehouse_id (warehouse_id);

-- 业务类型索引
ALTER TABLE inventory_log ADD INDEX idx_business_type (business_type);

-- 关联业务单号索引
ALTER TABLE inventory_log ADD INDEX idx_business_no (business_no);

-- 创建时间索引
ALTER TABLE inventory_log ADD INDEX idx_create_time (create_time);

-- 16. 操作日志表索引优化
-- 用户ID索引
ALTER TABLE sys_operation_log ADD INDEX idx_user_id (user_id);

-- 用户名索引
ALTER TABLE sys_operation_log ADD INDEX idx_username (username);

-- 模块索引
ALTER TABLE sys_operation_log ADD INDEX idx_module (module);

-- 操作类型索引
ALTER TABLE sys_operation_log ADD INDEX idx_operation_type (operation_type);

-- 创建时间索引
ALTER TABLE sys_operation_log ADD INDEX idx_create_time (create_time);

-- 17. 用户表索引优化
-- 用户名唯一索引（已存在）
ALTER TABLE sys_user ADD UNIQUE INDEX uk_username (username);

-- 状态索引
ALTER TABLE sys_user ADD INDEX idx_status (status);

-- 18. 角色表索引优化
-- 角色编码唯一索引（已存在）
ALTER TABLE sys_role ADD UNIQUE INDEX uk_role_code (role_code);

-- 状态索引
ALTER TABLE sys_role ADD INDEX idx_status (status);

-- 19. 权限表索引优化
-- 权限编码唯一索引（已存在）
ALTER TABLE sys_permission ADD UNIQUE INDEX uk_permission_code (permission_code);

-- 父权限ID索引
ALTER TABLE sys_permission ADD INDEX idx_parent_id (parent_id);

-- 20. 用户角色关联表索引优化
-- 用户ID索引
ALTER TABLE sys_user_role ADD INDEX idx_user_id (user_id);

-- 角色ID索引
ALTER TABLE sys_user_role ADD INDEX idx_role_id (role_id);

-- 21. 角色权限关联表索引优化
-- 角色ID索引
ALTER TABLE sys_role_permission ADD INDEX idx_role_id (role_id);

-- 权限ID索引
ALTER TABLE sys_role_permission ADD INDEX idx_permission_id (permission_id);
