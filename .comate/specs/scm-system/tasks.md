# SCM供应链管理系统开发任务计划

## 项目结构搭建

- [x] Task 1: 创建项目目录结构
  - 1.1: 创建 backend 后端项目目录
  - 1.2: 创建 frontend 前端项目目录
  - 1.3: 配置 Maven pom.xml（Spring Boot 3.5.11, Java 21, JPA）
  - 1.4: 配置前端 package.json（Vue 3, Element Plus, Vite, TypeScript）

## 后端基础框架

- [x] Task 2: 配置后端核心配置
  - 2.1: 创建 application.yml 主配置
  - 2.2: 创建 application-dev.yml 开发环境配置
  - 2.3: 创建 application-prod.yml 生产环境配置
  - 2.4: 配置数据库连接（createDatabaseIfNotExist=true, 表前缀scm_）
  - 2.5: 配置JPA自动建表策略

- [x] Task 3: 创建后端基础包结构
  - 3.1: 创建 config 包（配置类）
  - 3.2: 创建 controller 包（控制器）
  - 3.3: 创建 service 包（业务逻辑）
  - 3.4: 创建 repository 包（数据访问）
  - 3.5: 创建 entity 包（实体类）
  - 3.6: 创建 dto 包（数据传输对象）
  - 3.7: 创建 common 包（公共类）
  - 3.8: 创建 security 包（安全相关）

- [x] Task 4: 创建公共类
  - 4.1: 创建 Result 统一响应类
  - 4.2: 创建 Constants 常量类
  - 4.3: 创建 BaseEntity 基础实体类
  - 4.4: 创建 AppException 业务异常类

## 后端数据实体

- [x] Task 5: 创建用户权限相关实体
  - 5.1: 创建 User 实体类
  - 5.2: 创建 Role 实体类
  - 5.3: 创建 Permission 实体类
  - 5.4: 创建 UserRole 实体类
  - 5.5: 创建 RolePermission 实体类

- [x] Task 6: 创建基础数据实体
  - 6.1: 创建 ItemCategory 实体类（物料分类）
  - 6.2: 创建 Item 实体类（物料）
  - 6.3: 创建 Warehouse 实体类（仓库）
  - 6.4: 创建 Supplier 实体类（供应商）

- [x] Task 7: 创建业务实体
  - 7.1: 创建 PurchaseOrder/PurchaseOrderDetail 实体类
  - 7.2: 创建 InboundOrder/InboundOrderDetail 实体类
  - 7.3: 创建 Inventory 实体类（含乐观锁 version）
  - 7.4: 创建 InventoryLog 实体类
  - 7.5: 创建 SalesOrder/SalesOrderDetail 实体类
  - 7.6: 创建 OutboundOrder/OutboundOrderDetail 实体类
  - 7.7: 创建 ProductionOrder 实体类
  - 7.8: 创建 LogisticsOrder 实体类

- [x] Task 8: 创建数据字典实体
  - 8.1: 创建 DictType 实体类
  - 8.2: 创建 DictData 实体类

## 后端数据访问层

- [x] Task 9: 创建Repository接口
  - 9.1: 创建 UserRepository
  - 9.2: 创建 RoleRepository
  - 9.3: 创建 PermissionRepository
  - 9.4: 创建 ItemCategoryRepository
  - 9.5: 创建 ItemRepository
  - 9.6: 创建 WarehouseRepository
  - 9.7: 创建 SupplierRepository
  - 9.8: 创建 PurchaseOrderRepository
  - 9.9: 创建 InboundOrderRepository
  - 9.10: 创建 InventoryRepository
  - 9.11: 创建 InventoryLogRepository
  - 9.12: 创建 SalesOrderRepository
  - 9.13: 创建 OutboundOrderRepository
  - 9.14: 创建 ProductionOrderRepository
  - 9.15: 创建 LogisticsOrderRepository
  - 9.16: 创建 DictTypeRepository
  - 9.17: 创建 DictDataRepository

## 后端业务逻辑层

- [x] Task 10: 实现用户权限服务
  - 10.1: 创建 UserService 接口和实现
  - 10.2: 创建 RoleService 接口和实现
  - 10.3: 创建 PermissionService 接口和实现

- [x] Task 11: 实现基础数据服务
  - 11.1: 创建 ItemCategoryService 接口和实现
  - 11.2: 创建 ItemService 接口和实现
  - 11.3: 创建 WarehouseService 接口和实现
  - 11.4: 创建 SupplierService 接口和实现

- [x] Task 12: 实现采购管理服务
  - 12.1: 创建 PurchaseOrderService 接口和实现
  - 12.2: 创建 InboundOrderService 接口和实现
  - 12.3: 实现采购入库库存增加逻辑

- [x] Task 13: 实现库存管理服务
  - 13.1: 创建 InventoryService 接口和实现
  - 13.2: 实现库存锁定/释放逻辑（乐观锁+行锁）
  - 13.3: 实现库存流水记录逻辑

- [x] Task 14: 实现销售管理服务
  - 14.1: 创建 SalesOrderService 接口和实现
  - 14.2: 创建 OutboundOrderService 接口和实现
  - 14.3: 实现销售出库库存减少逻辑

- [x] Task 15: 实现生产管理服务
  - 15.1: 创建 ProductionOrderService 接口和实现
  - 15.2: 实现生产领料和完工入库逻辑

- [x] Task 16: 实现物流管理服务
  - 16.1: 创建 LogisticsOrderService 接口和实现

- [x] Task 17: 实现数据字典服务
  - 17.1: 创建 DictService 接口和实现

## 后端安全认证

- [x] Task 18: 实现JWT安全认证
  - 18.1: 创建 JwtTokenUtil 工具类
  - 18.2: 创建 JwtAuthenticationFilter 过滤器
  - 18.3: 创建 SecurityConfig 安全配置
  - 18.4: 实现登录接口

## 后端控制器层

- [x] Task 19: 创建用户权限控制器
  - 19.1: 创建 AuthController（登录登出）
  - 19.2: 创建 UserController
  - 19.3: 创建 RoleController
  - 19.4: 创建 PermissionController

- [x] Task 20: 创建基础数据控制器
  - 20.1: 创建 ItemCategoryController
  - 20.2: 创建 ItemController
  - 20.3: 创建 WarehouseController
  - 20.4: 创建 SupplierController

- [x] Task 21: 创建业务控制器
  - 21.1: 创建 PurchaseController
  - 21.2: 创建 InboundController
  - 21.3: 创建 InventoryController
  - 21.4: 创建 SalesController
  - 21.5: 创建 OutboundController
  - 21.6: 创建 ProductionController
  - 21.7: 创建 LogisticsController

- [x] Task 22: 创建系统控制器
  - 22.1: 创建 DictController

## 后端初始化数据

- [x] Task 23: 实现数据初始化
  - 23.1: 创建 DataInitializer 初始化器
  - 23.2: 初始化用户数据（admin/user）
  - 23.3: 初始化角色数据
  - 23.4: 初始化权限数据
  - 23.5: 初始化数据字典
  - 23.6: 初始化默认仓库数据

## 前端基础框架

- [x] Task 24: 配置前端项目
  - 24.1: 配置 vite.config.js
  - 24.2: 配置 tsconfig.json
  - 24.3: 创建 main.ts 入口文件
  - 24.4: 创建 App.vue 根组件

- [x] Task 25: 创建前端工具和请求
  - 25.1: 创建 request.ts axios封装
  - 25.2: 创建路由配置 router/index.ts
  - 25.3: 创建用户状态 stores/user.ts

- [x] Task 26: 创建前端布局和样式
  - 26.1: 创建 MainLayout.vue 主布局
  - 26.2: 创建 main.scss 全局样式

## 前端API接口

- [x] Task 27: 创建前端API
  - 27.1: 创建 user.js 用户接口
  - 27.2: 创建 role.js 角色接口
  - 27.3: 创建 permission.js 权限接口
  - 27.4: 创建 item.js 物料接口
  - 27.5: 创建 warehouse.js 仓库接口
  - 27.6: 创建 supplier.js 供应商接口
  - 27.7: 创建 purchase.js 采购接口
  - 27.8: 创建 inbound.js 入库接口
  - 27.9: 创建 inventory.js 库存接口
  - 27.10: 创建 sales.js 销售接口
  - 27.11: 创建 outbound.js 出库接口
  - 27.12: 创建 production.js 生产接口
  - 27.13: 创建 logistics.js 物流接口
  - 27.14: 创建 report.js 报表接口

## 前端页面组件

- [x] Task 28: 创建登录和仪表盘
  - 28.1: 创建 login/index.vue 登录页
  - 28.2: 创建 dashboard/index.vue 仪表盘

- [x] Task 29: 创建基础数据页面
  - 29.1: 创建 basic/item.vue 物料管理
  - 29.2: 创建 basic/warehouse.vue 仓库管理

- [x] Task 30: 创建业务模块页面
  - 30.1: 创建 supplier/index.vue 供应商管理
  - 30.2: 创建 purchase/index.vue 采购管理
  - 30.3: 创建 inventory/index.vue 库存管理
  - 30.4: 创建 sales/index.vue 销售管理
  - 30.5: 创建 production/index.vue 生产管理
  - 30.6: 创建 logistics/index.vue 物流管理
  - 30.7: 创建 report/index.vue 报表分析

- [x] Task 31: 创建系统管理页面
  - 31.1: 创建 system/user.vue 用户管理
  - 31.2: 创建 system/role.vue 角色管理
  - 31.3: 创建 system/permission.vue 权限管理
  - 31.4: 创建 system/inventory-warning.vue 库存预警
  - 31.5: 创建 system/operation-log.vue 操作日志
  - 31.6: 创建 error/404.vue 404页面

## 打包部署

- [x] Task 32: 创建打包脚本
  - 32.1: 创建 build.bat 打包脚本
  - 32.2: 脚本实现：清理后端target目录
  - 32.3: 脚本实现：清理前端dist目录
  - 32.4: 脚本实现：安装前端依赖
  - 32.5: 脚本实现：构建前端
  - 32.6: 脚本实现：复制前端dist到后端static目录
  - 32.7: 脚本实现：构建后端jar包

- [x] Task 33: 配置静态资源访问
    - 33.1: 配置后端静态资源映射
    - 33.2: 配置前端路由history模式支持
    - 33.3: 测试打包后的访问

## 测试验收

- [x] Task 34: 系统测试
  - 34.1: 测试数据库自动创建
  - 34.2: 测试初始化数据
  - 34.3: 测试登录功能
  - 34.4: 测试各模块CRUD
  - 34.5: 测试打包构建
  - 34.6: 测试部署访问
