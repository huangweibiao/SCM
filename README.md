# SCM供应链管理系统

## 项目简介

SCM供应链管理系统是一个基于Spring Boot 3.5.11 + MySQL 8.0开发的单供应链管理软件系统，覆盖供应商管理、采购管理、库存管理、销售管理、生产/委外管理、物流管理、报表分析等核心模块。

## 技术栈

- **后端框架**: Spring Boot 3.5.11
- **ORM框架**: MyBatis-Plus 3.5.9
- **数据库**: MySQL 8.0
- **连接池**: Druid 1.2.23
- **工具库**: Hutool 5.8.34
- **接口文档**: Knife4j 4.5.0
- **认证授权**: JWT + Spring Security
- **缓存**: Redis (可选)

## 项目结构

```
scm-backend/
├── src/main/java/com/scm/
│   ├── common/              # 通用模块
│   │   ├── constant/       # 常量定义
│   │   ├── enums/          # 枚举定义
│   │   ├── exception/      # 异常处理
│   │   ├── query/          # 查询对象
│   │   ├── result/         # 响应对象
│   │   └── util/           # 工具类
│   ├── config/             # 配置类
│   ├── controller/         # 控制器
│   ├── dto/                # 数据传输对象
│   ├── entity/             # 实体类
│   ├── mapper/             # MyBatis Mapper
│   ├── service/            # 业务接口
│   │   └── impl/           # 业务实现
│   └── ScmApplication.java # 启动类
├── src/main/resources/
│   ├── db/                 # 数据库脚本
│   │   ├── schema.sql      # 建表脚本
│   │   └── init-data.sql   # 初始化数据脚本
│   ├── mapper/             # MyBatis XML
│   └── application.yml     # 配置文件
└── pom.xml                 # Maven配置
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis (可选)

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p < src/main/resources/db/schema.sql

# 初始化数据
mysql -u root -p scm_db < src/main/resources/db/init-data.sql
```

### 3. 修改配置

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scm_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 4. 启动项目

```bash
mvn clean install
mvn spring-boot:run
```

### 5. 访问接口文档

启动成功后，访问: http://localhost:8080/api/doc.html

默认管理员账号:
- 用户名: admin
- 密码: admin123

## 核心功能模块

### 1. 基础数据管理
- 物料管理（创建、更新、删除、查询）
- 物料分类管理
- 仓库管理
- 数据字典管理

### 2. 供应商管理
- 供应商CRUD操作
- 供应商评级管理
- 供应商状态管理

### 3. 采购管理
- 采购订单创建/审核/取消
- 采购收货（部分/全部）
- 采购订单查询

### 4. 库存管理
- 库存入库/出库
- 库存查询
- 库存预警
- 库存流水记录

### 5. 销售管理
- 销售订单创建/审核
- 销售发货
- 客户信息管理

### 6. 生产/委外管理
- 生产工单管理
- 生产领料
- 产成品入库

### 7. 物流管理
- 物流订单创建
- 物流状态跟踪

### 8. 报表分析
- 采购报表
- 销售报表
- 库存报表

## 接口说明

### 接口前缀

- `/api/v1/basic` - 基础数据管理
- `/api/v1/supplier` - 供应商管理
- `/api/v1/purchase` - 采购管理
- `/api/v1/inventory` - 库存管理
- `/api/v1/sales` - 销售管理
- `/api/v1/production` - 生产管理
- `/api/v1/logistics` - 物流管理
- `/api/v1/report` - 报表分析

### 统一响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {},
  "timestamp": 1775374295000
}
```

### 分页查询参数

```
pageNum: 页码（从1开始）
pageSize: 每页条数
```

### 分页响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 100,
    "list": []
  },
  "timestamp": 1775374295000
}
```

## 数据库设计

### 核心表（共18张）

1. item_category - 物料分类表
2. item - 物料主表
3. warehouse - 仓库表
4. supplier - 供应商表
5. purchase_order - 采购订单主表
6. purchase_order_detail - 采购订单明细表
7. inbound_order - 入库单表
8. inbound_order_detail - 入库单明细表
9. inventory - 库存表（含乐观锁）
10. inventory_log - 库存流水表
11. sales_order - 销售订单主表
12. sales_order_detail - 销售订单明细表
13. outbound_order - 出库单表
14. outbound_order_detail - 出库单明细表
15. production_order - 生产工单表
16. logistics_order - 物流订单表
17. dict_type - 数据字典类型表
18. dict_data - 数据字典数据表

## 安全设计

### 认证授权
- 使用JWT Token认证
- 基于RBAC的权限控制
- 用户、角色、权限三级管理

### 数据安全
- 密码使用BCrypt加密
- 敏感数据脱敏
- 操作审计日志

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发规范
- 使用Lombok简化代码
- 统一异常处理

### API规范
- RESTful API设计
- 统一响应格式
- 参数校验使用Jakarta Validation

## 部署说明

### Docker部署（推荐）

```bash
# 构建镜像
docker build -t scm-backend:1.0.0 .

# 运行容器
docker run -d -p 8080:8080 \
  --name scm-backend \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/scm_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  scm-backend:1.0.0
```

### Jar包部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/scm-backend-1.0.0.jar
```

## 常见问题

### 1. 数据库连接失败
检查数据库配置是否正确，MySQL服务是否启动。

### 2. 接口返回401未认证
需要在请求头中携带JWT Token: `Authorization: Bearer {token}`

### 3. 库存更新失败
检查乐观锁版本号，重新提交请求。

## 联系方式

- 项目作者: SCM System
- 邮箱: admin@scm.com

## 许可证

MIT License
