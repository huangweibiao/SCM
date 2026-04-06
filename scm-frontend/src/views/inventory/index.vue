<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">库存管理</h1>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="物料ID">
          <el-input
            v-model="queryParams.itemId"
            placeholder="请输入物料ID"
            clearable
            @clear="handleQuery"
          />
        </el-form-item>
        <el-form-item label="仓库ID">
          <el-input
            v-model="queryParams.warehouseId"
            placeholder="请输入仓库ID"
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
          <span>库存列表</span>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="itemId" label="物料ID" width="100" />
        <el-table-column prop="warehouseId" label="仓库ID" width="100" />
        <el-table-column prop="qty" label="当前库存" width="150" />
        <el-table-column prop="lockedQty" label="锁定数量" width="150" />
        <el-table-column prop="availableQty" label="可用库存" width="150">
          <template #default="{ row }">
            <span :style="{ color: row.availableQty < 0 ? 'red' : '' }">
              {{ row.availableQty }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="batchNo" label="批次号" width="150" />
        <el-table-column prop="expireDate" label="过期日期" width="120" />
        <el-table-column prop="lastUpdateTime" label="最后更新时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
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

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="库存详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="物料ID">{{ currentRow.itemId }}</el-descriptions-item>
        <el-descriptions-item label="仓库ID">{{ currentRow.warehouseId }}</el-descriptions-item>
        <el-descriptions-item label="当前库存">{{ currentRow.qty }}</el-descriptions-item>
        <el-descriptions-item label="锁定数量">{{ currentRow.lockedQty }}</el-descriptions-item>
        <el-descriptions-item label="可用库存">
          <span :style="{ color: currentRow.availableQty < 0 ? 'red' : '' }">
            {{ currentRow.availableQty }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="批次号">{{ currentRow.batchNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="过期日期" :span="2">
          {{ currentRow.expireDate || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="版本号">{{ currentRow.version }}</el-descriptions-item>
        <el-descriptions-item label="最后更新时间">
          {{ currentRow.lastUpdateTime }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getInventoryPage } from '@/api/inventory'

const loading = ref(false)
const detailDialogVisible = ref(false)
const tableData = ref([])
const total = ref(0)
const currentRow = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  itemId: null,
  warehouseId: null
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getInventoryPage(queryParams)
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
  queryParams.itemId = null
  queryParams.warehouseId = null
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
