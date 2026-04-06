<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">物料管理</h1>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="物料编码">
          <el-input
            v-model="queryParams.itemCode"
            placeholder="请输入物料编码"
            clearable
            @clear="handleQuery"
          />
        </el-form-item>
        <el-form-item label="物料名称">
          <el-input
            v-model="queryParams.itemName"
            placeholder="请输入物料名称"
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
          <span>物料列表</span>
          <el-button type="primary" @click="handleAdd">新增物料</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="itemCode" label="物料编码" width="150" />
        <el-table-column prop="itemName" label="物料名称" width="200" />
        <el-table-column prop="spec" label="规格型号" width="150" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="categoryName" label="物料分类" width="150" />
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
        <el-form-item label="物料编码" prop="itemCode">
          <el-input v-model="formData.itemCode" placeholder="请输入物料编码" />
        </el-form-item>
        <el-form-item label="物料名称" prop="itemName">
          <el-input v-model="formData.itemName" placeholder="请输入物料名称" />
        </el-form-item>
        <el-form-item label="规格型号" prop="spec">
          <el-input v-model="formData.spec" placeholder="请输入规格型号" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="formData.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="物料分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择物料分类" style="width: 100%">
            <el-option label="原材料" :value="1" />
            <el-option label="半成品" :value="2" />
            <el-option label="产成品" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="安全库存" prop="safetyStock">
          <el-input-number v-model="formData.safetyStock" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最高库存" prop="maxStock">
          <el-input-number v-model="formData.maxStock" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最低库存" prop="minStock">
          <el-input-number v-model="formData.minStock" :min="0" :precision="2" style="width: 100%" />
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
import { getItemPage, createItem, updateItem, deleteItem } from '@/api/item'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  itemCode: '',
  itemName: '',
  categoryId: null,
  status: null
})

const formData = reactive({
  id: null,
  itemCode: '',
  itemName: '',
  spec: '',
  unit: '',
  categoryId: null,
  safetyStock: 0,
  maxStock: 0,
  minStock: 0
})

const formRules = {
  itemCode: [{ required: true, message: '请输入物料编码', trigger: 'blur' }],
  itemName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const formRef = ref(null)

// 查询列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getItemPage(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  queryParams.itemCode = ''
  queryParams.itemName = ''
  queryParams.categoryId = null
  queryParams.status = null
  handleQuery()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增物料'
  dialogVisible.value = true
  Object.assign(formData, {
    id: null,
    itemCode: '',
    itemName: '',
    spec: '',
    unit: '',
    categoryId: null,
    safetyStock: 0,
    maxStock: 0,
    minStock: 0
  })
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑物料'
  dialogVisible.value = true
  Object.assign(formData, row)
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除物料"${row.itemName}"吗?`, '提示', {
      type: 'warning'
    })
    await deleteItem(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (formData.id) {
          await updateItem(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createItem(formData)
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

// 关闭对话框
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
