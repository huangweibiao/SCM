<template>
  <div class="report-page">
    <h2 class="page-title">报表分析</h2>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header><span>采购报表</span></template>
          <div class="report-placeholder">
            <el-icon size="60"><DataAnalysis /></el-icon>
            <p>采购报表开发中...</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>销售报表</span></template>
          <div class="report-placeholder">
            <el-icon size="60"><DataAnalysis /></el-icon>
            <p>销售报表开发中...</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header><span>库存报表</span></template>
          <div class="report-placeholder">
            <el-icon size="60"><DataAnalysis /></el-icon>
            <p>库存报表开发中...</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>供应商报表</span></template>
          <div class="report-placeholder">
            <el-icon size="60"><DataAnalysis /></el-icon>
            <p>供应商报表开发中...</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { DataAnalysis } from '@element-plus/icons-vue'
</script>

<style scoped lang="scss">
.report-page {
  .report-placeholder {
    height: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #909399;
    p { margin-top: 10px; }
  }
}
</style>
