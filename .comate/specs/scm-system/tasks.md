# SCM供应链管理系统开发任务清单

## 任务总览

本任务清单将SCM系统开发分为四个阶段，共27个核心任务，覆盖项目搭建、基础模块、核心业务、完善功能、增强功能等完整开发流程。

---

## 第一阶段：基础设施搭建 (6个任务)

- [x] Task 1: Maven项目搭建与配置
    - 1.1: 创建Maven项目结构，配置pom.xml依赖(Spring Boot 3.5.11, MyBatis-Plus, MySQL, Druid连接池等)
    - 1.2: 配置application.yml(数据库连接、端口、日志配置)
    - 1.3: 创建启动类ScmApplication，配置包扫描路径
    - 1.4: 配置MyBatis-Plus(分页插件、乐观锁插件、逻辑删除插件)

- [x] Task 2: 数据库设计初始化
    - 2.1: 编建18张核心表的DDL建表SQL脚本
    - 2.2: 添加必要的索引和约束
    - 2.3: 创建初始化数据SQL脚本(数据字典类型、示例物料、示例仓库)
    - 2.4: 执行SQL脚本完成数据库初始化

- [x] Task 3: 通用模块开发
    - 3.1: 创建统一响应对象ApiResponse(code, msg, data, timestamp)
    - 3.2: 创建分页响应对象PageResult(total, list)
    - 3.3: 创建自定义业务异常BusinessException
    - 3.4: 创建全局异常处理器GlobalExceptionHandler
    - 3.5: 创建通用工具类(DateUtils, StringUtils, BeanUtils等)

- [x] Task 4: 枚举定义与常量类
- [x] Task 5: 基础实体类与DTO设计
- [x] Task 6: 基础数据模块 - 物料管理
    - 6.1: 创建Item实体类和ItemMapper
    - 6.2: 创建ItemCategory实体类和ItemCategoryMapper
    - 6.3: 创建ItemService接口和实现类
    - 6.4: 创建ItemCategoryService接口和实现类
    - 6.5: 实现物料CRUD接口(创建、更新、删除、查询列表、查询详情)
    - 6.6: 实现物料分类CRUD接口(创建、更新、删除、树形查询)
    - 6.7: 创建BasicController统一管理物料和分类接口

---

## 第二阶段：核心业务模块 (7个任务)

- [x] Task 7: 基础数据模块 - 仓库管理
    - 7.1: 创建Warehouse实体类和WarehouseMapper
    - 7.2: 创建WarehouseService接口和实现类
    - 7.3: 实现仓库CRUD接口(创建、更新、删除、查询列表)
    - 7.4: 在BasicController中添加仓库管理接口

- [x] Task 8: 基础数据模块 - 数据字典管理
    - 8.1: 创建DictType和DictData实体类及Mapper
    - 8.2: 创建DictService接口和实现类
    - 8.3: 实现字典类型和字典数据CRUD接口
    - 8.4: 实现根据字典编码查询字典数据接口
    - 8.5: 在BasicController中添加字典管理接口

- [x] Task 9: 供应商管理模块
    - 9.1: 创建Supplier实体类和SupplierMapper
    - 9.2: 创建SupplierService接口和实现类
    - 9.3: 实现供应商创建逻辑(生成唯一供应商编码)
    - 9.4: 实现供应商更新逻辑(更新、状态管理、评级更新)
    - 9.5: 实现供应商删除逻辑(删除前校验关联数据)
    - 9.6: 实现供应商查询接口(详情、分页列表)
    - 9.7: 创建SupplierController

- [x] Task 10: 采购管理模块 - 采购订单
    - 10.1: 创建PurchaseOrder和PurchaseOrderDetail实体类及Mapper
    - 10.2: 创建PurchaseOrderService接口和实现类
    - 10.3: 实现创建采购订单逻辑(验证供应商/物料、计算金额、生成订单号)
    - 10.4: 实现审核采购订单逻辑(状态流转、记录审核信息)
    - 10.5: 实现删除采购订单逻辑(仅待审核状态可删除)
    - 10.6: 实现采购订单查询接口(详情、分页列表)
    - 10.7: 创建PurchaseOrderController

- [x] Task 11: 采购管理模块 - 入库单与库存更新
    - 11.1: 创建InboundOrder和InboundOrderDetail实体类及Mapper
    - 11.2: 创建InboundOrderService接口和实现类
    - 11.3: 创建InventoryService接口和实现类
    - 11.4: 实现入库单创建逻辑(关联采购订单)
    - 11.5: 实现入库单确认逻辑(事务控制、行锁、乐观锁更新库存)
    - 11.6: 实现库存增加逻辑(更新inventory表、记录inventory_log)
    - 11.7: 实现采购收货逻辑(创建入库单、更新订单明细received_qty)
    - 11.8: 创建InboundOrderController

- [x] Task 12: 库存管理模块
    - 12.1: 创建Inventory和InventoryLog实体类及Mapper
    - 12.2: 实现库存查询接口(分页列表、详情)
    - 12.3: 实现库存流水查询接口(分页列表)
    - 12.4: 实现库存预警查询接口(可用库存<min_stock或>max_stock)
    - 12.5: 创建InventoryController
    - 12.6: 实现库存扣减逻辑(行锁+乐观锁、校验可用库存)
    - 12.7: 实现库存锁定逻辑(销售订单审核时锁定)
    - 12.8: 实现库存释放逻辑(出库时释放锁定)

- [x] Task 13: 销售管理模块
    - 13.1: 创建SalesOrder和SalesOrderDetail实体类及Mapper
    - 13.2: 创建OutboundOrder和OutboundOrderDetail实体类及Mapper
    - 13.3: 创建SalesOrderService和OutboundOrderService接口及实现类
    - 13.4: 实现创建销售订单逻辑(验证物料/仓库、计算金额、生成订单号)
    - 13.5: 实现审核销售订单逻辑(校验可用库存、锁定库存、状态流转)
    - 13.6: 实现销售发货逻辑(创建出库单、更新订单明细shipped_qty)
    - 13.7: 实现出库单确认逻辑(扣减库存、释放锁定、记录流水)
    - 13.8: 实现销售订单查询接口(详情、分页列表)
    - 13.9: 创建SalesOrderController和OutboundOrderController

---

## 第三阶段：完善功能模块 (7个任务)

- [x] Task 14: 生产/委外管理模块
    - 14.1: 创建ProductionOrder实体类和Mapper
    - 14.2: 创建ProductionOrderService接口和实现类
    - 14.3: 实现创建生产工单逻辑(指定产成品、生成工单号)
    - 14.4: 实现开始生产逻辑(状态流转、记录实际开始时间)
    - 14.5: 实现生产领料逻辑(创建出库单、扣减原材料库存)
    - 14.6: 实现完工入库逻辑(创建入库单、增加产成品库存、更新finished_qty)
    - 14.7: 实现工单查询接口(详情、分页列表)
    - 14.8: 创建ProductionOrderController

- [x] Task 15: 物流管理模块
    - 15.1: 创建LogisticsOrder实体类和Mapper
    - 15.2: 创建LogisticsOrderService接口和实现类
    - 15.3: 实现创建物流订单逻辑(关联采购/销售订单、生成物流单号)
    - 15.4: 实现更新物流状态逻辑(状态流转)
    - 15.5: 实现物流订单查询接口(详情、分页列表)
    - 15.6: 创建LogisticsOrderController

- [x] Task 16: 报表分析模块 - 采购报表
- [x] Task 17: 报表分析模块 - 销售报表
- [x] Task 18: 报表分析模块 - 库存报表
    - 18.1: 创建InventoryReportService接口和实现类
    - 18.2: 实现库存数量统计接口(按仓库/物料分类)
    - 18.3: 实现库存预警统计接口
    - 18.4: 在ReportController中添加库存报表接口

- [x] Task 19: 数据字典接口完善
    - 19.1: 实现数据字典缓存(使用本地缓存或Redis)
    - 19.2: 优化字典查询接口性能
    - 19.3: 在各业务模块中集成字典数据展示(订单状态、入库出库类型等)

- [x] Task 20: 订单状态自动流转完善
    - 20.1: 完善采购订单状态自动流转逻辑(全部收货后自动完成)
    - 20.2: 完善销售订单状态自动流转逻辑(全部发货后自动完成)
    - 20.3: 完善生产工单状态自动流转逻辑(全部完工后自动完成)
    - 20.4: 添加订单手动关闭功能

---

## 第四阶段：增强功能与优化 (7个任务)

- [x] Task 21: 权限管理模块 - 用户与角色
    - 21.1: 创建SysUser, SysRole, SysPermission实体类及Mapper
    - 21.2: 创建UserService, RoleService, PermissionService接口及实现类
    - 21.3: 实现用户CRUD接口(创建、更新、删除、查询、重置密码)
    - 21.4: 实现角色CRUD接口(创建、更新、删除、查询、分配权限)
    - 21.5: 实现权限CRUD接口(创建、更新、删除、查询、树形展示)
    - 21.6: 创建UserController, RoleController, PermissionController

- [x] Task 22: 认证授权集成
    - 22.1: 集成Spring Security + JWT
    - 22.2: 实现登录认证逻辑(生成JWT Token)
    - 22.3: 实现JWT Token验证过滤器
    - 22.4: 实现基于RBAC的权限控制(注解+拦截器)
    - 22.5: 配置SecurityConfig，放行登录等公开接口
    - 22.6: 创建AuthController，实现登录/登出/刷新Token接口

- [x] Task 23: 操作审计日志模块
    - 23.1: 创建OperationLog实体类和Mapper
    - 23.2: 创建OperationLogService接口和实现类
    - 23.3: 实现操作日志记录逻辑(使用AOP切面)
    - 23.4: 记录核心操作(订单审核、库存修改、权限变更等)
    - 23.5: 实现操作日志查询接口(分页列表)
    - 23.6: 创建OperationLogController

- [x] Task 24: 定时任务 - 库存预警
    - 24.1: 配置@EnableScheduling启用定时任务
    - 24.2: 创建库存预警定时任务类InventoryWarnScheduledTask
    - 24.3: 实现定时扫描库存表逻辑(每小时执行)
    - 24.4: 实现低库存预警生成逻辑(可用库存 < min_stock)
    - 24.5: 实现超库存预警生成逻辑(库存 > max_stock)
    - 24.6: 预警通知功能(简化版:日志记录；完整版:发送消息/邮件)

- [x] Task 25: 数据校验与异常处理完善
    - 25.1: 在DTO中添加参数校验注解(@NotNull, @Min, @Max等)
    - 25.2: 实现参数校验异常处理
    - 25.3: 完善业务异常处理(库存不足、参数错误等)
    - 25.4: 完善全局异常处理器，覆盖更多异常类型
    - 25.5: 统一错误码定义与错误信息

- [x] Task 26: 性能优化
    - 26.1: 添加数据库索引(订单号、物料编码、供应商id等)
    - 26.2: 优化查询SQL，避免N+1查询问题
    - 26.3: 使用批量操作(批量插入、批量更新)提升性能
    - 26.4: 实现物料信息缓存(本地缓存或Redis)
    - 26.5: 实现数据字典缓存
    - 26.6: 优化分页查询性能

- [x] Task 27: 接口文档与代码规范
    - 27.1: 集成Swagger/Knife4j，生成接口文档
    - 27.2: 为所有Controller接口添加Swagger注解
    - 27.3: 为所有Service方法添加JavaDoc注释
    - 27.4: 为复杂业务逻辑添加行内注释
    - 27.5: 代码格式化与规范检查
    - 27.6: 编写README.md文档(项目说明、启动方式、API文档链接)

---

## 开发注意事项

### 事务管理
- 所有涉及库存变动的操作必须使用@Transactional
- 入库、出库、订单审核等核心操作需要事务保障

### 并发控制
- 库存更新必须使用乐观锁(version字段)
- 高并发场景使用select for update行锁
- 乐观锁更新失败需要重试机制(最多3次)

### 数据一致性
- 所有库存变动必须记录inventory_log
- 订单状态流转必须符合业务规则
- 删除操作前必须校验关联数据

### 性能考虑
- 避免循环查库，使用批量操作
- 合理使用缓存(物料、字典等)
- 大数据量查询必须分页
- 避免N+1查询问题

### 安全考虑
- 所有业务接口需要JWT认证
- 敏感操作需要权限控制
- 密码使用BCrypt加密
- 敏感数据脱敏展示

### 代码规范
- 遵循阿里巴巴Java开发规范
- 类注释、方法注释完整
- 异常处理统一规范
- 命名规范统一
