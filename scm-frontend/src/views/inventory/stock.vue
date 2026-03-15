<template>
  <div class="page-container">
    <div class="page-header">
      <h2>库存查询</h2>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="SKU编码">
        <el-input v-model="searchForm.sku" placeholder="请输入SKU编码" clearable />
      </el-form-item>
      <el-form-item label="产品名称">
        <el-input v-model="searchForm.name" placeholder="请输入产品名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary">查询</el-button>
        <el-button>重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="stockData" stripe>
      <el-table-column prop="sku" label="SKU编码" width="120" />
      <el-table-column prop="name" label="产品名称" min-width="150" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="quantity" label="库存数量" width="100">
        <template #default="{ row }">
          <span :class="{ 'low-stock': row.quantity < 100 }">{{ row.quantity }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="warehouse" label="仓库" width="100" />
      <el-table-column prop="updatedAt" label="更新时间" width="160" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

const searchForm = reactive({
  sku: '',
  name: '',
})

const stockData = ref([
  {
    sku: 'SKU-001',
    name: '原材料A',
    category: '原材料',
    quantity: 500,
    unit: 'kg',
    warehouse: '主仓库',
    updatedAt: '2024-01-15 09:00',
  },
  {
    sku: 'SKU-002',
    name: '包装材料B',
    category: '包材',
    quantity: 80,
    unit: '件',
    warehouse: '主仓库',
    updatedAt: '2024-01-14 16:30',
  },
])
</script>

<style scoped lang="scss">
.page-container {
  .page-header {
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

  .low-stock {
    color: #f56c6c;
    font-weight: bold;
  }
}
</style>
