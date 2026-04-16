<template>
  <div class="operation-log-page">
    <h2 class="page-title">操作日志</h2>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="operateType" label="操作类型" width="120" />
        <el-table-column prop="operateUser" label="操作人" width="100" />
        <el-table-column prop="operateTime" label="操作时间" width="180" />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="method" label="请求方法" />
        <el-table-column prop="params" label="请求参数" show-overflow-tooltip />
        <el-table-column prop="result" label="结果" width="80">
          <template #default="{ row }">
            <el-tag :type="row.result === 'success' ? 'success' : 'danger'">{{ row.result }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 预留：操作日志功能需要后端配合实现
const loadData = async () => {
  loading.value = true
  // 暂时返回空数据
  tableData.value = []
  pagination.total = 0
  loading.value = false
}
onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.operation-log-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
