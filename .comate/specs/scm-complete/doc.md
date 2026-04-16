# SCM供应链管理系统 - 完善功能规格说明

## 需求概述

完善SCM系统中未完成的功能模块，主要包括前端路由完善、核心业务功能补全。

## 未完成功能清单

### 1. 前端路由完善
- `/system/role` - 角色管理页面路由
- `/system/permission` - 权限管理页面路由
- `/inbound` - 入库管理页面（使用现有purchase页面改造）
- `/outbound` - 出库管理页面（使用现有sales页面改造）

### 2. 采购管理模块完善
- 采购收货功能（部分收货/全部收货）
- 采购订单关闭功能
- InboundController 添加收货接口

### 3. 库存管理模块完善
- 库存调拨功能（仓库间调拨）
- 库存盘点功能（生成盘点单、盘点差异处理）
- InventoryService 添加调拨和盘点方法

### 4. 销售管理模块完善
- 销售发货功能（部分发货/全部发货）
- 客户信息管理
- SalesController 添加发货接口

### 5. 生产管理模块完善
- 生产领料功能（从库存扣减原材料）
- 产成品入库功能（完工后入库）

## 技术方案

### 前端路由配置
修改 `frontend/src/router/index.ts`，添加缺失的路由：
```typescript
{ path: 'system/role', component: () => import('../views/system/role.vue') }
{ path: 'system/permission', component: () => import('../views/system/permission.vue') }
{ path: 'inbound', component: () => import('../views/inbound/index.vue') }
{ path: 'outbound', component: () => import('../views/outbound/index.vue') }
```

### 后端接口设计

#### 采购收货接口
- `POST /api/purchase/{id}/receive` - 采购收货
- 请求参数：receiptDetails[{itemId, qty, batchNo}]

#### 库存调拨接口
- `POST /api/inventory/transfer` - 库存调拨
- 请求参数：fromWarehouseId, toWarehouseId, itemId, qty

#### 库存盘点接口
- `POST /api/inventory/check` - 创建盘点单
- `POST /api/inventory/check/{id}/result` - 提交盘点结果

#### 销售发货接口
- `POST /api/sales/{id}/deliver` - 销售发货
- 请求参数：deliveryDetails[{itemId, qty}]

#### 生产相关接口
- `POST /api/production/{id}/pick` - 生产领料
- `POST /api/production/{id}/finish` - 完工入库

### 前端页面
- 创建 `views/inbound/index.vue` - 入库管理页面
- 创建 `views/outbound/index.vue` - 出库管理页面

## 实施计划

1. 完善前端路由配置
2. 实现后端采购收货接口
3. 实现后端库存调拨和盘点接口
4. 实现后端销售发货接口
5. 实现后端生产领料和入库接口
6. 创建入库管理前端页面
7. 创建出库管理前端页面

## 边界条件
- 库存不足时禁止发货/领料
- 调拨时源仓库库存必须充足
- 盘点差异需要审批后才能调整库存
