<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑供应商' : '新增供应商' }}</h2>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      class="form-container"
    >
      <el-form-item label="供应商编码" prop="code">
        <el-input v-model="form.code" placeholder="请输入供应商编码" />
      </el-form-item>

      <el-form-item label="供应商名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入供应商名称" />
      </el-form-item>

      <el-form-item label="联系人" prop="contact">
        <el-input v-model="form.contact" placeholder="请输入联系人" />
      </el-form-item>

      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入联系电话" />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>

      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入地址" />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  code: '',
  name: '',
  contact: '',
  phone: '',
  email: '',
  address: '',
  status: 1,
  remark: '',
})

const rules: FormRules = {
  code: [{ required: true, message: '请输入供应商编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
  ],
}

onMounted(() => {
  if (isEdit.value) {
    loadSupplierData()
  }
})

async function loadSupplierData() {
  // TODO: Load supplier data from API
  form.code = 'SUP-001'
  form.name = '优质材料供应商A'
  form.contact = '张三'
  form.phone = '13800138001'
  form.email = 'zhangsan@supplier.com'
  form.address = '北京市朝阳区xxx街道'
  form.status = 1
  form.remark = '优质供应商'
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    // TODO: Call API
    await new Promise((resolve) => setTimeout(resolve, 500))
    ElMessage.success(isEdit.value ? '保存成功' : '创建成功')
    router.push('/supplier/list')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.page-container {
  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0;
    }
  }

  .form-container {
    max-width: 600px;
  }
}
</style>
