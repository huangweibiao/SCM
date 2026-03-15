<template>
  <div class="page-container">
    <div class="page-header">
      <h2>报表概览</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>采购趋势</span>
          </template>
          <div class="chart-placeholder">
            <el-icon :size="48"><TrendCharts /></el-icon>
            <p>采购订单趋势图表</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>库存分布</span>
          </template>
          <div class="chart-placeholder">
            <el-icon :size="48"><PieChart /></el-icon>
            <p>库存分类分布图表</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>供应商排名</span>
          </template>
          <el-table :data="supplierRanking" stripe>
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="name" label="供应商名称" />
            <el-table-column prop="orderCount" label="订单数量" width="100" />
            <el-table-column prop="totalAmount" label="总金额" width="150">
              <template #default="{ row }">
                ¥{{ row.totalAmount.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="onTimeRate" label="准时交付率" width="120">
              <template #default="{ row }">
                <el-tag :type="row.onTimeRate >= 90 ? 'success' : 'warning'">
                  {{ row.onTimeRate }}%
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const supplierRanking = ref([
  { name: '优质材料供应商A', orderCount: 45, totalAmount: 2500000, onTimeRate: 98 },
  { name: '高效物流供应商B', orderCount: 32, totalAmount: 1800000, onTimeRate: 95 },
  { name: '精密配件供应商C', orderCount: 28, totalAmount: 1200000, onTimeRate: 92 },
])

const mt20 = 'mt-20'
</script>

<style scoped lang="scss">
.page-container {
  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0;
    }
  }

  .chart-placeholder {
    height: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #909399;
    background-color: #fafafa;
    border-radius: 4px;
  }

  .mt-20 {
    margin-top: 20px;
  }
}
</style>
