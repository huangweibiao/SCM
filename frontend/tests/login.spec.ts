import { test, expect } from '@playwright/test';

test.describe('SCM登录测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // Wait for Vue app to mount and render login form
    await expect(page.locator('.login-container')).toBeVisible({ timeout: 15000 });
  });

  test('登录页面加载', async ({ page }) => {
    await expect(page.locator('.login-container')).toBeVisible();
    await expect(page.locator('.login-box')).toBeVisible();
    await expect(page.locator('.title')).toContainText('SCM供应链管理系统');
  });

  test('正确账号密码登录成功', async ({ page }) => {
    await page.getByPlaceholder('请输入用户名').fill('admin');
    await page.getByPlaceholder('请输入密码').fill('123456');
    await page.getByRole('button', { name: '登录' }).click();
    await page.waitForURL(/.*dashboard/, { timeout: 15000 });
    await expect(page.locator('[role="menubar"].el-menu')).toBeVisible({ timeout: 10000 });
  });
});