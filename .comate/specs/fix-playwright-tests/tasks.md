# Fix Playwright UI Tests - Task Plan

- [x] Task 1: Add SPA fallback route in HomeController
    - 1.1: Add @GetMapping mappings for all Vue Router paths (/login, /dashboard, /basic/**, /supplier, /purchase, /inbound, /inventory/**, /sales, /customer, /outbound, /production, /logistics, /report, /system/**)
    - 1.2: Implement forward:/index.html handler method for SPA fallback
    - 1.3: Rebuild backend JAR and restart server

- [x] Task 2: Fix login.spec.ts wait strategy
    - 2.1: Remove waitForLoadState('networkidle') from beforeEach
    - 2.2: Wait for .login-container element to be visible instead of relying on network idle
    - 2.3: Add explicit wait for login form elements before interaction

- [x] Task 3: Fix navigation.spec.ts menu handling
    - 3.1: Restructure menu items to distinguish direct items vs sub-menu items (with parent property)
    - 3.2: For sub-menu items, click parent first to expand, then click child item
    - 3.3: Add proper waits for menu expansion and page navigation

- [x] Task 4: Rebuild and run all Playwright tests
    - 4.1: Rebuild frontend and copy dist to backend static
    - 4.2: Rebuild backend JAR
    - 4.3: Restart Spring Boot server
    - 4.4: Run npx playwright test and verify all tests pass
