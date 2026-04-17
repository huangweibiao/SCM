# SCM供应链管理系统

基于 Spring Boot 3 + Vue 3 的供应链管理系统，前后端一体化打包部署。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.5.11, Spring Security, Spring Data JPA, MySQL |
| 前端 | Vue 3.4, Element Plus 2.6, Vue Router 4, Pinia, Vite 5 |
| 认证 | JWT (无状态) |
| 测试 | Playwright, JUnit 5 |

## 功能模块

- **基础数据**: 物料管理、仓库管理
- **采购链**: 供应商管理、采购管理、入库管理
- **库存链**: 库存查询、库存调拨、库存盘点、库存预警
- **销售链**: 销售管理、客户管理、出库管理
- **生产物流**: 生产管理、物流管理
- **报表分析**: Dashboard、数据报表
- **系统管理**: 用户管理、角色管理、权限管理、数据字典

## 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

## 快速开始

### 1. 数据库配置

默认连接 `localhost:3306/scm`，可通过环境变量覆盖：

```bash
export DB_URL=jdbc:mysql://localhost:3306/scm?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
export DB_USERNAME=root
export DB_PASSWORD=root
```

JPA `ddl-auto: update` 会自动建表。

### 2. 一键打包

```powershell
.\build.ps1
```

脚本会依次：清理 → 安装前端依赖 → 构建前端 → 复制 dist 到后端 static → 打包 JAR。

### 3. 启动

```bash
java -jar backend/target/scm-backend-1.0.0.jar
```

访问 http://localhost:8180 ，默认账号 `admin / 123456`。

### 4. 前端开发模式

```bash
cd frontend
npm install
npm run dev
```

Vite 默认启动在 5173 端口，API 请求代理到后端 8180。

## 项目结构

```
SCM/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/scm/
│   │   ├── config/             # 安全、CORS、异常处理等配置
│   │   ├── controller/         # REST 控制器
│   │   ├── entity/             # JPA 实体
│   │   ├── repository/         # 数据访问层
│   │   ├── security/           # JWT 过滤器
│   │   └── service/            # 业务逻辑层
│   └── src/main/resources/
│       ├── application.yml     # 应用配置
│       └── static/             # 前端构建产物（gitignore）
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── layouts/            # 布局组件
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   └── api/                # API 请求封装
│   ├── tests/                  # Playwright E2E 测试
│   └── playwright.config.ts
├── build.ps1                   # 一键打包脚本
└── build.bat                   # 一键打包脚本 (CMD)
```

## E2E 测试

```bash
cd frontend
npm run test            # 运行 Playwright 测试
npm run test:ui         # 交互式测试界面
npm run test:report     # 查看测试报告
```

测试覆盖：登录流程、菜单导航（含子菜单展开）、17 个 API 接口验证。

## 部署架构

前端构建后复制到 `backend/src/main/resources/static/`，与后端一起打包为单个 JAR。Spring Boot 通过 `HomeController` 将 Vue Router 路径回退到 `index.html`，实现 SPA 客户端路由。

```
浏览器 → Spring Boot (8180)
         ├── /api/**        → REST 接口 (JWT 鉴权)
         └── /login, /dashboard, ... → index.html (Vue Router)
```
