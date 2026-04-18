<template>
  <div class="permission-page">
    <h2 class="page-title">权限管理</h2>
    <el-card>
      <template #header>
        <el-button type="primary" @click="handleAdd()">新增权限</el-button>
      </template>
      <el-table :data="tableData" v-loading="loading" row-key="id" default-expand-all stripe>
        <el-table-column prop="permissionName" label="权限名称" width="200" />
        <el-table-column prop="permissionCode" label="权限编码" width="200" />
        <el-table-column prop="permissionType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getPermTypeTag(row.permissionType)">{{ getPermTypeText(row.permissionType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="component" label="组件" width="150" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleAdd(row)">子级</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑权限弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级权限">
          <el-tree-select v-model="form.parentId" :data="parentTreeOptions" :props="{ label: 'permissionName', value: 'id', children: 'children' }" placeholder="无（顶级权限）" clearable check-strictly style="width: 100%" />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-radio-group v-model="form.permissionType">
            <el-radio value="directory">目录</el-radio>
            <el-radio value="menu">菜单</el-radio>
            <el-radio value="button">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="请输入权限编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="路径" v-if="form.permissionType !== 'button'">
          <el-input v-model="form.path" placeholder="如 /basic/item" />
        </el-form-item>
        <el-form-item label="组件" v-if="form.permissionType === 'menu'">
          <el-input v-model="form.component" placeholder="如 basic/item" />
        </el-form-item>
        <el-form-item label="图标" v-if="form.permissionType !== 'button'">
          <el-input v-model="form.icon" placeholder="如 Document" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" v-if="isEdit">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { getPermissionTree, createPermission, updatePermission, deletePermission, getPermission } from '../../api/permission'

const loading = ref(false)
const tableData = ref<any[]>([])

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  id: null as number | null,
  parentId: 0 as number | null,
  permissionType: 'menu',
  permissionName: '',
  permissionCode: '',
  path: '',
  component: '',
  icon: '',
  sort: 0,
  status: 1
})

const dialogTitle = computed(() => isEdit.value ? '编辑权限' : '新增权限')

const rules = {
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permissionType: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

const parentTreeOptions = computed(() => {
  const addDisabled = (nodes: any[]): any[] => {
    return nodes.map(n => ({
      ...n,
      disabled: isEdit.value && n.id === form.id,
      children: n.children ? addDisabled(n.children) : undefined
    }))
  }
  return [{ id: 0, permissionName: '顶级权限', children: addDisabled(tableData.value) }]
})

const getPermTypeText = (type: string) => {
  const map: Record<string, string> = { directory: '目录', menu: '菜单', button: '按钮' }
  return map[type] || type
}
const getPermTypeTag = (type: string) => {
  const map: Record<string, string> = { directory: 'success', menu: '', button: 'info' }
  return map[type] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPermissionTree()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleAdd = (parent?: any) => {
  isEdit.value = false
  form.id = null
  form.parentId = parent ? parent.id : 0
  form.permissionType = 'menu'
  form.permissionName = ''
  form.permissionCode = ''
  form.path = ''
  form.component = ''
  form.icon = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = async (row: any) => {
  isEdit.value = true
  try {
    const res = await getPermission(row.id)
    const data = res.data || {}
    form.id = data.id
    form.parentId = data.parentId ?? 0
    form.permissionType = data.permissionType || 'menu'
    form.permissionName = data.permissionName || ''
    form.permissionCode = data.permissionCode || ''
    form.path = data.path || ''
    form.component = data.component || ''
    form.icon = data.icon || ''
    form.sort = data.sort ?? 0
    form.status = data.status ?? 1
    dialogVisible.value = true
  } catch (e) {
    // fallback to row data
    form.id = row.id
    form.parentId = row.parentId ?? 0
    form.permissionType = row.permissionType || 'menu'
    form.permissionName = row.permissionName || ''
    form.permissionCode = row.permissionCode || ''
    form.path = row.path || ''
    form.component = row.component || ''
    form.icon = row.icon || ''
    form.sort = row.sort ?? 0
    form.status = row.status ?? 1
    dialogVisible.value = true
  }
}

const resetForm = () => {
  formRef.value?.resetFields()
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      const data: any = {
        permissionName: form.permissionName,
        parentId: form.parentId ?? 0,
        permissionType: form.permissionType,
        path: form.path,
        component: form.component,
        icon: form.icon,
        sort: form.sort
      }

      if (isEdit.value) {
        data.status = form.status
        await updatePermission(form.id, data)
        ElMessage.success('更新成功')
      } else {
        data.permissionCode = form.permissionCode
        await createPermission(data)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e: any) {
      ElMessage.error(e.message || '操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = async (row: any) => {
  if (row.children && row.children.length > 0) {
    ElMessage.warning('该权限下有子权限，请先删除子权限')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除权限"${row.permissionName}"吗？`, '提示', { type: 'warning' })
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
</style>
