<template>
  <div class="logistics-page">
    <h2 class="page-title">物流管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="业务类型">
          <el-select v-model="searchForm.businessType" placeholder="请选择" clearable>
            <el-option label="采购" :value="10" /><el-option label="销售" :value="20" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待发货" :value="10" /><el-option label="运输中" :value="20" />
            <el-option label="已签收" :value="30" /><el-option label="拒收" :value="40" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="logisticsNo" label="物流单号" width="150" />
        <el-table-column prop="businessType" label="业务类型" width="100">
          <template #default="{ row }">{{ row.businessType === 10 ? '采购' : '销售' }}</template>
        </el-table-column>
        <el-table-column prop="courierName" label="快递公司" />
        <el-table-column prop="courierNo" label="快递单号" />
        <el-table-column prop="receivePerson" label="收货人" />
        <el-table-column prop="receivePhone" label="联系电话" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLogisticsOrderList } from '../../api/logistics'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ businessType: null, status: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const getStatusType = (status: number) => { const types: Record<number, string> = { 10: 'info', 20: 'warning', 30: 'success', 40: 'danger' }; return types[status] || 'info' }
const getStatusText = (status: number) => { const texts: Record<number, string> = { 10: '待发货', 20: '运输中', 30: '已签收', 40: '拒收' }; return texts[status] || '未知' }

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLogisticsOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list
    pagination.total = res.data.total
  } finally { loading.value = false }
}
const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleView = (row: any) => ElMessage.info('查看详情: ' + row.logisticsNo)
onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.logistics-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
