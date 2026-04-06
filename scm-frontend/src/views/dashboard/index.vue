<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h1 class="page-title">欢迎使用SCM供应链管理系统</h1>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409EFF"><ShoppingCart /></el-icon>
            <div class="stat-info">
              <div class="stat-value">128</div>
              <div class="stat-label">采购订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67C23A"><Sell /></el-icon>
            <div class="stat-info">
              <div class="stat-value">256</div>
              <div class="stat-label">销售订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#E6A23C"><Box /></el-icon>
            <div class="stat-info">
              <div class="stat-value">1,580</div>
              <div class="stat-label">库存物料</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#F56C6C"><OfficeBuilding /></el-icon>
            <div class="stat-info">
              <div class="stat-value">56</div>
              <div class="stat-label">供应商</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-card class="quick-access" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>快捷入口</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="4" v-for="item in quickAccessItems" :key="item.name">
          <div class="quick-access-item" @click="router.push(item.path)">
            <el-icon :size="40" :color="item.color">
              <component :is="item.icon" />
            </el-icon>
            <div class="item-name">{{ item.name }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const quickAccessItems = [
  { name: '物料管理', icon: 'Goods', path: '/basic/item', color: '#409EFF' },
  { name: '供应商管理', icon: 'OfficeBuilding', path: '/supplier', color: '#67C23A' },
  { name: '采购管理', icon: 'ShoppingCart', path: '/purchase', color: '#E6A23C' },
  { name: '库存管理', icon: 'Box', path: '/inventory', color: '#F56C6C' },
  { name: '销售管理', icon: 'Sell', path: '/sales', color: '#909399' },
  { name: '报表分析', icon: 'DataAnalysis', path: '/report', color: '#606266' }
]
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .stat-icon {
      font-size: 48px;
    }

    .stat-info {
      text-align: right;

      .stat-value {
        font-size: 32px;
        font-weight: bold;
        color: #303133;
        line-height: 1;
        margin-bottom: 8px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

.quick-access {
  .quick-access-item {
    text-align: center;
    padding: 20px;
    cursor: pointer;
    transition: all 0.3s;
    border-radius: 4px;

    &:hover {
      background: #f5f7fa;
      transform: translateY(-2px);
    }

    .item-name {
      margin-top: 10px;
      font-size: 14px;
      color: #606266;
    }
  }
}

.card-header {
  font-weight: 500;
  color: #303133;
}
</style>
