<template>
  <div class="admin-category">
    <el-card>
      <h2>分类管理</h2>
      <div class="search-bar">
        <el-input v-model="searchName" placeholder="输入分类名称搜索" style="width: 260px" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="openAddDialog">新增分类</el-button>
        <el-button type="danger" plain :disabled="!selectedRows.length" :loading="batchDeleting" @click="handleBatchDelete">
          批量删除{{ selectedRows.length ? `(${selectedRows.length})` : '' }}
        </el-button>
      </div>
      <el-table
        :data="tableData"
        v-loading="loading"
        element-loading-text="加载中…"
        border
        stripe
        style="margin-top:20px;width:100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="46" />
        <el-table-column label="ID" prop="id" width="80" sortable />
        <el-table-column label="分类名称" prop="name" min-width="160" />
        <el-table-column label="排序" prop="sort" width="100" sortable />
        <el-table-column label="状态" prop="status" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '启用' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top:20px;justify-content:flex-end;display:flex"
        @current-change="loadCategoryList"
        @size-change="handleSizeChange"
      />
    </el-card>
    <el-dialog v-model="dialogVisible" title="分类信息" width="480px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model.number="form.sort" placeholder="数字越小越靠前" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="启用" value="启用" />
            <el-option label="禁用" value="禁用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入分类后端接口
import { getCategoryPageApi, addCategoryApi, editCategoryApi, deleteCategoryApi } from '@/api/categoryData.js'
import { useUndo } from '@/composables/useUndo'
const { pushUndo } = useUndo()
const searchName = ref('')
const currentPage = ref(1)
const pageSize = ref(5)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const selectedRows = ref([])
const batchDeleting = ref(false)
const form = ref({
  id: '',
  name: '',
  sort: '',
  status: '启用'
})
// 加载分类列表（请求后端分页接口）
async function loadCategoryList() {
  loading.value = true
  try {
    const res = await getCategoryPageApi(currentPage.value, pageSize.value, searchName.value)
    tableData.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}
// 批量删除
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}
const handleBatchDelete = async () => {
  if (!selectedRows.value.length) return
  const rows = [...selectedRows.value]
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${rows.length} 个分类吗？删除后不可恢复。`, '批量删除', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  batchDeleting.value = true
  try {
    const results = await Promise.allSettled(rows.map((row) => deleteCategoryApi(row.id)))
    const okCount = results.filter((r) => r.status === 'fulfilled' && r.value === true).length
    if (okCount) ElMessage.success(`已删除 ${okCount} 个分类`)
    if (rows.length - okCount > 0) ElMessage.warning(`${rows.length - okCount} 个删除失败`)
    await loadCategoryList()
  } finally {
    batchDeleting.value = false
  }
}
const handleSearch = () => {
  currentPage.value = 1
  loadCategoryList()
}
const handleReset = () => {
  searchName.value = ''
  currentPage.value = 1
  loadCategoryList()
}
const handleSizeChange = () => {
  currentPage.value = 1
  loadCategoryList()
}
const openAddDialog = () => {
  isEdit.value = false
  form.value = {
    name: '',
    sort: '',
    status: '启用'
  }
  dialogVisible.value = true
}
const openEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}
const submitForm = async () => {
  let res
  if (isEdit.value) {
    form.value.id = editId.value
    res = await editCategoryApi(form.value)
  } else {
    res = await addCategoryApi(form.value)
  }
  // 适配后端直接返回布尔值（没有code包装）
  if (res) {
    dialogVisible.value = false
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    loadCategoryList()
  } else {
    ElMessage.error(isEdit.value ? '修改失败' : '新增失败')
  }
}
const handleDelete = (row) => {
  const index = tableData.value.findIndex((item) => item.id === row.id)
  const snapshot = { ...row }
  if (index > -1) tableData.value.splice(index, 1)
  total.value = Math.max(0, total.value - 1)
  pushUndo({
    message: `已删除分类「${row.name}」`,
    onUndo: () => {
      tableData.value.splice(index, 0, snapshot)
      total.value += 1
    },
    onCommit: async () => {
      try {
        const res = await deleteCategoryApi(row.id)
        if (res) {
          ElMessage.success('删除成功')
        } else {
          ElMessage.error('删除失败，该分类可能被景点绑定，无法删除')
        }
      } catch (err) {
        ElMessage.error('删除失败，该分类可能被景点绑定，无法删除')
      }
      loadCategoryList()
    }
  })
}
// 页面挂载自动加载数据
onMounted(() => {
  loadCategoryList()
})
</script>

<style scoped>
.admin-category {
  width: 100%;
}
.admin-category :deep(.el-card__body) {
  width: 100%;
  box-sizing: border-box;
}
.search-bar {
  display: flex;
  gap:12px;
  align-items:center;
  flex-wrap:wrap;
}
</style>