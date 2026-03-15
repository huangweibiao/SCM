<template>
  <div class="page-container">
    <div class="page-header">
      <h2>个人设置</h2>
    </div>

    <el-card>
      <template #header>
        <span>基本信息</span>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="form-container"
      >
        <el-form-item label="用户名">
          <el-input :value="userStore.username" disabled />
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input :value="userStore.user?.email" disabled />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="头像">
          <el-avatar :size="64" :src="form.avatar">
            {{ userStore.nickname?.charAt(0) }}
          </el-avatar>
          <el-button type="primary" link class="ml-10">更换头像</el-button>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  nickname: '',
  phone: '',
  avatar: '',
})

const rules: FormRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
}

onMounted(() => {
  if (userStore.user) {
    form.nickname = userStore.user.nickname || ''
    form.phone = userStore.user.phone || ''
    form.avatar = userStore.user.avatar || ''
  }
})

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const success = await userStore.updateProfile({
      nickname: form.nickname,
      phone: form.phone,
      avatar: form.avatar,
    })
    if (success) {
      ElMessage.success('保存成功')
    }
  } catch {
    ElMessage.error('保存失败')
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
    max-width: 500px;
  }

  .ml-10 {
    margin-left: 10px;
  }
}
</style>
