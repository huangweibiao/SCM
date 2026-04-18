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
        <el-table-column prop="moNo" label="工单号" width="160" />
        <el-table-column prop="itemId" label="产品" width="120">
          <template #default="{ row }">
            {{ getItemName(row.itemId) }}
          </template>
        </el-table-column>
        <el-table-column prop="qty" label="计划数量" width="100" />
        <el-table-column prop="finishedQty" label="已完成" width="100" />
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
            <el-button v-if="row.status === 10" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>

    <!-- 新增生产工单弹窗 -->
    <el-dialog v-model="addDialogVisible" title="新增生产工单" width="600px" @close="resetAddForm">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="产品" prop="itemId">
          <el-select v-model="addForm.itemId" placeholder="请选择产品" filterable style="width: 100%">
            <el-option v-for="i in itemOptions" :key="i.id" :label="i.itemName" :value="i.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划数量" prop="qty">
          <el-input-number v-model="addForm.qty" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划开始">
              <el-date-picker v-model="addForm.startDate" type="date" placeholder="选填" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划结束">
              <el-date-picker v-model="addForm.endDate" type="date" placeholder="选填" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="addForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="addLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 工单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" :title="'工单详情 - ' + currentOrder.moNo" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单号">{{ currentOrder.moNo }}</el-descriptions-item>
        <el-descriptions-item label="产品">{{ getItemName(currentOrder.itemId) }}</el-descriptions-item>
        <el-descriptions-item label="计划数量">{{ currentOrder.qty }}</el-descriptions-item>
        <el-descriptions-item label="已完成">{{ currentOrder.finishedQty }}</el-descriptions-item>
        <el-descriptions-item label="计划开始">{{ currentOrder.startDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="计划结束">{{ currentOrder.endDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际开始">{{ currentOrder.actualStart || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际结束">{{ currentOrder.actualEnd || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 领料弹窗 -->
    <el-dialog v-model="pickDialogVisible" title="生产领料" width="600px">
      <el-form :model="pickForm" label-width="100px">
        <el-form-item label="领料仓库">
          <el-select v-model="pickForm.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option v-for="w in warehouseOptions" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="领料明细">
          <el-table :data="pickDetails" border>
            <el-table-column prop="itemId" label="物料" width="150">
              <template #default="{ row }">
                {{ getItemName(row.itemId) }}
              </template>
            </el-table-column>
            <el-table-column label="领料数量">
              <template #default="{ row }">
                <el-input-number v-model="row.pickQty" :min="0" :precision="2" size="small" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-button type="primary" link @click="addPickRow">+ 添加物料</el-button>
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
          <el-select v-model="finishForm.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option v-for="w in warehouseOptions" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库数量">
          <el-input-number v-model="finishForm.finishedQty" :min="0.01" :precision="2" style="width: 100%" />
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
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { getProductionOrderList, createProductionOrder, deleteProductionOrder, startProduction, finishProduction, pickMaterials, getProductionOrder } from '../../api/production'
import { getAllWarehouses } from '../../api/warehouse'
import { getAllItems } from '../../api/item'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ status: null as number | null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 下拉选项
const warehouseOptions = ref<any[]>([])
const itemOptions = ref<any[]>([])

// 详情弹窗
const detailDialogVisible = ref(false)
const currentOrder = ref<any>({})

// 新增弹窗
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref<FormInstance>()
const addForm = reactive({
  itemId: null as number | null,
  qty: 1,
  startDate: '',
  endDate: '',
  remark: ''
})
const addRules = {
  itemId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  qty: [{ required: true, message: '请输入计划数量', trigger: 'blur' }]
}

// 领料相关
const pickDialogVisible = ref(false)
const pickForm = reactive({ warehouseId: null as number | null })
const pickDetails = ref<any[]>([])
const currentPickOrderId = ref<number | null>(null)

// 完工相关
const finishDialogVisible = ref(false)
const finishForm = reactive({ warehouseId: null as number | null, finishedQty: 0 })
const currentFinishOrderId = ref<number | null>(null)

const getStatusType = (status: number) => { const types: Record<number, string> = { 10: 'info', 20: 'warning', 30: 'success' }; return types[status] || 'info' }
const getStatusText = (status: number) => { const texts: Record<number, string> = { 10: '计划', 20: '生产中', 30: '完工' }; return texts[status] || '未知' }

const getItemName = (id: number) => {
  const i = itemOptions.value.find(i => i.id === id)
  return i ? i.itemName : id
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getProductionOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally { loading.value = false }
}

const loadOptions = async () => {
  try {
    const [whRes, itemRes] = await Promise.all([getAllWarehouses(), getAllItems()])
    warehouseOptions.value = whRes.data || []
    itemOptions.value = itemRes.data || []
  } catch (e) {
    console.error('加载选项失败', e)
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }

const handleAdd = () => {
  addForm.itemId = null
  addForm.qty = 1
  addForm.startDate = ''
  addForm.endDate = ''
  addForm.remark = ''
  addDialogVisible.value = true
}

const resetAddForm = () => {}

const submitAdd = async () => {
  if (!addFormRef.value) return
  await addFormRef.value.validate(async (valid) => {
    if (!valid) return
    addLoading.value = true
    try {
      const data: any = {
        itemId: addForm.itemId,
        qty: addForm.qty,
        remark: addForm.remark
      }
      if (addForm.startDate) data.startDate = addForm.startDate
      if (addForm.endDate) data.endDate = addForm.endDate

      await createProductionOrder(data)
      ElMessage.success('创建成功')
      addDialogVisible.value = false
      loadData()
    } catch (e: any) {
      ElMessage.error(e.message || '创建失败')
    } finally {
      addLoading.value = false
    }
  })
}

const handleView = async (row: any) => {
  try {
    const res = await getProductionOrder(row.id)
    currentOrder.value = res.data || row
  } catch (e) {
    currentOrder.value = row
  }
  detailDialogVisible.value = true
}

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
  pickForm.warehouseId = null
  pickDetails.value = [{ itemId: null, pickQty: 0 }]
  pickDialogVisible.value = true
}

const addPickRow = () => {
  pickDetails.value.push({ itemId: null, pickQty: 0 })
}

const submitPick = async () => {
  const details = pickDetails.value.filter(d => d.itemId && d.pickQty > 0).map(d => ({ itemId: d.itemId, qty: d.pickQty }))
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
  finishForm.warehouseId = null
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

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该生产工单吗？', '提示', { type: 'warning' })
    await deleteProductionOrder(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(() => {
  loadData()
  loadOptions()
})
</script>

<style scoped lang="scss">
.production-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
