<template>
  <el-container class="main-layout">
    <!-- 侧边栏 -->
    <el-aside width="220px">
      <div class="logo">
        <h1>SCM系统</h1>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-sub-menu index="/basic">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>基础数据</span>
          </template>
          <el-menu-item index="/basic/item">物料管理</el-menu-item>
          <el-menu-item index="/basic/warehouse">仓库管理</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/supplier">
          <el-icon><OfficeBuilding /></el-icon>
          <span>供应商管理</span>
        </el-menu-item>

        <el-sub-menu index="/purchase">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>采购管理</span>
          </template>
          <el-menu-item index="/purchase">采购订单</el-menu-item>
          <el-menu-item index="/inbound">入库单</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/inventory">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item index="/inventory">库存查询</el-menu-item>
          <el-menu-item index="/inventory/transfer">库存调拨</el-menu-item>
          <el-menu-item index="/inventory/check">库存盘点</el-menu-item>
          <el-menu-item index="/system/inventory-warning">库存预警</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/sales">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>销售管理</span>
          </template>
          <el-menu-item index="/sales">销售订单</el-menu-item>
          <el-menu-item index="/customer">客户管理</el-menu-item>
          <el-menu-item index="/outbound">出库单</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/production">
          <el-icon><Monitor /></el-icon>
          <span>生产管理</span>
        </el-menu-item>

        <el-menu-item index="/logistics">
          <el-icon><Van /></el-icon>
          <span>物流管理</span>
        </el-menu-item>

        <el-menu-item index="/report">
          <el-icon><TrendCharts /></el-icon>
          <span>报表分析</span>
        </el-menu-item>

        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/role">角色管理</el-menu-item>
          <el-menu-item index="/system/permission">权限管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 头部 -->
      <el-header>
        <div class="header-content">
          <div class="header-title">SCM供应链管理系统</div>
          <div class="header-user">
            <el-dropdown @command="handleCommand">
              <span class="user-name">
                {{ userStore.userInfo?.realName || userStore.userInfo?.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import {
  House, Box, OfficeBuilding, ShoppingCart, Goods,
  Monitor, Van, TrendCharts, Setting, ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.main-layout {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  overflow-x: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #2b3a4a;

    h1 {
      color: #fff;
      font-size: 18px;
      margin: 0;
    }
  }

  .el-menu {
    border-right: none;
  }
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;

  .header-content {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;

    .header-title {
      font-size: 18px;
      font-weight: 500;
    }

    .header-user {
      .user-name {
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 5px;
      }
    }
  }
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
