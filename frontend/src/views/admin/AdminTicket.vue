<template>
  <div class="admin-ticket">
    <el-card>
      <h2>门票管理</h2>
      <!-- 搜索区域 -->
      <div class="search-bar">
        <el-input v-model="searchName" placeholder="输入门票名称搜索" style="width: 260px" />
        <el-select v-model="searchSpot" placeholder="选择景点" style="width:200px">
          <el-option label="全部景点" value="" />
          <el-option v-for="item in spotList" :key="item.id" :label="item.name" :value="item.name" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="openAddDialog">新增门票</el-button>
        <el-button type="danger" plain :disabled="!selectedRows.length" :loading="batchDeleting" @click="handleBatchDelete">
          批量删除{{ selectedRows.length ? `(${selectedRows.length})` : '' }}
        </el-button>
        <el-popover trigger="click" :width="200" placement="bottom-end">
          <template #reference>
            <el-button>列设置</el-button>
          </template>
          <el-checkbox-group v-model="visibleColumns" class="col-setting">
            <el-checkbox label="spot">所属景点</el-checkbox>
            <el-checkbox label="type">票种</el-checkbox>
            <el-checkbox label="priceNow">售价</el-checkbox>
            <el-checkbox label="stock">库存</el-checkbox>
            <el-checkbox label="desc">简介</el-checkbox>
          </el-checkbox-group>
        </el-popover>
      </div>
      <!-- 门票表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        element-loading-text="加载中…"
        border
        stripe
        style="width:100%;margin-top:20px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="46" />
        <el-table-column prop="id" label="ID" width="70" sortable />
        <el-table-column v-if="visibleColumns.includes('spot')" prop="spot" label="所属景点" min-width="140" />
        <el-table-column prop="name" label="门票名称" min-width="160" />
        <el-table-column v-if="visibleColumns.includes('type')" prop="type" label="票种" width="90" />
        <el-table-column v-if="visibleColumns.includes('priceNow')" prop="priceNow" label="售价(元)" width="100" sortable />
        <el-table-column v-if="visibleColumns.includes('stock')" prop="stock" label="剩余库存" width="100" sortable />
        <el-table-column v-if="visibleColumns.includes('desc')" prop="desc" label="简介" show-overflow-tooltip />
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalCount"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top:20px;justify-content:flex-end;"
      />
    </el-card>
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="门票信息" width="620px">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="所属景点" prop="spot">
          <el-select v-model="form.spot" placeholder="选择景点">
            <el-option v-for="item in spotList" :key="item.id" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="门票名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="票种类型" prop="type">
          <el-input v-model="form.type"></el-input>
        </el-form-item>
        <el-form-item label="现价" prop="priceNow">
          <el-input v-model.number="form.priceNow"></el-input>
        </el-form-item>
        <el-form-item label="原价" prop="priceOld">
          <el-input v-model.number="form.priceOld"></el-input>
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <NumberStepper v-model="form.stock" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="有效期说明" prop="validDesc">
          <el-input v-model="form.validDesc"></el-input>
        </el-form-item>
        <el-form-item label="门票简介" prop="desc">
          <el-input v-model="form.desc" type="textarea" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTicketPageApi, addTicketApi, updateTicketApi, deleteTicketApi, spotList } from '@/api/ticketData.js'
import NumberStepper from '@/components/NumberStepper.vue'
import { useUndo } from '@/composables/useUndo'
const { pushUndo } = useUndo()
const searchName = ref('')
const searchSpot = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])
const batchDeleting = ref(false)
const visibleColumns = ref(['spot', 'type', 'priceNow', 'stock', 'desc'])
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const form = ref({
  id: '',
  spot: '',
  name: '',
  type: '',
  priceNow: '',
  priceOld: '',
  stock: 0,
  validDesc: '',
  desc: ''
})
// 加载门票分页数据
const loadTableData = async () => {
  loading.value = true
  try {
    const res = await getTicketPageApi(currentPage.value, pageSize.value, {
      name: searchName.value,
      spot: searchSpot.value
    })
    tableData.value = res.records || []
    totalCount.value = res.total || 0
  } finally {
    loading.value = false
  }
}
const handleSearch = () => {
  currentPage.value = 1
  loadTableData()
}
const handleReset = () => {
  searchName.value = ''
  searchSpot.value = ''
  currentPage.value = 1
  loadTableData()
}
// 新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  form.value = {
    id: '',
    spot: '',
    name: '',
    type: '',
    priceNow: '',
    priceOld: '',
    stock: 0,
    validDesc: '',
    desc: ''
  }
  dialogVisible.value = true
}
// 编辑弹窗
const openEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}
// 提交保存（调用后端接口）
const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateTicketApi(form.value)
    ElMessage.success('修改成功')
  } else {
    await addTicketApi(form.value)
    ElMessage.success('新增成功')
    //新增成功，重置页码到第一页
    currentPage.value = 1
  }
  dialogVisible.value = false
  loadTableData()
}
// 删除
const handleDelete = (row) => {
  // 直接从列表移除，底部给「撤销」条，5 秒后真正删除
  const index = tableData.value.findIndex((item) => item.id === row.id)
  const snapshot = { ...row }
  if (index > -1) tableData.value.splice(index, 1)
  totalCount.value = Math.max(0, totalCount.value - 1)
  pushUndo({
    message: `已删除门票「${row.name}」`,
    onUndo: () => {
      tableData.value.splice(index, 0, snapshot)
      totalCount.value += 1
    },
    onCommit: async () => {
      try {
        await deleteTicketApi(row.id)
        ElMessage.success('删除成功')
        loadTableData()
      } catch (err) {
        ElMessage.error('删除失败，请重试')
        loadTableData()
      }
    }
  })
}
// 批量删除
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}
const handleBatchDelete = async () => {
  if (!selectedRows.value.length) return
  const rows = [...selectedRows.value]
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${rows.length} 张门票吗？删除后不可恢复。`, '批量删除', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  batchDeleting.value = true
  try {
    const results = await Promise.allSettled(rows.map((row) => deleteTicketApi(row.id)))
    const okCount = results.filter((r) => r.status === 'fulfilled' && r.value === true).length
    if (okCount) ElMessage.success(`已删除 ${okCount} 张门票`)
    if (rows.length - okCount > 0) ElMessage.warning(`${rows.length - okCount} 张删除失败`)
    await loadTableData()
  } finally {
    batchDeleting.value = false
  }
}
// 分页切换
const pageChange = () => {
  loadTableData()
}
// 监听页码、页大小变化
watch(currentPage, pageChange)
watch(pageSize, pageChange)
onMounted(async () => {
  await loadTableData()
})
</script>
<style scoped>
.admin-ticket{
  padding:16px;
}
.search-bar{
  display:flex;
  gap:12px;
  align-items:center;
  flex-wrap:wrap;
}
.col-setting {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>
