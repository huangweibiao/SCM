<template>
  <div class="permission-page">
    <h2 class="page-title">权限管理</h2>
    <el-card>
      <el-table :data="tableData" v-loading="loading" row-key="id" default-expand-all stripe>
        <el-table-column prop="permissionName" label="权限名称" width="200" />
        <el-table-column prop="permissionCode" label="权限编码" width="200" />
        <el-table-column prop="permissionType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.permissionType === 'directory' ? 'success' : 'info'">{{ row.permissionType === 'directory' ? '目录' : '按钮' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPermissionTree } from '../../api/permission'

const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPermissionTree()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}
onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
</style>
