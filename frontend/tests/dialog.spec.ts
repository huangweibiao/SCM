import { test, expect } from '@playwright/test';

test.describe('SCM弹窗标题测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('.login-container')).toBeVisible({ timeout: 15000 });
    await page.getByPlaceholder('请输入用户名').fill('admin');
    await page.getByPlaceholder('请输入密码').fill('123456');
    await page.getByRole('button', { name: '登录' }).click();
    await page.waitForURL(/.*dashboard/, { timeout: 15000 });
    await expect(page.locator('[role="menubar"].el-menu')).toBeVisible({ timeout: 10000 });
  });

  // Test: new dialog title shows correct text (not function source)
  const crudModules = [
    { parent: '基础数据', name: '物料管理', path: '/basic/item', addBtn: '新增物料', createTitle: '新增物料' },
    { parent: '基础数据', name: '仓库管理', path: '/basic/warehouse', addBtn: '新增仓库', createTitle: '新增仓库' },
    { parent: null, name: '供应商管理', path: '/supplier', addBtn: '新增供应商', createTitle: '新增供应商' },
    { parent: '销售管理', name: '客户管理', path: '/customer', addBtn: '新增客户', createTitle: '新增客户' },
  ];

  for (const mod of crudModules) {
    test(`${mod.name} - 新增弹窗标题为"${mod.createTitle}"且不含函数源码`, async ({ page }) => {
      // Navigate
      if (mod.parent) {
        await page.locator('.el-sub-menu__title').filter({ hasText: mod.parent }).click();
      }
      await page.locator('.el-menu-item').filter({ hasText: mod.name }).click();
      await expect(page).toHaveURL(new RegExp(mod.path), { timeout: 10000 });

      // Click add button
      await page.getByRole('button', { name: mod.addBtn }).click();

      // Verify dialog title
      const titleLocator = page.locator('.el-dialog__title');
      await expect(titleLocator).toBeVisible({ timeout: 5000 });
      const titleText = await titleLocator.textContent();

      // Title must match expected text
      expect(titleText).toBe(mod.createTitle);
      // Title must NOT contain arrow function patterns
      expect(titleText).not.toContain('=>');
      expect(titleText).not.toContain('value?');
      expect(titleText).not.toContain('()');

      // Close dialog
      await page.locator('.el-dialog__headerbtn').click();
    });

    test(`${mod.name} - 编辑弹窗标题正确`, async ({ page }) => {
      // Navigate
      if (mod.parent) {
        await page.locator('.el-sub-menu__title').filter({ hasText: mod.parent }).click();
      }
      await page.locator('.el-menu-item').filter({ hasText: mod.name }).click();
      await expect(page).toHaveURL(new RegExp(mod.path), { timeout: 10000 });

      // Check if there's data with an edit button
      const editBtn = page.locator('.el-table__body .el-button').filter({ hasText: '编辑' }).first();
      const hasData = await editBtn.isVisible({ timeout: 5000 }).catch(() => false);

      if (hasData) {
        await editBtn.click();
        const titleLocator = page.locator('.el-dialog__title');
        await expect(titleLocator).toBeVisible({ timeout: 5000 });
        const titleText = await titleLocator.textContent();
        // Must NOT contain function source
        expect(titleText).not.toContain('=>');
        expect(titleText).not.toContain('value?');
        await page.locator('.el-dialog__headerbtn').click();
      } else {
        // No data in table - skip edit test but still pass
        test.skip();
      }
    });
  }
});
