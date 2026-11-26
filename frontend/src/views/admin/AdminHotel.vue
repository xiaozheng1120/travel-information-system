<template>
  <div class="admin-hotel">
    <el-card>
      <h2>住宿管理</h2>
      <!-- 搜索区域 -->
      <div class="search-bar">
        <el-input v-model="searchName" placeholder="输入酒店名称搜索" style="width: 260px" />
        <el-select v-model="searchTypeId" placeholder="选择酒店类型" style="width:200px">
          <el-option label="全部类型" :value="null" />
          <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="openAddDialog">新增酒店</el-button>
        <el-button type="danger" plain :disabled="!selectedRows.length" :loading="batchDeleting" @click="handleBatchDelete">
          批量删除{{ selectedRows.length ? `(${selectedRows.length})` : '' }}
        </el-button>
        <el-popover trigger="click" :width="200" placement="bottom-end">
          <template #reference>
            <el-button>列设置</el-button>
          </template>
          <el-checkbox-group v-model="visibleColumns" class="col-setting">
            <el-checkbox label="type">酒店类型</el-checkbox>
            <el-checkbox label="address">地址</el-checkbox>
            <el-checkbox label="priceRange">价格</el-checkbox>
            <el-checkbox label="img">图片</el-checkbox>
            <el-checkbox label="desc">简介</el-checkbox>
          </el-checkbox-group>
        </el-popover>
        <el-radio-group v-model="viewMode" size="small" style="margin-left:auto">
          <el-radio-button value="table">表格</el-radio-button>
          <el-radio-button value="card">卡片</el-radio-button>
        </el-radio-group>
      </div>
      <!-- 表格 -->
      <el-table
        v-if="viewMode === 'table'"
        :data="tableData"
        v-loading="loading"
        element-loading-text="加载中…"
        border
        stripe
        style="margin-top:20px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="46" />
        <el-table-column label="ID" prop="id" width="80" sortable />
        <el-table-column label="酒店名称" prop="name" min-width="150" />
        <el-table-column v-if="visibleColumns.includes('type')" label="酒店类型" prop="type" width="120" />
        <el-table-column v-if="visibleColumns.includes('address')" label="地址" prop="address" min-width="160" />
        <el-table-column v-if="visibleColumns.includes('priceRange')" label="价格(元/晚)" prop="priceRange" width="120" />
        <el-table-column v-if="visibleColumns.includes('img')" label="图片" prop="img">
          <template #default="scope">
            <el-image
              :src="scope.row.img"
              style="width:80px;height:50px;object-fit:cover"
              fit="cover"
              preview
            />
          </template>
        </el-table-column>
        <el-table-column v-if="visibleColumns.includes('desc')" label="简介" prop="desc" show-overflow-tooltip />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 卡片视图 -->
      <div v-else class="hotel-card-grid" v-loading="loading">
        <div v-for="item in tableData" :key="item.id" class="hotel-card">
          <div class="hotel-card-cover">
            <el-image v-if="item.img" :src="item.img" fit="cover" style="width:100%;height:100%" />
            <span v-else>暂无图片</span>
            <span class="hotel-type">{{ item.type || '住宿' }}</span>
          </div>
          <div class="hotel-card-body">
            <h3>{{ item.name }}</h3>
            <p class="hotel-addr">{{ item.address }}</p>
            <p class="hotel-desc">{{ item.desc || '暂无简介' }}</p>
            <div class="hotel-card-foot">
              <span class="hotel-price">{{ item.priceRange || '价格待定' }}</span>
            </div>
            <div class="hotel-card-actions">
              <el-button size="small" type="primary" @click="openEditDialog(item)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(item.id)">删除</el-button>
            </div>
          </div>
        </div>
        <div v-if="!tableData.length" class="hotel-empty">暂无住宿数据</div>
      </div>
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top:20px;justify-content:flex-end;display:flex"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="酒店信息" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="酒店名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入酒店名称" />
        </el-form-item>
        <el-form-item label="酒店类型" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择">
            <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属景点" prop="nearSpot">
          <el-input v-model="form.nearSpot" placeholder="例如：张家界" />
        </el-form-item>
        <el-form-item label="距离" prop="distance">
          <el-input v-model="form.distance" placeholder="例如：0.3公里" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="价格区间" prop="priceRange">
          <el-input v-model="form.priceRange" placeholder="例如：260-480" />
        </el-form-item>
        <el-form-item label="评分" prop="score">
          <el-input v-model.number="form.score" placeholder="0-5分" />
        </el-form-item>
        <el-form-item label="图片路径" prop="img">
          <el-input v-model="form.img" placeholder="例如 /h1.jpeg" />
        </el-form-item>
        <el-form-item label="酒店简介" prop="desc">
          <el-input v-model="form.desc" type="textarea" rows="3" placeholder="酒店介绍" />
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
// 后端真实接口
import { getHotelPageApi, addHotel, editHotel, deleteHotel, typeList } from '@/api/hotelData.js'
import { useUndo } from '@/composables/useUndo'
const { pushUndo } = useUndo()

const searchName = ref('')
// ✅关键修改：初始值改为null，【全部类型】选项value绑定null，不要空字符串
const searchTypeId = ref(null)
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])
const batchDeleting = ref(false)
const viewMode = ref('table')
const visibleColumns = ref(['type', 'address', 'priceRange', 'img', 'desc'])

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)

// 表单字段
const form = ref({
  id: '',
  name: '',
  typeId: '',
  type: '',
  nearSpot: '',
  distance: '',
  address: '',
  priceRange: '',
  score: '',
  img: '',
  desc: ''
})

// 请求后端分页列表
async function loadData() {
  loading.value = true
  try {
    const query = {}
    if(searchName.value) query.name = searchName.value
    // searchTypeId为null的时候不传递typeId参数，匹配mapper <if test="hotel.typeId != null">
    if(searchTypeId.value !== null){
      query.typeId = searchTypeId.value
    }
    const res = await getHotelPageApi(currentPage.value, pageSize.value, query)
    tableData.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  currentPage.value = 1
  loadData()
}
// 重置：重置为null，代表全部类型
const handleReset = () => {
  searchName.value = ''
  searchTypeId.value = null
  currentPage.value = 1
  loadData()
}

// 新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  editId.value = null
  form.value = {
    id: null,
    name: '',
    typeId: '',
    type: '',
    nearSpot: '',
    distance: '',
    address: '',
    priceRange: '',
    score: '',
    img: '',
    desc: ''
  }
  dialogVisible.value = true
}

// 编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  // 根据typeId回填type文字
  const typeItem = typeList.find(t => t.id === form.value.typeId)
  form.value.type = typeItem?.name || ''

  if (isEdit.value) {
    form.value.id = editId.value
    await editHotel(form.value)
    ElMessage.success('修改成功')
  } else {
    await addHotel(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData() // 操作完成刷新表格
}

// 删除
const handleDelete = (target) => {
  const id = typeof target === 'object' ? target.id : target
  const index = tableData.value.findIndex((item) => item.id === id)
  const snapshot = index > -1 ? { ...tableData.value[index] } : null
  if (index > -1) tableData.value.splice(index, 1)
  total.value = Math.max(0, total.value - 1)
  pushUndo({
    message: snapshot ? `已删除住宿「${snapshot.name}」` : '已删除 1 条住宿',
    onUndo: () => {
      if (snapshot) tableData.value.splice(index, 0, snapshot)
      total.value += 1
    },
    onCommit: async () => {
      try {
        await deleteHotel(id)
        ElMessage.success('删除成功')
      } catch (err) {
        ElMessage.error('删除失败，请重试')
      }
      loadData()
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
    await ElMessageBox.confirm(`确定删除选中的 ${rows.length} 条住宿吗？删除后不可恢复。`, '批量删除', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  batchDeleting.value = true
  try {
    const results = await Promise.allSettled(rows.map((row) => deleteHotel(row.id)))
    const okCount = results.filter((r) => r.status === 'fulfilled' && r.value === true).length
    if (okCount) ElMessage.success(`已删除 ${okCount} 条住宿`)
    if (rows.length - okCount > 0) ElMessage.warning(`${rows.length - okCount} 条删除失败`)
    await loadData()
  } finally {
    batchDeleting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.search-bar {
  display: flex;
  gap:12px;
  align-items:center;
  flex-wrap:wrap;
}
.hotel-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
  margin-top: 20px;
}
.hotel-card {
  border: 1px solid #e7edf5;
  border-radius: 14px;
  overflow: hidden;
  background: #fff;
  transition: box-shadow .2s;
}
.hotel-card:hover {
  box-shadow: 0 8px 24px rgba(31, 59, 95, .12);
}
.hotel-card-cover {
  position: relative;
  height: 130px;
  background: #f3f6fb;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a8b6c8;
  font-size: 12px;
}
.hotel-type {
  position: absolute;
  left: 10px;
  top: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  background: rgba(0, 0, 0, .45);
  color: #fff;
  font-size: 12px;
}
.hotel-card-body {
  padding: 12px 14px 14px;
}
.hotel-card-body h3 {
  margin: 0 0 6px;
  font-size: 15px;
  color: #22344c;
}
.hotel-addr {
  margin: 0 0 6px;
  font-size: 12px;
  color: #7d8fa8;
}
.hotel-desc {
  margin: 0 0 10px;
  min-height: 36px;
  font-size: 12px;
  color: #8b9bb0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.hotel-price {
  color: #f56c6c;
  font-weight: 700;
  font-size: 15px;
}
.hotel-card-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
.hotel-empty {
  grid-column: 1 / -1;
  text-align: center;
  color: #9aa9bd;
  padding: 40px 0;
}
.col-setting {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>