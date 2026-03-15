# SCM 供应链管理系统

一个基于 Spring Boot 3 + Vue 3 的供应链管理系统，支持 OAuth2 单点登录。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.5.11
- Spring Security + OAuth2 Client
- Spring Data JPA
- MySQL 8
- Lombok / MapStruct

### 前端
- Vue 3.5
- TypeScript
- Vue Router 4
- Pinia
- Element Plus
- Vite
- Axios

## 项目结构

```
SCM/
├── scm-backend/                 # 后端 Spring Boot 项目
│   ├── src/main/java/com/scm/
│   │   ├── ScmApplication.java  # 主启动类
│   │   ├── config/              # 配置类
│   │   ├── controller/          # 控制器
│   │   ├── service/             # 服务层
│   │   ├── repository/          # 数据访问层
│   │   ├── entity/              # 实体类
│   │   └── dto/                 # 数据传输对象
│   └── src/main/resources/
│       ├── application.yml      # 主配置文件
│       ├── application-test.yml # 测试环境配置
│       ├── application-prod.yml # 生产环境配置
│       └── db/                  # 数据库脚本
│
├── scm-frontend/                # 前端 Vue 项目
│   ├── src/
│   │   ├── api/                 # API 接口
│   │   ├── assets/              # 静态资源
│   │   ├── components/          # 公共组件
│   │   ├── layouts/             # 布局组件
│   │   ├── router/              # 路由配置
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── styles/              # 样式文件
│   │   ├── views/               # 页面组件
│   │   ├── App.vue              # 根组件
│   │   └── main.ts              # 入口文件
│   ├── vite.config.ts           # Vite 配置
│   └── package.json             # NPM 配置
│
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+ / npm 9+
- MySQL 8.0+
- Maven 3.8+

### 数据库初始化

```bash
# 创建数据库并初始化表结构
mysql -u root -p < scm-backend/src/main/resources/db/init.sql
```

### 后端启动

```bash
cd scm-backend

# 修改数据库配置 (或设置环境变量)
# 编辑 src/main/resources/application.yml

# 启动后端服务
mvn spring-boot:run

# 或先安装前端依赖并打包
mvn clean package -DskipTests
java -jar target/scm-backend-1.0.0-SNAPSHOT.jar
```

### 前端开发

```bash
cd scm-frontend

# 安装依赖
npm install

# 启动开发服务器 (带代理到后端)
npm run dev

# 构建生产版本
npm run build
```

### 生产部署

```bash
cd scm-backend

# 完整构建 (包含前端)
mvn clean package

# 前端会被自动构建并复制到 Spring Boot 静态资源目录
# 启动应用
java -jar target/scm-backend-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

## OAuth2 配置

系统已配置为 OAuth2 客户端，需要连接到已有的 OAuth2 认证服务器。

### 配置参数

在 `application.yml` 中配置：

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          custom-oauth2:
            client-id: your-client-id
            client-secret: your-client-secret
        provider:
          custom-oauth2:
            authorization-uri: http://your-oauth2-server/oauth2/authorize
            token-uri: http://your-oauth2-server/oauth2/token
            user-info-uri: http://your-oauth2-server/userinfo
```

### 环境变量

也可以通过环境变量配置：

| 变量名 | 说明 |
|--------|------|
| `OAUTH2_CLIENT_ID` | OAuth2 客户端 ID |
| `OAUTH2_CLIENT_SECRET` | OAuth2 客户端密钥 |
| `OAUTH2_AUTH_URI` | 授权端点 |
| `OAUTH2_TOKEN_URI` | Token 端点 |
| `OAUTH2_USER_INFO_URI` | 用户信息端点 |
| `DB_HOST` | 数据库主机 |
| `DB_PORT` | 数据库端口 |
| `DB_NAME` | 数据库名称 |
| `DB_USERNAME` | 数据库用户名 |
| `DB_PASSWORD` | 数据库密码 |

## 功能模块

- **工作台** - 系统概览和快捷操作
- **供应商管理** - 供应商信息维护
- **采购管理** - 采购订单管理
- **库存管理** - 库存查询与预警
- **报表中心** - 数据分析与报表
- **系统设置** - 用户个人设置

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/me` | GET | 获取当前用户信息 |
| `/api/auth/status` | GET | 检查登录状态 |
| `/api/auth/profile` | PUT | 更新用户资料 |
| `/api/auth/logout` | POST | 退出登录 |
| `/api/health` | GET | 健康检查 |

## 开发指南

### 后端开发

1. 实体类放在 `entity` 包
2. Repository 放在 `repository` 包
3. Service 放在 `service` 包
4. Controller 放在 `controller` 包
5. DTO 放在 `dto` 包

### 前端开发

1. 页面组件放在 `views` 目录
2. 公共组件放在 `components` 目录
3. API 调用放在 `api` 目录
4. 状态管理使用 Pinia，放在 `stores` 目录

## License

MIT License
