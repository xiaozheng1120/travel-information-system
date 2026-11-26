<template>
  <div class="admin-banner">
    <el-card shadow="never">
      <h2>轮播图管理</h2>
      <!-- 搜索区域 -->
      <div class="search-bar" style="display:flex;gap:12px;align-items:center;margin:16px 0;">
        <el-input v-model="searchTitle" placeholder="输入轮播标题搜索" style="width:260px"/>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="openAddDialog">新增轮播图</el-button>
        <el-button type="danger" plain :disabled="!selectedRows.length" :loading="batchDeleting" @click="handleBatchDelete">
          批量删除{{ selectedRows.length ? `(${selectedRows.length})` : '' }}
        </el-button>
      </div>
      <!-- 表格 -->
      <el-table
        :data="pageTableData"
        v-loading="loading"
        element-loading-text="加载中…"
        border
        stripe
        style="width:100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="46" />
        <el-table-column label="ID" prop="id" width="80" align="center" sortable />
        <el-table-column label="轮播图片" width="180" align="center">
          <template #default="scope">
            <el-image
              :src="scope.row.img"
              style="width:140px;height:70px;object-fit:cover;border-radius:4px"
              fit="cover"
              preview
            />
          </template>
        </el-table-column>
        <el-table-column label="主标题" prop="subTitle" min-width="160" show-overflow-tooltip/>
        <el-table-column label="描述" prop="desc" min-width="220" show-overflow-tooltip/>
        <el-table-column label="跳转地址" prop="link" min-width="160" show-overflow-tooltip/>
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '启用' ? 'success' : 'info'" size="small">
              {{scope.row.status}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页（对接后端分页，total用后端返回总数） -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        style="margin-top:16px;justify-content:flex-end;display:flex"
        @change="loadData"
      />
    </el-card>
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="轮播图信息" width="620px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="主标题">
          <el-input v-model="form.subTitle" placeholder="请输入轮播图主标题"/>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.desc" type="textarea" :rows="3" placeholder="请输入描述文字"/>
        </el-form-item>
        <el-form-item label="大图地址">
          <el-input v-model="form.img" placeholder="示例：/banner/big/banner5.jpg"/>
        </el-form-item>
        <el-form-item label="缩略图地址">
          <el-input v-model="form.thumb" placeholder="示例：/banner/thumb/thumb5.jpg"/>
        </el-form-item>
        <el-form-item label="跳转路径">
          <el-input v-model="form.link" placeholder="跳转路由，例 /spot，不跳转可留空"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="启用" value="启用"/>
            <el-option label="禁用" value="禁用"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {addBanner,editBanner,delBanner,getBannerPage} from '@/api/bannerData.js'
import { useUndo } from '@/composables/useUndo'
const { pushUndo } = useUndo()
const searchTitle = ref('')
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const pageTableData = ref([])
const loading = ref(false)
const selectedRows = ref([])
const batchDeleting = ref(false)
const dialogVisible = ref(false)
const form = ref({
  id:null,
  subTitle:'',
  desc:'',
  img:'',
  thumb:'',
  link:'',
  status:'启用'
})
// 加载后端分页数据
const loadData = async ()=>{
  loading.value = true
  try {
    const res = await getBannerPage(currentPage.value, pageSize.value, {subTitle:searchTitle.value})
    pageTableData.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}
//页面加载
onMounted(()=>{
  loadData()
})
const handleSearch = ()=>{
  currentPage.value = 1
  loadData()
}
const handleReset = ()=>{
  searchTitle.value = ''
  currentPage.value =1
  loadData()
}
//新增弹窗
const openAddDialog = ()=>{
  form.value = {
    id:null,
    subTitle:'',
    desc:'',
    img:'',
    thumb:'',
    link:'',
    status:'启用'
  }
  dialogVisible.value = true
}
//编辑弹窗
const openEditDialog = (row)=>{
  form.value = {...row}
  dialogVisible.value = true
}
//提交保存
const submitForm = async ()=>{
  if(form.value.id){
    await editBanner(form.value)
    ElMessage.success('编辑成功')
  }else{
    await addBanner({...form.value})
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}
//删除（增加二次确认弹窗，防止误删）
const handleDelete = (target) => {
  const id = typeof target === 'object' ? target.id : target
  const index = pageTableData.value.findIndex((item) => item.id === id)
  const snapshot = index > -1 ? { ...pageTableData.value[index] } : null
  if (index > -1) pageTableData.value.splice(index, 1)
  total.value = Math.max(0, total.value - 1)
  pushUndo({
    message: '已删除 1 条轮播图',
    onUndo: () => {
      if (snapshot) pageTableData.value.splice(index, 0, snapshot)
      total.value += 1
    },
    onCommit: async () => {
      try {
        await delBanner(id)
        ElMessage.success('删除成功')
        loadData()
      } catch (err) {
        ElMessage.error('删除失败，请重试')
        loadData()
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
    await ElMessageBox.confirm(`确定删除选中的 ${rows.length} 张轮播图吗？删除后不可恢复。`, '批量删除', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  batchDeleting.value = true
  try {
    const results = await Promise.allSettled(rows.map((row) => delBanner(row.id)))
    const okCount = results.filter((r) => r.status === 'fulfilled' && r.value === true).length
    if (okCount) ElMessage.success(`已删除 ${okCount} 张轮播图`)
    if (rows.length - okCount > 0) ElMessage.warning(`${rows.length - okCount} 张删除失败`)
    await loadData()
  } finally {
    batchDeleting.value = false
  }
}
</script>
<style scoped>
.admin-banner{
  padding:10px;
}
</style>
