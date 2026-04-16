<template>
  <div class="inventory-page">
    <h2 class="page-title">库存管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="物料ID"><el-input v-model="searchForm.itemId" placeholder="物料ID" clearable /></el-form-item>
        <el-form-item label="仓库ID"><el-input v-model="searchForm.warehouseId" placeholder="仓库ID" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="itemId" label="物料ID" width="100" />
        <el-table-column prop="warehouseId" label="仓库ID" width="100" />
        <el-table-column prop="qty" label="库存数量" />
        <el-table-column prop="lockedQty" label="锁定数量" />
        <el-table-column prop="availableQty" label="可用数量" />
        <el-table-column prop="batchNo" label="批次号" width="120" />
        <el-table-column prop="expireDate" label="过期日期" width="120" />
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getInventoryList } from '../../api/inventory'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ itemId: null, warehouseId: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getInventoryList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } finally { loading.value = false }
}
const handleSearch = () => { pagination.pageNum = 1; loadData() }
onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.inventory-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
