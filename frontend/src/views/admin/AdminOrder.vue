<template>
  <div class="admin-order">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="订单编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="searchForm.orderType" placeholder="全部类型" clearable style="width: 130px">
            <el-option label="门票订单" value="1" />
            <el-option label="住宿订单" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option label="待出行" value="1" />
            <el-option label="已完成" value="2" />
            <el-option label="已取消" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单日期" v-if="advancedVisible">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button link type="primary" @click="advancedVisible = !advancedVisible">
            {{ advancedVisible ? '收起高级筛选' : '高级筛选' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="type-summary">
        <span class="summary-item"><i class="dot ticket"></i>本页门票 {{ typeSummary.ticket }} 笔</span>
        <span class="summary-item"><i class="dot hotel"></i>本页住宿 {{ typeSummary.hotel }} 笔</span>
        <span class="summary-item muted">全部共 {{ page.total }} 笔订单</span>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="tableLoading" element-loading-text="加载中…" border stripe style="width: 100%" empty-text="暂无订单数据">
        <el-table-column label="订单编号" prop="orderNo" min-width="170">
          <template #default="scope">
            <span class="order-no">{{ scope.row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="92">
          <template #default="scope">
            <el-tag :type="orderTypeOf(scope.row) === 2 ? 'warning' : 'primary'" effect="light" size="small">
              {{ orderTypeOf(scope.row) === 2 ? '住宿' : '门票' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" min-width="160">
          <template #default="scope">
            {{ displayName(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="预订人" prop="personName" width="100" />
        <el-table-column label="手机号" prop="phone" width="130" />
        <el-table-column label="数量" width="96">
          <template #default="scope">
            {{ scope.row.count }}{{ orderTypeOf(scope.row) === 2 ? ' 晚' : ' 张' }}
          </template>
        </el-table-column>
        <el-table-column label="出行日期" width="118">
          <template #default="scope">
            {{ formatDate(scope.row.travelDate) || '—' }}
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="112" align="right">
          <template #default="scope">
            <span class="price">¥{{ money(scope.row.totalPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row)" effect="light" size="small">
              {{ getStatusText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="150">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="openDetail(scope.row)">详情</el-button>
            <el-button
              v-if="Number(scope.row.status) === 1"
              type="success"
              link
              size="small"
              @click="handleVerify(scope.row)"
            >
              {{ orderTypeOf(scope.row) === 2 ? '确认完成' : '核销' }}
            </el-button>
            <el-button
              v-if="Number(scope.row.status) === 1"
              type="danger"
              link
              size="small"
              @click="handleCancelOrder(scope.row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :total="page.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadTable"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 订单详情抽屉：门票 / 住宿分别展示 -->
    <el-drawer v-model="drawerVisible" title="订单详情" size="440px" class="order-drawer">
      <div v-if="currentOrder.id" class="detail-body">
        <div class="detail-head">
          <span class="detail-type" :class="orderTypeOf(currentOrder) === 2 ? 'hotel' : 'ticket'">
            {{ orderTypeOf(currentOrder) === 2 ? '住宿订单' : '门票订单' }}
          </span>
          <el-tag :type="getStatusTagType(currentOrder)" effect="light" size="small">
            {{ getStatusText(currentOrder) }}
          </el-tag>
        </div>
        <h3 class="detail-title">{{ displayName(currentOrder) }}</h3>
        <div class="detail-amount">¥{{ money(currentOrder.totalPrice) }}</div>

        <div class="detail-block">
          <p class="block-title">订单信息</p>
          <ul class="detail-list">
            <li><span>订单编号</span><em>{{ currentOrder.orderNo }}</em></li>
            <li><span>订单类型</span><em>{{ orderTypeOf(currentOrder) === 2 ? '酒店住宿' : '景点门票' }}</em></li>
            <li>
              <span>{{ orderTypeOf(currentOrder) === 2 ? '住宿天数' : '购票数量' }}</span>
              <em>{{ currentOrder.count }}{{ orderTypeOf(currentOrder) === 2 ? ' 晚' : ' 张' }}</em>
            </li>
            <li>
              <span>{{ orderTypeOf(currentOrder) === 2 ? '入住日期' : '出行日期' }}</span>
              <em>{{ formatDate(currentOrder.travelDate) || '未指定' }}</em>
            </li>
            <li><span>下单时间</span><em>{{ formatDateTime(currentOrder.createTime) }}</em></li>
          </ul>
        </div>

        <div class="detail-block">
          <p class="block-title">预订人信息</p>
          <ul class="detail-list">
            <li><span>预订人</span><em>{{ currentOrder.personName || '—' }}</em></li>
            <li><span>联系电话</span><em>{{ currentOrder.phone || '—' }}</em></li>
            <li><span>用户 ID</span><em>{{ currentOrder.userId ?? '—' }}</em></li>
          </ul>
        </div>

        <div class="detail-block">
          <p class="block-title">状态流转</p>
          <ul class="timeline">
            <li class="done">
              <i></i>
              <div>
                <p>订单已提交</p>
                <span>{{ formatDateTime(currentOrder.createTime) }}</span>
              </div>
            </li>
            <li :class="{ done: Number(currentOrder.status) === 2, cancel: Number(currentOrder.status) === 3 }">
              <i></i>
              <div>
                <p>{{ Number(currentOrder.status) === 3 ? '订单已取消' : Number(currentOrder.status) === 2 ? (orderTypeOf(currentOrder) === 2 ? '已完成入住' : '已核销完成') : '等待出行' }}</p>
                <span>{{ Number(currentOrder.status) === 1 ? (orderTypeOf(currentOrder) === 2 ? '入住后由管理员确认完成' : '到店后由管理员核销') : '状态已更新' }}</span>
              </div>
            </li>
          </ul>
        </div>

        <div class="detail-actions">
          <el-button
            v-if="Number(currentOrder.status) === 1"
            type="success"
            @click="handleVerify(currentOrder)"
          >
            {{ orderTypeOf(currentOrder) === 2 ? '确认完成入住' : '核销订单' }}
          </el-button>
          <el-button
            v-if="Number(currentOrder.status) === 1"
            type="danger"
            plain
            @click="handleCancelOrder(currentOrder)"
          >
            取消订单
          </el-button>
          <el-button @click="drawerVisible = false">关闭</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPageApi, verifyOrderApi, cancelOrder } from '@/api/orderData.js'
import { getOrderDisplayStatus } from '@/utils/orderStatus.js'
import { useUndo } from '@/composables/useUndo'

const { pushUndo } = useUndo()

const searchForm = reactive({
  orderNo: '',
  orderType: '',
  status: '',
  startDate: '',
  endDate: ''
})
const advancedVisible = ref(false)
const dateRange = ref([])
const page = reactive({ current: 1, size: 10, total: 0 })
const drawerVisible = ref(false)
const currentOrder = ref({})
const tableData = ref([])
const loadedRows = ref([])
const tableLoading = ref(false)

const money = (v) =>
  Number(v || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })

const orderTypeOf = (row) => Number(row.orderType || (row.hotelId ? 2 : 1))
const displayName = (row) => row.hotelName || row.ticketName || '未知商品'

// 按订单类型统计当前页的数量；全量总数看 page.total
const typeSummary = computed(() => {
  const rows = tableData.value
  let hotel = 0
  rows.forEach((r) => {
    if (orderTypeOf(r) === 2) hotel += 1
  })
  return { hotel, ticket: rows.length - hotel }
})

const getStatusText = (row) => getOrderDisplayStatus(row)
const getStatusTagType = (row) => {
  const text = getOrderDisplayStatus(row)
  if (text === '待出行') return 'primary'
  if (text === '已完成') return 'success'
  if (text === '已取消') return 'danger'
  if (text === '已过期') return 'warning'
  return 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return ''
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return String(dateStr)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}`
}

const loadTable = async () => {
  tableLoading.value = true
  try {
    const params = { ...searchForm }
    // 后台“待出行”同样排除已过期订单，避免出现过期单混在待出行里
    if (String(params.status) === '1') {
      params.excludeExpired = true
    }
    const res = await getOrderPageApi(page.current, page.size, params)
    if (res) {
      tableData.value = res.records || []
      loadedRows.value = res.records || []
      page.total = Number(res.total || 0)
    } else {
      tableData.value = []
      loadedRows.value = []
      page.total = 0
    }
  } catch (err) {
    console.error('加载订单列表失败', err)
    tableData.value = []
    page.total = 0
  } finally {
    tableLoading.value = false
  }
}

const handleSearch = () => {
  searchForm.startDate = dateRange.value?.[0] || ''
  searchForm.endDate = dateRange.value?.[1] || ''
  page.current = 1
  loadTable()
}
const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.orderType = ''
  searchForm.status = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
  dateRange.value = []
  page.current = 1
  loadTable()
}
const handleSizeChange = () => {
  page.current = 1
  loadTable()
}

const openDetail = (row) => {
  currentOrder.value = { ...row }
  drawerVisible.value = true
}

const handleVerify = async (row) => {
  const isHotel = orderTypeOf(row) === 2
  try {
    await ElMessageBox.confirm(
      isHotel
        ? `确定将住宿订单【${displayName(row)}】标记为已完成入住？完成后用户无法再取消订单`
        : `确定核销订单【${displayName(row)}】？核销后标记为已完成，用户无法再取消订单`,
      isHotel ? '确认完成' : '订单核销',
      { confirmButtonText: isHotel ? '确认完成' : '确认核销', cancelButtonText: '取消', type: 'success' }
    )
  } catch {
    // 用户点了“取消”，不提示错误
    return
  }
  try {
    const res = await verifyOrderApi(row.id)
    if (res) {
      ElMessage.success(isHotel ? '订单已确认完成！' : '订单核销成功！')
      row.status = 2
      if (currentOrder.value.id === row.id) currentOrder.value.status = 2
      loadTable()
    } else {
      ElMessage.error(isHotel ? '操作失败，仅待出行订单可以确认完成' : '核销失败，仅待出行订单可以核销')
    }
  } catch (err) {
    console.error('订单状态更新失败', err)
    ElMessage.error(isHotel ? '确认失败，请重试' : '核销失败，请重试')
  }
}

const handleCancelOrder = (row) => {
  const prevStatus = row.status
  const inTable = tableData.value.find((r) => r.id === row.id)
  if (inTable) inTable.status = 3
  if (currentOrder.value.id === row.id) {
    drawerVisible.value = false
  }
  pushUndo({
    message: `已取消订单「${displayName(row)}」`,
    onUndo: () => {
      if (inTable) inTable.status = prevStatus
    },
    onCommit: async () => {
      try {
        const res = await cancelOrder(row.id)
        if (res) {
          ElMessage.success('订单取消成功！库存已归还')
        } else {
          ElMessage.error('取消失败，仅待出行订单可取消')
        }
      } catch (err) {
        ElMessage.error('取消失败，请重试')
      }
      loadTable()
    }
  })
}

onMounted(() => {
  loadTable()
})
</script>

<style scoped>
.admin-order {
  padding: 0;
}
.search-card {
  border-radius: 10px;
  border: none;
  margin-bottom: 16px;
}
.search-card :deep(.el-card__body) {
  padding: 16px 22px 12px;
}
.search-form :deep(.el-form-item) {
  margin-bottom: 8px;
}
.type-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-top: 6px;
  border-top: 1px dashed #f3f0e9;
  font-size: 12px;
  color: #6b7280;
}
.summary-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
.summary-item.muted {
  color: #9ca3af;
}
.dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  display: inline-block;
}
.dot.ticket {
  background: #0e7c86;
}
.dot.hotel {
  background: #e6a23c;
}
.table-card {
  border-radius: 10px;
  border: none;
}
.table-card :deep(.el-card__body) {
  padding: 16px 18px 18px;
}
.order-no {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 12px;
  color: #0e7c86;
}
.price {
  font-weight: 700;
  color: #1f2a3d;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 详情抽屉 */
.detail-body {
  padding: 0 4px;
}
.detail-head {
  display: flex;
  align-items: center;
  gap: 8px;
}
.detail-type {
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 11.5px;
  font-weight: 600;
}
.detail-type.ticket {
  background: #ecf5ff;
  color: #0e7c86;
}
.detail-type.hotel {
  background: #fdf6ec;
  color: #b88230;
}
.detail-title {
  margin: 12px 0 4px;
  font-size: 18px;
  color: #1f2a3d;
}
.detail-amount {
  font-size: 22px;
  font-weight: 700;
  color: #0e7c86;
  margin-bottom: 14px;
}
.detail-block {
  margin-top: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fbff;
  border: 1px solid #eaf2fd;
}
.block-title {
  margin: 0 0 10px;
  font-size: 12px;
  font-weight: 700;
  color: #7f93ad;
  letter-spacing: 0.5px;
}
.detail-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.detail-list li {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 6px 0;
  font-size: 13px;
  color: #7f93ad;
}
.detail-list em {
  font-style: normal;
  color: #2f3a4d;
  text-align: right;
  word-break: break-all;
}
.timeline {
  list-style: none;
  margin: 0;
  padding: 0;
}
.timeline li {
  position: relative;
  display: flex;
  gap: 10px;
  padding: 0 0 14px 2px;
}
.timeline li:last-child {
  padding-bottom: 0;
}
.timeline li i {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #d5e3f5;
  margin-top: 3px;
  flex-shrink: 0;
}
.timeline li.done i {
  background: #67c23a;
  box-shadow: 0 0 0 3px rgba(103, 194, 58, 0.14);
}
.timeline li.cancel i {
  background: #f56c6c;
  box-shadow: 0 0 0 3px rgba(245, 108, 108, 0.14);
}
.timeline p {
  margin: 0;
  font-size: 12.5px;
  color: #2f3a4d;
}
.timeline span {
  font-size: 11px;
  color: #9ca3af;
}
.detail-actions {
  margin-top: 18px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
