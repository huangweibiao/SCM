<template>
  <div class="transfer-page">
    <h2 class="page-title">库存调拨</h2>
    <el-card>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="源仓库" prop="fromWarehouseId">
          <el-select v-model="form.fromWarehouseId" placeholder="请选择源仓库" @change="handleFromWarehouseChange">
            <el-option label="主仓库 (ID: 1)" :value="1" />
            <el-option label="成品仓库 (ID: 2)" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标仓库" prop="toWarehouseId">
          <el-select v-model="form.toWarehouseId" placeholder="请选择目标仓库">
            <el-option label="主仓库 (ID: 1)" :value="1" />
            <el-option label="成品仓库 (ID: 2)" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="物料" prop="itemId">
          <el-input v-model="form.itemId" type="number" placeholder="请输入物料ID" />
        </el-form-item>
        <el-form-item label="调拨数量" prop="qty">
          <el-input-number v-model="form.qty" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">确认调拨</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <h3>调拨说明</h3>
      <ul>
        <li>库存调拨指将物料从源仓库调拨到目标仓库</li>
        <li>调拨时系统会自动从源仓库扣减库存，向目标仓库增加库存</li>
        <li>源仓库必须有足够的可用库存才能进行调拨</li>
      </ul>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, FormInstance } from 'element-plus'
import { transferInventory } from '../../../api/inventory'

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  fromWarehouseId: 1,
  toWarehouseId: 2,
  itemId: null as number | null,
  qty: 1
})

const rules = {
  fromWarehouseId: [{ required: true, message: '请选择源仓库', trigger: 'change' }],
  toWarehouseId: [{ required: true, message: '请选择目标仓库', trigger: 'change' }],
  itemId: [{ required: true, message: '请输入物料ID', trigger: 'blur' }],
  qty: [{ required: true, message: '请输入调拨数量', trigger: 'blur' }]
}

const handleFromWarehouseChange = () => {
  if (form.fromWarehouseId === form.toWarehouseId) {
    form.toWarehouseId = form.fromWarehouseId === 1 ? 2 : 1
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.fromWarehouseId === form.toWarehouseId) {
        ElMessage.warning('源仓库和目标仓库不能相同')
        return
      }
      if (form.itemId === null || form.qty <= 0) {
        ElMessage.warning('请输入有效的物料ID和调拨数量')
        return
      }

      loading.value = true
      try {
        await transferInventory({
          fromWarehouseId: form.fromWarehouseId,
          toWarehouseId: form.toWarehouseId,
          itemId: form.itemId,
          qty: form.qty
        })
        ElMessage.success('调拨成功')
        handleReset()
      } catch (e: any) {
        ElMessage.error(e.message || '调拨失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  formRef.value?.resetFields()
  form.fromWarehouseId = 1
  form.toWarehouseId = 2
  form.itemId = null
  form.qty = 1
}
</script>

<style scoped lang="scss">
.transfer-page {
  padding: 20px;
}
</style>
