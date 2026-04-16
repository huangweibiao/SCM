# SCM供应链管理系统开发规格说明

## 项目概述

基于SCM供应链管理单体软件系统详细设计方案，开发完整的供应链管理系统，包含前端Vue 3应用和后端Spring Boot应用。

## 技术选型

### 后端
- Spring Boot 3.5.11
- Java 21
- Spring Data JPA
- MySQL 8.0
- 不使用Lombok和Hutool
- 表名前缀：scm_

### 前端
- Vue 3
- Element Plus
- TypeScript
- Vite构建
- 请求库：axios

### 部署
- 前端打包后静态资源嵌入后端
- 数据库连接使用createDatabaseIfNotExist=true
- 支持开发/生产配置

## 数据库设计

### 核心表结构（表名前缀：scm_）

| 表名 | 说明 |
|------|------|
| scm_user | 用户表 |
| scm_role | 角色表 |
| scm_permission | 权限表 |
| scm_user_role | 用户角色关联 |
| scm_role_permission | 角色权限关联 |
| scm_item_category | 物料分类表 |
| scm_item | 物料主表 |
| scm_warehouse | 仓库表 |
| scm_supplier | 供应商表 |
| scm_purchase_order | 采购订单主表 |
| scm_purchase_order_detail | 采购订单明细表 |
| scm_inbound_order | 入库单表 |
| scm_inbound_order_detail | 入库单明细表 |
| scm_inventory | 库存表（含乐观锁） |
| scm_inventory_log | 库存流水表 |
| scm_sales_order | 销售订单主表 |
| scm_sales_order_detail | 销售订单明细表 |
| scm_outbound_order | 出库单表 |
| scm_outbound_order_detail | 出库单明细表 |
| scm_production_order | 生产工单表 |
| scm_logistics_order | 物流订单表 |
| scm_dict_type | 数据字典类型表 |
| scm_dict_data | 数据字典数据表 |

## 功能模块

### 1. 系统基础模块
- 用户管理（CRUD、角色分配）
- 角色管理（CRUD、权限分配）
- 权限管理（模块-操作-按钮级别）
- 数据字典管理

### 2. 基础数据模块
- 物料分类管理
- 物料管理
- 仓库管理
- 供应商管理

### 3. 采购管理模块
- 采购订单创建/审核/跟踪
- 采购收货（部分/全部）
- 采购订单关闭

### 4. 库存管理模块
- 库存入库/出库/调拨
- 库存盘点
- 库存锁定/释放
- 库存预警
- 库存流水记录

### 5. 销售管理模块
- 销售订单创建/审核/跟踪
- 销售发货
- 客户信息管理

### 6. 生产管理模块
- 生产工单创建/计划/执行/完工
- 生产领料
- 产成品入库

### 7. 物流管理模块
- 运输订单创建
- 运输状态跟踪
- 物流信息同步

### 8. 报表分析模块
- 采购/销售/库存/供应商报表

## 后端架构

### 包结构
```
com.scm
├── config          # 配置类
├── controller      # 控制器
├── service         # 业务逻辑
├── repository      # 数据访问
├── entity          # 实体类
├── dto             # 数据传输对象
├── common          # 公共类（常量、异常、工具）
└── security        # 安全相关
```

### 核心配置
- application-dev.yml：开发环境配置
- application-prod.yml：生产环境配置
- 数据自动创建（JPA策略：update）
- 初始化数据加载器

## 前端架构

### 目录结构
```
src/
├── api/            # API接口
├── views/          # 页面组件
├── router/         # 路由配置
├── stores/         # 状态管理
├── utils/          # 工具函数
└── styles/         # 样式文件
```

### 路由结构
- /login：登录页
- /：主布局
  - /dashboard：仪表盘
  - /basic/item：物料管理
  - /basic/warehouse：仓库管理
  - /supplier：供应商管理
  - /purchase：采购管理
  - /inventory：库存管理
  - /sales：销售管理
  - /production：生产管理
  - /logistics：物流管理
  - /report：报表分析
  - /system/user：用户管理
  - /system/role：角色管理
  - /system/permission：权限管理

## 打包部署流程

### 构建脚本（build.bat）
1. 清理后端target目录
2. 清理前端dist目录
3. 安装前端依赖
4. 构建前端
5. 复制前端dist到后端src/main/resources/static
6. 构建后端jar包

### 部署访问
- 前端资源通过Spring Boot静态资源访问
- 默认路径：/index.html

## 初始化数据

### 用户数据
- 管理员用户：admin/123456（角色：超级管理员）
- 普通用户：user/123456（角色：普通用户）

### 角色数据
- 超级管理员：所有权限
- 普通用户：基础查询权限

### 权限数据
- 系统管理：用户、角色、权限管理
- 基础数据：物料、仓库、供应商管理
- 业务模块：采购、库存、销售、生产、物流管理

### 数据字典
- 订单状态
- 入库类型
- 出库类型
- 物流状态

## 验收标准

1. 系统启动后自动创建数据库表
2. 初始化数据正确导入
3. 登录功能正常
4. 各模块CRUD功能正常
5. 打包脚本可正常构建前端+后端
6. 打包后的jar可正常启动并访问前端
