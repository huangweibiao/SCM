<template>
  <div class="page-container">
    <div class="page-header">
      <h2>供应商列表</h2>
      <el-button type="primary" @click="$router.push('/supplier/create')">
        <el-icon><Plus /></el-icon>
        新增供应商
      </el-button>
    </div>

    <!-- Search Form -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="供应商名称">
        <el-input v-model="searchForm.name" placeholder="请输入供应商名称" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- Table -->
    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="code" label="供应商编码" width="120" />
      <el-table-column prop="name" label="供应商名称" min-width="150" />
      <el-table-column prop="contact" label="联系人" width="100" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)

const searchForm = reactive({
  name: '',
  status: undefined as number | undefined,
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const tableData = ref([
  {
    id: 1,
    code: 'SUP-001',
    name: '优质材料供应商A',
    contact: '张三',
    phone: '13800138001',
    email: 'zhangsan@supplier.com',
    status: 1,
    createdAt: '2024-01-10 10:00',
  },
  {
    id: 2,
    code: 'SUP-002',
    name: '高效物流供应商B',
    contact: '李四',
    phone: '13800138002',
    email: 'lisi@supplier.com',
    status: 1,
    createdAt: '2024-01-12 14:30',
  },
])

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    // TODO: Call API
    pagination.total = 2
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  loadData()
}

function resetSearch() {
  searchForm.name = ''
  searchForm.status = undefined
  handleSearch()
}

function handleEdit(row: { id: number }) {
  router.push(`/supplier/edit/${row.id}`)
}

async function handleDelete(row: { id: number; name: string }) {
  await ElMessageBox.confirm(`确定要删除供应商"${row.name}"吗？`, '提示', {
    type: 'warning',
  })
  // TODO: Call delete API
  ElMessage.success('删除成功')
  loadData()
}
</script>

<style scoped lang="scss">
.page-container {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
    }
  }

  .search-form {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #fafafa;
    border-radius: 4px;
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
