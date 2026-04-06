<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">物流订单管理</h1>
    </div>

    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="业务类型">
          <el-select v-model="queryParams.businessType" placeholder="请选择" clearable @clear="handleQuery">
            <el-option label="采购" :value="1" />
            <el-option label="销售" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable @clear="handleQuery">
            <el-option label="待发货" :value="0" />
            <el-option label="运输中" :value="1" />
            <el-option label="已送达" :value="2" />
            <el-option label="已签收" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增物流单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="lgNo" label="物流单号" width="180" />
        <el-table-column prop="businessType" label="业务类型" width="100">
          <template #default="{ row }">
            {{ row.businessType === 1 ? '采购' : '销售' }}
          </template>
        </el-table-column>
        <el-table-column prop="businessNo" label="业务单号" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiver" label="收货人" width="120" />
        <el-table-column prop="receiverPhone" label="收货电话" width="130" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button type="warning" link size="small" @click="handleUpdateStatus(row)">更新状态</el-button>
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

    <el-dialog v-model="dialogVisible" title="物流订单" width="600px" @close="handleCloseDialog">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="业务类型" prop="businessType">
          <el-select v-model="formData.businessType" placeholder="请选择">
            <el-option label="采购" :value="1" />
            <el-option label="销售" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务单号" prop="businessNo">
          <el-input v-model="formData.businessNo" placeholder="请输入业务单号" />
        </el-form-item>
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="formData.receiver" placeholder="请输入收货人" />
        </el-form-item>
        <el-form-item label="收货电话" prop="receiverPhone">
          <el-input v-model="formData.receiverPhone" placeholder="请输入收货电话" />
        </el-form-item>
        <el-form-item label="收货地址" prop="receiverAddress">
          <el-input v-model="formData.receiverAddress" type="textarea" :rows="3" placeholder="请输入收货地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="物流详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="物流单号">{{ currentRow.lgNo }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">
          {{ currentRow.businessType === 1 ? '采购' : '销售' }}
        </el-descriptions-item>
        <el-descriptions-item label="业务单号">{{ currentRow.businessNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(currentRow.status) }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ currentRow.receiver }}</el-descriptions-item>
        <el-descriptions-item label="收货电话">{{ currentRow.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentRow.receiverAddress }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLogisticsOrderPage, createLogisticsOrder, updateLogisticsStatus } from '@/api/logistics'

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
  businessType: null,
  status: null,
  startDate: '',
  endDate: ''
})

const formData = reactive({
  id: null,
  businessType: null,
  businessNo: '',
  receiver: '',
  receiverPhone: '',
  receiverAddress: ''
})

const formRules = {
  businessType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  businessNo: [{ required: true, message: '请输入业务单号', trigger: 'blur' }],
  receiver: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入收货电话', trigger: 'blur' }]
}

const formRef = ref(null)

const getList = async () => {
  loading.value = true
  try {
    const res = await getLogisticsOrderPage(queryParams)
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
  queryParams.businessType = null
  queryParams.status = null
  handleQuery()
}

const handleAdd = () => {
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    businessType: null,
    businessNo: '',
    receiver: '',
    receiverPhone: '',
    receiverAddress: ''
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await createLogisticsOrder(formData)
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

const handleUpdateStatus = (row) => {
  ElMessageBox.prompt('请选择新状态', '更新状态', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '0-待发货 1-运输中 2-已送达 3-已签收',
    inputPattern: /^[0-3]$/,
    inputErrorMessage: '请输入0-3之间的数字'
  }).then(({ value }) => {
    updateLogisticsStatus(row.id, value).then(() => {
      ElMessage.success('更新成功')
      getList()
    })
  })
}

const getStatusText = (status) => {
  const map = { 0: '待发货', 1: '运输中', 2: '已送达', 3: '已签收' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'primary', 2: 'success', 3: 'success' }
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
