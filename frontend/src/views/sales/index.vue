<template>
  <div class="sales-page">
    <h2 class="page-title">销售管理</h2>
    <el-card class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="客户名称"><el-input v-model="searchForm.customerName" placeholder="客户名称" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审核" :value="10" /><el-option label="已审核" :value="20" />
            <el-option label="部分发货" :value="30" /><el-option label="已完成" :value="40" />
            <el-option label="已关闭" :value="50" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button type="primary" @click="handleAdd">新增销售订单</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="soNo" label="销售单号" width="150" />
        <el-table-column prop="customerName" label="客户名称" />
        <el-table-column prop="customerPhone" label="客户电话" width="120" />
        <el-table-column prop="warehouseId" label="仓库" width="120">
          <template #default="{ row }">
            {{ getWarehouseName(row.warehouseId) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderDate" label="下单日期" width="120" />
        <el-table-column prop="totalAmount" label="总金额" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button v-if="row.status === 10" link type="primary" @click="handleAudit(row)">审核</el-button>
            <el-button v-if="row.status === 20 || row.status === 30" link type="success" @click="handleDeliver(row)">发货</el-button>
            <el-button v-if="row.status !== 40 && row.status !== 50" link type="warning" @click="handleClose(row)">关闭</el-button>
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 10" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" @current-change="loadData" layout="total, prev, pager, next" />
    </el-card>

    <!-- 新增销售订单弹窗 -->
    <el-dialog v-model="addDialogVisible" title="新增销售订单" width="800px" @close="resetAddForm">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-select v-model="addForm.customerName" placeholder="请选择或输入客户" filterable allow-create default-first-option style="width: 100%" @change="onCustomerChange">
                <el-option v-for="c in customerOptions" :key="c.id" :label="c.customerName" :value="c.customerName" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户电话">
              <el-input v-model="addForm.customerPhone" placeholder="请输入客户电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发货仓库" prop="warehouseId">
              <el-select v-model="addForm.warehouseId" placeholder="请选择仓库" style="width: 100%">
                <el-option v-for="w in warehouseOptions" :key="w.id" :label="w.warehouseName" :value="w.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="addForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>

        <el-divider content-position="left">销售明细</el-divider>
        <el-table :data="addForm.items" border style="margin-bottom: 12px">
          <el-table-column label="物料" width="200">
            <template #default="{ row }">
              <el-select v-model="row.itemId" placeholder="选择物料" filterable style="width: 100%">
                <el-option v-for="i in itemOptions" :key="i.id" :label="i.itemName" :value="i.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.qty" :min="0.01" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.price" :min="0" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="税率(%)" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.taxRate" :min="0" :max="100" :precision="2" size="small" style="width: 100%" />
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

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" :title="'订单详情 - ' + currentOrder.soNo" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="销售单号">{{ currentOrder.soNo }}</el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ currentOrder.customerName }}</el-descriptions-item>
        <el-descriptions-item label="客户电话">{{ currentOrder.customerPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ getWarehouseName(currentOrder.warehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="下单日期">{{ currentOrder.orderDate }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="orderDetails" border style="margin-top: 16px">
        <el-table-column prop="itemId" label="物料ID" width="80" />
        <el-table-column prop="qty" label="数量" width="100" />
        <el-table-column prop="price" label="单价" width="100" />
        <el-table-column prop="taxRate" label="税率" width="80" />
        <el-table-column prop="taxAmount" label="税额" width="100" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="shippedQty" label="已发货" width="100" />
        <el-table-column prop="remainQty" label="剩余" width="100" />
      </el-table>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog v-model="deliverDialogVisible" title="销售发货" width="600px">
      <el-form :model="deliverForm" label-width="100px">
        <el-form-item label="发货仓库">
          <el-select v-model="deliverForm.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option v-for="w in warehouseOptions" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="发货明细">
          <el-table :data="deliverDetails" border>
            <el-table-column prop="itemId" label="物料ID" width="80" />
            <el-table-column prop="qty" label="订购数量" width="100" />
            <el-table-column prop="shippedQty" label="已发货" width="100" />
            <el-table-column prop="remainQty" label="剩余" width="100" />
            <el-table-column label="本次发货">
              <template #default="{ row }">
                <el-input-number v-model="row.deliverQty" :min="0" :max="row.remainQty" :precision="2" size="small" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deliverDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDeliver">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { getSalesOrderList, createSalesOrder, deleteSalesOrder, auditSalesOrder, deliverSalesOrder, getSalesOrderDetails, getSalesOrder } from '../../api/sales'
import { getCustomerList } from '../../api/customer'
import { getAllWarehouses } from '../../api/warehouse'
import { getAllItems } from '../../api/item'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ customerName: '', status: null as number | null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

// 下拉选项
const customerOptions = ref<any[]>([])
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
  customerName: '',
  customerPhone: '',
  warehouseId: null as number | null,
  remark: '',
  items: [] as { itemId: number | null; qty: number; price: number; taxRate: number }[]
})
const addRules = {
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }]
}

// 发货相关
const deliverDialogVisible = ref(false)
const deliverForm = reactive({ warehouseId: null as number | null })
const deliverDetails = ref<any[]>([])
const currentOrderId = ref<number | null>(null)

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 10: 'info', 20: 'success', 30: 'warning', 40: 'success', 50: 'danger' }
  return types[status] || 'info'
}
const getStatusText = (status: number) => {
  const texts: Record<number, string> = { 10: '待审核', 20: '已审核', 30: '部分发货', 40: '已完成', 50: '已关闭' }
  return texts[status] || '未知'
}

const getWarehouseName = (id: number) => {
  const w = warehouseOptions.value.find(w => w.id === id)
  return w ? w.warehouseName : id
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSalesOrderList({ pageNum: pagination.pageNum, pageSize: pagination.pageSize, ...searchForm })
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } finally { loading.value = false }
}

const loadOptions = async () => {
  try {
    const [custRes, whRes, itemRes] = await Promise.all([
      getCustomerList({ pageNum: 1, pageSize: 9999 }),
      getAllWarehouses(),
      getAllItems()
    ])
    customerOptions.value = custRes.data.list || custRes.data || []
    warehouseOptions.value = whRes.data || []
    itemOptions.value = itemRes.data || []
  } catch (e) {
    console.error('加载选项失败', e)
  }
}

const handleSearch = () => { pagination.pageNum = 1; loadData() }

const handleAdd = () => {
  addForm.customerName = ''
  addForm.customerPhone = ''
  addForm.warehouseId = null
  addForm.remark = ''
  addForm.items = [{ itemId: null, qty: 1, price: 0, taxRate: 0 }]
  addDialogVisible.value = true
}

const onCustomerChange = (val: string) => {
  const c = customerOptions.value.find(c => c.customerName === val)
  if (c) {
    addForm.customerPhone = c.phone || c.customerPhone || ''
  }
}

const addFormItem = () => {
  addForm.items.push({ itemId: null, qty: 1, price: 0, taxRate: 0 })
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
      ElMessage.warning('请至少添加一条有效的销售明细')
      return
    }

    addLoading.value = true
    try {
      await createSalesOrder({
        customerName: addForm.customerName,
        customerPhone: addForm.customerPhone,
        warehouseId: addForm.warehouseId,
        remark: addForm.remark,
        items: validItems.map(i => ({
          itemId: i.itemId,
          qty: i.qty,
          price: i.price,
          taxRate: i.taxRate
        }))
      })
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
    const res = await getSalesOrder(row.id)
    currentOrder.value = res.data || row
  } catch (e) {
    currentOrder.value = row
  }
  try {
    const res = await getSalesOrderDetails(row.id)
    orderDetails.value = res.data || []
  } catch (e) {
    orderDetails.value = []
  }
  detailDialogVisible.value = true
}

const handleAudit = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认审核通过该销售订单？（将锁定库存）', '提示', { type: 'info' })
    await auditSalesOrder(row.id, { status: 20, auditBy: 1 })
    ElMessage.success('审核成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleDeliver = async (row: any) => {
  currentOrderId.value = row.id
  deliverForm.warehouseId = row.warehouseId || null

  try {
    const res = await getSalesOrderDetails(row.id)
    deliverDetails.value = (res.data || []).map((d: any) => ({
      ...d,
      deliverQty: 0
    }))
    deliverDialogVisible.value = true
  } catch (e) {
    ElMessage.error('获取订单明细失败')
  }
}

const submitDeliver = async () => {
  const details = deliverDetails.value
    .filter(d => d.deliverQty > 0)
    .map(d => ({ itemId: d.itemId, qty: d.deliverQty }))

  if (details.length === 0) {
    ElMessage.warning('请输入发货数量')
    return
  }

  try {
    await deliverSalesOrder(currentOrderId.value, {
      warehouseId: deliverForm.warehouseId,
      details
    })
    ElMessage.success('发货成功')
    deliverDialogVisible.value = false
    loadData()
  } catch (e: any) {
    ElMessage.error(e.message || '发货失败')
  }
}

const handleClose = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认关闭该销售订单？关闭后不可恢复！', '提示', { type: 'warning' })
    await auditSalesOrder(row.id, { status: 50 })
    ElMessage.success('关闭成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该销售订单吗？', '提示', { type: 'warning' })
    await deleteSalesOrder(row.id)
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
.sales-page .el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
