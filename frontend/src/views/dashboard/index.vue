<template>
  <div class="dashboard">
    <h2 class="page-title">首页</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #409EFF">
              <el-icon size="30"><Box /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">1,280</div>
              <div class="stats-label">物料总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #67C23A">
              <el-icon size="30"><OfficeBuilding /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">12</div>
              <div class="stats-label">仓库数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #E6A23C">
              <el-icon size="30"><Supplier /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">85</div>
              <div class="stats-label">供应商数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon" style="background: #F56C6C">
              <el-icon size="30"><WarningFilled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">5</div>
              <div class="stats-label">库存预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据展示 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>待处理业务</span>
          </template>
          <el-table :data="pendingBusiness" style="width: 100%">
            <el-table-column prop="type" label="业务类型" width="120" />
            <el-table-column prop="count" label="数量" />
            <el-table-column prop="status" label="状态" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>库存预警</span>
          </template>
          <el-table :data="inventoryWarnings" style="width: 100%">
            <el-table-column prop="itemCode" label="物料编码" width="120" />
            <el-table-column prop="itemName" label="物料名称" />
            <el-table-column prop="availableQty" label="可用库存" />
            <el-table-column prop="warnType" label="预警类型">
              <template #default="{ row }">
                <el-tag :type="row.warnType === 1 ? 'danger' : 'warning'">
                  {{ row.warnType === 1 ? '低库存' : '超库存' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Box, OfficeBuilding, WarningFilled } from '@element-plus/icons-vue'

const pendingBusiness = ref([
  { type: '采购订单', count: 5, status: '待审核' },
  { type: '销售订单', count: 3, status: '待审核' },
  { type: '入库单', count: 8, status: '待确认' },
  { type: '出库单', count: 6, status: '待确认' }
])

const inventoryWarnings = ref([])
</script>

<style scoped lang="scss">
.dashboard {
  .stats-row {
    margin-bottom: 20px;
  }

  .stats-card {
    .stats-content {
      display: flex;
      align-items: center;
      gap: 20px;

      .stats-icon {
        width: 60px;
        height: 60px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }

      .stats-info {
        .stats-value {
          font-size: 28px;
          font-weight: 600;
          color: #303133;
        }

        .stats-label {
          font-size: 14px;
          color: #909399;
          margin-top: 5px;
        }
      }
    }
  }
}
</style>
