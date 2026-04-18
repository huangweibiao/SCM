<template>
  <div class="report-page">
    <h2 class="page-title">报表分析</h2>

    <!-- 仪表盘数据 -->
    <el-row :gutter="20" class="dashboard-row">
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="dashboard-item">
            <el-icon size="40" color="#409EFF"><ShoppingCart /></el-icon>
            <div class="dashboard-info">
              <div class="dashboard-value">{{ dashboard.purchaseTotal || 0 }}</div>
              <div class="dashboard-label">采购订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="dashboard-item">
            <el-icon size="40" color="#67C23A"><Sell /></el-icon>
            <div class="dashboard-info">
              <div class="dashboard-value">{{ dashboard.salesTotal || 0 }}</div>
              <div class="dashboard-label">销售订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="dashboard-item">
            <el-icon size="40" color="#E6A23C"><Box /></el-icon>
            <div class="dashboard-info">
              <div class="dashboard-value">{{ dashboard.inventoryTotal || 0 }}</div>
              <div class="dashboard-label">库存记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="dashboard-card">
          <div class="dashboard-item">
            <el-icon size="40" color="#909399"><OfficeBuilding /></el-icon>
            <div class="dashboard-info">
              <div class="dashboard-value">{{ dashboard.supplierCount || 0 }}</div>
              <div class="dashboard-label">供应商</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 采购和销售报表 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>采购报表</span>
              <el-button type="primary" size="small" @click="loadPurchaseReport">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单总数">{{ purchaseReport.totalCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="总金额">¥{{ purchaseReport.totalAmount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="待处理">{{ purchaseReport.statusCount?.[10] || 0 }}</el-descriptions-item>
            <el-descriptions-item label="已完成">{{ purchaseReport.statusCount?.[40] || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>销售报表</span>
              <el-button type="primary" size="small" @click="loadSalesReport">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单总数">{{ salesReport.totalCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="总金额">¥{{ salesReport.totalAmount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="待处理">{{ salesReport.statusCount?.[10] || 0 }}</el-descriptions-item>
            <el-descriptions-item label="已完成">{{ salesReport.statusCount?.[40] || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <!-- 库存和供应商报表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>库存报表</span>
              <el-button type="primary" size="small" @click="loadInventoryReport">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="库存记录数">{{ inventoryReport.totalRecords || 0 }}</el-descriptions-item>
            <el-descriptions-item label="总库存量">{{ inventoryReport.totalQty || 0 }}</el-descriptions-item>
          </el-descriptions>
          <el-table :data="inventoryReport.details?.slice(0, 5) || []" size="small" style="margin-top: 10px">
            <el-table-column prop="itemCode" label="物料编码" />
            <el-table-column prop="itemName" label="物料名称" />
            <el-table-column prop="qty" label="库存数量" />
            <el-table-column prop="availableQty" label="可用数量" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>供应商报表</span>
              <el-button type="primary" size="small" @click="loadSupplierReport">
                <el-icon><Refresh /></el-icon> 刷新
              </el-button>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="供应商总数">{{ supplierReport.totalCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="正常">{{ supplierReport.statusCount?.[1] || 0 }}</el-descriptions-item>
            <el-descriptions-item label="停用">{{ supplierReport.statusCount?.[0] || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ShoppingCart, Sell, Box, OfficeBuilding, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const dashboard = ref<any>({})
const purchaseReport = ref<any>({})
const salesReport = ref<any>({})
const inventoryReport = ref<any>({})
const supplierReport = ref<any>({})

const loadDashboard = async () => {
  try {
    const res = await request.get('/report/dashboard')
    dashboard.value = res.data || {}
  } catch (error) {
    console.error('Failed to load dashboard:', error)
  }
}

const loadPurchaseReport = async () => {
  try {
    const res = await request.get('/report/purchase')
    purchaseReport.value = res.data || {}
  } catch (error) {
    console.error('Failed to load purchase report:', error)
  }
}

const loadSalesReport = async () => {
  try {
    const res = await request.get('/report/sales')
    salesReport.value = res.data || {}
  } catch (error) {
    console.error('Failed to load sales report:', error)
  }
}

const loadInventoryReport = async () => {
  try {
    const res = await request.get('/report/inventory')
    inventoryReport.value = res.data || {}
  } catch (error) {
    console.error('Failed to load inventory report:', error)
  }
}

const loadSupplierReport = async () => {
  try {
    const res = await request.get('/report/supplier')
    supplierReport.value = res.data || {}
  } catch (error) {
    console.error('Failed to load supplier report:', error)
  }
}

const loadAll = async () => {
  await Promise.all([
    loadDashboard(),
    loadPurchaseReport(),
    loadSalesReport(),
    loadInventoryReport(),
    loadSupplierReport()
  ])
}

onMounted(() => {
  loadAll()
})
</script>

<style scoped lang="scss">
.report-page {
  .dashboard-row {
    margin-bottom: 20px;
  }

  .dashboard-card {
    .dashboard-item {
      display: flex;
      align-items: center;
      gap: 15px;

      .dashboard-info {
        .dashboard-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
        }

        .dashboard-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
