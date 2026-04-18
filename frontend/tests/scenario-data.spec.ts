import { test, expect, type APIRequestContext, type Page } from '@playwright/test'

type AuthBody = {
  token: string
}

type WarehouseOption = {
  id: number
  warehouseName: string
}

type BasicEntity = {
  id: number
}

type OrderEntity = BasicEntity & {
  poNo?: string
  soNo?: string
  moNo?: string
  logisticsNo?: string
  status: number
}

type OrderDetail = {
  itemId: number
  qty: number
  receivedQty?: number
  remainQty?: number
  shippedQty?: number
}

type WarningItem = {
  itemCode: string
  itemName: string
  warehouseId: number
  availableQty: number
  minStock: number
  maxStock: number
  warnType: number
}

type ReportSnapshot = {
  dashboard: Record<string, unknown>
  purchase: Record<string, unknown>
  sales: Record<string, unknown>
  inventory: Record<string, unknown>
  supplier: Record<string, unknown>
}

type ScenarioContext = {
  token: string
  warehouseId: number
  purchase: {
    orderNo: string
    itemId: number
    row: Record<string, unknown>
    detail: OrderDetail
    inventory: Record<string, unknown>
  }
  sales: {
    orderNo: string
    customerName: string
    row: Record<string, unknown>
    detail: OrderDetail
  }
  production: {
    orderNo: string
    itemName: string
    row: Record<string, unknown>
  }
  logistics: {
    orderNo: string
    courierNo: string
    row: Record<string, unknown>
  }
  warnings: {
    low: WarningItem
    high: WarningItem
  }
  reports: ReportSnapshot
}

const today = new Date().toISOString().slice(0, 10)

function authHeaders(token: string) {
  return { Authorization: `Bearer ${token}` }
}

function escapeRegExp(value: string) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

function formatNumberish(value: unknown) {
  if (value === null || value === undefined) {
    return '0'
  }

  const text = String(value)
  if (!text.includes('.')) {
    return text
  }

  return text.replace(/\.0+$/, '').replace(/(\.\d*?)0+$/, '$1')
}

function numberPattern(value: unknown) {
  const normalized = escapeRegExp(formatNumberish(value))
  return new RegExp(`(?:^|\\D)${normalized}(?:0+)?(?:\\D|$)`)
}

async function apiGet<T>(request: APIRequestContext, path: string, token: string): Promise<T> {
  const response = await request.get(path, { headers: authHeaders(token) })
  expect(response.ok()).toBeTruthy()
  const body = await response.json()
  expect(body.code).toBe(200)
  return body.data as T
}

async function apiPost<T>(request: APIRequestContext, path: string, token: string, data?: unknown): Promise<T> {
  const response = await request.post(path, {
    headers: authHeaders(token),
    data,
  })
  expect(response.ok()).toBeTruthy()
  const body = await response.json()
  expect(body.code).toBe(200)
  return body.data as T
}

async function loginToken(request: APIRequestContext) {
  const response = await request.post('/api/auth/login', {
    data: { username: 'admin', password: '123456' },
  })
  expect(response.ok()).toBeTruthy()
  const body = await response.json()
  expect(body.code).toBe(200)
  return (body.data as AuthBody).token
}

async function loginAsAdmin(page: Page) {
  await page.goto('/')
  await expect(page.locator('.login-container')).toBeVisible({ timeout: 15000 })
  await page.getByPlaceholder('请输入用户名').fill('admin')
  await page.getByPlaceholder('请输入密码').fill('123456')
  await page.getByRole('button', { name: '登录' }).click()
  await page.waitForURL(/.*dashboard/, { timeout: 15000 })
  await expect(page.locator('[role="menubar"].el-menu')).toBeVisible({ timeout: 10000 })
}

async function gotoPage(page: Page, path: string, title: string) {
  await page.goto(path)
  await expect(page.locator('.page-title')).toHaveText(title, { timeout: 15000 })
}

async function selectFormOption(page: Page, label: string, option: string) {
  const formItem = page.locator('.el-form-item').filter({ hasText: label }).first()
  await formItem.locator('.el-select .el-input__wrapper').click()
  await page.locator('.el-select-dropdown:visible .el-select-dropdown__item').filter({ hasText: option }).first().click()
}

async function createItem(
  request: APIRequestContext,
  token: string,
  params: {
    itemCode: string
    itemName: string
    minStock: number
    maxStock: number
    safetyStock: number
  }
) {
  return apiPost<BasicEntity & { itemCode: string }>(request, '/api/basic/item', token, {
    itemCode: params.itemCode,
    itemName: params.itemName,
    spec: 'UI-SCENARIO',
    unit: 'pcs',
    categoryId: 1,
    safetyStock: params.safetyStock,
    minStock: params.minStock,
    maxStock: params.maxStock,
  })
}

async function createSupplier(request: APIRequestContext, token: string, runId: string) {
  return apiPost<BasicEntity & { supplierCode: string }>(request, '/api/supplier', token, {
    supplierCode: `UITEST-SUP-${runId}`,
    supplierName: `UITest Supplier ${runId}`,
    contactPerson: 'Scenario Owner',
    phone: '13800000000',
    email: `ui-${runId}@example.com`,
    address: 'UI scenario address',
    paymentTerms: 'Net 30',
    rating: 5,
  })
}

async function createPurchaseScenario(
  request: APIRequestContext,
  token: string,
  params: {
    supplierId: number
    warehouseId: number
    itemId: number
    qty: number
    price: number
    taxRate: number
    receiveQty: number
    remark: string
  }
) {
  const created = await apiPost<OrderEntity>(
    request,
    '/api/purchase',
    token,
    {
      supplierId: params.supplierId,
      warehouseId: params.warehouseId,
      remark: params.remark,
      items: [
        {
          itemId: params.itemId,
          qty: params.qty,
          price: params.price,
          taxRate: params.taxRate,
        },
      ],
    }
  )

  await apiPost(request, `/api/purchase/${created.id}/audit`, token, { status: 20, auditBy: 1 })

  if (params.receiveQty > 0) {
    await apiPost(request, `/api/purchase/${created.id}/receive`, token, {
      warehouseId: params.warehouseId,
      details: [{ itemId: params.itemId, qty: params.receiveQty }],
    })
  }

  const row = await apiGet<Record<string, unknown>>(request, `/api/purchase/${created.id}`, token)
  const details = await apiGet<OrderDetail[]>(request, `/api/purchase/${created.id}/details`, token)
  return { row, detail: details[0] }
}

async function createSalesScenario(
  request: APIRequestContext,
  token: string,
  params: {
    warehouseId: number
    itemId: number
    customerName: string
    qty: number
    price: number
    taxRate: number
    deliverQty: number
    remark: string
  }
) {
  const created = await apiPost<OrderEntity>(
    request,
    '/api/sales',
    token,
    {
      customerName: params.customerName,
      customerPhone: '13900000000',
      warehouseId: params.warehouseId,
      remark: params.remark,
      items: [
        {
          itemId: params.itemId,
          qty: params.qty,
          price: params.price,
          taxRate: params.taxRate,
        },
      ],
    }
  )

  await apiPost(request, `/api/sales/${created.id}/audit`, token, { status: 20, auditBy: 1 })
  await apiPost(request, `/api/sales/${created.id}/deliver`, token, {
    warehouseId: params.warehouseId,
    details: [{ itemId: params.itemId, qty: params.deliverQty }],
  })

  const row = await apiGet<Record<string, unknown>>(request, `/api/sales/${created.id}`, token)
  const details = await apiGet<OrderDetail[]>(request, `/api/sales/${created.id}/details`, token)
  return { row, detail: details[0] }
}

async function createProductionScenario(
  request: APIRequestContext,
  token: string,
  params: {
    itemId: number
    qty: number
    remark: string
  }
) {
  const created = await apiPost<OrderEntity>(
    request,
    '/api/production',
    token,
    {
      itemId: params.itemId,
      qty: params.qty,
      startDate: today,
      endDate: today,
      remark: params.remark,
    }
  )

  await apiPost(request, `/api/production/${created.id}/start`, token)
  const row = await apiGet<Record<string, unknown>>(request, `/api/production/${created.id}`, token)
  return { row }
}

async function createLogisticsScenario(
  request: APIRequestContext,
  token: string,
  params: {
    businessId: number
    courierNo: string
  }
) {
  const created = await apiPost<OrderEntity>(
    request,
    '/api/logistics',
    token,
    {
      businessType: 20,
      businessId: params.businessId,
      courierName: 'UI Logistics Express',
      courierNo: params.courierNo,
      sendAddress: 'Shanghai sender address',
      receiveAddress: 'Chengdu receiver address',
      receivePerson: 'Scenario Receiver',
      receivePhone: '13600000000',
      logisticsFee: 32,
    }
  )

  await apiPost(request, `/api/logistics/${created.id}/status`, token, { status: 20 })
  const row = await apiGet<Record<string, unknown>>(request, `/api/logistics/${created.id}`, token)
  return { row }
}

async function buildScenarioContext(request: APIRequestContext): Promise<ScenarioContext> {
  const token = await loginToken(request)
  const runId = Date.now().toString()
  const warehouses = await apiGet<WarehouseOption[]>(request, '/api/basic/warehouse/all', token)
  expect(warehouses.length).toBeGreaterThan(0)
  const warehouseId = warehouses[0].id

  const supplier = await createSupplier(request, token, runId)

  const purchaseItem = await createItem(request, token, {
    itemCode: `UITEST-PUR-${runId}`,
    itemName: `UITest Purchase ${runId}`,
    safetyStock: 5,
    minStock: 2,
    maxStock: 200,
  })

  const lowWarningItem = await createItem(request, token, {
    itemCode: `UITEST-LOW-${runId}`,
    itemName: `UITest Low ${runId}`,
    safetyStock: 10,
    minStock: 10,
    maxStock: 50,
  })

  const highWarningItem = await createItem(request, token, {
    itemCode: `UITEST-HIGH-${runId}`,
    itemName: `UITest High ${runId}`,
    safetyStock: 10,
    minStock: 5,
    maxStock: 20,
  })

  const salesItem = await createItem(request, token, {
    itemCode: `UITEST-SAL-${runId}`,
    itemName: `UITest Sales ${runId}`,
    safetyStock: 5,
    minStock: 1,
    maxStock: 100,
  })

  const productionItem = await createItem(request, token, {
    itemCode: `UITEST-PROD-${runId}`,
    itemName: `UITest Production ${runId}`,
    safetyStock: 1,
    minStock: 0,
    maxStock: 100,
  })

  const purchaseScenario = await createPurchaseScenario(request, token, {
    supplierId: supplier.id,
    warehouseId,
    itemId: purchaseItem.id,
    qty: 12,
    price: 20,
    taxRate: 13,
    receiveQty: 5,
    remark: `UI partial receive ${runId}`,
  })

  await createPurchaseScenario(request, token, {
    supplierId: supplier.id,
    warehouseId,
    itemId: lowWarningItem.id,
    qty: 8,
    price: 10,
    taxRate: 0,
    receiveQty: 8,
    remark: `UI low warning ${runId}`,
  })

  await createPurchaseScenario(request, token, {
    supplierId: supplier.id,
    warehouseId,
    itemId: highWarningItem.id,
    qty: 25,
    price: 12,
    taxRate: 0,
    receiveQty: 25,
    remark: `UI high warning ${runId}`,
  })

  await createPurchaseScenario(request, token, {
    supplierId: supplier.id,
    warehouseId,
    itemId: salesItem.id,
    qty: 10,
    price: 18,
    taxRate: 0,
    receiveQty: 10,
    remark: `UI sales stock ${runId}`,
  })

  const salesScenario = await createSalesScenario(request, token, {
    warehouseId,
    itemId: salesItem.id,
    customerName: `UITest Customer ${runId}`,
    qty: 8,
    price: 15,
    taxRate: 13,
    deliverQty: 3,
    remark: `UI partial deliver ${runId}`,
  })

  const productionScenario = await createProductionScenario(request, token, {
    itemId: productionItem.id,
    qty: 4,
    remark: `UI production ${runId}`,
  })

  const logisticsScenario = await createLogisticsScenario(request, token, {
    businessId: Number(salesScenario.row.id),
    courierNo: `UI-LOG-${runId}`,
  })

  const purchaseInventory = await apiGet<{ list: Record<string, unknown>[] }>(
    request,
    `/api/inventory?pageNum=1&pageSize=10&itemId=${purchaseItem.id}&warehouseId=${warehouseId}`,
    token
  )

  const warnings = await apiGet<WarningItem[]>(request, '/api/inventory/warnings', token)
  const lowWarning = warnings.find((item) => item.itemCode === lowWarningItem.itemCode)
  const highWarning = warnings.find((item) => item.itemCode === highWarningItem.itemCode)
  expect(lowWarning).toBeTruthy()
  expect(highWarning).toBeTruthy()

  const reports: ReportSnapshot = {
    dashboard: await apiGet<Record<string, unknown>>(request, '/api/report/dashboard', token),
    purchase: await apiGet<Record<string, unknown>>(request, '/api/report/purchase', token),
    sales: await apiGet<Record<string, unknown>>(request, '/api/report/sales', token),
    inventory: await apiGet<Record<string, unknown>>(request, '/api/report/inventory', token),
    supplier: await apiGet<Record<string, unknown>>(request, '/api/report/supplier', token),
  }

  return {
    token,
    warehouseId,
    purchase: {
      orderNo: String(purchaseScenario.row.poNo),
      itemId: purchaseItem.id,
      row: purchaseScenario.row,
      detail: purchaseScenario.detail,
      inventory: purchaseInventory.list[0],
    },
    sales: {
      orderNo: String(salesScenario.row.soNo),
      customerName: String(salesScenario.row.customerName),
      row: salesScenario.row,
      detail: salesScenario.detail,
    },
    production: {
      orderNo: String(productionScenario.row.moNo),
      itemName: productionItem.itemName,
      row: productionScenario.row,
    },
    logistics: {
      orderNo: String(logisticsScenario.row.logisticsNo),
      courierNo: String(logisticsScenario.row.courierNo),
      row: logisticsScenario.row,
    },
    warnings: {
      low: lowWarning!,
      high: highWarning!,
    },
    reports,
  }
}

test.describe('SCM场景数据UI回归', () => {
  test.describe.configure({ mode: 'serial' })
  test.setTimeout(120000)

  let scenario: ScenarioContext

  test.beforeAll(async ({ request }) => {
    scenario = await buildScenarioContext(request)
  })

  test.beforeEach(async ({ page }) => {
    await loginAsAdmin(page)
  })

  test('采购与库存页面展示部分收货场景', async ({ page }) => {
    await gotoPage(page, '/purchase', '采购管理')

    const purchaseRow = page.locator('.el-table__body tr').filter({ hasText: scenario.purchase.orderNo }).first()
    await expect(purchaseRow).toBeVisible()
    await expect(purchaseRow).toContainText('部分收货')

    await purchaseRow.locator('.el-button').filter({ hasText: '详情' }).click()
    const purchaseDialog = page.locator('.el-dialog:visible').last()
    await expect(purchaseDialog).toContainText(`订单详情 - ${scenario.purchase.orderNo}`)

    const detailRow = purchaseDialog.locator('.el-table__body tr').first()
    await expect(detailRow).toContainText(numberPattern(scenario.purchase.detail.qty))
    await expect(detailRow).toContainText(numberPattern(scenario.purchase.detail.receivedQty))
    await expect(detailRow).toContainText(numberPattern(scenario.purchase.detail.remainQty))

    await gotoPage(page, '/inventory', '库存管理')
    await page.getByPlaceholder('物料ID').fill(String(scenario.purchase.itemId))
    await page.getByPlaceholder('仓库ID').fill(String(scenario.warehouseId))
    await page.getByRole('button', { name: '查询' }).click()

    const inventoryRow = page.locator('.el-table__body tr').first()
    await expect(inventoryRow).toContainText(String(scenario.purchase.itemId))
    await expect(inventoryRow).toContainText(String(scenario.warehouseId))
    await expect(inventoryRow).toContainText(numberPattern(scenario.purchase.inventory.qty))
    await expect(inventoryRow).toContainText(numberPattern(scenario.purchase.inventory.availableQty))
  })

  test('库存预警与销售页面展示场景数据', async ({ page }) => {
    await gotoPage(page, '/system/inventory-warning', '库存预警')

    const lowWarningRow = page.locator('.el-table__body tr').filter({ hasText: scenario.warnings.low.itemCode }).first()
    await expect(lowWarningRow).toBeVisible()
    await expect(lowWarningRow).toContainText('低库存')
    await expect(lowWarningRow).toContainText(numberPattern(scenario.warnings.low.availableQty))

    const highWarningRow = page.locator('.el-table__body tr').filter({ hasText: scenario.warnings.high.itemCode }).first()
    await expect(highWarningRow).toBeVisible()
    await expect(highWarningRow).toContainText('超库存')
    await expect(highWarningRow).toContainText(numberPattern(scenario.warnings.high.availableQty))

    await gotoPage(page, '/sales', '销售管理')

    const salesRow = page.locator('.el-table__body tr').filter({ hasText: scenario.sales.orderNo }).first()
    await expect(salesRow).toBeVisible()
    await expect(salesRow).toContainText(scenario.sales.customerName)
    await expect(salesRow).toContainText('部分发货')

    await salesRow.locator('.el-button').filter({ hasText: '详情' }).click()
    const salesDialog = page.locator('.el-dialog:visible').last()
    await expect(salesDialog).toContainText(`订单详情 - ${scenario.sales.orderNo}`)

    const detailRow = salesDialog.locator('.el-table__body tr').first()
    await expect(detailRow).toContainText(numberPattern(scenario.sales.detail.qty))
    await expect(detailRow).toContainText(numberPattern(scenario.sales.detail.shippedQty))
    await expect(detailRow).toContainText(numberPattern(scenario.sales.detail.remainQty))
  })

  test('生产与物流页面支持按场景状态过滤', async ({ page }) => {
    await gotoPage(page, '/production', '生产管理')
    await selectFormOption(page, '状态', '生产中')
    await page.getByRole('button', { name: '查询' }).click()

    const productionRow = page.locator('.el-table__body tr').filter({ hasText: scenario.production.orderNo }).first()
    await expect(productionRow).toBeVisible()
    await expect(productionRow).toContainText(scenario.production.itemName)
    await expect(productionRow).toContainText('生产中')

    await productionRow.locator('.el-button').filter({ hasText: '详情' }).click()
    const productionDialog = page.locator('.el-dialog:visible').last()
    await expect(productionDialog).toContainText(`工单详情 - ${scenario.production.orderNo}`)
    await expect(productionDialog).toContainText('生产中')

    await gotoPage(page, '/logistics', '物流管理')
    await selectFormOption(page, '业务类型', '销售')
    await selectFormOption(page, '物流状态', '运输中')
    await page.getByRole('button', { name: '查询' }).click()

    const logisticsRow = page.locator('.el-table__body tr').filter({ hasText: scenario.logistics.orderNo }).first()
    await expect(logisticsRow).toBeVisible()
    await expect(logisticsRow).toContainText('销售')
    await expect(logisticsRow).toContainText('运输中')
    await expect(logisticsRow).toContainText(scenario.logistics.courierNo)

    await logisticsRow.locator('.el-button').filter({ hasText: '详情' }).click()
    await expect(page.locator('.el-message')).toContainText(scenario.logistics.orderNo)
  })

  test('报表分析页面与后端统计保持一致', async ({ page }) => {
    await gotoPage(page, '/report', '报表分析')

    const purchaseDashboard = page.locator('.dashboard-card').filter({ hasText: '采购订单' }).first()
    await expect(purchaseDashboard).toContainText(String(scenario.reports.dashboard.purchaseTotal))

    const salesDashboard = page.locator('.dashboard-card').filter({ hasText: '销售订单' }).first()
    await expect(salesDashboard).toContainText(String(scenario.reports.dashboard.salesTotal))

    const inventoryDashboard = page.locator('.dashboard-card').filter({ hasText: '库存记录' }).first()
    await expect(inventoryDashboard).toContainText(String(scenario.reports.dashboard.inventoryTotal))

    const supplierDashboard = page.locator('.dashboard-card').filter({ hasText: '供应商' }).first()
    await expect(supplierDashboard).toContainText(String(scenario.reports.dashboard.supplierCount))

    const purchaseCard = page.locator('.el-card').filter({ hasText: '采购报表' }).first()
    await expect(purchaseCard).toContainText(`订单总数${formatNumberish(scenario.reports.purchase.totalCount)}`)
    await expect(purchaseCard).toContainText(`总金额¥${formatNumberish(scenario.reports.purchase.totalAmount)}`)
    await expect(purchaseCard).toContainText(`待处理${formatNumberish((scenario.reports.purchase.statusCount as Record<string, unknown>)?.['10'] ?? 0)}`)
    await expect(purchaseCard).toContainText(`已完成${formatNumberish((scenario.reports.purchase.statusCount as Record<string, unknown>)?.['40'] ?? 0)}`)

    const salesCard = page.locator('.el-card').filter({ hasText: '销售报表' }).first()
    await expect(salesCard).toContainText(`订单总数${formatNumberish(scenario.reports.sales.totalCount)}`)
    await expect(salesCard).toContainText(`总金额¥${formatNumberish(scenario.reports.sales.totalAmount)}`)
    await expect(salesCard).toContainText(`待处理${formatNumberish((scenario.reports.sales.statusCount as Record<string, unknown>)?.['10'] ?? 0)}`)
    await expect(salesCard).toContainText(`已完成${formatNumberish((scenario.reports.sales.statusCount as Record<string, unknown>)?.['40'] ?? 0)}`)

    const inventoryCard = page.locator('.el-card').filter({ hasText: '库存报表' }).first()
    await expect(inventoryCard).toContainText(`库存记录数${formatNumberish(scenario.reports.inventory.totalRecords)}`)
    await expect(inventoryCard).toContainText(`总库存量${formatNumberish(scenario.reports.inventory.totalQty)}`)
    await expect(inventoryCard).toContainText(scenario.warnings.low.itemCode)

    const supplierCard = page.locator('.el-card').filter({ hasText: '供应商报表' }).first()
    await expect(supplierCard).toContainText(`供应商总数${formatNumberish(scenario.reports.supplier.totalCount)}`)
    await expect(supplierCard).toContainText(`正常${formatNumberish((scenario.reports.supplier.statusCount as Record<string, unknown>)?.['1'] ?? 0)}`)
    await expect(supplierCard).toContainText(`停用${formatNumberish((scenario.reports.supplier.statusCount as Record<string, unknown>)?.['0'] ?? 0)}`)
  })
})
