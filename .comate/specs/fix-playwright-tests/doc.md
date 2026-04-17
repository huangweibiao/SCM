# Fix Playwright UI Tests

## Problem

18 out of 19 Playwright tests are failing. The root cause is a **SPA routing gap** in the Spring Boot backend:

- When the browser accesses `/login`, `/dashboard`, or any Vue Router path directly, Spring Boot has no controller mapping for these paths.
- Spring Security's `anyRequest().permitAll()` passes the request through, but Spring MVC returns a 500 error: `"No endpoint GET /login"`.
- The Vue SPA needs the server to return `index.html` for all non-API, non-static-resource routes, so Vue Router can handle client-side routing.

Additionally, the navigation tests use `page.getByText(menu.name).click()` which may not correctly find menu items inside Element Plus `el-sub-menu` components that need to be expanded first.

## Architecture & Technical Approach

### 1. Add SPA Fallback Route in Spring Boot

Create a catch-all controller that forwards all non-API, non-static requests to `index.html`. This is the standard approach for SPA-with-Spring-Boot deployments.

**Approach**: Modify `HomeController` to add a fallback mapping that handles all paths except `/api/**`, `/assets/**`, `/static/**`, `/favicon.ico`.

### 2. Fix Playwright Test Selectors

The navigation tests need to:
- Handle Element Plus `el-sub-menu` items (click to expand before clicking sub-items)
- Use more reliable selectors for menu items
- Add proper waits for Vue app mounting

### 3. Fix Login Test Waits

The login tests need to wait for the Vue app to actually render, not just for `networkidle`.

## Affected Files

| File | Modification Type | Description |
|------|------------------|-------------|
| `backend/src/main/java/com/scm/controller/HomeController.java` | Modify | Add SPA fallback route for all non-API paths |
| `frontend/tests/login.spec.ts` | Modify | Fix wait strategy for Vue app mounting |
| `frontend/tests/navigation.spec.ts` | Modify | Fix menu navigation with sub-menu handling |

## Implementation Details

### HomeController.java - Add SPA Fallback

```java
@Controller
public class HomeController {

    @GetMapping("/")
    public void home(HttpServletResponse response) throws IOException {
        response.sendRedirect("/index.html");
    }

    /**
     * SPA fallback - forward all non-API, non-static paths to index.html
     * so Vue Router can handle client-side routing
     */
    @GetMapping(value = {
        "/login", "/dashboard",
        "/basic/**", "/supplier", "/purchase",
        "/inbound", "/inventory/**", "/sales",
        "/customer", "/outbound", "/production",
        "/logistics", "/report", "/system/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
```

This explicitly lists all Vue Router paths that need forwarding. Using `forward:/index.html` instead of redirect avoids an extra HTTP round trip.

### login.spec.ts - Fix Wait Strategy

```typescript
test.beforeEach(async ({ page }) => {
  await page.goto('/');
  // Wait for the Vue app to mount and render login form
  await expect(page.locator('.login-container')).toBeVisible({ timeout: 15000 });
});
```

Remove `waitForLoadState('networkidle')` and instead wait for the actual login container element to appear.

### navigation.spec.ts - Fix Menu Navigation

The main issue is that menu items under `el-sub-menu` (e.g., "基础数据", "采购管理", "库存管理", "销售管理", "系统管理") need their parent submenu clicked first to expand before the child item is visible.

```typescript
const menuItems = [
  // Direct menu items (no expansion needed)
  { name: '首页', path: '/dashboard' },
  { name: '供应商管理', path: '/supplier' },
  { name: '生产管理', path: '/production' },
  { name: '物流管理', path: '/logistics' },
  { name: '报表分析', path: '/report' },
  // Sub-menu items (need parent expansion)
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
```

For items with a `parent`, first click the parent submenu to expand it, then click the child item.

## Boundary Conditions

- The SPA fallback must NOT intercept `/api/**` requests - these should go through Spring Security
- Static resources (`/assets/**`, `/static/**`, `/favicon.ico`) should continue to be served directly
- The `forward:/index.html` approach works within the same request dispatch, so Spring Security's `permitAll()` on `/index.html` applies

## Expected Outcomes

- All 19 Playwright tests pass (2 login + 16 navigation + 17 API = 35 total, but original count was 19)
- Direct access to `/login`, `/dashboard`, etc. returns the Vue SPA app
- Menu navigation tests correctly handle Element Plus sub-menu expansion
