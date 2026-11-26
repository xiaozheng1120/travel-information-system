<template>
  <div class="admin-home">
    <!-- 欢迎条 -->
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-inner">
        <div class="welcome-left">
          <img src="/admin-avatar.jpeg" alt="管理员头像" class="welcome-avatar" />
          <div class="welcome-text">
            <p class="title">欢迎回来，{{ loginUser.name || loginUser.username || 'admin' }}</p>
            <p class="desc">
              <el-icon size="13"><Clock /></el-icon>
              {{ currentTime }} · 今天也要好好运营这座旅游小站
            </p>
          </div>
        </div>
        <div class="welcome-right">
          <span class="role-tag">{{ loginUser.role || '管理员' }}</span>
          <button type="button" class="refresh-btn" :class="{ loading: loading }" @click="loadDashboard">
            刷新数据
          </button>
        </div>
      </div>
    </el-card>

    <!-- KPI 卡片 -->
    <div class="kpi-grid">
      <el-card v-for="item in kpis" :key="item.label" shadow="never" class="kpi-card">
        <div class="kpi-top">
          <span class="kpi-label">{{ item.label }}</span>
          <span class="kpi-icon" :style="{ background: item.bg, color: item.color }">
            <el-icon size="17"><component :is="item.icon" /></el-icon>
          </span>
        </div>
        <div class="kpi-num">{{ item.prefix }}{{ item.value }}</div>
        <div class="kpi-foot">{{ item.foot }}</div>
      </el-card>
    </div>

    <!-- 图表：趋势 + 状态占比 -->
    <el-row :gutter="14" class="chart-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="never" class="chart-card">
          <div class="chart-head">
            <div>
              <h2>订单趋势</h2>
              <p class="chart-sub">订单量与营收随时间的变化</p>
            </div>
            <div class="range-switch">
              <button
                v-for="opt in rangeOptions"
                :key="opt.value"
                type="button"
                class="range-btn"
                :class="{ active: orderRange === opt.value }"
                @click="changeRange(opt.value)"
              >
                {{ opt.label }}
              </button>
            </div>
          </div>
          <div ref="trendRef" class="chart-box trend"></div>
          <div v-if="!trendHasData" class="chart-empty">这段时间还没有订单数据</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="chart-card">
          <div class="chart-head">
            <div>
              <h2>订单状态占比</h2>
              <p class="chart-sub">待出行 / 已完成 / 已取消</p>
            </div>
          </div>
          <div ref="statusRef" class="chart-box status"></div>
          <div v-if="!statusHasData" class="chart-empty">暂无订单状态数据</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表：热门景点 + 最近订单 -->
    <el-row :gutter="14" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <div class="chart-head">
            <div>
              <h2>热门景点 Top5</h2>
              <p class="chart-sub">按下单数量统计</p>
            </div>
          </div>
          <div ref="topRef" class="chart-box top"></div>
          <div v-if="!topHasData" class="chart-empty">还没有景点订单，先去前台下个单吧</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card recent-card">
          <div class="chart-head">
            <div>
              <h2>最近订单</h2>
              <p class="chart-sub">最新 5 笔订单</p>
            </div>
            <button type="button" class="more-link" @click="$router.push('/admin/order')">全部订单 →</button>
          </div>
          <div v-if="recentOrders.length === 0" class="chart-empty small">暂无订单</div>
          <ul v-else class="recent-list">
            <li v-for="row in recentOrders" :key="row.id" class="recent-item">
              <span class="type-dot" :class="orderTypeOf(row) === 2 ? 'hotel' : 'ticket'"></span>
              <div class="recent-main">
                <p class="recent-name">{{ displayName(row) }}</p>
                <p class="recent-meta">{{ row.personName || '游客' }} · {{ shortTime(row.createTime) }}</p>
              </div>
              <div class="recent-right">
                <p class="recent-price">¥{{ money(row.totalPrice) }}</p>
                <span class="status-pill" :class="statusClass(row.status)">{{ statusText(row.status) }}</span>
              </div>
            </li>
          </ul>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能入口 -->
    <div class="card-grid">
      <el-card
        v-for="item in funcs"
        :key="item.path"
        shadow="never"
        class="func-card"
        @click="$router.push(item.path)"
      >
        <div class="icon-wrap" :style="{ background: item.bg }">
          <el-icon size="24"><component :is="item.icon" /></el-icon>
        </div>
        <p class="card-title">{{ item.title }}</p>
        <p class="card-desc">{{ item.desc }}</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import {
  User,
  Clock,
  MapLocation,
  Document,
  Ticket,
  Picture,
  Menu,
  DocumentCopy,
  OfficeBuilding,
  Money,
  AlarmClock
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getUserListApi } from '../../api/userData.js'
import { getSpotCountApi } from '../../api/spotData.js'
import { getGuidePageApi } from '../../api/guidedata'
import { getOrderPageApi } from '@/api/orderData.js'
import { getTicketPageApi } from '@/api/ticketData.js'

const loginUser = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))
const currentTime = ref('')
const loading = ref(false)

const userList = ref([])
const orders = ref([])
const orderTotal = ref(0)
const ticketRows = ref([])
const totalSpot = ref(0)
const totalGuide = ref(0)

const orderRange = ref(7)
const rangeOptions = [
  { label: '近 7 天', value: 7 },
  { label: '近 30 天', value: 30 }
]

/* ---------------- 数据计算 ---------------- */
const money = (v) => Number(v || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
const orderTypeOf = (row) => Number(row.orderType || (row.hotelId ? 2 : 1))
const statusText = (s) => {
  const v = Number(s)
  if (v === 1) return '待出行'
  if (v === 2) return '已完成'
  if (v === 3) return '已取消'
  return '未知'
}
const statusClass = (s) => {
  const v = Number(s)
  if (v === 1) return 'pending'
  if (v === 2) return 'done'
  if (v === 3) return 'cancel'
  return ''
}
const displayName = (row) => row.hotelName || row.ticketName || '未知商品'

const totalRevenue = computed(() =>
  orders.value.reduce((sum, row) => (Number(row.status) === 3 ? sum : sum + Number(row.totalPrice || 0)), 0)
)
const pendingCount = computed(() => orders.value.filter((row) => Number(row.status) === 1).length)
const hotelOrderCount = computed(() => orders.value.filter((row) => orderTypeOf(row) === 2).length)
const ticketOrderCount = computed(() => orders.value.length - hotelOrderCount.value)
const todayOrderCount = computed(() => {
  const today = dayKey(new Date())
  return orders.value.filter((row) => dayKey(row.createTime) === today).length
})

const kpis = computed(() => [
  {
    label: '累计营收',
    prefix: '¥',
    value: money(totalRevenue.value),
    foot: `${orderTotal.value} 笔订单 · 已排除取消订单`,
    icon: Money,
    bg: 'rgba(14,124,134,.12)',
    color: '#0e7c86'
  },
  {
    label: '订单总数',
    prefix: '',
    value: orderTotal.value,
    foot: `今日 ${todayOrderCount.value} 笔 · 待出行 ${pendingCount.value} 笔`,
    icon: DocumentCopy,
    bg: 'rgba(47,157,108,.12)',
    color: '#67c23a'
  },
  {
    label: '系统用户',
    prefix: '',
    value: userList.value.length,
    foot: '含管理员与普通用户',
    icon: User,
    bg: 'rgba(230,162,60,.12)',
    color: '#e6a23c'
  },
  {
    label: '景点 / 攻略',
    prefix: '',
    value: `${totalSpot.value} / ${totalGuide.value}`,
    foot: '景点与攻略内容总量',
    icon: MapLocation,
    bg: 'rgba(144,147,153,.12)',
    color: '#909399'
  }
])

const recentOrders = computed(() =>
  [...orders.value]
    .sort((a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0))
    .slice(0, 5)
)

/* ---------------- 图表数据 ---------------- */
const dayKey = (d) => {
  if (!d) return ''
  const date = new Date(d)
  if (Number.isNaN(date.getTime())) return ''
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}
const shortTime = (v) => {
  if (!v) return ''
  const d = new Date(v)
  if (Number.isNaN(d.getTime())) return String(v)
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${m}-${day} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const trendSeries = computed(() => {
  const days = []
  const map = {}
  for (let i = orderRange.value - 1; i >= 0; i--) {
    const d = new Date()
    d.setDate(d.getDate() - i)
    const key = dayKey(d)
    days.push({ key, label: `${d.getMonth() + 1}/${d.getDate()}`, count: 0, revenue: 0 })
    map[key] = days[days.length - 1]
  }
  orders.value.forEach((row) => {
    const key = dayKey(row.createTime)
    if (!map[key]) return
    map[key].count += 1
    if (Number(row.status) !== 3) map[key].revenue += Number(row.totalPrice || 0)
  })
  return days
})
const trendHasData = computed(() => trendSeries.value.some((d) => d.count > 0))

const statusCounts = computed(() => {
  const counts = { 1: 0, 2: 0, 3: 0 }
  orders.value.forEach((row) => {
    const s = Number(row.status)
    if (counts[s] !== undefined) counts[s] += 1
  })
  return counts
})
const statusHasData = computed(() => orders.value.length > 0)

const spotTop = computed(() => {
  const map = {}
  orders.value.forEach((row) => {
    if (orderTypeOf(row) !== 1) return
    const ticket = ticketRows.value.find((t) => Number(t.id) === Number(row.ticketId))
    const name = ticket?.spot || row.ticketName || '未知景点'
    const qty = Number(row.count || 1)
    map[name] = (map[name] || 0) + qty
  })
  return Object.entries(map)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 5)
})
const topHasData = computed(() => spotTop.value.length > 0)

/* ---------------- ECharts ---------------- */
const trendRef = ref(null)
const statusRef = ref(null)
const topRef = ref(null)
let trendChart = null
let statusChart = null
let topChart = null

const axisCommon = {
  axisLine: { lineStyle: { color: '#e5e7eb' } },
  axisTick: { show: false },
  axisLabel: { color: '#6b7280', fontSize: 11 }
}

const renderTrend = () => {
  if (!trendRef.value) return
  if (!trendChart) trendChart = echarts.init(trendRef.value)
  const days = trendSeries.value
  trendChart.setOption(
    {
      color: ['#0e7c86', '#67c23a'],
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(20,40,70,.9)',
        borderWidth: 0,
        textStyle: { color: '#fff', fontSize: 12 }
      },
      legend: {
        top: 0,
        left: 'center',
        itemWidth: 10,
        itemHeight: 10,
        textStyle: { color: '#6b7280', fontSize: 11 }
      },
      grid: { left: 8, right: 8, top: 48, bottom: 4, containLabel: true },
      xAxis: { type: 'category', data: days.map((d) => d.label), ...axisCommon },
      yAxis: [
        {
          type: 'value',
          name: '订单',
          nameTextStyle: { color: '#9ca3af', fontSize: 11 },
          splitLine: { lineStyle: { color: '#f1f5f9' } },
          ...axisCommon
        },
        {
          type: 'value',
          name: '营收',
          nameTextStyle: { color: '#9ca3af', fontSize: 11 },
          splitLine: { show: false },
          axisLabel: { color: '#6b7280', fontSize: 11, formatter: (v) => `¥${v}` }
        }
      ],
      series: [
        {
          name: '订单量',
          type: 'bar',
          barWidth: days.length > 15 ? 8 : 16,
          itemStyle: {
            borderRadius: [6, 6, 0, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#7cc4ca' },
              { offset: 1, color: '#0e7c86' }
            ])
          },
          data: days.map((d) => d.count)
        },
        {
          name: '营收',
          type: 'line',
          yAxisIndex: 1,
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { width: 2, color: '#67c23a' },
          itemStyle: { color: '#67c23a' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(47,157,108,.22)' },
              { offset: 1, color: 'rgba(47,157,108,.02)' }
            ])
          },
          data: days.map((d) => Number(d.revenue.toFixed(2)))
        }
      ]
    },
    true
  )
}

const renderStatus = () => {
  if (!statusRef.value) return
  if (!statusChart) statusChart = echarts.init(statusRef.value)
  const c = statusCounts.value
  statusChart.setOption(
    {
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(20,40,70,.9)',
        borderWidth: 0,
        textStyle: { color: '#fff', fontSize: 12 }
      },
      legend: {
        bottom: 0,
        itemWidth: 9,
        itemHeight: 9,
        textStyle: { color: '#6b7280', fontSize: 11 }
      },
      series: [
        {
          type: 'pie',
          radius: ['52%', '76%'],
          center: ['50%', '44%'],
          avoidLabelOverlap: true,
          itemStyle: { borderColor: '#fff', borderWidth: 3, borderRadius: 6 },
          label: { show: false },
          emphasis: { scale: true, scaleSize: 4, label: { show: false } },
          data: [
            { value: c[1], name: '待出行', itemStyle: { color: '#0e7c86' } },
            { value: c[2], name: '已完成', itemStyle: { color: '#67c23a' } },
            { value: c[3], name: '已取消', itemStyle: { color: '#f56c6c' } }
          ]
        }
      ]
    },
    true
  )
}

const renderTop = () => {
  if (!topRef.value) return
  if (!topChart) topChart = echarts.init(topRef.value)
  const rows = spotTop.value
  topChart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        backgroundColor: 'rgba(20,40,70,.9)',
        borderWidth: 0,
        textStyle: { color: '#fff', fontSize: 12 }
      },
      grid: { left: 8, right: 18, top: 10, bottom: 4, containLabel: true },
      xAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: '#f1f5f9' } },
        ...axisCommon
      },
      yAxis: {
        type: 'category',
        inverse: true,
        data: rows.map((r) => r[0]),
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#374151', fontSize: 12 }
      },
      series: [
        {
          type: 'bar',
          barWidth: 14,
          itemStyle: {
            borderRadius: [0, 7, 7, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#0e7c86' },
              { offset: 1, color: '#7cc4ca' }
            ])
          },
          label: { show: true, position: 'right', color: '#0e7c86', fontSize: 11 },
          data: rows.map((r) => r[1])
        }
      ]
    },
    true
  )
}

const renderAll = () => {
  nextTick(() => {
    renderTrend()
    renderStatus()
    renderTop()
  })
}

const changeRange = (value) => {
  if (orderRange.value === value) return
  orderRange.value = value
  renderTrend()
}

const resizeHandler = () => {
  trendChart?.resize()
  statusChart?.resize()
  topChart?.resize()
}

/* ---------------- 数据加载 ---------------- */
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const loadDashboard = async () => {
  loading.value = true
  try {
    const [userRes, spotCount, guideRes, orderRes, ticketRes] = await Promise.allSettled([
      getUserListApi(),
      getSpotCountApi(),
      getGuidePageApi(1, 1, {}),
      getOrderPageApi(1, 200, {}),
      getTicketPageApi(1, 200, {})
    ])

    if (userRes.status === 'fulfilled' && userRes.value?.data?.code === 200) {
      userList.value = userRes.value.data.data || []
    }
    if (spotCount.status === 'fulfilled') {
      totalSpot.value = Number(spotCount.value || 0)
    }
    if (guideRes.status === 'fulfilled') {
      totalGuide.value = Number(guideRes.value?.total || 0)
    }
    if (orderRes.status === 'fulfilled' && orderRes.value) {
      orders.value = orderRes.value.records || []
      orderTotal.value = Number(orderRes.value.total || orders.value.length)
    }
    if (ticketRes.status === 'fulfilled' && ticketRes.value) {
      ticketRows.value = ticketRes.value.records || []
    }
  } catch (err) {
    console.error('看板数据加载失败', err)
  } finally {
    loading.value = false
    renderAll()
  }
}

const funcs = [
  { path: '/admin/user', title: '用户管理', desc: '管理系统用户信息', icon: User, bg: 'linear-gradient(135deg,#a29bfe,#7c6fe0)' },
  { path: '/admin/banner', title: '轮播图管理', desc: '管理首页轮播图片', icon: Picture, bg: 'linear-gradient(135deg,#55efc4,#1abc9c)' },
  { path: '/admin/category', title: '分类管理', desc: '管理景点分类信息', icon: Menu, bg: 'linear-gradient(135deg,#fdcb6e,#f39c12)' },
  { path: '/admin/spot', title: '景点管理', desc: '管理旅游景点信息', icon: MapLocation, bg: 'linear-gradient(135deg,#36cfc9,#27ae60)' },
  { path: '/admin/guide', title: '攻略管理', desc: '管理旅游攻略内容', icon: Document, bg: 'linear-gradient(135deg,#ff7675,#e74c3c)' },
  { path: '/admin/hotel', title: '住宿管理', desc: '管理酒店住宿信息', icon: OfficeBuilding, bg: 'linear-gradient(135deg,#74b9ff,#3498db)' },
  { path: '/admin/order', title: '订单管理', desc: '管理用户订单信息', icon: DocumentCopy, bg: 'linear-gradient(135deg,#ffeaa7,#f1c40f)' },
  { path: '/admin/ticket', title: '门票管理', desc: '管理景点门票信息', icon: Ticket, bg: 'linear-gradient(135deg,#ff9f43,#e67e22)' }
]

let timer = null
onMounted(async () => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  await loadDashboard()
  window.addEventListener('resize', resizeHandler)
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
  window.removeEventListener('resize', resizeHandler)
  trendChart?.dispose()
  statusChart?.dispose()
  topChart?.dispose()
  trendChart = statusChart = topChart = null
})
</script>

<style scoped>
.admin-home {
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 欢迎条 */
.welcome-card {
  border-radius: 16px;
  border: none;
}
.welcome-card :deep(.el-card__body) {
  padding: 16px 20px;
}
.welcome-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}
.welcome-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.welcome-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e2edff;
}
.welcome-text .title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2a3d;
  margin: 0 0 5px;
}
.welcome-text .desc {
  font-size: 12.5px;
  color: #6b7280;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 5px;
  flex-wrap: wrap;
}
.welcome-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.role-tag {
  padding: 5px 12px;
  border-radius: 999px;
  background: #ecf5ff;
  border: 1px solid #e6f4f5;
  color: #0e7c86;
  font-size: 12px;
  font-weight: 600;
}
.refresh-btn {
  padding: 6px 12px;
  border-radius: 9px;
  border: 1px solid #e5e7eb;
  background: #fff;
  color: #6b7280;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.refresh-btn:hover {
  border-color: #0e7c86;
  color: #0e7c86;
  background: #eef5ff;
}
.refresh-btn.loading {
  opacity: 0.6;
  pointer-events: none;
}

/* KPI */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}
.kpi-card {
  border-radius: 16px;
  border: none;
}
.kpi-card :deep(.el-card__body) {
  padding: 16px 18px;
}
.kpi-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.kpi-label {
  font-size: 12.5px;
  color: #6b7280;
}
.kpi-icon {
  width: 30px;
  height: 30px;
  border-radius: 9px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.kpi-num {
  margin-top: 8px;
  font-size: 24px;
  line-height: 1.2;
  font-weight: 700;
  color: #1f2a3d;
  letter-spacing: 0.3px;
}
.kpi-foot {
  margin-top: 6px;
  font-size: 11.5px;
  color: #9ca3af;
}

/* 图表卡片 */
.chart-row :deep(.el-col) {
  margin-bottom: 0;
}
.chart-card {
  border-radius: 16px;
  border: none;
  position: relative;
  height: 100%;
}
.chart-card :deep(.el-card__body) {
  padding: 14px 16px 10px;
}
.chart-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 4px;
}
.chart-head h2 {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: #1f2a3d;
  position: relative;
  padding-left: 9px;
}
.chart-head h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 13px;
  border-radius: 3px;
  background: linear-gradient(180deg, #7cc4ca, #0e7c86);
}
.chart-sub {
  margin: 3px 0 0 9px;
  font-size: 11px;
  color: #9ca3af;
}
.range-switch {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}
.range-btn {
  padding: 4px 10px;
  font-size: 11.5px;
  border-radius: 7px;
  border: 1px solid #e2ebf9;
  background: #fff;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.18s ease;
}
.range-btn.active {
  border-color: #0e7c86;
  background: #ecf5ff;
  color: #0e7c86;
  font-weight: 600;
}
.chart-box {
  width: 100%;
}
.chart-box.trend {
  height: 250px;
}
.chart-box.status {
  height: 250px;
}
.chart-box.top {
  height: 250px;
}
.chart-empty {
  position: absolute;
  inset: 46px 0 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a8b8cc;
  font-size: 12.5px;
  pointer-events: none;
}
.chart-empty.small {
  position: static;
  padding: 40px 0;
}

/* 最近订单 */
.more-link {
  border: none;
  background: transparent;
  color: #0e7c86;
  font-size: 12px;
  cursor: pointer;
  padding: 2px 0;
}
.more-link:hover {
  color: #0e7c86;
  text-decoration: underline;
}
.recent-list {
  list-style: none;
  margin: 8px 0 0;
  padding: 0;
}
.recent-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px dashed #f3f0e9;
}
.recent-item:last-child {
  border-bottom: none;
}
.type-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.type-dot.ticket {
  background: #0e7c86;
}
.type-dot.hotel {
  background: #e6a23c;
}
.recent-main {
  flex: 1;
  min-width: 0;
}
.recent-name {
  margin: 0;
  font-size: 12.5px;
  color: #2f3a4d;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.recent-meta {
  margin: 2px 0 0;
  font-size: 11px;
  color: #9ca3af;
}
.recent-right {
  text-align: right;
  flex-shrink: 0;
}
.recent-price {
  margin: 0;
  font-size: 13px;
  font-weight: 700;
  color: #1f2a3d;
}
.status-pill {
  display: inline-block;
  margin-top: 3px;
  padding: 1px 8px;
  border-radius: 999px;
  font-size: 10.5px;
}
.status-pill.pending {
  background: #ecf5ff;
  color: #0e7c86;
}
.status-pill.done {
  background: #f0f9eb;
  color: #529b2e;
}
.status-pill.cancel {
  background: #fef0f0;
  color: #c45656;
}

/* 功能入口 */
.card-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}
.func-card {
  border-radius: 14px;
  border: none;
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
  text-align: center;
}
.func-card :deep(.el-card__body) {
  padding: 18px 14px;
}
.func-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 24px rgba(15, 60, 70, 0.16);
}
.icon-wrap {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 9px;
  color: #fff;
}
.card-title {
  font-size: 13.5px;
  font-weight: 700;
  color: #2f3a4d;
  margin: 4px 0 3px;
}
.card-desc {
  font-size: 11.5px;
  color: #9ca3af;
  margin: 0;
}

@media (max-width: 1200px) {
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 640px) {
  .kpi-grid,
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
