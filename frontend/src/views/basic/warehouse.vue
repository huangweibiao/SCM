<template>
  <div class="warehouse-page">
    <h2 class="page-title">仓库管理</h2>

    <!-- 搜索栏 -->
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="仓库编码/名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增仓库</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="warehouseCode" label="仓库编码" width="120" />
        <el-table-column prop="warehouseName" label="仓库名称" />
        <el-table-column prop="address" label="仓库地址" />
        <el-table-column prop="manager" label="管理员" width="100" />
        <el-table-column prop="phone" label="联系电话" width="120" />
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
        <el-form-item label="仓库编码" prop="warehouseCode">
          <el-input v-model="form.warehouseCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="warehouseName">
          <el-input v-model="form.warehouseName" />
        </el-form-item>
        <el-form-item label="仓库地址">
          <el-input v-model="form.address" type="textarea" />
        </el-form-item>
        <el-form-item label="管理员">
          <el-input v-model="form.manager" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
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
import { getWarehouseList, createWarehouse, updateWarehouse, deleteWarehouse } from '../../api/warehouse'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const searchForm = reactive({
  keyword: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  warehouseCode: '',
  warehouseName: '',
  address: '',
  manager: '',
  phone: '',
  status: 1
})

const rules = {
  warehouseCode: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }],
  warehouseName: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }]
}

const dialogTitle = () => isEdit.value ? '编辑仓库' : '新增仓库'

const loadData = async () => {
  loading.value = true
  try {
    const res = await getWarehouseList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword
    })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    warehouseCode: '',
    warehouseName: '',
    address: '',
    manager: '',
    phone: '',
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该仓库吗？', '提示', {
      type: 'warning'
    })
    await deleteWarehouse(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateWarehouse(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createWarehouse(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.warehouse-page {
  .el-pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
