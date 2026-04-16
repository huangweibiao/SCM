<template>
  <div class="dict-page">
    <h2 class="page-title">数据字典</h2>

    <el-tabs v-model="activeTab" type="border-card">
      <!-- 字典类型管理 -->
      <el-tab-pane label="字典类型" name="types">
        <el-card>
          <el-form :inline="true" :model="typeQuery" class="search-form">
            <el-form-item label="类型编码">
              <el-input v-model="typeQuery.typeCode" placeholder="请输入类型编码" clearable />
            </el-form-item>
            <el-form-item label="类型名称">
              <el-input v-model="typeQuery.typeName" placeholder="请输入类型名称" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadTypes">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button type="success" @click="openTypeDialog()">
                <el-icon><Plus /></el-icon>新增
              </el-button>
            </el-form-item>
          </el-form>

          <el-table :data="typeList" border stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="typeCode" label="类型编码" width="150" />
            <el-table-column prop="typeName" label="类型名称" width="150" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '正常' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openTypeDialog(row)">
                  <el-icon><Edit /></el-icon>编辑
                </el-button>
                <el-button type="danger" link size="small" @click="deleteType(row.id)">
                  <el-icon><Delete /></el-icon>删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="typeQuery.pageNum"
            v-model:page-size="typeQuery.pageSize"
            :total="typeTotal"
            layout="total, prev, pager, next"
            style="margin-top: 15px; justify-content: flex-end"
            @current-change="loadTypes"
          />
        </el-card>
      </el-tab-pane>

      <!-- 字典数据管理 -->
      <el-tab-pane label="字典数据" name="data">
        <el-card>
          <el-form :inline="true" :model="dataQuery" class="search-form">
            <el-form-item label="字典类型">
              <el-select v-model="dataQuery.dictTypeId" placeholder="请选择字典类型" clearable>
                <el-option v-for="t in typeList" :key="t.id" :label="t.typeName" :value="t.id" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadData">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button type="success" :disabled="!dataQuery.dictTypeId" @click="openDataDialog()">
                <el-icon><Plus /></el-icon>新增
              </el-button>
            </el-form-item>
          </el-form>

          <el-table :data="dataList" border stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="dictTypeId" label="类型ID" width="100" />
            <el-table-column prop="dictLabel" label="字典标签" width="150" />
            <el-table-column prop="dictValue" label="字典值" width="150" />
            <el-table-column prop="sort" label="排序" width="80" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '正常' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openDataDialog(row)">
                  <el-icon><Edit /></el-icon>编辑
                </el-button>
                <el-button type="danger" link size="small" @click="deleteData(row.id)">
                  <el-icon><Delete /></el-icon>删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="dataQuery.pageNum"
            v-model:page-size="dataQuery.pageSize"
            :total="dataTotal"
            layout="total, prev, pager, next"
            style="margin-top: 15px; justify-content: flex-end"
            @current-change="loadData"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 字典类型对话框 -->
    <el-dialog v-model="typeDialogVisible" :title="typeForm.id ? '编辑字典类型' : '新增字典类型'" width="500px">
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="100px">
        <el-form-item label="类型编码" prop="typeCode">
          <el-input v-model="typeForm.typeCode" placeholder="请输入类型编码" />
        </el-form-item>
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="typeForm.typeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="typeForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="typeForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitType">确定</el-button>
      </template>
    </el-dialog>

    <!-- 字典数据对话框 -->
    <el-dialog v-model="dataDialogVisible" :title="dataForm.id ? '编辑字典数据' : '新增字典数据'" width="500px">
      <el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
        <el-form-item label="字典类型" prop="dictTypeId">
          <el-select v-model="dataForm.dictTypeId" placeholder="请选择字典类型">
            <el-option v-for="t in typeList" :key="t.id" :label="t.typeName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="dataForm.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input v-model="dataForm.dictValue" placeholder="请输入字典值" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="dataForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dataForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitData">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 标签页
const activeTab = ref('types')

// 字典类型查询
const typeQuery = reactive({
  typeCode: '',
  typeName: '',
  pageNum: 1,
  pageSize: 10
})
const typeList = ref<any[]>([])
const typeTotal = ref(0)

// 字典类型表单
const typeDialogVisible = ref(false)
const typeFormRef = ref()
const typeForm = reactive({
  id: null as number | null,
  typeCode: '',
  typeName: '',
  description: '',
  status: 1
})
const typeRules = {
  typeCode: [{ required: true, message: '请输入类型编码', trigger: 'blur' }],
  typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }]
}

// 字典数据查询
const dataQuery = reactive({
  dictTypeId: null as number | null,
  pageNum: 1,
  pageSize: 10
})
const dataList = ref<any[]>([])
const dataTotal = ref(0)

// 字典数据表单
const dataDialogVisible = ref(false)
const dataFormRef = ref()
const dataForm = reactive({
  id: null as number | null,
  dictTypeId: null as number | null,
  dictLabel: '',
  dictValue: '',
  sort: 0,
  status: 1
})
const dataRules = {
  dictTypeId: [{ required: true, message: '请选择字典类型', trigger: 'change' }],
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }]
}

// 加载字典类型
const loadTypes = async () => {
  try {
    const res = await request.get('/system/dict/types', { params: typeQuery })
    if (res.data.code === 200) {
      typeList.value = res.data.data.list || []
      typeTotal.value = res.data.data.total || 0
    }
  } catch (error) {
    console.error('Failed to load types:', error)
  }
}

// 打开字典类型对话框
const openTypeDialog = (row?: any) => {
  if (row) {
    Object.assign(typeForm, row)
  } else {
    typeForm.id = null
    typeForm.typeCode = ''
    typeForm.typeName = ''
    typeForm.description = ''
    typeForm.status = 1
  }
  typeDialogVisible.value = true
}

// 提交字典类型
const submitType = async () => {
  if (!typeFormRef.value) return
  await typeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (typeForm.id) {
          await request.put(`/system/dict/types/${typeForm.id}`, typeForm)
        } else {
          await request.post('/system/dict/types', typeForm)
        }
        ElMessage.success('操作成功')
        typeDialogVisible.value = false
        loadTypes()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

// 删除字典类型
const deleteType = (id: number) => {
  ElMessageBox.confirm('确认删除该字典类型吗?', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/system/dict/types/${id}`)
      ElMessage.success('删除成功')
      loadTypes()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 加载字典数据
const loadData = async () => {
  if (!dataQuery.dictTypeId) {
    dataList.value = []
    return
  }
  try {
    const res = await request.get('/system/dict/data', { params: dataQuery })
    if (res.data.code === 200) {
      dataList.value = res.data.data || []
    }
  } catch (error) {
    console.error('Failed to load data:', error)
  }
}

// 打开字典数据对话框
const openDataDialog = (row?: any) => {
  if (row) {
    Object.assign(dataForm, row)
  } else {
    dataForm.id = null
    dataForm.dictTypeId = dataQuery.dictTypeId
    dataForm.dictLabel = ''
    dataForm.dictValue = ''
    dataForm.sort = 0
    dataForm.status = 1
  }
  dataDialogVisible.value = true
}

// 提交字典数据
const submitData = async () => {
  if (!dataFormRef.value) return
  await dataFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dataForm.id) {
          await request.put(`/system/dict/data/${dataForm.id}`, dataForm)
        } else {
          await request.post('/system/dict/data', dataForm)
        }
        ElMessage.success('操作成功')
        dataDialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

// 删除字典数据
const deleteData = (id: number) => {
  ElMessageBox.confirm('确认删除该字典数据吗?', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/system/dict/data/${id}`)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  loadTypes()
})
</script>

<style scoped lang="scss">
.dict-page {
  .page-title {
    margin-bottom: 20px;
  }

  .search-form {
    margin-bottom: 15px;
  }
}
</style>
