<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
const goHome = () => router.push('/')
</script>

<style scoped lang="scss">
.not-found {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  .content {
    text-align: center;
    h1 {
      font-size: 120px;
      color: #409EFF;
      margin: 0;
    }
    p {
      font-size: 18px;
      color: #606266;
      margin: 20px 0;
    }
  }
}
</style>
