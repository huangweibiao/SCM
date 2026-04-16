# SCM供应链管理系统 - 完善功能任务计划

## 1. 前端路由完善

- [x] Task 1: 完善前端路由配置
  - 1.1: 添加 /system/role 路由
  - 1.2: 添加 /system/permission 路由
  - 1.3: 添加 /inbound 入库管理路由
  - 1.4: 添加 /outbound 出库管理路由

## 2. 采购管理模块完善

- [x] Task 2: 实现采购收货功能
  - 2.1: PurchaseController 添加 receive 接口
  - 2.2: PurchaseOrderServiceImpl 添加 receive 方法
  - 2.3: 前端 purchase/index.vue 添加收货按钮和弹窗
  - 2.4: 前端 api/purchase.js 添加 receive API

- [x] Task 3: 实现采购订单关闭功能
  - 3.1: PurchaseController 添加 close 接口
  - 3.2: PurchaseOrderServiceImpl 添加 close 方法
  - 3.3: 前端添加关闭按钮

## 3. 库存管理模块完善

- [x] Task 4: 实现库存调拨功能
  - 4.1: InventoryController 添加 transfer 接口
  - 4.2: InventoryServiceImpl 添加 transfer 方法（源仓库扣减，目标仓库增加）
  - 4.3: 前端创建库存调拨页面
  - 4.4: 前端 api/inventory.js 添加 transfer API

- [x] Task 5: 实现库存盘点功能
  - 5.1: InventoryController 添加 check/create 和 check/result 接口
  - 5.2: InventoryServiceImpl 添加盘点相关方法
  - 5.3: 前端创建库存盘点页面
  - 5.4: 前端 api/inventory.js 添加盘点 API

## 4. 销售管理模块完善

- [x] Task 6: 实现销售发货功能
  - 6.1: SalesController 添加 deliver 接口
  - 6.2: SalesOrderServiceImpl 添加 deliver 方法
  - 6.3: 前端 sales/index.vue 添加发货按钮和弹窗
  - 6.4: 前端 api/sales.js 添加 deliver API

- [x] Task 7: 实现客户信息管理
  - 7.1: 创建 Customer 实体和 Repository
  - 7.2: 创建 CustomerService
  - 7.3: 创建 CustomerController
  - 7.4: 前端创建 customer/index.vue 页面

## 5. 生产管理模块完善

- [x] Task 8: 实现生产领料功能
  - 8.1: ProductionController 添加 pick 接口
  - 8.2: ProductionOrderServiceImpl 添加 pickMaterials 方法
  - 8.3: 前端 production/index.vue 添加领料按钮
  - 8.4: 前端 api/production.js 添加 pick API

- [x] Task 9: 实现产成品入库功能
  - 9.1: ProductionController 添加 finish 接口
  - 9.2: ProductionOrderServiceImpl 添加 finishProduction 方法
  - 9.3: 前端添加入库确认功能
  - 9.4: 前端 api/production.js 添加 finish API

## 6. 前端页面创建

- [x] Task 10: 创建入库管理页面
  - 10.1: 创建 views/inbound/index.vue 页面
  - 10.2: 实现入库单列表、创建、审核功能

- [x] Task 11: 创建出库管理页面
  - 11.1: 创建 views/outbound/index.vue 页面
  - 11.2: 实现出库单列表、创建、审核功能

## 7. 其他功能完善

- [ ] Task 12: 完善报表分析
  - 12.1: 后端添加报表数据查询接口
  - 12.2: 前端 report/index.vue 添加图表展示

- [ ] Task 13: 完善数据字典管理
  - 13.1: 后端 DictController 完善 CRUD
  - 13.2: 前端创建数据字典管理页面

## 实施顺序

1. 先完成 Task 1 - 前端路由（快速修复）
2. 再完成 Task 2-3 - 采购模块
3. 然后 Task 4-5 - 库存模块
4. 接着 Task 6-7 - 销售模块
5. 然后 Task 8-9 - 生产模块
6. 接着 Task 10-11 - 创建新页面
7. 最后 Task 12-13 - 其他功能
