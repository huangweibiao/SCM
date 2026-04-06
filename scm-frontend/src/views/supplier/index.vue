<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">供应商管理</h1>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="供应商编码">
          <el-input
            v-model="queryParams.supplierCode"
            placeholder="请输入供应商编码"
            clearable
            @clear="handleQuery"
          />
        </el-form-item>
        <el-form-item label="供应商名称">
          <el-input
            v-model="queryParams.supplierName"
            placeholder="请输入供应商名称"
            clearable
            @clear="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>供应商列表</span>
          <el-button type="primary" @click="handleAdd">新增供应商</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="supplierCode" label="供应商编码" width="150" />
        <el-table-column prop="supplierName" label="供应商名称" width="200" />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="rating" label="评级" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="供应商编码" prop="supplierCode">
          <el-input v-model="formData.supplierCode" placeholder="留空自动生成" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="formData.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" type="textarea" placeholder="请输入地址" :rows="3" />
        </el-form-item>
        <el-form-item label="付款条件" prop="paymentTerms">
          <el-input v-model="formData.paymentTerms" placeholder="请输入付款条件" />
        </el-form-item>
        <el-form-item label="供应商评级" prop="rating">
          <el-rate v-model="formData.rating" show-text />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSupplierPage, createSupplier, updateSupplier, deleteSupplier } from '@/api/supplier'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  supplierCode: '',
  supplierName: '',
  status: null
})

const formData = reactive({
  id: null,
  supplierCode: '',
  supplierName: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
  paymentTerms: '',
  rating: 3
})

const formRules = {
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
}

const formRef = ref(null)

const getList = async () => {
  loading.value = true
  try {
    const res = await getSupplierPage(queryParams)
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
  queryParams.supplierCode = ''
  queryParams.supplierName = ''
  queryParams.status = null
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增供应商'
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    supplierCode: '',
    supplierName: '',
    contactPerson: '',
    phone: '',
    email: '',
    address: '',
    paymentTerms: '',
    rating: 3
  })
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑供应商'
  dialogVisible.value = true
  Object.assign(formData, row)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除供应商"${row.supplierName}"吗?`, '提示', {
      type: 'warning'
    })
    await deleteSupplier(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (formData.id) {
          await updateSupplier(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createSupplier(formData)
          ElMessage.success('创建成功')
        }
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

const handleClose = () => {
  formRef.value?.resetFields()
  dialogVisible.value = false
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
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
