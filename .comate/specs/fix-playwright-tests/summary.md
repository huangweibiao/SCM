# Fix Playwright UI Tests - Summary

## Problem

18 out of 19 Playwright tests were failing. Root causes:

1. **SPA routing gap**: Direct access to `/login`, `/dashboard`, etc. returned `{"code":500,"msg":"系统异常：No endpoint GET /login"}` because Spring Boot had no controller for Vue Router paths.
2. **`forward:/index.html` conflict**: Using Spring MVC forward triggered the `GlobalExceptionHandler` (`@RestControllerAdvice`) which intercepted exceptions during the forward dispatch and returned JSON error responses instead of HTML.
3. **Strict mode violation**: `.el-menu` selector matched 6 elements (1 main menu + 5 inline sub-menus), causing Playwright strict mode errors.
4. **Sub-menu handling**: Navigation tests didn't expand Element Plus `el-sub-menu` components before clicking child items.

## Changes Made

### 1. HomeController.java - Direct stream response instead of forward

**File**: `backend/src/main/java/com/scm/controller/HomeController.java`

Changed approach from `forward:/index.html` (which conflicts with GlobalExceptionHandler) to directly reading `static/index.html` from classpath and writing to response stream. This bypasses the Spring MVC dispatch entirely and avoids the exception handler.

Added catch-all `@GetMapping` for all Vue Router paths: `/`, `/login`, `/dashboard`, `/basic/**`, `/supplier`, `/purchase`, `/inbound`, `/inventory/**`, `/sales`, `/customer`, `/outbound`, `/production`, `/logistics`, `/report`, `/system/**`.

### 2. login.spec.ts - Fixed wait strategy

**File**: `frontend/tests/login.spec.ts`

- Replaced `waitForLoadState('networkidle')` with `expect(.login-container).toBeVisible()` to wait for Vue app mounting
- Added proper timeout for `waitForURL(/.*dashboard/)`
- Used `[role="menubar"].el-menu` selector for dashboard verification

### 3. navigation.spec.ts - Fixed menu navigation

**File**: `frontend/tests/navigation.spec.ts`

- Split menu items into `directMenuItems` (no expansion needed) and `subMenuItems` (require parent submenu expansion)
- For sub-menu items: click `.el-sub-menu__title` first to expand, then click `.el-menu-item` child
- Used `[role="menubar"].el-menu` to avoid strict mode violation with multiple `.el-menu` elements
- Added proper timeouts throughout

## Test Results

**36/36 passed** (previously 1/19):

- 2 login tests passed
- 17 navigation tests passed (5 direct + 12 sub-menu)
- 17 API tests passed
