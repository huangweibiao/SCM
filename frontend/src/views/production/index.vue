<template>
  <div class="production-page">
    <h2 class="page-title">生产管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="计划" :value="10" /><el-option label="生产中" :value="20" /><el-option label="完工" :value="30" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button type="primary" @click="handleAdd">新增生产工单</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="moNo" label="工单号" width="150" />
        <el-table-column prop="itemId" label="产品ID" width="100" />
        <el-table-column prop="qty" label="计划数量" />
        <el-table-column prop="finishedQty" label="已完成" />
        <el-table-column prop="startDate" label="计划开始" width="120" />
        <el-table-column prop="endDate" label="计划结束" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button v-if="row.status === 10" link type="primary" @click="handleStart(row)">开始</el-button>
            <el-button v-if="row.status === 20" link type="warning" @click="handlePick(row)">领料</el-button>
            <el-button v-if="row.status === 20" link type="success" @click="handleFinish(row)">完工</el-button>
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>

    <!-- 领料弹窗 -->
    <el-dialog v-model="pickDialogVisible" title="生产领料" width="600px">
      <el-form :model="pickForm" label-width="100px">
        <el-form-item label="领料仓库">
          <el-select v-model="pickForm.warehouseId" placeholder="请选择仓库">
            <el-option label="主仓库 (ID: 1)" :value="1" />
            <el-option label="成品仓库 (ID: 2)" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="领料明细">
          <el-table :data="pickDetails" border>
            <el-table-column prop="itemId" label="物料ID" width="80" />
            <el-table-column prop="itemName" label="物料名称" />
            <el-table-column label="领料数量">
              <template #default="{ row }">
                <el-input-number v-model="row.pickQty" :min="0" :precision="2" size="small" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pickDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPick">确认领料</el-button>
      </template>
    </el-dialog>

    <!-- 完工弹窗 -->
    <el-dialog v-model="finishDialogVisible" title="完工入库" width="500px">
      <el-form :model="finishForm" label-width="100px">
        <el-form-item label="入库仓库">
          <el-select v-model="finishForm.warehouseId" placeholder="请选择仓库">
            <el-option label="主仓库 (ID: 1)" :value="1" />
            <el-option label="成品仓库 (ID: 2)" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库数量">
          <el-input-number v-model="finishForm.finishedQty" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="finishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFinish">确认入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductionOrderList, startProduction, finishProduction, pickMaterials } from '../../api/production'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ status: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 领料相关
const pickDialogVisible = ref(false)
const pickForm = reactive({ warehouseId: 1 })
const pickDetails = ref<any[]>([])
const currentPickOrderId = ref<number | null>(null)

// 完工相关
const finishDialogVisible = ref(false)
const finishForm = reactive({ warehouseId: 1, finishedQty: 0 })
const currentFinishOrderId = ref<number | null>(null)

const getStatusType = (status: number) => { const types: Record<number, string> = { 10: 'info', 20: 'warning', 30: 'success' }; return types[status] || 'info' }
const getStatusText = (status: number) => { const texts: Record<number, string> = { 10: '计划', 20: '生产中', 30: '完工' }; return texts[status] || '未知' }

const loadData = async () => {
  loading.value = true
  try {
    const res = await getProductionOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally { loading.value = false }
}
const handleSearch = () => { pagination.pageNum = 1; loadData() }
const handleAdd = () => ElMessage.info('请在后续功能中完善')
const handleView = (row: any) => ElMessage.info('查看详情: ' + row.moNo)

const handleStart = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认开始生产？', '提示', { type: 'info' })
    await startProduction(row.id)
    ElMessage.success('已开始生产')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handlePick = (row: any) => {
  currentPickOrderId.value = row.id
  pickForm.warehouseId = 1
  // 模拟领料数据
  pickDetails.value = [
    { itemId: 1, itemName: '原材料A', pickQty: 0 },
    { itemId: 2, itemName: '原材料B', pickQty: 0 }
  ]
  pickDialogVisible.value = true
}

const submitPick = async () => {
  const details = pickDetails.value.filter(d => d.pickQty > 0).map(d => ({ itemId: d.itemId, qty: d.pickQty }))
  if (details.length === 0) {
    ElMessage.warning('请输入领料数量')
    return
  }
  try {
    await pickMaterials(currentPickOrderId.value, { warehouseId: pickForm.warehouseId, details })
    ElMessage.success('领料成功')
    pickDialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '领料失败')
  }
}

const handleFinish = (row: any) => {
  currentFinishOrderId.value = row.id
  finishForm.warehouseId = 1
  finishForm.finishedQty = Number(row.qty) - Number(row.finishedQty)
  finishDialogVisible.value = true
}

const submitFinish = async () => {
  if (finishForm.finishedQty <= 0) {
    ElMessage.warning('请输入有效的入库数量')
    return
  }
  try {
    await finishProduction(currentFinishOrderId.value, {
      finishedQty: finishForm.finishedQty,
      warehouseId: finishForm.warehouseId,
      price: 0,
      batchNo: 'BATCH' + Date.now()
    })
    ElMessage.success('完工成功')
    finishDialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '完工失败')
  }
}
onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.production-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
