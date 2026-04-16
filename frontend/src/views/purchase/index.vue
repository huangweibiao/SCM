<template>
  <div class="purchase-page">
    <h2 class="page-title">采购管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审核" :value="10" />
            <el-option label="已审核" :value="20" />
            <el-option label="部分收货" :value="30" />
            <el-option label="已完成" :value="40" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="primary" @click="handleAdd">新增采购订单</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="poNo" label="采购单号" width="150" />
        <el-table-column prop="supplierId" label="供应商ID" width="100" />
        <el-table-column prop="warehouseId" label="仓库ID" width="100" />
        <el-table-column prop="orderDate" label="下单日期" width="120" />
        <el-table-column prop="totalAmount" label="总金额" />
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
import { getPurchaseOrderList, deletePurchaseOrder, auditPurchaseOrder } from '../../api/purchase'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ status: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const getStatusType = (status: number) => {
  const types = { 10: 'info', 20: 'success', 30: 'warning', 40: 'success', 50: 'danger' }
  return types[status as keyof typeof types] || 'info'
}

const getStatusText = (status: number) => {
  const texts = { 10: '待审核', 20: '已审核', 30: '部分收货', 40: '已完成', 50: '已关闭' }
  return texts[status as keyof typeof texts] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPurchaseOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleAdd = () => ElMessage.info('请在后续功能中完善新增表单')
const handleView = (row: any) => ElMessage.info('查看详情: ' + row.poNo)
const handleAudit = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认审核通过该采购订单？', '提示', { type: 'info' })
    await auditPurchaseOrder(row.id, { status: 20, auditBy: 1 })
    ElMessage.success('审核成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该采购订单吗？', '提示', { type: 'warning' })
    await deletePurchaseOrder(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}
onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.purchase-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
