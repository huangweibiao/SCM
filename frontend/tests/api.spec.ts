import { test, expect } from '@playwright/test';

test.describe('SCM API接口测试', () => {
  let token: string;

  test.beforeAll(async ({ request }) => {
    const res = await request.post('/api/auth/login', {
      data: { username: 'admin', password: '123456' },
    });
    const body = await res.json();
    token = body.data?.token;
  });

  const apiTests = [
    { method: 'GET', path: '/api/basic/item', name: '物料管理' },
    { method: 'GET', path: '/api/basic/warehouse', name: '仓库管理' },
    { method: 'GET', path: '/api/supplier', name: '供应商' },
    { method: 'GET', path: '/api/purchase', name: '采购订单' },
    { method: 'GET', path: '/api/inbound', name: '入库单' },
    { method: 'GET', path: '/api/inventory', name: '库存查询' },
    { method: 'GET', path: '/api/inventory/warnings', name: '库存预警' },
    { method: 'GET', path: '/api/sales', name: '销售订单' },
    { method: 'GET', path: '/api/customer', name: '客户管理' },
    { method: 'GET', path: '/api/outbound', name: '出库单' },
    { method: 'GET', path: '/api/production', name: '生产工单' },
    { method: 'GET', path: '/api/logistics', name: '物流订单' },
    { method: 'GET', path: '/api/report/dashboard', name: '报表分析' },
    { method: 'GET', path: '/api/system/user', name: '用户管理' },
    { method: 'GET', path: '/api/system/role', name: '角色管理' },
    { method: 'GET', path: '/api/system/permission', name: '权限管理' },
    { method: 'GET', path: '/api/system/dict/types', name: '数据字典' },
  ];

  for (const api of apiTests) {
    test(`${api.method} ${api.name}`, async ({ request }) => {
      const res = await request.get(api.path, {
        headers: { Authorization: `Bearer ${token}` },
      });
      expect(res.ok()).toBe(true);
      const body = await res.json();
      expect(body.code).toBe(200);
    });
  }
});