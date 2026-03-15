<template>
  <div class="dashboard-page">
    <el-row :gutter="20">
      <!-- Statistics Cards -->
      <el-col :span="6" v-for="stat in statistics" :key="stat.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="28">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Recent Activities -->
    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
              <el-button type="primary" link>查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentActivities" stripe>
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.statusType">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" class="action-btn">
              <el-icon><Plus /></el-icon>
              新增采购订单
            </el-button>
            <el-button type="success" class="action-btn">
              <el-icon><Plus /></el-icon>
              新增供应商
            </el-button>
            <el-button type="warning" class="action-btn">
              <el-icon><Search /></el-icon>
              库存查询
            </el-button>
            <el-button type="info" class="action-btn">
              <el-icon><Download /></el-icon>
              导出报表
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const statistics = ref([
  { title: '供应商数量', value: '128', icon: 'OfficeBuilding', color: '#409EFF' },
  { title: '采购订单', value: '56', icon: 'ShoppingCart', color: '#67C23A' },
  { title: '库存SKU', value: '1,234', icon: 'Box', color: '#E6A23C' },
  { title: '待处理事项', value: '12', icon: 'Bell', color: '#F56C6C' },
])

const recentActivities = ref([
  { type: '采购', description: '采购订单 PO-2024-001 已创建', time: '2024-01-15 10:30', status: '已完成', statusType: 'success' },
  { type: '库存', description: '产品 SKU-001 入库 100 件', time: '2024-01-15 09:15', status: '已完成', statusType: 'success' },
  { type: '供应商', description: '供应商 供应商A 已更新', time: '2024-01-14 16:45', status: '待审核', statusType: 'warning' },
  { type: '采购', description: '采购订单 PO-2024-002 待审批', time: '2024-01-14 14:20', status: '待审批', statusType: 'info' },
])
</script>

<style scoped lang="scss">
.dashboard-page {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }

      .stat-info {
        margin-left: 16px;

        .stat-value {
          font-size: 28px;
          font-weight: 600;
          color: #333;
        }

        .stat-title {
          font-size: 14px;
          color: #999;
          margin-top: 4px;
        }
      }
    }
  }

  .activity-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .quick-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .action-btn {
      width: 100%;
      justify-content: flex-start;
    }
  }

  .mt-20 {
    margin-top: 20px;
  }
}
</style>
