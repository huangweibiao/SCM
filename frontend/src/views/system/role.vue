<template>
  <div class="role-page">
    <h2 class="page-title">角色管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true">
        <el-form-item><el-button type="primary" @click="handleAdd">新增角色</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="description" label="描述" />
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色编码" prop="roleCode"><el-input v-model="form.roleCode" :disabled="isEdit" /></el-form-item>
        <el-form-item label="角色名称" prop="roleName"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
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
import { getRoleList, createRole, updateRole, deleteRole } from '../../api/role'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, roleCode: '', roleName: '', description: '' })
const rules = { roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }], roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; Object.assign(form, { id: null, roleCode: '', roleName: '', description: '' }); dialogVisible.value = true }
const handleEdit = (row: any) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', { type: 'warning' })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        isEdit.value ? await updateRole(form.id, form) : await createRole(form)
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
.role-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
