import { test, expect } from '@playwright/test';

test.describe('SCM菜单导航测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // Wait for Vue app to mount and render login form
    await expect(page.locator('.login-container')).toBeVisible({ timeout: 15000 });
    await page.getByPlaceholder('请输入用户名').fill('admin');
    await page.getByPlaceholder('请输入密码').fill('123456');
    await page.getByRole('button', { name: '登录' }).click();
    // Wait for dashboard to load
    await page.waitForURL(/.*dashboard/, { timeout: 15000 });
    await expect(page.locator('[role="menubar"].el-menu')).toBeVisible({ timeout: 10000 });
  });

  // Direct menu items (no sub-menu expansion needed)
  const directMenuItems = [
    { name: '首页', path: '/dashboard' },
    { name: '供应商管理', path: '/supplier' },
    { name: '生产管理', path: '/production' },
    { name: '物流管理', path: '/logistics' },
    { name: '报表分析', path: '/report' },
  ];

  for (const menu of directMenuItems) {
    test(`导航到${menu.name}`, async ({ page }) => {
      await page.locator('.el-menu-item').filter({ hasText: menu.name }).click();
      await expect(page).toHaveURL(new RegExp(menu.path), { timeout: 10000 });
    });
  }

  // Sub-menu items (need parent expansion first)
  const subMenuItems = [
    { parent: '基础数据', name: '物料管理', path: '/basic/item' },
    { parent: '基础数据', name: '仓库管理', path: '/basic/warehouse' },
    { parent: '采购管理', name: '采购订单', path: '/purchase' },
    { parent: '采购管理', name: '入库单', path: '/inbound' },
    { parent: '库存管理', name: '库存查询', path: '/inventory' },
    { parent: '销售管理', name: '销售订单', path: '/sales' },
    { parent: '销售管理', name: '客户管理', path: '/customer' },
    { parent: '销售管理', name: '出库单', path: '/outbound' },
    { parent: '系统管理', name: '用户管理', path: '/system/user' },
    { parent: '系统管理', name: '角色管理', path: '/system/role' },
    { parent: '系统管理', name: '权限管理', path: '/system/permission' },
    { parent: '系统管理', name: '数据字典', path: '/system/dict' },
  ];

  for (const menu of subMenuItems) {
    test(`导航到${menu.name}`, async ({ page }) => {
      // Click the parent sub-menu to expand it
      await page.locator('.el-sub-menu__title').filter({ hasText: menu.parent }).click();
      // Wait for sub-menu items to appear, then click the target
      await page.locator('.el-menu-item').filter({ hasText: menu.name }).click();
      await expect(page).toHaveURL(new RegExp(menu.path), { timeout: 10000 });
    });
  }
});