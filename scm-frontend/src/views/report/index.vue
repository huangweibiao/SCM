<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">报表分析</h1>
    </div>

    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="采购报表" name="purchase">
        <div class="report-section">
          <h3>采购订单量统计</h3>
          <el-form :inline="true" class="report-form">
            <el-form-item label="开始日期">
              <el-date-picker v-model="purchaseParams.startDate" type="date" placeholder="请选择" />
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker v-model="purchaseParams.endDate" type="date" placeholder="请选择" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getPurchaseCount">查询</el-button>
            </el-form-item>
          </el-form>
          <el-descriptions v-if="purchaseCountData" :column="3" border>
            <el-descriptions-item label="订单总数">{{ purchaseCountData.totalCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="待审核">{{ purchaseCountData.pendingCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="已审核">{{ purchaseCountData.auditedCount || 0 }}</el-descriptions-item>
          </el-descriptions>

          <h3>采购金额统计</h3>
          <el-form :inline="true" class="report-form">
            <el-form-item>
              <el-button type="primary" @click="getPurchaseAmount">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="purchaseAmountData" border stripe>
            <el-table-column prop="supplierId" label="供应商ID" width="120" />
            <el-table-column prop="totalAmount" label="采购金额" width="150">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="orderCount" label="订单数量" width="120" />
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="销售报表" name="sales">
        <div class="report-section">
          <h3>销售订单量统计</h3>
          <el-form :inline="true" class="report-form">
            <el-form-item label="开始日期">
              <el-date-picker v-model="salesParams.startDate" type="date" placeholder="请选择" />
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker v-model="salesParams.endDate" type="date" placeholder="请选择" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getSalesCount">查询</el-button>
            </el-form-item>
          </el-form>
          <el-descriptions v-if="salesCountData" :column="3" border>
            <el-descriptions-item label="订单总数">{{ salesCountData.totalCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="待审核">{{ salesCountData.pendingCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="已审核">{{ salesCountData.auditedCount || 0 }}</el-descriptions-item>
          </el-descriptions>

          <h3>销售金额统计</h3>
          <el-form :inline="true" class="report-form">
            <el-form-item>
              <el-button type="primary" @click="getSalesAmount">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="salesAmountData" border stripe>
            <el-table-column prop="customerId" label="客户ID" width="120" />
            <el-table-column prop="totalAmount" label="销售金额" width="150">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="orderCount" label="订单数量" width="120" />
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="库存报表" name="inventory">
        <div class="report-section">
          <h3>库存数量统计</h3>
          <el-form :inline="true" class="report-form">
            <el-form-item label="仓库ID">
              <el-input v-model="inventoryParams.warehouseId" placeholder="请输入" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getInventoryCount">查询</el-button>
            </el-form-item>
          </el-form>
          <el-descriptions v-if="inventoryCountData" :column="4" border>
            <el-descriptions-item label="物料总数">{{ inventoryCountData.itemCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="库存总数量">{{ inventoryCountData.totalQty || 0 }}</el-descriptions-item>
            <el-descriptions-item label="库存总金额">¥{{ inventoryCountData.totalAmount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="预警数量">{{ inventoryCountData.warnCount || 0 }}</el-descriptions-item>
          </el-descriptions>

          <h3>库存预警统计</h3>
          <el-form :inline="true" class="report-form">
            <el-form-item>
              <el-button type="primary" @click="getInventoryWarn">查询</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="inventoryWarnData" border stripe>
            <el-table-column prop="itemId" label="物料ID" width="120" />
            <el-table-column prop="warehouseId" label="仓库ID" width="120" />
            <el-table-column prop="qty" label="当前库存" width="120" />
            <el-table-column prop="minQty" label="安全下限" width="120" />
            <el-table-column prop="maxQty" label="库存上限" width="120" />
            <el-table-column prop="warnType" label="预警类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.warnType === 'LOW' ? 'danger' : 'warning'">
                  {{ row.warnType === 'LOW' ? '低库存' : '高库存' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getPurchaseOrderCountReport,
  getPurchaseAmountReport,
  getSalesOrderCountReport,
  getSalesAmountReport,
  getInventoryCountReport,
  getInventoryWarnReport
} from '@/api/report'

const activeTab = ref('purchase')
const loading = ref(false)

const purchaseParams = reactive({ startDate: null, endDate: null })
const salesParams = reactive({ startDate: null, endDate: null })
const inventoryParams = reactive({ warehouseId: null, categoryId: null })

const purchaseCountData = ref(null)
const purchaseAmountData = ref([])
const salesCountData = ref(null)
const salesAmountData = ref([])
const inventoryCountData = ref(null)
const inventoryWarnData = ref([])

const getPurchaseCount = async () => {
  try {
    const res = await getPurchaseOrderCountReport(purchaseParams)
    purchaseCountData.value = res.data
  } catch (error) {
    console.error('查询失败:', error)
  }
}

const getPurchaseAmount = async () => {
  try {
    const res = await getPurchaseAmountReport(purchaseParams)
    purchaseAmountData.value = res.data
  } catch (error) {
    console.error('查询失败:', error)
  }
}

const getSalesCount = async () => {
  try {
    const res = await getSalesOrderCountReport(salesParams)
    salesCountData.value = res.data
  } catch (error) {
    console.error('查询失败:', error)
  }
}

const getSalesAmount = async () => {
  try {
    const res = await getSalesAmountReport(salesParams)
    salesAmountData.value = res.data
  } catch (error) {
    console.error('查询失败:', error)
  }
}

const getInventoryCount = async () => {
  try {
    const res = await getInventoryCountReport(inventoryParams)
    inventoryCountData.value = res.data
  } catch (error) {
    console.error('查询失败:', error)
  }
}

const getInventoryWarn = async () => {
  try {
    const res = await getInventoryWarnReport()
    inventoryWarnData.value = res.data
  } catch (error) {
    console.error('查询失败:', error)
  }
}

getPurchaseCount()
getPurchaseAmount()
getSalesCount()
getSalesAmount()
getInventoryCount()
getInventoryWarn()
</script>

<style scoped lang="scss">
.report-section {
  padding: 20px;

  h3 {
    margin: 20px 0 10px 0;
    font-size: 16px;
    color: #333;
  }

  .report-form {
    margin-bottom: 20px;
  }
}

.el-tabs {
  min-height: 500px;
}
</style>
