# SCM供应链管理系统 UI测试方案

## 测试目标
使用Playwright对SCM供应链管理系统进行端到端UI测试，验证以下功能：
1. 登录功能
2. 菜单导航
3. 页面访问
4. API接口调用

## 测试环境
- 前端: http://localhost:8180
- 测试框架: Playwright
- 浏览器: Chromium/Firefox/Webkit

## 测试用例

### 1. 登录测试
- [ ] 登录页面加载正常
- [ ] 使用正确账号密码登录成功
- [ ] 使用错误账号密码登录失败
- [ ] 未输入账号密码登录失败
- [ ] 登录后跳转到首页

### 2. 菜单导航测试
- [ ] 点击物料管理菜单
- [ ] 点击仓库管理菜单
- [ ] 点击供应商管理菜单
- [ ] 点击采购管理菜单
- [ ] 点击入库管理菜单
- [ ] 点击库存管理菜单
- [ ] 点击销售管理菜单
- [ ] 点击客户管理菜单
- [ ] 点击出库管理菜单
- [ ] 点击生产管理菜单
- [ ] 点击物流管理菜单
- [ ] 点击报表分析菜单
- [ ] 点击用户管理菜单
- [ ] 点击角色管理菜单
- [ ] 点击权限管理菜单
- [ ] 点击数据字典菜单

### 3. 页面功能测试
- [ ] 物料列表分页加载
- [ ] 物料新增功能
- [ ] 物料编辑功能
- [ ] 物料删除功能
- [ ] 仓库下拉列表加载
- [ ] 供应商搜索功能

### 4. API接口测试
- [ ] GET /api/basic/item - 物料列表
- [ ] GET /api/basic/warehouse - 仓库列表
- [ ] GET /api/supplier - 供应商列表
- [ ] GET /api/purchase - 采购订单
- [ ] GET /api/inbound - 入库单
- [ ] GET /api/inventory - 库存查询
- [ ] GET /api/sales - 销售订单
- [ ] GET /api/customer - 客户列表
- [ ] GET /api/outbound - 出库单
- [ ] GET /api/production - 生产工单
- [ ] GET /api/logistics - 物流订单
- [ ] GET /api/report/dashboard - 报表
- [ ] GET /api/system/user - 用户管理
- [ ] GET /api/system/role - 角色管理
- [ ] GET /api/system/permission - 权限管理
- [ ] GET /api/system/dict/types - 数据字典

## 测试账号
- 用户名: admin
- 密码: 123456