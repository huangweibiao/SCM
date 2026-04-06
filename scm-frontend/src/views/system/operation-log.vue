<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">操作日志</h1>
    </div>

    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="模块">
          <el-input v-model="queryParams.module" placeholder="请输入模块" clearable @clear="handleQuery" />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-input v-model="queryParams.operationType" placeholder="请输入操作类型" clearable @clear="handleQuery" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @clear="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable @clear="handleQuery">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="120" />
        <el-table-column prop="description" label="操作描述" width="200" />
        <el-table-column prop="username" label="用户名" width="100" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="method" label="请求方法" width="200" />
        <el-table-column prop="requestMethod" label="请求方式" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="costTime" label="耗时(ms)" width="100" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
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

    <el-dialog v-model="detailDialogVisible" title="操作日志详情" width="800px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="模块">{{ currentRow.module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ currentRow.operationType }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentRow.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentRow.realName }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentRow.method }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ currentRow.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求URL">{{ currentRow.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="客户端IP">{{ currentRow.ip }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRow.status === 1 ? 'success' : 'danger'">
            {{ currentRow.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentRow.costTime }}ms</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentRow.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <h4>请求参数</h4>
      <pre class="code-block">{{ currentRow?.requestParams || '-' }}</pre>
      <h4>响应结果</h4>
      <pre class="code-block">{{ currentRow?.responseData || '-' }}</pre>
      <h4 v-if="currentRow?.errorMsg">错误信息</h4>
      <pre class="code-block error-text" v-if="currentRow?.errorMsg">{{ currentRow.errorMsg }}</pre>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOperationLogPage } from '@/api/operation-log'

const loading = ref(false)
const detailDialogVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const currentRow = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  module: '',
  operationType: '',
  username: '',
  status: null
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getOperationLogPage(queryParams)
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
  queryParams.module = ''
  queryParams.operationType = ''
  queryParams.username = ''
  queryParams.status = null
  handleQuery()
}

const handleDetail = (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
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
  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}

.code-block {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

.error-text {
  color: #f56c6c;
}
</style>
