<template>
  <div class="page-container">
    <div class="page-header">
      <h2>采购订单</h2>
      <el-button type="primary">
        <el-icon><Plus /></el-icon>
        新增订单
      </el-button>
    </div>

    <el-table :data="orders" stripe>
      <el-table-column prop="orderNo" label="订单编号" width="150" />
      <el-table-column prop="supplierName" label="供应商" min-width="150" />
      <el-table-column prop="totalAmount" label="订单金额" width="120">
        <template #default="{ row }">
          ¥{{ row.totalAmount.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default>
          <el-button type="primary" link>查看</el-button>
          <el-button type="primary" link>编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const orders = ref([
  {
    orderNo: 'PO-2024-001',
    supplierName: '优质材料供应商A',
    totalAmount: 50000,
    status: '已完成',
    createdAt: '2024-01-15 10:30',
  },
  {
    orderNo: 'PO-2024-002',
    supplierName: '高效物流供应商B',
    totalAmount: 30000,
    status: '待审批',
    createdAt: '2024-01-14 14:20',
  },
])

function getStatusType(status: string) {
  const types: Record<string, string> = {
    '已完成': 'success',
    '待审批': 'warning',
    '进行中': 'primary',
    '已取消': 'info',
  }
  return types[status] || 'info'
}
</script>

<style scoped lang="scss">
.page-container {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
    }
  }
}
</style>
