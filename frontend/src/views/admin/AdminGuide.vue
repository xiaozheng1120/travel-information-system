<template>
  <div class="admin-guide">
    <el-card>
      <h2>攻略管理</h2>
      <!-- 搜索区域，和截图布局一模一样 -->
      <div class="search-bar" style="margin:16px 0;display:flex;gap:12px;align-items:center;flex-wrap:wrap">
        <el-input v-model="searchTitle" placeholder="输入攻略标题搜索" style="width:260px" />
        <el-select v-model="searchCategory" placeholder="攻略分类" style="width:200px">
          <el-option label="全部分类" value="" />
          <el-option label="自然风光" value="自然风光" />
          <el-option label="古城古镇" value="古城古镇" />
          <el-option label="城市人文" value="城市人文" />
          <el-option label="滨海休闲" value="滨海休闲" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="openAddDialog">新增攻略</el-button>
        <el-button type="danger" plain :disabled="!selectedRows.length" :loading="batchDeleting" @click="handleBatchDelete">
          批量删除{{ selectedRows.length ? `(${selectedRows.length})` : '' }}
        </el-button>
        <el-popover trigger="click" :width="200" placement="bottom-end">
          <template #reference>
            <el-button>列设置</el-button>
          </template>
          <el-checkbox-group v-model="visibleColumns" class="col-setting">
            <el-checkbox label="img">封面</el-checkbox>
            <el-checkbox label="category">分类</el-checkbox>
            <el-checkbox label="author">作者</el-checkbox>
            <el-checkbox label="date">发布时间</el-checkbox>
            <el-checkbox label="views">浏览量</el-checkbox>
            <el-checkbox label="rating">推荐指数</el-checkbox>
            <el-checkbox label="content">攻略内容</el-checkbox>
          </el-checkbox-group>
        </el-popover>
      </div>
      <!-- 表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        element-loading-text="加载中…"
        border
        stripe
        style="width:100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="46" :selectable="rowSelectable" />
        <el-table-column prop="id" label="ID" width="70" sortable />
        <el-table-column v-if="visibleColumns.includes('img')" label="封面" width="90">
          <template #default="scope">
            <el-image
              :src="getGuideImg(scope.row.img)"
              style="width:56px;height:40px;border-radius:6px"
              fit="cover"
              preview
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="攻略标题" min-width="180" sortable />
        <el-table-column v-if="visibleColumns.includes('category')" prop="category" label="分类" width="110" />
        <el-table-column v-if="visibleColumns.includes('author')" prop="author" label="作者" width="100" />
        <el-table-column v-if="visibleColumns.includes('date')" prop="date" label="发布时间" width="170" sortable>
          <template #default="scope">
            {{ formatDate(scope.row.date) }}
          </template>
        </el-table-column>
        <el-table-column v-if="visibleColumns.includes('views')" prop="views" label="浏览量" width="100" sortable />
        <el-table-column v-if="visibleColumns.includes('rating')" prop="rating" label="推荐指数" width="100" sortable />
        <el-table-column label="推荐首页" width="100">
          <template #default="scope">
            <el-switch
              :model-value="Number(scope.row.isRecommend) === 1"
              @change="(val) => toggleRecommend(scope.row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="90">
          <template #default="scope">
            <el-switch
              :model-value="Number(scope.row.isFixed) === 1"
              @change="(val) => toggleFixed(scope.row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column v-if="visibleColumns.includes('content')" label="攻略内容" min-width="220" show-overflow-tooltip>
          <template #default="scope">
            {{ plainText(scope.row.content) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="190" fixed="right">
          <template #default="scope">
            <el-button v-if="!scope.row.isFixed" type="primary" link @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.isFixed" type="info" link disabled>固定</el-button>
            <el-button type="primary" link @click="openComments(scope.row)">评论</el-button>
            <el-button v-if="!scope.row.isFixed" type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div style="margin-top:16px;display:flex;align-items:center;gap:8px">
        <span>Total {{ total }}</span>
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          layout="sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSearch"
        />
      </div>
    </el-card>
    <!-- 新增/编辑弹窗：表单和前台guide.vue发布攻略完全一致 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑攻略' : '新增攻略'" width="860px" top="6vh" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author"></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="自然风光" value="自然风光"></el-option>
            <el-option label="古城古镇" value="古城古镇"></el-option>
            <el-option label="城市人文" value="城市人文"></el-option>
            <el-option label="滨海休闲" value="滨海休闲"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="攻略介绍">
          <RichTextEditor v-model="form.content" placeholder="支持加粗、分点、标题、引用和插入图片，详情页会按富文本渲染" />
        </el-form-item>
        <el-form-item label="推荐首页">
          <el-switch v-model="form.isRecommend" :active-value="1" :inactive-value="0" />
          <span style="margin-left:10px;color:#909399;font-size:12px">开启后前台攻略列表会优先展示</span>
        </el-form-item>
        <el-form-item label="推荐指数">
          <el-input-number v-model="form.rating" :min="0" :max="5" :step="0.1" :precision="1" />
          <span style="margin-left:10px;color:#909399;font-size:12px">详情页显示为 {{ Number(form.rating || 0).toFixed(1) }}</span>
        </el-form-item>
        <el-form-item label="攻略图片">
          <el-radio-group v-model="form.img">
            <el-radio v-for="pic in imgOptions" :key="pic.value" :label="pic.value">
              <img :src="pic.src" style="width:100px;height:60px;object-fit:cover;border-radius:4px;margin-right:6px" />
              {{ pic.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="作者头像">
          <el-radio-group v-model="form.avatar">
            <el-radio v-for="pic in avatarOptions" :key="pic.value" :label="pic.value">
              <img :src="pic.src" style="width:100px;height:60px;object-fit:cover;border-radius:4px;margin-right:6px" />
              {{ pic.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 评论查看抽屉 -->
    <el-drawer v-model="commentDrawer" :title="`评论 · ${commentGuideTitle}`" size="420px">
      <div v-if="commentLoading" class="comment-empty">加载中…</div>
      <div v-else-if="commentList.length === 0" class="comment-empty">这篇攻略还没有评论</div>
      <div v-else class="comment-list">
        <div v-for="item in commentList" :key="item.id" class="comment-item">
          <div class="comment-head">
            <span class="comment-user">{{ item.username || '匿名用户' }}</span>
            <span class="comment-time">{{ formatDate(item.createTime) }}</span>
          </div>
          <p class="comment-content">{{ item.content }}</p>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 图片资源 仅用于弹窗预览，不会存入数据库
import guide1 from '@/assets/images/guide1.jpeg'
import guide2 from '@/assets/images/guide2.jpeg'
import guide3 from '@/assets/images/guide3.jpeg'
import guide4 from '@/assets/images/guide4.jpeg'
import guide5 from '@/assets/images/guide5.jpeg'
import guide6 from '@/assets/images/guide6.jpeg'
import guide7 from '@/assets/images/guide7.jpeg'
import guide8 from '@/assets/images/guide8.jpeg'
import guide9 from '@/assets/images/guide9.png'
import avatar1 from '@/assets/images/avatar1.jpeg'
import avatar2 from '@/assets/images/avatar2.jpeg'
import avatar3 from '@/assets/images/avatar3.jpeg'
import avatar4 from '@/assets/images/avatar4.jpeg'
import avatar5 from '@/assets/images/avatar5.jpeg'
import avatar6 from '@/assets/images/avatar6.jpeg'
import avatar7 from '@/assets/images/avatar7.jpeg'
import avatar8 from '@/assets/images/avatar8.jpeg'
// 导入后端接口（使用你之前写好的guidedata.js）
import { getGuidePageApi, addGuideApi, updateGuideApi, deleteGuideApi } from '@/api/guidedata'
import { getCommentListApi } from '@/api/commentData.js'
import RichTextEditor from '@/components/RichTextEditor.vue'
import { useUndo } from '@/composables/useUndo'
const { pushUndo } = useUndo()

const searchTitle = ref('')
const searchCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])
const batchDeleting = ref(false)
const visibleColumns = ref(['img', 'category', 'author', 'date', 'views', 'rating', 'content'])

// 评论抽屉
const commentDrawer = ref(false)
const commentLoading = ref(false)
const commentList = ref([])
const commentGuideTitle = ref('')

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const form = ref({
  title: '',
  author: '',
  category: '',
  content: '',
  rating: 4.5,
  img: '',
  avatar: '',
  isFixed: 0,
  isRecommend: 0
})

// ！！重点：value是【文件名】存入数据库，src是图片预览用的地址
const imgOptions = [
  { label: 'guide1洪崖洞', value: 'guide1.jpeg', src: guide1 },
  { label: 'guide2阳朔', value: 'guide2.jpeg', src: guide2 },
  { label: 'guide3西安', value: 'guide3.jpeg', src: guide3 },
  { label: 'guide4荔波小七孔', value: 'guide4.jpeg', src: guide4 },
  { label: 'guide5乌镇', value: 'guide5.jpeg', src: guide5 },
  { label: 'guide6大连', value: 'guide6.jpeg', src: guide6 },
  { label: 'guide7鸣沙山', value: 'guide7.jpeg', src: guide7 },
  { label: 'guide8德天瀑布', value: 'guide8.jpeg', src: guide8 },
  { label: 'guide9老君山', value: 'guide9.png', src: guide9 },
]
const avatarOptions = [
  { label: 'avatar1', value: 'avatar1.jpeg', src: avatar1 },
  { label: 'avatar2', value: 'avatar2.jpeg', src: avatar2 },
  { label: 'avatar3', value: 'avatar3.jpeg', src: avatar3 },
  { label: 'avatar4', value: 'avatar4.jpeg', src: avatar4 },
  { label: 'avatar5', value: 'avatar5.jpeg', src: avatar5 },
  { label: 'avatar6', value: 'avatar6.jpeg', src: avatar6 },
  { label: 'avatar7', value: 'avatar7.jpeg', src: avatar7 },
  { label: 'avatar8', value: 'avatar8.jpeg', src: avatar8 },
]

// 时间格式化函数，和前台页面统一
const formatDate = (dateVal) => {
  if(!dateVal) return ''
  const d = new Date(dateVal)
  const year = d.getFullYear()
  const month = String(d.getMonth()+1).padStart(2,'0')
  const day = String(d.getDate()).padStart(2,'0')
  const hour = String(d.getHours()).padStart(2,'0')
  const minute = String(d.getMinutes()).padStart(2,'0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 图片：上传后的 http 地址直接用，老数据是 assets 文件名
const getGuideImg = (img) => {
  if (!img) return ''
  if (img.startsWith('http') || img.startsWith('/')) return img
  return new URL(`../../assets/images/${img}`, import.meta.url).href
}

// 富文本转纯文本，用于表格预览
const plainText = (html) => {
  if (!html) return ''
  return String(html)
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}
// 固定初始化攻略不允许批量删除
const rowSelectable = (row) => !row.isFixed

// 加载后端数据库攻略数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getGuidePageApi(currentPage.value, pageSize.value, {
      title: searchTitle.value,
      category: searchCategory.value
    })
    tableData.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

// 单篇开关：推荐到首页
const toggleRecommend = async (row, val) => {
  try {
    const res = await updateGuideApi({ ...row, isRecommend: val ? 1 : 0 })
    if (res === false) throw new Error('update failed')
    row.isRecommend = val ? 1 : 0
    ElMessage.success(val ? '已推荐到首页' : '已取消推荐')
  } catch (e) {
    ElMessage.error('操作失败，请重试')
  }
}

// 置顶开关：置顶后固定在前台攻略列表前面，且不允许删除
const toggleFixed = async (row, val) => {
  if (val) {
    try {
      await ElMessageBox.confirm(
        `置顶后「${row.title}」会排在攻略列表前面，且不能直接删除。确定置顶吗？`,
        '置顶攻略',
        { type: 'warning', confirmButtonText: '确定置顶', cancelButtonText: '取消' }
      )
    } catch (e) {
      return
    }
  }
  try {
    const res = await updateGuideApi({ ...row, isFixed: val ? 1 : 0 })
    if (res === false) throw new Error('update failed')
    row.isFixed = val ? 1 : 0
    ElMessage.success(val ? '已置顶' : '已取消置顶')
    loadData()
  } catch (e) {
    ElMessage.error('操作失败，请重试')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  const rows = selectedRows.value.filter((row) => !row.isFixed)
  if (!rows.length) {
    ElMessage.warning('请先选择要删除的攻略（固定攻略不可删除）')
    return
  }
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${rows.length} 篇攻略吗？删除后不可恢复。`, '批量删除', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  batchDeleting.value = true
  try {
    const results = await Promise.allSettled(rows.map((row) => deleteGuideApi(row.id)))
    const okCount = results.filter((r) => r.status === 'fulfilled' && r.value === true).length
    const failCount = rows.length - okCount
    if (okCount) ElMessage.success(`已删除 ${okCount} 篇攻略`)
    if (failCount) ElMessage.warning(`${failCount} 篇删除失败`)
    await loadData()
  } finally {
    batchDeleting.value = false
  }
}

// 查看评论
const openComments = async (row) => {
  commentGuideTitle.value = row.title
  commentDrawer.value = true
  commentLoading.value = true
  commentList.value = []
  try {
    const res = await getCommentListApi(row.id)
    commentList.value = res || []
  } catch (e) {
    ElMessage.error('评论加载失败')
  } finally {
    commentLoading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}
const handleReset = () => {
  searchTitle.value = ''
  searchCategory.value = ''
  currentPage.value =1
  loadData()
}
const openAddDialog = ()=>{
  isEdit.value = false
  form.value = {title:'',author:'',category:'',content:'',rating:4.5,img:'',avatar:'',isFixed:0,isRecommend:0}
  dialogVisible.value = true
}
const openEditDialog = (row)=>{
  isEdit.value = true
  editId.value = row.id
  form.value = {...row}
  dialogVisible.value = true
}
const submitForm = async ()=>{
  if(isEdit.value){
    form.value.id = editId.value
    await updateGuideApi(form.value)
    ElMessage.success('修改成功')
  }else{
    form.value.date = new Date()
    await addGuideApi(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}
const handleDelete = (row) => {
  const index = tableData.value.findIndex((item) => item.id === row.id)
  const snapshot = { ...row }
  if (index > -1) tableData.value.splice(index, 1)
  total.value = Math.max(0, total.value - 1)
  pushUndo({
    message: `已删除攻略「${row.title || row.id}」`,
    onUndo: () => {
      tableData.value.splice(index, 0, snapshot)
      total.value += 1
    },
    onCommit: async () => {
      try {
        // 后端校验 isFixed=1 不可删除
        await deleteGuideApi(row.id)
        ElMessage.success('删除成功')
        loadData()
      } catch (err) {
        ElMessage.error('删除失败，固定攻略不可删除')
        loadData()
      }
    }
  })
}

onMounted(()=>{
  loadData()
})
</script>
<style scoped>
.search-bar{
  display:flex;
  align-items:center;
  gap:12px;
  flex-wrap:wrap;
}
.comment-empty {
  padding: 40px 0;
  text-align: center;
  color: #9aa9bd;
  font-size: 13px;
}
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.comment-item {
  padding: 12px 14px;
  border-radius: 10px;
  background: #f8fbff;
  border: 1px solid #e6eef9;
}
.comment-head {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #93a5bb;
  margin-bottom: 6px;
}
.comment-user {
  font-weight: 600;
  color: #2f4b73;
}
.comment-content {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #33475f;
  white-space: pre-wrap;
}
.col-setting {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>