# SCM供应链管理单体软件系统详细设计方案

本方案为中小型企业设计**SCM供应链管理单体软件系统**，覆盖采购、库存、销售、轻量生产/委外、供应商、物流管理等核心模块，实现从订单到交付的全链路管理，兼顾开发便捷性、事务强一致性，同时为后续扩展预留弹性。方案整合系统架构、模块设计、数据库设计、业务流程、接口规范、安全与性能设计等全维度内容，形成可直接落地的设计文档。

## 一、系统概述

### 1.1 系统目标

1. 提升供应链各环节信息透明度与运营效率，实现采购、库存、销售、生产、物流的一体化管理；

2. 建立库存动态监控与预警机制，避免库存积压或短缺，优化库存周转率；

3. 实现订单全生命周期跟踪，支持采购/销售订单的审批、执行、完成全流程管理；

4. 提供供应商全维度管理与评估能力，夯实供应链上游合作基础；

5. 配套数据分析与报表功能，为企业经营决策提供数据支撑；

6. 完善权限管理、操作审计与安全控制，保障系统数据安全与合规性。

### 1.2 系统范围

|类型|包含模块|
|---|---|
|核心模块|供应商管理、采购管理、库存管理、销售管理、生产/委外管理（轻量）、物流管理、报表分析|
|基础/支撑模块|系统基础数据管理（物料、仓库、部门等）、用户与权限管理、消息通知、审计日志、系统设置、第三方接口集成（ERP/财务系统）|
## 二、系统技术架构设计

本系统采用**单体架构+模块化设计**，遵循三层架构思想，模块间通过服务接口解耦，既保证核心交易场景的事务强一致性，又降低开发、部署与维护成本，同时为后续拆分为微服务预留扩展空间。

### 2.1 技术选型（可替换）

|架构层级|技术选型示例|选型原因|
|---|---|---|
|前端（表现层）|Vue3 + Element Plus / React + Ant Design|组件丰富、开发效率高，支持PC/移动端适配|
|后端框架（业务逻辑层）|Spring Boot 2.7+ / MyBatis-Plus / JPA|稳定可靠、模块化易扩展，适配单体架构开发|
|数据库（数据持久层）|MySQL 8.0 / PostgreSQL 15|关系型数据库，保障数据强一致性，适配供应链交易场景|
|缓存|Redis|用于会话管理、库存锁、数据字典、高频数据查询加速|
|消息队列（可选）|RabbitMQ / RocketMQ / Kafka|异步处理单据、库存变动、订单状态通知，削峰填谷|
|日志/监控|ELK / Prometheus + Grafana|实现系统监控、日志收集分析、操作审计|
|部署|Nginx + 单Jar + 单MySQL / Docker容器化|单体部署简单，Docker支持轻量容器化，便于后续集群扩展|
|认证授权|JWT + OAuth2 + RBAC|实现用户认证、基于角色的权限控制|
### 2.2 系统架构图

```Plain Text

+-------------------------------------------------------------+
|                   用户界面（PC Web/移动端）                  |
+-------------------------------------------------------------+
                 |
                 v
+-------------------------------------------------------------+
|                 业务逻辑层（单体应用-模块化解耦）             |
|  +--------+  +--------+  +--------+  +--------+  +--------+ |
|  |供应商管理|  |采购管理|  |库存管理|  |销售管理|  |生产管理| |
|  +--------+  +--------+  +--------+  +--------+  +--------+ |
|  +--------+  +--------+  +--------+  +--------+  +--------+ |
|  |物流管理|  |报表分析|  |基础数据|  |权限管理|  |系统设置| |
|  +--------+  +--------+  +--------+  +--------+  +--------+ |
+-------------------------------------------------------------+
                 |
                 v
+-------------------------------------------------------------+
|                 数据持久层                                   |
|  +---------------------+  +-------------------------------+ |
|  | MySQL/PostgreSQL    |  | Redis（缓存/锁/数据字典）     | |
|  | 核心业务表/流水表   |  |                               | |
|  +---------------------+  +-------------------------------+ |
+-------------------------------------------------------------+
|  配套组件：消息队列/日志监控/CI/CD（GitLab CI/Jenkins）     |
+-------------------------------------------------------------+
```

## 三、核心模块详细设计

各模块采用**高内聚、低耦合**设计，Service层可跨模块调用核心能力（如库存更新被入库/出库模块调用），所有模块共享同一数据库连接池，定时任务通过`@Scheduled`实现。

### 3.1 供应商管理模块

**核心功能**：供应商信息全生命周期管理、供应商评估/评级、付款条件管理、供应商状态管控（启用/停用）；

**核心关联**：为采购管理模块提供合格供应商池，关联采购订单的供应商归属。

### 3.2 采购管理模块

**核心功能**：采购订单创建/审核/跟踪、采购收货（部分/全部）、采购订单关闭、采购报价管理、采购合同管理；

**核心关联**：关联供应商管理模块（获取供应商信息）、库存管理模块（采购入库触发库存增加）、生产管理模块（按需生成采购订单）。

### 3.3 库存管理模块

**核心功能**：库存入库/出库/调拨、库存盘点、库存快照、库存锁定/释放（销售预占）、库存预警（安全/最高/最低库存）、库存流水记录；

**核心定位**：系统核心模块，为采购、销售、生产模块提供库存数据支撑，保障库存一致性。

### 3.4 销售管理模块

**核心功能**：销售订单创建/审核/跟踪、销售发货（部分/全部）、客户信息管理、退货/换货处理、销售订单完成确认；

**核心关联**：关联库存管理模块（审核锁库存、出库减库存）、物流管理模块（发货关联物流订单）。

### 3.5 生产/委外管理模块（轻量）

**核心功能**：生产工单创建/计划/执行/完工、生产领料（关联出库）、产成品入库（关联入库）、委外加工单管理；

**核心关联**：关联库存管理模块（领料减库存、完工增库存）、采购管理模块（委外物料采购）。

### 3.6 物流管理模块

**核心功能**：运输订单创建、运输状态跟踪、发货/收货物流信息同步、运输成本计算；

**核心关联**：关联销售管理模块（销售出库关联物流）、采购管理模块（采购入库关联物流）。

### 3.7 报表分析模块

**核心功能**：采购报表（订单量/金额/完成率）、销售报表（订单量/金额/发货率）、库存报表（库存数量/周转率/预警）、供应商报表（合作量/交付率）；

**技术支撑**：前端使用ECharts/Highcharts实现数据可视化，后端通过SQL聚合/ELK/ClickHouse加速大数据量分析。

### 3.8 系统基础数据模块

**核心功能**：物料（编码/分类/规格）、仓库、部门、数据字典（订单状态/入库/出库类型等）的统一管理；

**核心定位**：为所有业务模块提供基础数据标准，保障系统数据一致性。

## 四、数据库详细设计

### 4.1 核心设计原则

1. 采用关系型数据库设计，通过外键（FK）建立实体关联，保障数据完整性；

2. 核心交易表（库存、订单）增加**唯一约束/索引**，提升查询与操作效率；

3. 库存表增加**乐观锁（version）** 与**行锁（select for update）**，保障高并发下库存一致性；

4. 订单类表采用**主表+明细表**设计，主表存整体信息，明细表存物料级明细；

5. 增加**库存流水/操作日志**相关表（补充设计），记录库存变动与关键操作，便于追溯；

6. 为后续多租户扩展，所有核心表预留`tenant_id`字段。

### 4.2 核心表结构设计（全字段补充）

#### 1. 物料分类表（item_category）- 补充

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|category_code|varchar(50)|物料分类编码|unique, not null|
|category_name|varchar(200)|物料分类名称|not null|
|parent_id|bigint|上级分类id|FK, default 0|
|sort|int|排序号|default 0|
|status|tinyint|1启用 0停用|default 1|
#### 2. 物料主表（item）

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|item_code|varchar(50)|物料编码|unique, not null|
|item_name|varchar(200)|物料名称|not null|
|spec|varchar(200)|规格型号||
|unit|varchar(20)|单位（个/箱/吨）|not null|
|category_id|bigint|物料分类id|FK|
|safety_stock|decimal(18,2)|安全库存|default 0|
|max_stock|decimal(18,2)|最高库存|default 0|
|min_stock|decimal(18,2)|最低库存|default 0|
|status|tinyint|1启用 0停用|default 1|
|create_time|datetime|创建时间|default CURRENT_TIMESTAMP|
|update_time|datetime|更新时间|default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP|
#### 3. 仓库表（warehouse）- 补充

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|warehouse_code|varchar(50)|仓库编码|unique, not null|
|warehouse_name|varchar(200)|仓库名称|not null|
|address|varchar(500)|仓库地址||
|manager|varchar(50)|仓库管理员||
|phone|varchar(20)|联系电话||
|status|tinyint|1启用 0停用|default 1|
#### 4. 供应商表（supplier）

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|supplier_code|varchar(50)|供应商编码|unique|
|supplier_name|varchar(200)|供应商名称|not null|
|contact_person|varchar(50)|联系人||
|phone|varchar(20)|电话||
|email|varchar(100)|邮箱||
|address|varchar(500)|地址||
|payment_terms|varchar(100)|付款条件（账期）||
|rating|tinyint|供应商评级（1-5星）|default 3|
|status|tinyint|1启用 0停用|default 1|
|create_time|datetime|创建时间|default CURRENT_TIMESTAMP|
#### 5. 采购订单主表（purchase_order）

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|po_no|varchar(50)|采购订单号|unique, not null|
|supplier_id|bigint|供应商id|FK|
|warehouse_id|bigint|收货仓库id|FK|
|order_date|date|下单日期|not null|
|total_amount|decimal(18,2)|订单总金额（含税）||
|status|tinyint|10待审核 20已审核 30部分收货 40已完成 50关闭|default 10|
|create_by|bigint|制单人id||
|create_time|datetime|创建时间|default CURRENT_TIMESTAMP|
|audit_by|bigint|审核人id||
|audit_time|datetime|审核时间||
|remark|varchar(500)|备注||
#### 6. 采购订单明细表（purchase_order_detail）

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|po_id|bigint|采购订单id|FK|
|item_id|bigint|物料id|FK|
|qty|decimal(18,2)|采购数量|not null|
|price|decimal(18,4)|不含税单价||
|tax_rate|decimal(5,2)|税率%|default 0|
|tax_amount|decimal(18,2)|税额（qty*price*tax_rate/100）||
|amount|decimal(18,2)|含税金额（qty*price*(1+tax_rate/100)）||
|received_qty|decimal(18,2)|已收货数量|default 0|
|remain_qty|decimal(18,2)|未收货数量（qty-received_qty）||
#### 7. 入库单表（inbound_order）

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|inbound_no|varchar(50)|入库单号|unique, not null|
|po_id|bigint|关联采购订单id（可为空）|FK|
|mo_id|bigint|关联生产工单id（可为空）|FK|
|warehouse_id|bigint|入库仓库id|FK|
|inbound_type|tinyint|10采购入库 20生产入库 30退货入库 40调拨入库|not null|
|inbound_date|datetime|入库日期|not null|
|total_qty|decimal(18,2)|总入库数量||
|total_amount|decimal(18,2)|总入库金额||
|status|tinyint|10草稿 20已确认|default 10|
|create_by|bigint|制单人id||
|remark|varchar(500)|备注||
#### 8. 入库单明细表（inbound_order_detail）- 补充

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|inbound_id|bigint|入库单id|FK|
|item_id|bigint|物料id|FK|
|po_detail_id|bigint|关联采购订单明细id|FK|
|qty|decimal(18,2)|入库数量|not null|
|price|decimal(18,4)|入库单价||
|amount|decimal(18,2)|入库金额||
|batch_no|varchar(50)|物料批次号||
|expire_date|date|物料过期日期||
#### 9. 库存表（inventory）- 核心，补充乐观锁

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|item_id|bigint|物料id|FK|
|warehouse_id|bigint|仓库id|FK|
|qty|decimal(18,2)|当前库存数量|not null, default 0|
|locked_qty|decimal(18,2)|锁定数量（销售预占）|default 0|
|available_qty|decimal(18,2)|可用库存（qty-locked_qty）|generated always as (qty-locked_qty)|
|batch_no|varchar(50)|物料批次号||
|expire_date|date|物料过期日期||
|version|int|乐观锁版本号|default 0|
|last_update_time|datetime|最后更新时间|default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP|
|**唯一约束**|(item_id, warehouse_id, batch_no)|物料+仓库+批次唯一||
#### 10. 库存流水表（inventory_log）- 补充

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|item_id|bigint|物料id|FK|
|warehouse_id|bigint|仓库id|FK|
|before_qty|decimal(18,2)|操作前库存|not null|
|change_qty|decimal(18,2)|库存变动量（+入库/-出库）||
|after_qty|decimal(18,2)|操作后库存|not null|
|business_type|tinyint|业务类型（10入库 20出库 30调拨 40盘点）||
|business_id|bigint|关联业务单id（入库/出库/订单id）||
|operate_by|bigint|操作人id||
|operate_time|datetime|操作时间|default CURRENT_TIMESTAMP|
|remark|varchar(500)|操作备注||
#### 11. 销售订单主表（sales_order）

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|so_no|varchar(50)|销售订单号|unique, not null|
|customer_name|varchar(200)|客户名称|not null|
|customer_phone|varchar(20)|客户电话||
|warehouse_id|bigint|发货仓库id|FK|
|order_date|date|下单日期|default CURRENT_DATE|
|total_amount|decimal(18,2)|订单总金额（含税）||
|status|tinyint|10待审核 20已审核 30部分发货 40已完成|default 10|
|create_by|bigint|制单人id||
|audit_by|bigint|审核人id||
|audit_time|datetime|审核时间||
|remark|varchar(500)|备注||
#### 12. 销售订单明细表（sales_order_detail）

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|so_id|bigint|销售订单id|FK|
|item_id|bigint|物料id|FK|
|qty|decimal(18,2)|销售数量|not null|
|price|decimal(18,4)|不含税单价||
|tax_rate|decimal(5,2)|税率%|default 0|
|tax_amount|decimal(18,2)|税额||
|amount|decimal(18,2)|含税金额||
|shipped_qty|decimal(18,2)|已发货数量|default 0|
|remain_qty|decimal(18,2)|未发货数量||
#### 13. 出库单表（outbound_order）

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|outbound_no|varchar(50)|出库单号|unique, not null|
|so_id|bigint|关联销售订单id|FK|
|mo_id|bigint|关联生产工单id（领料）|FK|
|warehouse_id|bigint|出库仓库id|FK|
|outbound_type|tinyint|10销售出库 20生产领料 30退货出库 40调拨出库|not null|
|outbound_date|datetime|出库日期|not null|
|total_qty|decimal(18,2)|总出库数量||
|total_amount|decimal(18,2)|总出库金额||
|status|tinyint|10草稿 20已确认|default 10|
|create_by|bigint|制单人id||
|remark|varchar(500)|备注||
#### 14. 出库单明细表（outbound_order_detail）- 补充

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|outbound_id|bigint|出库单id|FK|
|item_id|bigint|物料id|FK|
|so_detail_id|bigint|关联销售订单明细id|FK|
|qty|decimal(18,2)|出库数量|not null|
|price|decimal(18,4)|出库单价||
|amount|decimal(18,2)|出库金额||
|batch_no|varchar(50)|物料批次号||
#### 15. 生产工单表（production_order）- 轻量

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|mo_no|varchar(50)|工单号|unique, not null|
|item_id|bigint|产成品物料id|FK|
|qty|decimal(18,2)|计划生产数量|not null|
|finished_qty|decimal(18,2)|已完工数量|default 0|
|start_date|date|计划开始日期||
|end_date|date|计划结束日期||
|actual_start|datetime|实际开始时间||
|actual_end|datetime|实际结束时间||
|status|tinyint|10计划 20生产中 30完工|default 10|
|remark|varchar(500)|备注||
#### 16. 物流订单表（logistics_order）- 补充

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|tenant_id|bigint|租户id（预留）|default 0|
|logistics_no|varchar(50)|物流单号|unique, not null|
|business_type|tinyint|关联业务类型（10采购 20销售）|not null|
|business_id|bigint|关联业务单id（采购/销售订单id）|FK|
|courier_name|varchar(100)|快递公司名称||
|courier_no|varchar(50)|快递单号||
|send_address|varchar(500)|发货地址||
|receive_address|varchar(500)|收货地址|not null|
|receive_person|varchar(50)|收货人|not null|
|receive_phone|varchar(20)|收货人电话|not null|
|status|tinyint|10待发货 20运输中 30已签收 40拒收|default 10|
|logistics_fee|decimal(18,2)|物流费用|default 0|
|create_time|datetime|创建时间|default CURRENT_TIMESTAMP|
|update_time|datetime|更新时间|default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP|
#### 17. 数据字典类型表（dict_type）- 补充

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|dict_code|varchar(50)|字典类型编码|unique, not null|
|dict_name|varchar(200)|字典类型名称|not null|
|sort|int|排序号|default 0|
|status|tinyint|1启用 0停用|default 1|
#### 18. 数据字典数据表（dict_data）- 补充

|字段名|类型|说明|约束|
|---|---|---|---|
|id|bigint|主键|PK|
|dict_type_id|bigint|字典类型id|FK|
|dict_label|varchar(100)|字典标签|not null|
|dict_value|varchar(100)|字典值|not null|
|sort|int|排序号|default 0|
|status|tinyint|1启用 0停用|default 1|
### 4.3 核心实体关系

```Plain Text

Item_Category --< Item
Warehouse --< Inventory --< Inventory_Log
Item --< Purchase_Order_Detail --< Purchase_Order --< Supplier
Purchase_Order --< Inbound_Order --< Inventory
Item --< Sales_Order_Detail --< Sales_Order
Sales_Order --< Outbound_Order --< Inventory
Item --< Production_Order --< Inbound_Order/Outbound_Order
Purchase_Order/Sales_Order --< Logistics_Order
Dict_Type --< Dict_Data
```

## 五、核心业务流程与关键设计点

### 5.1 库存一致性保障（核心）

库存为系统核心数据，需严格保障**操作原子性、数据一致性**，针对高并发场景采用**行锁+乐观锁**双重保障：

1. 入库/出库等核心操作使用`@Transactional`声明事务，通过`select for update`加行锁，锁定库存表指定物料+仓库的记录，避免并发修改；

2. 库存更新强制使用**乐观锁**，通过`version`字段实现，更新失败则触发重试机制，SQL示例：

```SQL

update inventory 
set qty = qty - #{outQty}, locked_qty = locked_qty - #{lockQty}, version = version + 1 
where item_id = ? and warehouse_id = ? and version = #{oldVersion} and qty - #{outQty} >= 0
```

1. 所有库存变动**必须**记录库存流水表，便于后续数据追溯与问题排查。

### 5.2 采购到入库全流程

1. 制单：选择合格供应商、物料，创建采购订单（状态10待审核）；

2. 审核：审核通过后订单状态改为20已审核，审核不通过则驳回；

3. 收货：根据采购订单创建入库单（类型10采购入库），支持**部分收货**，每次收货更新采购订单明细的`received_qty`；

4. 入库确认：入库单审核通过后，触发库存增加，同步更新库存表与库存流水表；

5. 完成：采购订单所有物料全部收货后，状态自动改为40已完成，支持手动关闭（状态50）。

### 5.3 销售到出库全流程

1. 制单：选择客户、物料、发货仓库，创建销售订单（状态10待审核）；

2. 审核：审核时校验物料可用库存，足够则**锁定库存**（库存表`locked_qty`增加），订单状态改为20已审核；库存不足则审核失败；

3. 发货：根据销售订单创建出库单（类型10销售出库），支持**部分发货**，每次发货更新销售订单明细的`shipped_qty`；

4. 出库确认：出库单审核通过后，触发库存减少+锁定库存释放（库存表`qty`减少、`locked_qty`减少），同步更新库存表与库存流水表；

5. 完成：销售订单所有物料全部发货后，状态自动改为40已完成。

### 5.4 生产工单到领料/完工流程

1. 创单：创建生产工单，指定产成品物料、计划生产数量（状态10计划）；

2. 领料：根据工单创建出库单（类型20生产领料），出库单确认后扣减原材料库存；

3. 生产：工单状态改为20生产中，记录实际开始时间；

4. 完工：生产完成后，创建入库单（类型20生产入库），入库单确认后增加产成品库存；更新工单`finished_qty`；

5. 完成：工单所有计划数量完工后，状态改为30完工，记录实际结束时间。

### 5.5 库存预警流程

1. 系统通过`@Scheduled`定时任务（如每小时）扫描库存表；

2. 校验物料**可用库存**是否低于`min_stock`/安全库存，或高于`max_stock`；

3. 触发预警：通过系统消息/邮件通知仓库管理员与采购专员，同时在系统首页展示预警信息；

4. 预警解除：当库存恢复至合理范围后，自动解除预警。

## 六、接口设计规范

### 6.1 接口整体规范

1. 采用**RESTful API**设计风格，接口路径以`/api/v1/`为前缀，按模块划分；

2. 请求方式：GET（查询）、POST（创建）、PUT（更新）、DELETE（删除/停用）；

3. 数据格式：请求/响应均为JSON，统一返回格式包含`code`（状态码）、`msg`（提示信息）、`data`（返回数据）、`timestamp`（时间戳）；

4. 分页查询：统一使用`pageNum`（页码）、`pageSize`（每页条数）作为请求参数，返回`total`（总条数）、`list`（数据列表）；

5. 认证授权：所有接口需携带JWT令牌，放在请求头`Authorization`中，格式为`Bearer {token}`；

6. 异常处理：系统统一捕获异常，返回标准化错误码与提示信息，避免暴露底层异常。

### 6.2 模块接口前缀划分

|模块|接口前缀|模块|接口前缀|
|---|---|---|---|
|供应商管理|/api/v1/supplier|库存管理|/api/v1/inventory|
|采购管理|/api/v1/purchase|销售管理|/api/v1/sales|
|生产管理|/api/v1/production|物流管理|/api/v1/logistics|
|基础数据|/api/v1/basic|报表分析|/api/v1/report|
|权限管理|/api/v1/permission|系统设置|/api/v1/system|
### 6.3 核心接口示例

#### 示例1：创建采购订单（POST /api/v1/purchase/order）

**请求参数**：

```JSON

{
    "supplierId": 1001,
    "warehouseId": 2001,
    "orderDate": "2026-04-04",
    "remark": "月度采购",
    "items": [
        {
            "itemId": 3001,
            "qty": 100.00,
            "price": 50.0000,
            "taxRate": 13.00
        }
    ]
}
```

**响应参数**：

```JSON

{
    "code": 200,
    "msg": "创建成功",
    "data": {
        "poId": 4001,
        "poNo": "PO202604040001",
        "status": 10
    },
    "timestamp": 1775374295000
}
```

#### 示例2：库存预警查询（GET /api/v1/inventory/warn）

**请求参数**：`?pageNum=1&pageSize=10&warnType=1`（warnType：1低库存 2超库存）

**响应参数**：

```JSON

{
    "code": 200,
    "msg": "查询成功",
    "data": {
        "total": 5,
        "list": [
            {
                "itemId": 3001,
                "itemCode": "ITEM001",
                "itemName": "XX物料",
                "warehouseId": 2001,
                "warehouseName": "主仓库",
                "availableQty": 50.00,
                "minStock": 100.00,
                "warnType": 1
            }
        ]
    },
    "timestamp": 1775374295000
}
```

## 七、安全与性能设计

### 7.1 安全设计

1. **认证与授权**：基于JWT+OAuth2实现用户认证，RBAC模型实现权限控制，细粒度控制到“模块-操作-按钮”；

2. **数据传输安全**：所有接口采用HTTPS协议，避免数据传输过程中被窃取/篡改；

3. **数据库安全**：数据库账号遵循**最小权限原则**，仅授予系统必要的增删改查权限；禁止直接暴露数据库端口到公网；

4. **操作审计**：记录所有核心操作（如订单审核、库存修改、权限变更）的审计日志，包含操作人、操作时间、操作内容、操作前后数据；

5. **数据脱敏**：对敏感数据（如客户电话、供应商邮箱、地址）进行脱敏处理，展示时隐藏部分字符；

6. **防刷限流**：对高频接口（如登录、查询）进行限流，避免恶意刷接口导致系统崩溃。

### 7.2 性能设计

1. **缓存优化**：将高频查询数据（物料信息、数据字典、库存快照）缓存到Redis，设置合理的过期时间，减少数据库查询压力；

2. **数据库优化**：为核心表的查询字段（如订单号、物料编码、供应商id）建立**索引**；大表采用分页查询，避免全表扫描；

3. **异步处理**：将非核心同步操作（如消息通知、日志记录、报表统计）通过消息队列异步处理，提升接口响应速度；

4. **读写分离**：后续系统数据量增大后，可快速扩展为数据库主从架构，主库负责写操作，从库负责读操作，提升数据库处理能力；

5. **代码优化**：避免嵌套查询、循环操作数据库，通过批量操作（如批量插入/更新）提升效率；Service层避免冗余逻辑，保证代码简洁高效。

## 八、后续扩展设计

为避免单体架构后期扩展受限，系统设计时提前预留**弹性扩展点**，后续可根据企业业务发展快速扩展，无需大规模重构。

|扩展方向|单体中提前准备工作|
|---|---|
|拆分为微服务|按模块进行**分包管理**（如com.scm.purchase、com.scm.inventory），模块间通过Service接口通信，避免硬编码耦合|
|多租户部署|所有核心表已预留`tenant_id`字段，后续仅需增加租户管理模块，实现租户数据隔离|
|大数据量分析|增加**订单事实表、库存日快照表**，将历史数据同步至数仓（如ClickHouse），避免业务库与分析库资源竞争|
|多仓库/多地域|仓库表已支持多仓库管理，后续可增加地域字段，实现库存的跨地域调拨与管理|
|第三方系统集成|预留标准化接口，支持与ERP、财务系统、WMS、TMS等系统的对接，采用接口签名保障集成数据安全|
|集群部署|系统采用无状态设计，后续可通过部署多实例+Nginx负载均衡实现集群部署，提升系统可用性|
|移动端适配|前端采用Vue3/React开发，支持响应式布局，后续可快速开发小程序/APP端，复用后端接口|
## 九、部署设计

### 9.1 基础部署方案（单体）

适用于中小型企业初期使用，部署简单、维护成本低：

1. 应用端：单台服务器部署Nginx + Spring Boot单Jar包，Nginx负责反向代理与静态资源服务；

2. 数据库：单台服务器部署MySQL/PostgreSQL，开启定时备份，保障数据安全；

3. 缓存/消息队列：单台服务器部署Redis/RabbitMQ，与应用端同服务器或独立服务器均可。

### 9.2 容器化部署方案（推荐）

基于Docker实现容器化部署，提升部署效率与环境一致性：

1. 为应用、数据库、Redis、消息队列分别制作Docker镜像；

2. 通过`docker-compose`编排容器，实现一键启动/停止/重启；

3. 数据卷挂载实现容器数据持久化，避免容器销毁导致数据丢失。

### 9.3 自动化部署

集成CI/CD工具（GitLab CI / Jenkins），实现**代码提交-编译-测试-部署**自动化：

1. 代码提交至Git仓库后，自动触发编译与单元测试；

2. 测试通过后，自动构建Docker镜像并推送到镜像仓库；

3. 自动执行部署脚本，将新镜像部署至生产/测试环境。
> （注：文档部分内容可能由 AI 生成）