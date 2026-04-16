<template>
  <div class="sales-page">
    <h2 class="page-title">销售管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="客户名称"><el-input v-model="searchForm.customerName" placeholder="客户名称" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审核" :value="10" /><el-option label="已审核" :value="20" />
            <el-option label="部分发货" :value="30" /><el-option label="已完成" :value="40" />
            <el-option label="已关闭" :value="50" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button type="primary" @click="handleAdd">新增销售订单</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="soNo" label="销售单号" width="150" />
        <el-table-column prop="customerName" label="客户名称" />
        <el-table-column prop="customerPhone" label="客户电话" width="120" />
        <el-table-column prop="warehouseId" label="仓库ID" width="100" />
        <el-table-column prop="orderDate" label="下单日期" width="120" />
        <el-table-column prop="totalAmount" label="总金额" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-if="row.status === 10" link type="primary" @click="handleAudit(row)">审核</el-button>
            <el-button v-if="row.status === 20 || row.status === 30" link type="success" @click="handleDeliver(row)">发货</el-button>
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 10" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>

    <!-- 发货弹窗 -->
    <el-dialog v-model="deliverDialogVisible" title="销售发货" width="600px">
      <el-form :model="deliverForm" label-width="100px">
        <el-form-item label="发货仓库">
          <el-select v-model="deliverForm.warehouseId" placeholder="请选择仓库" disabled>
            <el-option label="主仓库 (ID: 1)" :value="1" />
            <el-option label="成品仓库 (ID: 2)" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="发货明细">
          <el-table :data="deliverDetails" border>
            <el-table-column prop="itemId" label="物料ID" width="80" />
            <el-table-column prop="qty" label="订购数量" width="100" />
            <el-table-column prop="shippedQty" label="已发货" width="100" />
            <el-table-column prop="remainQty" label="剩余" width="100" />
            <el-table-column label="本次发货">
              <template #default="{ row }">
                <el-input-number v-model="row.deliverQty" :min="0" :max="row.remainQty" :precision="2" size="small" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deliverDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDeliver">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSalesOrderList, deleteSalesOrder, auditSalesOrder, deliverSalesOrder, getSalesOrderDetails } from '../../api/sales'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ customerName: '', status: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 发货相关
const deliverDialogVisible = ref(false)
const deliverForm = reactive({ warehouseId: 1 })
const deliverDetails = ref<any[]>([])
const currentOrderId = ref<number | null>(null)

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 10: 'info', 20: 'success', 30: 'warning', 40: 'success', 50: 'danger' }
  return types[status] || 'info'
}
const getStatusText = (status: number) => {
  const texts: Record<number, string> = { 10: '待审核', 20: '已审核', 30: '部分发货', 40: '已完成', 50: '已关闭' }
  return texts[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSalesOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleAdd = () => ElMessage.info('请在后续功能中完善')
const handleView = (row: any) => ElMessage.info('查看详情: ' + row.soNo)

const handleAudit = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认审核通过该销售订单？（将锁定库存）', '提示', { type: 'info' })
    await auditSalesOrder(row.id, { status: 20, auditBy: 1 })
    ElMessage.success('审核成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleDeliver = async (row: any) => {
  currentOrderId.value = row.id
  deliverForm.warehouseId = row.warehouseId || 1

  try {
    const res = await getSalesOrderDetails(row.id)
    deliverDetails.value = (res.data || []).map((d: any) => ({
      ...d,
      deliverQty: 0
    }))
    deliverDialogVisible.value = true
  } catch (e) {
    ElMessage.error('获取订单明细失败')
  }
}

const submitDeliver = async () => {
  const details = deliverDetails.value
    .filter(d => d.deliverQty > 0)
    .map(d => ({ itemId: d.itemId, qty: d.deliverQty }))

  if (details.length === 0) {
    ElMessage.warning('请输入发货数量')
    return
  }

  try {
    await deliverSalesOrder(currentOrderId.value, { details })
    ElMessage.success('发货成功')
    deliverDialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '发货失败')
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该销售订单吗？', '提示', { type: 'warning' })
    await deleteSalesOrder(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.sales-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
