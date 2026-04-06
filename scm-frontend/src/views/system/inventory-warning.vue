<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">库存预警</h1>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>低库存预警</span>
              <el-button type="primary" size="small" @click="refreshWarnings">刷新</el-button>
            </div>
          </template>
          <el-table :data="lowStockWarnings" border stripe v-loading="loading" max-height="400">
            <el-table-column prop="itemCode" label="物料编码" width="120" />
            <el-table-column prop="itemName" label="物料名称" width="150" />
            <el-table-column prop="warehouseName" label="仓库名称" width="120" />
            <el-table-column prop="qty" label="当前库存" width="100">
              <template #default="{ row }">
                <span style="color: #f56c6c">{{ row.qty }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="minQty" label="安全下限" width="100" />
          </el-table>
          <div v-if="lowStockWarnings.length === 0" class="empty-text">暂无低库存预警</div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>高库存预警</span>
              <el-button type="primary" size="small" @click="refreshWarnings">刷新</el-button>
            </div>
          </template>
          <el-table :data="highStockWarnings" border stripe v-loading="loading" max-height="400">
            <el-table-column prop="itemCode" label="物料编码" width="120" />
            <el-table-column prop="itemName" label="物料名称" width="150" />
            <el-table-column prop="warehouseName" label="仓库名称" width="120" />
            <el-table-column prop="qty" label="当前库存" width="100">
              <template #default="{ row }">
                <span style="color: #e6a23c">{{ row.qty }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="maxQty" label="库存上限" width="100" />
          </el-table>
          <div v-if="highStockWarnings.length === 0" class="empty-text">暂无高库存预警</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <el-button type="primary" @click="manualCheck">手动触发库存预警检查</el-button>
      <span style="margin-left: 10px; color: #909399; font-size: 14px;">
        提示：系统每天上午9点自动执行库存预警检查
      </span>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLowStockWarnings, getHighStockWarnings, checkInventoryWarning } from '@/api/inventory-warning'

const loading = ref(false)
const lowStockWarnings = ref([])
const highStockWarnings = ref([])

const refreshWarnings = async () => {
  loading.value = true
  try {
    const [lowRes, highRes] = await Promise.all([
      getLowStockWarnings(),
      getHighStockWarnings()
    ])
    lowStockWarnings.value = lowRes.data
    highStockWarnings.value = highRes.data
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

const manualCheck = async () => {
  try {
    await checkInventoryWarning()
    ElMessage.success('库存预警检查已触发')
    refreshWarnings()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

onMounted(() => {
  refreshWarnings()
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-text {
  text-align: center;
  padding: 20px;
  color: #909399;
}
</style>
