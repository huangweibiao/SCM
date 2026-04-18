# Fix Dialog Title & Type Cast Bugs

## Problem

Two systemic bugs affect all CRUD modules:

### Bug 1: Dialog Title Not Evaluating (Frontend)

`dialogTitle` defined as arrow function or plain const instead of `computed`, causing:
- Arrow function: title displays raw function source like `()=>g.value?"编辑物料":"新增物料"`
- Plain const: title never updates when `isEdit` changes

Affected files:
| File | Line | Current Pattern |
|------|------|----------------|
| `supplier/index.vue` | 123 | `const dialogTitle = () => isEdit.value ? '编辑供应商' : '新增供应商'` |
| `basic/warehouse.vue` | 121 | `const dialogTitle = () => isEdit.value ? '编辑仓库' : '新增仓库'` |
| `customer/index.vue` | 110 | `const dialogTitle = isEdit.value ? '编辑客户' : '新增客户'` |

Already fixed: `basic/item.vue` (line 132) - uses `computed(() => ...)`

### Bug 2: ClassCastException on Form Submit (Backend)

Jackson deserializes JSON numbers as `Integer` or `Double`, but service code directly casts to `Long` or `BigDecimal`, causing `ClassCastException`.

Affected files (30+ `(Long)` casts, 29+ `(Integer)` casts, 6+ `(BigDecimal)` casts):
- ItemServiceImpl (already fixed)
- WarehouseServiceImpl (already fixed)
- UserServiceImpl (already fixed)
- SupplierServiceImpl (already fixed)
- CustomerServiceImpl (already fixed)
- SalesOrderServiceImpl (partially fixed)
- PurchaseOrderServiceImpl (not fixed)
- RoleServiceImpl (not fixed)
- PermissionServiceImpl (not fixed)
- ItemCategoryServiceImpl (not fixed)
- DictServiceImpl (not fixed)
- ProductionOrderServiceImpl (not fixed)
- OutboundOrderServiceImpl (not fixed)
- InboundOrderServiceImpl (not fixed)
- LogisticsOrderServiceImpl (not fixed)
- InventoryServiceImpl (not fixed)

Solution: Created `ParamUtils` utility class with safe `getLong()`, `getInteger()`, `getBigDecimal()`, `getString()` methods that handle Number-to-target-type conversion via `Number.longValue()`, `Number.intValue()`, `BigDecimal.valueOf(Number.doubleValue())`.

## Architecture

- Frontend: Replace arrow function/const with `computed(() => isEdit.value ? '编辑X' : '新增X')`
- Backend: Replace unsafe `(Type) params.get(...)` with `ParamUtils.getType(params, ...)`

## Affected Files

**Frontend (3 files):**
- `frontend/src/views/supplier/index.vue` - Fix dialogTitle
- `frontend/src/views/basic/warehouse.vue` - Fix dialogTitle
- `frontend/src/views/customer/index.vue` - Fix dialogTitle + import computed

**Backend (11 files remaining):**
- All *ServiceImpl.java files - Replace unsafe casts with ParamUtils

## Expected Outcomes

- All dialog titles display correct text ("新增X" / "编辑X")
- All form submissions work without ClassCastException
- Playwright tests cover create/edit dialog for each module
