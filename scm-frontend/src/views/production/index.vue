<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">生产工单管理</h1>
    </div>

    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="物料ID">
          <el-input v-model="queryParams.itemId" placeholder="请输入物料ID" clearable @clear="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable @clear="handleQuery">
            <el-option label="待生产" :value="0" />
            <el-option label="生产中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已关闭" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增工单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="moNo" label="工单编号" width="180" />
        <el-table-column prop="itemId" label="物料ID" width="100" />
        <el-table-column prop="planQty" label="计划数量" width="100" />
        <el-table-column prop="finishedQty" label="完工数量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" type="success" link size="small" @click="handleStart(row)">开始生产</el-button>
            <el-button v-if="row.status === 1" type="warning" link size="small" @click="handleComplete(row)">完工</el-button>
            <el-button v-if="row.status === 0" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" title="生产工单" width="600px" @close="handleCloseDialog">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="物料ID" prop="itemId">
          <el-input v-model="formData.itemId" placeholder="请输入物料ID" />
        </el-form-item>
        <el-form-item label="计划数量" prop="planQty">
          <el-input v-model.number="formData.planQty" type="number" placeholder="请输入计划数量" />
        </el-form-item>
        <el-form-item label="计划开始日期" prop="planStartDate">
          <el-date-picker v-model="formData.planStartDate" type="date" placeholder="请选择" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计划完成日期" prop="planEndDate">
          <el-date-picker v-model="formData.planEndDate" type="date" placeholder="请选择" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="工单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="工单编号">{{ currentRow.moNo }}</el-descriptions-item>
        <el-descriptions-item label="物料ID">{{ currentRow.itemId }}</el-descriptions-item>
        <el-descriptions-item label="计划数量">{{ currentRow.planQty }}</el-descriptions-item>
        <el-descriptions-item label="完工数量">{{ currentRow.finishedQty }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(currentRow.status) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRow.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductionOrderPage, createProductionOrder, deleteProductionOrder, startProduction, completeProduction } from '@/api/production'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const currentRow = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  itemId: null,
  status: null,
  startDate: '',
  endDate: ''
})

const formData = reactive({
  id: null,
  itemId: null,
  planQty: null,
  planStartDate: null,
  planEndDate: null
})

const formRules = {
  itemId: [{ required: true, message: '请输入物料ID', trigger: 'blur' }],
  planQty: [{ required: true, message: '请输入计划数量', trigger: 'blur' }]
}

const formRef = ref(null)

const getList = async () => {
  loading.value = true
  try {
    const res = await getProductionOrderPage(queryParams)
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
  queryParams.itemId = null
  queryParams.status = null
  handleQuery()
}

const handleAdd = () => {
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    itemId: null,
    planQty: null,
    planStartDate: null,
    planEndDate: null
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await createProductionOrder(formData)
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

const handleDetail = (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要开始生产工单"${row.moNo}"吗?`, '提示', { type: 'warning' })
    await startProduction(row.id)
    ElMessage.success('开始生产成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const handleComplete = async (row) => {
  ElMessageBox.prompt('请输入完工数量', '完工确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入有效的数量'
  }).then(({ value }) => {
    ElMessageBox.prompt('请选择入库仓库', '仓库选择', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /^\d+$/,
      inputErrorMessage: '请输入有效的仓库ID'
    }).then(({ value: warehouseId }) => {
      completeProduction(row.id, value, warehouseId).then(() => {
        ElMessage.success('完工成功')
        getList()
      })
    })
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除工单"${row.moNo}"吗?`, '提示', { type: 'warning' })
    await deleteProductionOrder(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const getStatusText = (status) => {
  const map = { 0: '待生产', 1: '生产中', 2: '已完成', 3: '已关闭' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
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
