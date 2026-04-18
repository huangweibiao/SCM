# Fix Dialog Title & Type Cast Bugs - Task Plan

- [x] Task 1: Fix frontend dialogTitle bugs in 3 Vue files
    - 1.1: Fix supplier/index.vue - change arrow function to computed
    - 1.2: Fix basic/warehouse.vue - change arrow function to computed
    - 1.3: Fix customer/index.vue - change plain const to computed

- [x] Task 2: Fix remaining backend Service unsafe casts (Part 1 - Order modules)
    - 2.1: Fix SalesOrderServiceImpl - remaining (Long), (Integer), (BigDecimal) casts in detail maps
    - 2.2: Fix PurchaseOrderServiceImpl - all unsafe casts
    - 2.3: Fix InboundOrderServiceImpl - all unsafe casts
    - 2.4: Fix OutboundOrderServiceImpl - all unsafe casts
    - 2.5: Fix ProductionOrderServiceImpl - all unsafe casts
    - 2.6: Fix LogisticsOrderServiceImpl - all unsafe casts
    - 2.7: Fix InventoryServiceImpl - all unsafe casts

- [x] Task 3: Fix remaining backend Service unsafe casts (Part 2 - System modules)
    - 3.1: Fix RoleServiceImpl - (Integer) cast
    - 3.2: Fix PermissionServiceImpl - (Long) and (Integer) casts
    - 3.3: Fix ItemCategoryServiceImpl - (Long) and (Integer) casts
    - 3.4: Fix DictServiceImpl - (Long) and (Integer) casts

- [x] Task 4: Rebuild and verify
    - 4.1: Rebuild backend JAR
    - 4.2: Restart Spring Boot server
    - 4.3: Run Playwright tests to verify

- [x] Task 5: Add Playwright CRUD dialog tests for all modules
    - 5.1: Add create dialog open test for each module (verify dialog title shows "新增X")
    - 5.2: Add edit dialog open test for each module (verify dialog title shows "编辑X")
    - 5.3: Run all Playwright tests and verify pass
