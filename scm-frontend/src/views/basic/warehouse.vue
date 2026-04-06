<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">仓库管理</h1>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="仓库编码">
          <el-input
            v-model="queryParams.warehouseCode"
            placeholder="请输入仓库编码"
            clearable
            @clear="handleQuery"
          />
        </el-form-item>
        <el-form-item label="仓库名称">
          <el-input
            v-model="queryParams.warehouseName"
            placeholder="请输入仓库名称"
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
          <span>仓库列表</span>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="warehouseCode" label="仓库编码" width="150" />
        <el-table-column prop="warehouseName" label="仓库名称" width="200" />
        <el-table-column prop="address" label="仓库地址" />
        <el-table-column prop="manager" label="管理员" width="120" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getWarehousePage } from '@/api/warehouse'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  warehouseCode: '',
  warehouseName: '',
  status: null
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getWarehousePage(queryParams)
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
  queryParams.warehouseCode = ''
  queryParams.warehouseName = ''
  queryParams.status = null
  handleQuery()
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
