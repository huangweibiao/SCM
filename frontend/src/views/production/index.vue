<template>
  <div class="production-page">
    <h2 class="page-title">生产管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="计划" :value="10" /><el-option label="生产中" :value="20" /><el-option label="完工" :value="30" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button type="primary" @click="handleAdd">新增生产工单</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="moNo" label="工单号" width="150" />
        <el-table-column prop="itemId" label="产品ID" width="100" />
        <el-table-column prop="qty" label="计划数量" />
        <el-table-column prop="finishedQty" label="已完成" />
        <el-table-column prop="startDate" label="计划开始" width="120" />
        <el-table-column prop="endDate" label="计划结束" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 10" link type="primary" @click="handleStart(row)">开始</el-button>
            <el-button v-if="row.status === 20" link type="success" @click="handleFinish(row)">完工</el-button>
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductionOrderList, startProduction, finishProduction } from '../../api/production'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ status: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const getStatusType = (status: number) => { const types: Record<number, string> = { 10: 'info', 20: 'warning', 30: 'success' }; return types[status] || 'info' }
const getStatusText = (status: number) => { const texts: Record<number, string> = { 10: '计划', 20: '生产中', 30: '完工' }; return texts[status] || '未知' }

const loadData = async () => {
  loading.value = true
  try {
    const res = await getProductionOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } finally { loading.value = false }
}
const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleAdd = () => ElMessage.info('请在后续功能中完善')
const handleView = (row: any) => ElMessage.info('查看详情: ' + row.moNo)
const handleStart = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认开始生产？', '提示', { type: 'info' })
    await startProduction(row.id)
    ElMessage.success('已开始生产')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}
const handleFinish = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认完工入库？', '提示', { type: 'info' })
    await finishProduction(row.id, { finishedQty: row.qty, warehouseId: 1, price: 0 })
    ElMessage.success('完工成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}
onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.production-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
