<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">销售订单管理</h1>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="客户ID">
          <el-input v-model="queryParams.customerId" placeholder="请输入客户ID" clearable @clear="handleQuery" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable @clear="handleQuery">
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
            <el-option label="部分发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已关闭" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单编号">
          <el-input v-model="queryParams.soNo" placeholder="请输入订单编号" clearable @clear="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增订单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="soNo" label="订单编号" width="180" />
        <el-table-column prop="customerId" label="客户ID" width="100" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" type="success" link size="small" @click="handleAudit(row)">审核</el-button>
            <el-button v-if="row.status === 0" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            <el-button v-if="row.status === 0 || row.status === 1" type="warning" link size="small" @click="handleClose(row)">关闭</el-button>
            <el-button v-if="row.status === 1" type="info" link size="small" @click="handleShipment(row)">发货</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" title="销售订单" width="800px" @close="handleCloseDialog">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="客户ID" prop="customerId">
          <el-input v-model="formData.customerId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="订单明细">
          <el-button type="primary" size="small" @click="handleAddDetail">添加明细</el-button>
        </el-form-item>
        <el-table :data="formData.details" border>
          <el-table-column prop="itemId" label="物料ID" width="150">
            <template #default="{ row, $index }">
              <el-input v-model="row.itemId" placeholder="物料ID" size="small" />
            </template>
          </el-table-column>
          <el-table-column prop="qty" label="数量" width="120">
            <template #default="{ row, $index }">
              <el-input v-model.number="row.qty" type="number" placeholder="数量" size="small" />
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="120">
            <template #default="{ row, $index }">
              <el-input v-model.number="row.price" type="number" placeholder="单价" size="small" />
            </template>
          </el-table-column>
          <el-table-column prop="deliveryDate" label="交货日期" width="150">
            <template #default="{ row, $index }">
              <el-date-picker v-model="row.deliveryDate" type="date" placeholder="交货日期" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" @click="handleRemoveDetail($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="900px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="订单编号">{{ currentRow.soNo }}</el-descriptions-item>
        <el-descriptions-item label="客户ID">{{ currentRow.customerId }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentRow.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(currentRow.status) }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ currentRow.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRow.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <h4>订单明细</h4>
      <el-table :data="details" border>
        <el-table-column prop="itemId" label="物料ID" width="120" />
        <el-table-column prop="qty" label="数量" width="100" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="shippedQty" label="已发数量" width="100" />
        <el-table-column prop="deliveryDate" label="交货日期" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSalesOrderPage, createSalesOrder, deleteSalesOrder, auditSalesOrder, closeSalesOrder, getSalesOrderDetails } from '@/api/sales'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const details = ref([])
const currentRow = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  customerId: null,
  status: null,
  soNo: '',
  startDate: '',
  endDate: ''
})

const formData = reactive({
  id: null,
  customerId: null,
  details: []
})

const formRules = {
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }]
}

const formRef = ref(null)

const getList = async () => {
  loading.value = true
  try {
    const res = await getSalesOrderPage(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const handleReset = () => {
  queryParams.customerId = null
  queryParams.status = null
  queryParams.soNo = ''
  handleQuery()
}

const handleAdd = () => {
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    customerId: null,
    details: []
  })
}

const handleAddDetail = () => {
  formData.details.push({
    itemId: null,
    qty: null,
    price: null,
    deliveryDate: null
  })
}

const handleRemoveDetail = (index) => {
  formData.details.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (formData.details.length === 0) {
        ElMessage.warning('请添加订单明细')
        return
      }
      submitLoading.value = true
      try {
        await createSalesOrder(formData)
        ElMessage.success('创建成功')
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleCloseDialog = () => {
  formRef.value?.resetFields()
  dialogVisible.value = false
}

const handleDetail = async (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
  try {
    const res = await getSalesOrderDetails(row.id)
    details.value = res.data
  } catch (error) {
    console.error('查询明细失败:', error)
  }
}

const handleAudit = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要审核订单"${row.soNo}"吗?`, '提示', { type: 'warning' })
    await auditSalesOrder(row.id, 1)
    ElMessage.success('审核成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除订单"${row.soNo}"吗?`, '提示', { type: 'warning' })
    await deleteSalesOrder(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleClose = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要关闭订单"${row.soNo}"吗?`, '提示', { type: 'warning' })
    await closeSalesOrder(row.id)
    ElMessage.success('关闭成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('关闭失败:', error)
    }
  }
}

const handleShipment = (row) => {
  ElMessage.info('发货功能在出库单中处理')
}

const getStatusText = (status) => {
  const map = { 0: '待审核', 1: '已审核', 2: '部分发货', 3: '已完成', 4: '已关闭' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'primary', 3: 'success', 4: 'info' }
  return map[status] || ''
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.search-card {
  margin-bottom: 20px;
}

.table-card {
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
