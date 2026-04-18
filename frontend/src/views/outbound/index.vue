<template>
  <div class="outbound-page">
    <h2 class="page-title">出库管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="出库单号">
          <el-input v-model="searchForm.outboundNo" placeholder="出库单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审核" :value="10" />
            <el-option label="已审核" :value="20" />
            <el-option label="已完成" :value="30" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="primary" @click="handleAdd">新增出库单</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="outboundNo" label="出库单号" width="160" />
        <el-table-column prop="outboundType" label="出库类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOutboundTypeTag(row.outboundType)">{{ getOutboundTypeText(row.outboundType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warehouseId" label="仓库" width="120">
          <template #default="{ row }">
            {{ getWarehouseName(row.warehouseId) }}
          </template>
        </el-table-column>
        <el-table-column prop="soId" label="销售单ID" width="100" />
        <el-table-column prop="totalQty" label="总数量" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="120" />
        <el-table-column prop="outboundDate" label="出库日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button v-if="row.status === 10" link type="primary" @click="handleAudit(row)">审核</el-button>
            <el-button v-if="row.status === 20" link type="success" @click="handleConfirm(row)">确认出库</el-button>
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 10" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>

    <!-- 新增出库单弹窗 -->
    <el-dialog v-model="addDialogVisible" title="新增出库单" width="850px" @close="resetAddForm">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出库类型" prop="outboundType">
              <el-select v-model="addForm.outboundType" placeholder="请选择出库类型" style="width: 100%">
                <el-option label="销售出库" :value="10" />
                <el-option label="生产出库" :value="20" />
                <el-option label="调拨出库" :value="30" />
                <el-option label="其他出库" :value="40" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出库仓库" prop="warehouseId">
              <el-select v-model="addForm.warehouseId" placeholder="请选择仓库" style="width: 100%">
                <el-option v-for="w in warehouseOptions" :key="w.id" :label="w.warehouseName" :value="w.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售单ID">
              <el-input-number v-model="addForm.soId" :min="1" placeholder="选填" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产单ID">
              <el-input-number v-model="addForm.moId" :min="1" placeholder="选填" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="addForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>

        <el-divider content-position="left">出库明细</el-divider>
        <el-table :data="addForm.items" border style="margin-bottom: 12px">
          <el-table-column label="物料" width="200">
            <template #default="{ row }">
              <el-select v-model="row.itemId" placeholder="选择物料" filterable style="width: 100%">
                <el-option v-for="i in itemOptions" :key="i.id" :label="i.itemName" :value="i.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="130">
            <template #default="{ row }">
              <el-input-number v-model="row.qty" :min="0.01" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="130">
            <template #default="{ row }">
              <el-input-number v-model="row.price" :min="0" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="批次号" width="140">
            <template #default="{ row }">
              <el-input v-model="row.batchNo" placeholder="选填" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button link type="danger" @click="addForm.items.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" link @click="addFormItem">+ 添加物料行</el-button>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="addLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 出库单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" :title="'出库单详情 - ' + currentOrder.outboundNo" width="750px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="出库单号">{{ currentOrder.outboundNo }}</el-descriptions-item>
        <el-descriptions-item label="出库类型">{{ getOutboundTypeText(currentOrder.outboundType) }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ getWarehouseName(currentOrder.warehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="出库日期">{{ currentOrder.outboundDate }}</el-descriptions-item>
        <el-descriptions-item label="总数量">{{ currentOrder.totalQty }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="销售单ID">{{ currentOrder.soId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="生产单ID">{{ currentOrder.moId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="orderDetails" border style="margin-top: 16px">
        <el-table-column prop="itemId" label="物料ID" width="80" />
        <el-table-column prop="qty" label="数量" width="100" />
        <el-table-column prop="price" label="单价" width="100" />
        <el-table-column prop="amount" label="金额" width="100" />
        <el-table-column prop="batchNo" label="批次号" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { getOutboundOrderList, createOutbound, deleteOutboundOrder, auditOutboundOrder, confirmOutbound, getOutboundDetails, getOutbound } from '../../api/outbound'
import { getAllWarehouses } from '../../api/warehouse'
import { getAllItems } from '../../api/item'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ outboundNo: '', status: null as number | null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 下拉选项
const warehouseOptions = ref<any[]>([])
const itemOptions = ref<any[]>([])

// 详情弹窗
const detailDialogVisible = ref(false)
const currentOrder = ref<any>({})
const orderDetails = ref<any[]>([])

// 新增弹窗
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref<FormInstance>()
const addForm = reactive({
  outboundType: null as number | null,
  warehouseId: null as number | null,
  soId: null as number | null,
  moId: null as number | null,
  remark: '',
  items: [] as { itemId: number | null; qty: number; price: number; batchNo: string }[]
})
const addRules = {
  outboundType: [{ required: true, message: '请选择出库类型', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }]
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 10: 'info', 20: 'success', 30: 'warning' }
  return types[status] || 'info'
}
const getStatusText = (status: number) => {
  const texts: Record<number, string> = { 10: '待审核', 20: '已审核', 30: '已完成' }
  return texts[status] || '未知'
}

const getOutboundTypeText = (type: number) => {
  const texts: Record<number, string> = { 10: '销售出库', 20: '生产出库', 30: '调拨出库', 40: '其他出库' }
  return texts[type] || '未知'
}
const getOutboundTypeTag = (type: number) => {
  const tags: Record<number, string> = { 10: '', 20: 'success', 30: 'warning', 40: 'info' }
  return tags[type] || 'info'
}

const getWarehouseName = (id: number) => {
  const w = warehouseOptions.value.find(w => w.id === id)
  return w ? w.warehouseName : id
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOutboundOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
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

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleAdd = () => {
  addForm.outboundType = null
  addForm.warehouseId = null
  addForm.soId = null
  addForm.moId = null
  addForm.remark = ''
  addForm.items = [{ itemId: null, qty: 1, price: 0, batchNo: '' }]
  addDialogVisible.value = true
}

const addFormItem = () => {
  addForm.items.push({ itemId: null, qty: 1, price: 0, batchNo: '' })
}

const resetAddForm = () => {
  addForm.items = []
}

const submitAdd = async () => {
  if (!addFormRef.value) return
  await addFormRef.value.validate(async (valid) => {
    if (!valid) return

    const validItems = addForm.items.filter(i => i.itemId && i.qty > 0)
    if (validItems.length === 0) {
      ElMessage.warning('请至少添加一条有效的出库明细')
      return
    }

    addLoading.value = true
    try {
      const items = validItems.map(i => {
        const item: any = { itemId: i.itemId, qty: i.qty, price: i.price }
        if (i.batchNo) item.batchNo = i.batchNo
        return item
      })
      const data: any = {
        outboundType: addForm.outboundType,
        warehouseId: addForm.warehouseId,
        remark: addForm.remark,
        items
      }
      if (addForm.soId) data.soId = addForm.soId
      if (addForm.moId) data.moId = addForm.moId

      await createOutbound(data)
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
    const res = await getOutbound(row.id)
    currentOrder.value = res.data || row
  } catch (e) {
    currentOrder.value = row
  }
  try {
    const res = await getOutboundDetails(row.id)
    orderDetails.value = res.data || []
  } catch (e) {
    orderDetails.value = []
  }
  detailDialogVisible.value = true
}

const handleAudit = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认审核该出库单?', '提示', { type: 'warning' })
    await auditOutboundOrder(row.id)
    ElMessage.success('审核成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleConfirm = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认出库？出库后库存将减少！', '提示', { type: 'info' })
    await confirmOutbound(row.id)
    ElMessage.success('出库成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认删除该出库单?', '提示', { type: 'warning' })
    await deleteOutboundOrder(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => {
  loadData()
  loadOptions()
})
</script>

<style scoped lang="scss">
.outbound-page {
  .el-pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
