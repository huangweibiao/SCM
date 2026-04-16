<template>
  <div class="inbound-page">
    <h2 class="page-title">入库管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="入库单号">
          <el-input v-model="searchForm.inboundNo" placeholder="入库单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审核" :value="10" />
            <el-option label="已审核" :value="20" />
            <el-option label="已完成" :value="30" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="primary" @click="handleAdd">新增入库单</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="inboundNo" label="入库单号" width="150" />
        <el-table-column prop="purchaseOrderId" label="采购单ID" width="100" />
        <el-table-column prop="warehouseId" label="仓库ID" width="100" />
        <el-table-column prop="inboundDate" label="入库日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 10" link type="primary" @click="handleAudit(row)">审核</el-button>
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 10" link type="danger" @click="handleDelete(row)">删除</el-button>
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
import { getInboundOrderList, deleteInboundOrder, auditInboundOrder } from '../../api/inbound'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ inboundNo: '', status: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 10: 'info', 20: 'success', 30: 'warning' }
  return types[status] || 'info'
}
const getStatusText = (status: number) => {
  const texts: Record<number, string> = { 10: '待审核', 20: '已审核', 30: '已完成' }
  return texts[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getInboundOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleAdd = () => {
  ElMessage.info('新增入库功能开发中')
}

const handleAudit = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认审核该入库单?', '提示', { type: 'warning' })
    await auditInboundOrder(row.id)
    ElMessage.success('审核成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const handleView = (row: any) => {
  ElMessage.info('详情功能开发中')
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认删除该入库单?', '提示', { type: 'warning' })
    await deleteInboundOrder(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.inbound-page {
  .el-pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
