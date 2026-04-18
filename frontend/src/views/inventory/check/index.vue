<template>
  <div class="check-page">
    <h2 class="page-title">库存盘点</h2>

    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="仓库">
          <el-select v-model="searchForm.warehouseId" placeholder="请选择仓库" clearable style="width: 720px">
            <el-option label="主仓库 (ID: 1)" :value="1" />
            <el-option label="成品仓库 (ID: 2)" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreateCheck">新建盘点</el-button>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="checkNo" label="盘点单号" width="150" />
        <el-table-column prop="warehouseId" label="仓库ID" width="100" />
        <el-table-column prop="checkDate" label="盘点日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleCheck(row)">盘点</el-button>
            <el-button link type="success" @click="handleSubmitResult(row)">提交结果</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>

    <!-- 盘点弹窗 -->
    <el-dialog v-model="checkDialogVisible" title="库存盘点" width="800px">
      <p>仓库: {{ checkForm.warehouseId }}</p>
      <el-table :data="checkDetails" border>
        <el-table-column prop="itemId" label="物料ID" width="80" />
        <el-table-column prop="itemName" label="物料名称" />
        <el-table-column prop="systemQty" label="系统库存" width="100" />
        <el-table-column label="实际库存">
          <template #default="{ row }">
            <el-input-number v-model="row.actualQty" :min="0" :precision="2" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="差异" width="100">
          <template #default="{ row }">
            <span :class="{ 'diff-positive': row.diff > 0, 'diff-negative': row.diff < 0 }">
              {{ row.diff }}
            </span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="checkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCheckResult">提交结果</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getInventoryList, createInventoryCheck, submitCheckResult } from '../../../api/inventory'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ warehouseId: null as number | null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 盘点相关
const checkDialogVisible = ref(false)
const checkDetails = ref<any[]>([])
const currentCheckId = ref<number | null>(null)
const checkForm = reactive({ warehouseId: 1 })

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 10: 'info', 20: 'success' }
  return types[status] || 'info'
}
const getStatusText = (status: number) => {
  const texts: Record<number, string> = { 10: '盘点中', 20: '已完成' }
  return texts[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getInventoryList({ pageNum: 1, pageSize: 100, ...searchForm })
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }

const handleCreateCheck = async () => {
  if (!searchForm.warehouseId) {
    ElMessage.warning('请先选择仓库')
    return
  }

  try {
    await createInventoryCheck({ warehouseId: searchForm.warehouseId })
    ElMessage.success('盘点单创建成功')
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '创建失败')
  }
}

const handleCheck = (row: any) => {
  checkForm.warehouseId = row.warehouseId
  // 模拟盘点数据
  checkDetails.value = [
    { itemId: 1, itemName: '物料A', systemQty: 100, actualQty: 100, diff: 0 },
    { itemId: 2, itemName: '物料B', systemQty: 50, actualQty: 50, diff: 0 }
  ]
  checkDialogVisible.value = true
}

const handleSubmitResult = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认提交盘点结果？差异将自动调整库存', '提示', { type: 'warning' })
    await submitCheckResult(row.id, { details: [] })
    ElMessage.success('提交成功')
    loadData()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '提交失败')
    }
  }
}

const submitCheckResult = async () => {
  const details = checkDetails.value.map(d => ({
    itemId: d.itemId,
    warehouseId: checkForm.warehouseId,
    actualQty: d.actualQty
  }))

  try {
    await submitCheckResult(currentCheckId.value, { details })
    ElMessage.success('提交成功')
    checkDialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '提交失败')
  }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.check-page {
  .el-pagination { margin-top: 20px; justify-content: flex-end; }
  .diff-positive { color: #67C23A; }
  .diff-negative { color: #F56C6C; }
}
</style>
