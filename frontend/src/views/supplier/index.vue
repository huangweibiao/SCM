<template>
  <div class="supplier-page">
    <h2 class="page-title">供应商管理</h2>

    <!-- 搜索栏 -->
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="供应商编码/名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增供应商</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="supplierCode" label="供应商编码" width="120" />
        <el-table-column prop="supplierName" label="供应商名称" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="rating" label="评级" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        @current-change="loadData"
        layout="total, prev, pager, next"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="供应商编码" prop="supplierCode">
          <el-input v-model="form.supplierCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="form.supplierName" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea" />
        </el-form-item>
        <el-form-item label="付款条件">
          <el-input v-model="form.paymentTerms" />
        </el-form-item>
        <el-form-item label="评级">
          <el-rate v-model="form.rating" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { getSupplierList, createSupplier, updateSupplier, deleteSupplier } from '../../api/supplier'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const searchForm = reactive({ keyword: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const form = reactive({
  id: null, supplierCode: '', supplierName: '', contactPerson: '',
  phone: '', email: '', address: '', paymentTerms: '', rating: 3, status: 1
})

const rules = {
  supplierCode: [{ required: true, message: '请输入供应商编码', trigger: 'blur' }],
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
}

const dialogTitle = () => isEdit.value ? '编辑供应商' : '新增供应商'

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSupplierList({
      pageNum: pagination.pageNum, pageSize: pagination.pageSize, keyword: searchForm.keyword
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleReset = () => { searchForm.keyword = ''; handleSearch() }

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, supplierCode: '', supplierName: '', contactPerson: '', phone: '', email: '', address: '', paymentTerms: '', rating: 3, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: any) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该供应商吗？', '提示', { type: 'warning' })
    await deleteSupplier(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) { console.error(error) }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        isEdit.value ? await updateSupplier(form.id, form) : await createSupplier(form)
        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        dialogVisible.value = false
        loadData()
      } catch (error) { console.error(error) }
    }
  })
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.supplier-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
