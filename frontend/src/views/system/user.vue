<template>
  <div class="user-page">
    <h2 class="page-title">用户管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名"><el-input v-model="searchForm.username" placeholder="用户名" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button type="primary" @click="handleAdd">新增用户</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username"><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
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
import { getUserList, createUser, updateUser, deleteUser } from '../../api/user'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const searchForm = reactive({ username: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, username: '', password: '', realName: '', phone: '', email: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, keyword: searchForm.username })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } finally { loading.value = false }
}
const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleAdd = () => { isEdit.value = false; Object.assign(form, { id: null, username: '', password: '', realName: '', phone: '', email: '' }); dialogVisible.value = true }
const handleEdit = (row: any) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        isEdit.value ? await updateUser(form.id, form) : await createUser(form)
        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        dialogVisible.value = false
        loadData()
      } catch (e) { console.error(e) }
    }
  })
}
onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.user-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
