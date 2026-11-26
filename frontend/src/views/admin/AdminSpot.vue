<template>
  <div class="admin-spot">
    <el-card>
      <h2>景点管理</h2>
      <!-- 搜索区域 -->
      <div class="search-bar">
        <el-input v-model="searchName" placeholder="输入景点名称搜索" style="width: 260px" />
        <el-select v-model="searchCategoryId" placeholder="选择分类" style="width:200px">
          <el-option label="全部分类" value="" />
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="openAddDialog">新增景点</el-button>
        <el-button type="danger" plain :disabled="!selectedRows.length" :loading="batchDeleting" @click="handleBatchDelete">
          批量删除{{ selectedRows.length ? `(${selectedRows.length})` : '' }}
        </el-button>
        <el-button type="danger" @click="resetAllSpot">重置全部景点数据</el-button>
        <el-popover trigger="click" :width="200" placement="bottom-end">
          <template #reference>
            <el-button>列设置</el-button>
          </template>
          <el-checkbox-group v-model="visibleColumns" class="col-setting">
            <el-checkbox label="img">图片</el-checkbox>
            <el-checkbox label="area">所在地区</el-checkbox>
            <el-checkbox label="categoryName">景点分类</el-checkbox>
            <el-checkbox label="price">门票价格</el-checkbox>
            <el-checkbox label="level">景点等级</el-checkbox>
            <el-checkbox label="desc">景点简介</el-checkbox>
            <el-checkbox label="status">状态</el-checkbox>
          </el-checkbox-group>
        </el-popover>
        <el-radio-group v-model="viewMode" size="small" style="margin-left:auto">
          <el-radio-button value="table">表格</el-radio-button>
          <el-radio-button value="card">卡片</el-radio-button>
        </el-radio-group>
      </div>
      <!-- 景点表格 -->
      <el-table
        v-if="viewMode === 'table'"
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
        <el-table-column v-if="visibleColumns.includes('img')" label="图片" width="96">
          <template #default="scope">
            <el-image
              v-if="scope.row.img"
              :src="scope.row.img"
              style="width:64px;height:44px;border-radius:6px"
              fit="cover"
              preview
            />
            <span v-else class="no-img">无图</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="景点名称" min-width="140" sortable />
        <el-table-column v-if="visibleColumns.includes('area')" prop="area" label="所在地区" width="130" />
        <el-table-column v-if="visibleColumns.includes('categoryName')" prop="categoryName" label="景点分类" width="120" />
        <el-table-column v-if="visibleColumns.includes('price')" prop="price" label="门票价格" width="120" sortable />
        <el-table-column v-if="visibleColumns.includes('level')" prop="level" label="景点等级" width="110" />
        <el-table-column v-if="visibleColumns.includes('desc')" prop="desc" label="景点简介" show-overflow-tooltip />
        <el-table-column v-if="visibleColumns.includes('status')" prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 卡片视图 -->
      <div v-else class="spot-card-grid" v-loading="loading">
        <div v-for="item in tableData" :key="item.id" class="spot-card">
          <div class="spot-card-cover">
            <el-image v-if="item.img" :src="item.img" fit="cover" style="width:100%;height:100%" />
            <span v-else class="no-img">暂无图片</span>
            <span class="spot-badge">{{ item.level || '景区' }}</span>
          </div>
          <div class="spot-card-body">
            <h3>{{ item.name }}</h3>
            <p class="spot-meta">{{ item.area }} · {{ item.categoryName }}</p>
            <p class="spot-desc">{{ item.desc || '暂无简介' }}</p>
            <div class="spot-card-foot">
              <span class="spot-price">¥{{ item.price ?? '—' }}</span>
              <el-tag :type="item.status === 1 ? 'success' : 'danger'" size="small">
                {{ item.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </div>
            <div class="spot-card-actions">
              <el-button size="small" type="primary" @click="openEdit(item)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(item)">删除</el-button>
            </div>
          </div>
        </div>
        <div v-if="!tableData.length" class="spot-empty">暂无景点数据</div>
      </div>
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
    <el-dialog v-model="dialogVisible" title="景点信息" width="620px">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="所在地区" prop="area">
          <el-input v-model="form.area"></el-input>
        </el-form-item>
        <el-form-item label="景点分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" @change="handleCategoryChange">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <!-- 新增：门票价格 -->
        <el-form-item label="门票价格" prop="price">
          <el-input v-model.number="form.price" type="number" placeholder="请输入门票价格"></el-input>
        </el-form-item>
        <!-- ========== 新增：详情页信息卡字段（前台景点详情展示） ========== -->
        <el-form-item label="开放时间" prop="openTime">
          <el-input v-model="form.openTime" placeholder="例如 08:00 - 18:00 或 全天开放"></el-input>
        </el-form-item>
        <el-form-item label="建议时长" prop="playTime">
          <el-input v-model="form.playTime" placeholder="例如 1-2天 / 3-5小时"></el-input>
        </el-form-item>
        <el-form-item label="景点等级" prop="level">
          <el-select v-model="form.level" placeholder="选择等级">
            <el-option label="5A级景区" value="5A级景区" />
            <el-option label="4A级景区" value="4A级景区" />
            <el-option label="3A级景区" value="3A级景区" />
            <el-option label="暂无评级" value="暂无评级" />
          </el-select>
        </el-form-item>
        <el-form-item label="景点标签" prop="tags">
          <el-input v-model="form.tags" placeholder="多个标签用英文逗号分隔，例如：自然风光,拍照打卡"></el-input>
        </el-form-item>
        <!-- 景点图片上传：真实上传到后端，刷新后仍然有效 -->
        <el-form-item label="景点图片" prop="img">
          <div v-if="form.img" style="margin-bottom:8px;">
            <el-image :src="form.img" style="width:120px;height:120px;border-radius:8px" fit="cover" />
          </div>
          <el-upload
            action="#"
            :auto-upload="true"
            :show-file-list="false"
            :http-request="handleImgUpload"
            accept="image/*"
          >
            <el-button type="primary" plain :loading="uploading">
              <el-icon v-if="!uploading" style="margin-right:4px;"><Plus /></el-icon>
              {{ uploading ? '上传中…' : (form.img ? '重新上传图片' : '选择图片上传') }}
            </el-button>
          </el-upload>
          <div style="width:100%;font-size:12px;color:#94a3b8;line-height:1.7;margin-top:6px;">
            支持 jpg / png / webp，单张不超过 10MB；图片会上传到服务器保存，刷新后不会失效
          </div>
        </el-form-item>
        <el-form-item label="景点简介" prop="desc">
          <el-input v-model="form.desc" type="textarea" rows="4"></el-input>
        </el-form-item>
        <!-- ========== 新增：详情长介绍（description，前台详情页展示） ========== -->
        <el-form-item label="详情长介绍" prop="description">
          <el-input v-model="form.description" type="textarea" rows="5" placeholder="填写详情页展示的长篇景点介绍"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
import { Plus } from '@element-plus/icons-vue'
// 导入后端接口函数
import { getSpotListApi, addSpotApi, updateSpotApi, deleteSpotApi } from '@/api/spotData.js'
// 导入新接口 getCategoryEnableAllApi
import { getCategoryListApi, getCategoryEnableAllApi } from '@/api/categoryData.js'
// 导入图片上传接口
import { uploadImageApi } from '@/api/uploadData.js'
import { useUndo } from '@/composables/useUndo'
const { pushUndo } = useUndo()
// 搜索条件
const searchName = ref('')
const searchCategoryId = ref('')
// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
// 表格数据
const tableData = ref([])
// 加载状态 & 批量删除
const loading = ref(false)
const selectedRows = ref([])
const batchDeleting = ref(false)
const viewMode = ref('table')
const visibleColumns = ref(['img', 'area', 'categoryName', 'price', 'level', 'desc', 'status'])
// 分类下拉数据
const categoryList = ref([])
//弹窗
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const form = ref({
  id: '',
  name: '',
  area: '',
  categoryId: '',
  categoryName: '',
  price: '',
  openTime: '',
  playTime: '',
  level: '',
  tags: '',
  img: '',
  desc: '',
  description: '', // 新增详情长介绍字段
  status: 1
})
// 图片选择后立即上传到后端，表单里只保存服务器返回的图片地址
const uploading = ref(false)
async function handleImgUpload(options) {
  if (!options || !options.file) return
  uploading.value = true
  try {
    const res = await uploadImageApi(options.file)
    if (res && res.url) {
      form.value.img = res.url
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error((res && res.msg) || '图片上传失败，请重试')
    }
  } catch (e) {
    ElMessage.error('图片上传失败，请检查后端是否已启动上传接口')
  } finally {
    uploading.value = false
  }
}
// 分类下拉选中回调，自动回填分类名称
const handleCategoryChange = (cateId) => {
  const cateItem = categoryList.value.find(c => c.id === cateId)
  if(cateItem){
    form.value.categoryName = cateItem.name
  }else{
    form.value.categoryName = ''
  }
}
// 加载景点列表，带上搜索参数传给后端
async function loadSpotList() {
  loading.value = true
  try {
    const res = await getSpotListApi(currentPage.value, pageSize.value, searchName.value, searchCategoryId.value)
    tableData.value = res.records || []
    totalCount.value = res.total || 0
  } finally {
    loading.value = false
  }
}
// 【修改】加载分类下拉，使用新接口 getCategoryEnableAllApi，只获取启用分类
async function loadCategoryList() {
  const res = await getCategoryEnableAllApi()
  console.log('分类返回', res)
  // 后端直接返回数组，不需要取 res.data
  categoryList.value = res
}
// 查询
const handleSearch = () => {
  currentPage.value = 1
  loadSpotList()
}
const handleReset = () => {
  searchName.value = ''
  searchCategoryId.value = ''
  currentPage.value = 1
  loadSpotList()
}
// 重置全部景点（提示：对接后端后这个是演示按钮，你可以后续看需求保留/删除）
const resetAllSpot = () => {
  ElMessage.warning('该功能已切换后端，重置数据请在数据库操作！')
}
// 打开新增弹窗，初始化带上description
const openAddDialog = () => {
  isEdit.value = false
  form.value = {
    id: '',
    name: '',
    area: '',
    categoryId: '',
    categoryName: '',
    price: '',
    openTime: '',
    playTime: '',
    level: '',
    tags: '',
    img: '',
    desc: '',
    description: '', //新增
    status: 1
  }
  dialogVisible.value = true
}
// 打开编辑弹窗，自动把row里面的description回显到表单
const openEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}
// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    // 编辑
    await updateSpotApi(form.value)
    ElMessage.success('修改成功')
  } else {
    // 新增
    await addSpotApi(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadSpotList()
}
// 删除景点
const handleDelete = (row) => {
  const index = tableData.value.findIndex((item) => item.id === row.id)
  const snapshot = { ...row }
  if (index > -1) tableData.value.splice(index, 1)
  totalCount.value = Math.max(0, totalCount.value - 1)
  pushUndo({
    message: `已删除景点「${row.name}」`,
    onUndo: () => {
      tableData.value.splice(index, 0, snapshot)
      totalCount.value += 1
    },
    onCommit: async () => {
      try {
        await deleteSpotApi(row.id)
        ElMessage.success('删除成功')
        loadSpotList()
      } catch (err) {
        ElMessage.error('删除失败，请重试')
        loadSpotList()
      }
    }
  })
}
// 批量删除景点
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}
const handleBatchDelete = async () => {
  if (!selectedRows.value.length) return
  const rows = [...selectedRows.value]
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${rows.length} 个景点吗？删除后不可恢复。`, '批量删除', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  batchDeleting.value = true
  try {
    const results = await Promise.allSettled(rows.map((row) => deleteSpotApi(row.id)))
    const okCount = results.filter((r) => r.status === 'fulfilled' && r.value === true).length
    if (okCount) ElMessage.success(`已删除 ${okCount} 个景点`)
    if (rows.length - okCount > 0) ElMessage.warning(`${rows.length - okCount} 个删除失败`)
    await loadSpotList()
  } finally {
    batchDeleting.value = false
  }
}
// 页面初始化加载数据
onMounted(() => {
  loadSpotList()
  loadCategoryList()
})
// 监听分页变化，页码或每页条数改变自动刷新
watch(currentPage, ()=> loadSpotList())
watch(pageSize, ()=> loadSpotList())
</script>

<style scoped>
.admin-spot{
  padding:16px;
}
.search-bar{
  display:flex;
  gap:12px;
  align-items:center;
  flex-wrap:wrap;
}
.no-img {
  font-size: 12px;
  color: #a8b6c8;
}
.col-setting {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.spot-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
  gap: 18px;
  margin-top: 20px;
}
.spot-card {
  border: 1px solid #e7edf5;
  border-radius: 14px;
  overflow: hidden;
  background: #fff;
  transition: box-shadow .2s;
}
.spot-card:hover {
  box-shadow: 0 8px 24px rgba(31, 59, 95, .12);
}
.spot-card-cover {
  position: relative;
  height: 130px;
  background: #f3f6fb;
  display: flex;
  align-items: center;
  justify-content: center;
}
.spot-badge {
  position: absolute;
  left: 10px;
  top: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  background: rgba(0, 0, 0, .45);
  color: #fff;
  font-size: 12px;
}
.spot-card-body {
  padding: 12px 14px 14px;
}
.spot-card-body h3 {
  margin: 0 0 6px;
  font-size: 15px;
  color: #22344c;
}
.spot-meta {
  margin: 0 0 6px;
  font-size: 12px;
  color: #7d8fa8;
}
.spot-desc {
  margin: 0 0 10px;
  font-size: 12px;
  color: #8b9bb0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 36px;
}
.spot-card-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.spot-price {
  color: #f56c6c;
  font-weight: 700;
  font-size: 16px;
}
.spot-card-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
.spot-empty {
  grid-column: 1 / -1;
  text-align: center;
  color: #9aa9bd;
  padding: 40px 0;
}
</style>
