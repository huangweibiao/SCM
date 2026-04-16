<template>
  <div class="inventory-warning-page">
    <h2 class="page-title">库存预警</h2>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="itemCode" label="物料编码" width="120" />
        <el-table-column prop="itemName" label="物料名称" />
        <el-table-column prop="warehouseId" label="仓库ID" width="100" />
        <el-table-column prop="availableQty" label="可用库存" />
        <el-table-column prop="minStock" label="最低库存" />
        <el-table-column prop="maxStock" label="最高库存" />
        <el-table-column prop="warnType" label="预警类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.warnType === 1 ? 'danger' : 'warning'">
              {{ row.warnType === 1 ? '低库存' : '超库存' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getInventoryWarnings } from '../../api/inventory'

const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getInventoryWarnings()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}
onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
</style>
