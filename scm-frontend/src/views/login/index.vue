<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="@/assets/logo.svg" alt="SCM" class="logo" />
        <h1>SCM 供应链管理系统</h1>
        <p>Supply Chain Management System</p>
      </div>
      
      <div class="login-content">
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          @click="handleOAuth2Login"
          class="login-btn"
        >
          <el-icon><User /></el-icon>
          使用统一认证登录
        </el-button>
        
        <div class="login-footer">
          <p>登录即表示您同意我们的服务条款和隐私政策</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)

async function handleOAuth2Login() {
  loading.value = true
  
  try {
    // Check if already logged in
    await userStore.fetchCurrentUser()
    
    // Redirect to intended page or dashboard
    const redirect = route.query.redirect as string
    router.push(redirect || '/dashboard')
  } catch {
    // Redirect to OAuth2 authorization endpoint
    const redirect = route.query.redirect as string
    const currentPath = redirect || '/dashboard'
    window.location.href = `/oauth2/authorization/custom-oauth2?redirect_uri=${encodeURIComponent(window.location.origin + currentPath)}`
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .logo {
    width: 64px;
    height: 64px;
    margin-bottom: 20px;
  }

  h1 {
    font-size: 24px;
    color: #333;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: #999;
  }
}

.login-content {
  .login-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
  }
}

.login-footer {
  margin-top: 30px;
  text-align: center;

  p {
    font-size: 12px;
    color: #999;
  }
}
</style>
