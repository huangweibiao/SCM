# Fix Dialog Title & Type Cast Bugs - Summary

## Problem

1. **Dialog Title Bug**: `dialogTitle` defined as arrow function (`const dialogTitle = () => ...`) or plain const instead of `computed`, causing title to display raw function source like `()=>g.value?"编辑物料":"新增物料"` or never update when `isEdit` changes.
2. **ClassCastException**: Jackson deserializes JSON numbers as `Integer`/`Double`, but service code directly casts to `Long`/`BigDecimal`, causing `ClassCastException` at runtime.

## Changes Made

### Frontend (3 files)

| File | Fix |
|------|-----|
| `frontend/src/views/supplier/index.vue` | `const dialogTitle = () => ...` → `computed(() => ...)` |
| `frontend/src/views/basic/warehouse.vue` | `const dialogTitle = () => ...` → `computed(() => ...)` |
| `frontend/src/views/customer/index.vue` | `const dialogTitle = isEdit.value ? ... : ...` → `computed(() => ...)` |

### Backend (16 files)

Created `com.scm.util.ParamUtils` with safe type conversion methods:
- `getLong(map, key)` - handles Integer→Long via `Number.longValue()`
- `getInteger(map, key)` - handles Number→Integer via `Number.intValue()`
- `getBigDecimal(map, key)` - handles Integer/Double→BigDecimal via `BigDecimal.valueOf(Number.doubleValue())`
- `getString(map, key)` - null-safe toString

Replaced all 65+ unsafe casts across 15 Service implementations:
- ItemServiceImpl, WarehouseServiceImpl, UserServiceImpl, SupplierServiceImpl, CustomerServiceImpl
- SalesOrderServiceImpl, PurchaseOrderServiceImpl, InboundOrderServiceImpl, OutboundOrderServiceImpl
- ProductionOrderServiceImpl, LogisticsOrderServiceImpl, InventoryServiceImpl
- RoleServiceImpl, PermissionServiceImpl, ItemCategoryServiceImpl, DictServiceImpl

### Tests

Added `frontend/tests/dialog.spec.ts` covering 4 CRUD modules:
- New dialog title verification (must show correct text, not function source)
- Edit dialog title verification (when data exists)

## Test Results

**42 passed, 2 skipped** (edit tests for supplier/customer skipped due to no data in table)
- 17 API tests
- 2 login tests
- 17 navigation tests
- 8 dialog tests (6 passed, 2 skipped)
