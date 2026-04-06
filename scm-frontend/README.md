# SCM供应链管理系统 - 前端

## 项目简介

SCM供应链管理系统前端是基于Vue 3 + Element Plus开发的单页面应用，提供完整的供应链管理功能界面。

## 技术栈

- **核心框架**: Vue 3.4.0
- **UI组件库**: Element Plus 2.5.0
- **构建工具**: Vite 5.0.8
- **状态管理**: Pinia 2.1.7
- **HTTP客户端**: Axios 1.6.0
- **路由**: Vue Router 4.2.0
- **工具库**: dayjs 1.11.10

## 项目结构

```
scm-frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/                # API接口
│   │   ├── index.js       # 认证相关API
│   │   ├── item.js        # 物料API
│   │   ├── supplier.js    # 供应商API
│   │   ├── warehouse.js   # 仓库API
│   │   ├── inventory.js   # 库存API
│   │   ├── purchase.js    # 采购API
│   │   ├── sales.js       # 销售API
│   │   ├── inbound.js     # 入库API
│   │   ├── outbound.js    # 出库API
│   │   ├── production.js  # 生产API
│   │   ├── logistics.js   # 物流API
│   │   ├── report.js      # 报表API
│   │   ├── user.js        # 用户API
│   │   ├── role.js        # 角色API
│   │   ├── permission.js  # 权限API
│   │   ├── operation-log.js   # 操作日志API
│   │   └── inventory-warning.js # 库存预警API
│   ├── layouts/            # 布局组件
│   │   └── MainLayout.vue # 主布局
│   ├── router/             # 路由配置
│   │   └── index.js       # 路由定义
│   ├── stores/             # 状态管理
│   │   └── user.js        # 用户状态
│   ├── styles/             # 样式文件
│   │   └── main.scss      # 全局样式
│   ├── utils/              # 工具函数
│   │   └── request.js     # Axios封装
│   ├── views/              # 页面组件
│   │   ├── basic/         # 基础数据
│   │   │   ├── item.vue   # 物料管理
│   │   │   └── warehouse.vue # 仓库管理
│   │   ├── supplier/      # 供应商管理
│   │   │   └── index.vue
│   │   ├── inventory/     # 库存管理
│   │   │   └── index.vue
│   │   ├── purchase/      # 采购管理
│   │   │   └── index.vue
│   │   ├── sales/         # 销售管理
│   │   │   └── index.vue
│   │   ├── production/    # 生产管理
│   │   │   └── index.vue
│   │   ├── logistics/     # 物流管理
│   │   │   └── index.vue
│   │   ├── report/        # 报表分析
│   │   │   └── index.vue
│   │   ├── system/        # 系统管理
│   │   │   ├── user.vue           # 用户管理
│   │   │   ├── operation-log.vue   # 操作日志
│   │   │   └── inventory-warning.vue # 库存预警
│   │   ├── dashboard/     # 仪表盘
│   │   │   └── index.vue
│   │   ├── login/         # 登录
│   │   │   └── index.vue
│   │   └── error/         # 错误页面
│   │       └── 404.vue
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
├── index.html             # HTML模板
├── package.json           # 项目配置
├── vite.config.js         # Vite配置
└── README.md              # 说明文档
```

## 快速开始

### 1. 环境要求

- Node.js 16+
- npm 或 yarn 或 pnpm

### 2. 安装依赖

```bash
npm install
# 或
yarn install
# 或
pnpm install
```

### 3. 修改配置

修改 `src/utils/request.js` 中的后端接口地址：

```javascript
const service = axios.create({
  baseURL: 'http://localhost:8080/api', // 修改为你的后端地址
  timeout: 10000
})
```

### 4. 启动开发服务器

```bash
npm run dev
# 或
yarn dev
# 或
pnpm dev
```

访问 http://localhost:5173

### 5. 构建生产版本

```bash
npm run build
# 或
yarn build
# 或
pnpm build
```

## 功能模块

### 1. 基础数据管理
- ✅ 物料管理（创建、编辑、删除、查询）
- ✅ 仓库管理（创建、编辑、删除、查询）

### 2. 供应商管理
- ✅ 供应商CRUD操作
- ✅ 供应商评级管理
- ✅ 供应商状态管理

### 3. 库存管理
- ✅ 库存查询
- ✅ 库存详情
- ✅ 库存流水

### 4. 采购管理
- ✅ 采购订单管理（创建、审核、删除、关闭）
- ✅ 订单明细管理
- ✅ 订单状态跟踪

### 5. 销售管理
- ✅ 销售订单管理（创建、审核、删除、关闭）
- ✅ 订单明细管理
- ✅ 订单状态跟踪

### 6. 生产管理
- ✅ 生产工单管理（创建、开始生产、完工、删除）
- ✅ 生产进度跟踪
- ✅ 工单详情查看

### 7. 物流管理
- ✅ 物流订单管理（创建、更新状态、详情）
- ✅ 物流状态跟踪
- ✅ 收货信息管理

### 8. 报表分析
- ✅ 采购报表（订单量、金额、完成率）
- ✅ 销售报表（订单量、金额、发货率）
- ✅ 库存报表（数量统计、预警统计）

### 9. 系统管理
- ✅ 用户管理（创建、编辑、删除、重置密码）
- ✅ 操作日志（查询、详情）
- ✅ 库存预警（低库存、高库存、手动检查）

## API接口封装

所有API接口封装在 `src/api` 目录下，按模块划分：

- `auth.js` - 认证相关（登录、登出、获取用户信息）
- `item.js` - 物料管理
- `supplier.js` - 供应商管理
- `warehouse.js` - 仓库管理
- `inventory.js` - 库存管理
- `purchase.js` - 采购订单
- `sales.js` - 销售订单
- `inbound.js` - 入库单
- `outbound.js` - 出库单
- `production.js` - 生产工单
- `logistics.js` - 物流订单
- `report.js` - 报表统计
- `user.js` - 用户管理
- `role.js` - 角色管理
- `permission.js` - 权限管理
- `operation-log.js` - 操作日志
- `inventory-warning.js` - 库存预警

## 状态管理

使用Pinia进行状态管理，目前包含：

- `user` - 用户状态（登录状态、用户信息、Token）

## 路由配置

- `/login` - 登录页
- `/dashboard` - 仪表盘
- `/basic` - 基础数据（物料、仓库）
- `/supplier` - 供应商管理
- `/inventory` - 库存管理
- `/purchase` - 采购管理
- `/sales` - 销售管理
- `/production` - 生产管理
- `/logistics` - 物流管理
- `/report` - 报表分析
- `/system` - 系统管理（用户、操作日志、库存预警）

## 开发规范

### 1. 代码风格
- 使用Composition API（`<script setup>`）
- 统一使用 `ref` 和 `reactive`
- 组件命名使用大驼峰
- 文件命名使用短横线

### 2. API调用
```javascript
import { getUserPage } from '@/api/user'

const getList = async () => {
  loading.value = true
  try {
    const res = await getUserPage(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}
```

### 3. 表单验证
```javascript
const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      // 提交逻辑
    }
  })
}
```

## 默认账号

- 用户名: admin
- 密码: admin123

## 浏览器支持

- Chrome (最新版)
- Firefox (最新版)
- Safari (最新版)
- Edge (最新版)

## 常见问题

### 1. 接口请求401未认证
- 检查Token是否正确设置
- 检查后端接口地址配置
- 重新登录获取新Token

### 2. 跨域问题
- 开发环境已配置代理，无需额外处理
- 生产环境需要后端配置CORS

### 3. 页面白屏
- 检查控制台错误信息
- 确认依赖是否安装完整
- 清除浏览器缓存

## 性能优化

1. 路由懒加载
2. 组件按需引入
3. 图片压缩
4. 代码分割
5. Gzip压缩

## 部署说明

### Nginx配置示例

```nginx
server {
  listen 80;
  server_name scm.example.com;

  root /var/www/scm-frontend;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;
  }

  location /api {
    proxy_pass http://localhost:8080/api;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }
}
```

## 更新日志

### v1.0.0 (2026-04-06)
- ✅ 完成所有核心功能模块开发
- ✅ 完成15个API接口模块封装
- ✅ 完成16个功能页面开发
- ✅ 完成路由配置和状态管理
- ✅ 完成统一的请求拦截和错误处理

## 联系方式

- 项目作者: SCM System
- 邮箱: support@scm.com

## 许可证

MIT License
