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
            <el-option label="已关闭" :value="50" />
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
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button v-if="row.status === 10" link type="primary" @click="handleAudit(row)">审核</el-button>
            <el-button v-if="row.status === 20 || row.status === 30" link type="success" @click="handleReceive(row)">收货</el-button>
            <el-button v-if="row.status !== 40 && row.status !== 50" link type="warning" @click="handleClose(row)">关闭</el-button>
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 10" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>

    <!-- 收货弹窗 -->
    <el-dialog v-model="receiveDialogVisible" title="采购收货" width="600px">
      <el-form :model="receiveForm" label-width="100px">
        <el-form-item label="选择仓库">
          <el-select v-model="receiveForm.warehouseId" placeholder="请选择仓库">
            <el-option label="主仓库 (ID: 1)" :value="1" />
            <el-option label="成品仓库 (ID: 2)" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="收货明细">
          <el-table :data="receiveDetails" border>
            <el-table-column prop="itemId" label="物料ID" width="80" />
            <el-table-column prop="qty" label="订购数量" width="100" />
            <el-table-column prop="receivedQty" label="已收货" width="100" />
            <el-table-column prop="remainQty" label="剩余" width="100" />
            <el-table-column label="本次收货">
              <template #default="{ row }">
                <el-input-number v-model="row.receiveQty" :min="0" :max="row.remainQty" :precision="2" size="small" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="receiveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReceive">确认收货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { getPurchaseOrderList, deletePurchaseOrder, auditPurchaseOrder, receivePurchaseOrder, closePurchaseOrder, getPurchaseOrderDetails } from '../../api/purchase'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ status: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 收货相关
const receiveDialogVisible = ref(false)
const receiveForm = reactive({ warehouseId: 1 })
const receiveDetails = ref<any[]>([])
const currentOrderId = ref<number | null>(null)

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 10: 'info', 20: 'success', 30: 'warning', 40: 'success', 50: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = { 10: '待审核', 20: '已审核', 30: '部分收货', 40: '已完成', 50: '已关闭' }
  return texts[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPurchaseOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
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

const handleReceive = async (row: any) => {
  currentOrderId.value = row.id
  receiveForm.warehouseId = row.warehouseId || 1

  try {
    const res = await getPurchaseOrderDetails(row.id)
    receiveDetails.value = (res.data || []).map((d: any) => ({
      ...d,
      receiveQty: 0
    }))
    receiveDialogVisible.value = true
  } catch (e) {
    ElMessage.error('获取订单明细失败')
  }
}

const submitReceive = async () => {
  const details = receiveDetails.value
    .filter(d => d.receiveQty > 0)
    .map(d => ({ itemId: d.itemId, qty: d.receiveQty }))

  if (details.length === 0) {
    ElMessage.warning('请输入收货数量')
    return
  }

  try {
    await receivePurchaseOrder(currentOrderId.value, {
      warehouseId: receiveForm.warehouseId,
      details
    })
    ElMessage.success('收货成功')
    receiveDialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '收货失败')
  }
}

const handleClose = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认关闭该采购订单？关闭后不可恢复！', '提示', { type: 'warning' })
    await closePurchaseOrder(row.id)
    ElMessage.success('关闭成功')
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
