<template>
    <div class="order-page">
        <!-- 导航【和门票页一字不差完全统一】 -->
        <div class="nav-header">
            <div class="nav-container">
                <div class="logo">
                    <img src="/logo.jpeg" alt="logo" class="logo-img" />
                    <span class="logo-text">旅游信息系统</span>
                </div>
                <el-menu mode="horizontal" :router="true" class="nav-menu" :default-active="$route.path">
                    <el-menu-item index="/home">
                        <el-icon><House /></el-icon>
                        <span>首页</span>
                    </el-menu-item>
                    <el-menu-item index="/spot">
                        <el-icon><MapLocation /></el-icon>
                        <span>景点</span>
                    </el-menu-item>
                    <el-menu-item index="/guide">
                        <el-icon><Document /></el-icon>
                        <span>攻略</span>
                    </el-menu-item>
                    <el-menu-item index="/hotel">
                        <el-icon><OfficeBuilding /></el-icon>
                        <span>周边住宿</span>
                    </el-menu-item>
                    <el-menu-item index="/ticket">
                        <el-icon><Ticket /></el-icon>
                        <span>门票预订</span>
                    </el-menu-item>
                    <el-menu-item index="/order">
                        <el-icon><List /></el-icon>
                        <span>我的订单</span>
                    </el-menu-item>
                    <el-menu-item index="/profile">
                        <el-icon><User /></el-icon>
                        <span>个人中心</span>
                    </el-menu-item>
                    <el-menu-item v-if="userInfo?.role === '管理员'" index="/admin/home">
                        <el-icon><Setting /></el-icon>
                        <span>后台管理</span>
                    </el-menu-item>
                </el-menu>
                <div class="user-box">
                    <el-image
                        :src="getAvatarSrc(userInfo)"
                        style="width:34px;height:34px;border-radius:50%;object-fit:cover"
                        fit="cover"
                    />
                    <div class="user-name">{{ userInfo?.username }}</div>
                    <el-button text @click="logout">退出登录</el-button>
                </div>
            </div>
        </div>
        <div class="container">
            <div class="outer-wrap">
                <!-- 标题区 -->
                <div class="page-title">
                    <h2 style="display:flex;align-items:center;gap:10px;margin:0 0 8px 0;">
                        <span
                            style="display:inline-flex;align-items:center;justify-content:center;width:36px;height:36px;border-radius:50%;background:#eaf6f7;color:#0e7c86;">
                            <el-icon style="font-size:20px;">
                                <List />
                            </el-icon>
                        </span>
                        我的订单
                    </h2>
                    <p>查看你预订的门票与住宿订单，支持查看详情、取消订单与删除历史订单</p>
                    <hr class="divider" />
                </div>
                <!-- 状态筛选 -->
                <div class="filter-card">
                    <div class="filter-title">📋 订单筛选</div>
                    <div class="status-tabs">
                        <span v-for="s in statusMap" :key="s.value" class="status-tab"
                            :class="{ active: filterStatusItem.value === s.value }"
                            @click="filterStatusItem = s">
                            {{ s.label }}
                        </span>
                    </div>
                </div>
                <!-- 订单列表 -->
                <div v-if="pageOrders.length > 0" class="order-list">
                    <div class="order-card" v-for="order in pageOrders" :key="order.orderNo">
                        <div class="order-head">
                            <div class="order-id">订单号：{{ order.orderNo }}</div>
                            <el-tag :type="statusTagType(order.statusText)" size="small">{{ order.statusText }}</el-tag>
                        </div>
                        <div class="order-body">
                            <div class="order-info">
                                <div class="ticket-name">{{ order.ticketName }}</div>
                                <div class="meta">
                                    <span>🕐 下单时间：{{ formatDateTime(order.createTime) || "暂无" }}</span>
                                    <span>📅 {{ order.orderType === 2 ? '入住日期' : '出行日期' }}：{{ formatDate(order.travelDate) }}</span>
                                    <span>{{ order.orderType === 2 ? '🏨 住宿天数' : '🎫 购票数量' }}：{{ order.count }}{{ order.orderType === 2 ? ' 晚' : ' 张' }}</span>
                                    <span>👤 {{ order.orderType === 2 ? '入住人' : '出行人' }}：{{ order.personName || "暂无" }}</span>
                                    <span>📞 手机号：{{ order.phone || "暂无" }}</span>
                                </div>
                            </div>
                            <div class="order-price">
                                <span class="amount">¥{{ order.totalPrice }}</span>
                            </div>
                        </div>
                        <div class="order-foot">
                            <el-button size="small" @click="goDetail(order)">查看详情</el-button>
                            <el-button size="small" type="danger" plain
                                v-if="order.statusText === '待出行' && canCancel(order)"
                                @click="handleCancelOrder(order)">取消订单</el-button>
                            <el-button size="small" type="danger" plain
                                v-if="order.statusText === '已取消' || order.statusText === '已完成'"
                                @click="handleDeleteOrder(order)">删除订单</el-button>
                        </div>
                    </div>
                </div>
                <!-- ✅ 动态空状态提示 -->
                <el-empty v-else :description="emptyDesc" />
                <!-- 分页 -->
                <div class="pagination-wrap" v-if="pageOrders.length > 0 && total > pageSize">
                    <span>共 {{ total }} 条</span>
                    <el-pagination v-model:current-page="pageNum" :page-size="pageSize" layout="prev, pager, next"
                        :total="total" @current-change="loadOrders" />
                </div>
            </div>
        </div>
        <!-- 底部 和门票页统一 -->
        <div class="footer-white">
            <div class="footer-divider"></div>
            <div class="footer-simple">
                <span>旅游信息系统 · 订单板块</span>
                <span>©2026 旅游信息系统 All Rights Reserved</span>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { User, House, MapLocation, Document, OfficeBuilding, Ticket, List, Setting } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loadOrderFromBackend, cancelOrder, deleteOrderApi } from '@/api/orderData.js'
import { getOrderDisplayStatus } from '@/utils/orderStatus.js'
import { useUndo } from '@/composables/useUndo'
const { pushUndo } = useUndo()
import { useRouter } from 'vue-router'
const router = useRouter()
const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))
function getAvatarSrc(user) {
  if (user?.role === "管理员") {
    return "/admin-avatar.jpeg"
  }
  if (user?.avatar) {
    if (user.avatar.startsWith('http') || user.avatar.startsWith('/')) {
      return user.avatar
    }
    return new URL(`../assets/images/${user.avatar}`, import.meta.url).href
  }
  return new URL(`../assets/images/default-avatar.png`, import.meta.url).href
}
const formatDate = (timeStr) => {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    if (isNaN(date.getTime())) return timeStr
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
}
const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  if (isNaN(date.getTime())) return timeStr
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}:${s}`
}
const canCancel = (order) => {
    if (!order.travelDate) return false
    const d = new Date(order.travelDate)
    if (isNaN(d.getTime())) return true
    return d > new Date()
}
const statusMap = [
  { label: '全部', value: '' },
  { label: '待出行', value: '1' },
  { label: '已完成', value: '2' },
  { label: '已取消', value: '3' }
]
const filterStatusItem = ref(statusMap[0])
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)
const tableData = ref([])
// 打开独立订单详情页
const goDetail = (order) => {
    router.push(`/orderDetail/${order.id}`)
}

// 空状态文字映射
const emptyTextMap = {
  '全部': '暂无订单',
  '待出行': '暂无待出行订单',
  '已完成': '暂无已完成订单',
  '已取消': '暂无已取消订单'
}
const emptyDesc = computed(() => {
  return emptyTextMap[filterStatusItem.value.label]
})

async function loadOrders() {
  try {
    // 只把当前筛选状态传给后端，由后端按状态分页查询
    const params = { userId: userInfo.value.id }
    if (filterStatusItem.value.value) {
      params.status = filterStatusItem.value.value
      // “待出行”只展示真正还能出行的订单，已过期的不再混进来
      if (filterStatusItem.value.value === '1') {
        params.excludeExpired = true
      }
    }
    const res = await loadOrderFromBackend(pageNum.value, pageSize.value, params)
    console.log("后端原始返回：", res)
    if(res){
      tableData.value = res.records.map(item=>{
        return {
          ...item,
          statusText: getOrderDisplayStatus(item)
        }
      })
      total.value = res.total || 0
      // 当前页被删/取消空了但总数还有，自动退到上一页，避免分页被隐藏后回不去
      if (tableData.value.length === 0 && total.value > 0 && pageNum.value > 1) {
        const lastPage = Math.max(1, Math.ceil(total.value / pageSize.value))
        pageNum.value = Math.min(pageNum.value - 1, lastPage)
        return loadOrders()
      }
    }else{
      tableData.value = []
      total.value = 0
    }
  } catch (err) {
    console.error("加载订单失败：", err)
    tableData.value = []
    total.value = 0
  }
}
// 后端已按状态筛选，这里直接展示当前页数据
const pageOrders = computed(() => tableData.value)
const statusTagType = (status) => {
    if (status === '待出行') return 'primary'
    if (status === '已完成') return 'success'
    if (status === '已取消') return 'info'
    if (status === '已过期') return 'warning'
    return 'danger'
}
const handleCancelOrder = (order) => {
    // 先直接生效，底部给「撤销」条，5 秒后没撤销才真正调后端
    const prevStatus = order.status
    const prevStatusText = order.statusText
    order.status = 3
    order.statusText = '已取消'
    pushUndo({
        message: `已取消订单「${order.ticketName}」`,
        onUndo: () => {
            order.status = prevStatus
            order.statusText = prevStatusText
        },
        onCommit: async () => {
            try {
                await cancelOrder(order.id)
                ElMessage.success('订单已取消，库存已归还')
                await loadOrders()
            } catch (err) {
                console.error('取消订单失败', err)
                ElMessage.error('取消失败，请重试')
                await loadOrders()
            }
        }
    })
}
const handleDeleteOrder = (order) => {
    // 先从列表里移除，撤销时原样放回，不请求后端
    const index = tableData.value.findIndex((item) => item.id === order.id)
    const snapshot = { ...order }
    if (index > -1) tableData.value.splice(index, 1)
    total.value = Math.max(0, total.value - 1)
    pushUndo({
        message: `已删除订单「${order.ticketName}」`,
        onUndo: () => {
            tableData.value.splice(index, 0, snapshot)
            total.value += 1
        },
        onCommit: async () => {
            try {
                await deleteOrderApi(order.id)
                ElMessage.success('订单已删除')
                await loadOrders()
            } catch (err) {
                console.error('删除订单失败', err)
                ElMessage.error('删除失败，请重试')
                await loadOrders()
            }
        }
    })
}
watch(filterStatusItem, () => {
  pageNum.value = 1
  // 状态变化后重新请求后端，保证筛选和分页总数都按当前状态计算
  loadOrders()
})
const logout = () => {
sessionStorage.removeItem('userInfo')
ElMessage.success('已退出登录')
router.push('/login')
}
onMounted(async () => {
  await loadOrders()
})
</script>
<style scoped>
.order-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 12% 12%, rgba(14, 124, 134, 0.12), transparent 28%),
    radial-gradient(circle at 88% 18%, rgba(242, 140, 91, 0.10), transparent 32%),
    linear-gradient(180deg, #faf8f5 0%, #f7f4ee 45%, #f3f0e9 100%);
}
/* 导航 完全复刻门票 */
.nav-header {
  position: sticky;
  top: 0;
  z-index: 999;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: saturate(160%) blur(14px);
  -webkit-backdrop-filter: saturate(160%) blur(14px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 6px 24px rgba(15, 60, 70, 0.08);
}
.nav-container {
  width: 92%;
  max-width: 1400px;
  margin: 0 auto;
  height: 72px;
  display: flex;
  align-items: center;
  gap: 24px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}
.logo-img {
  width: 38px;
  height: 38px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 4px 14px rgba(14, 124, 134, 0.35);
}
.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 0.5px;
}
.nav-menu {
  flex: 1;
}
:deep(.el-menu--horizontal) {
  border-bottom: none !important;
  background: transparent !important;
}
:deep(.el-menu-item) {
  color: #4b5563 !important;
  font-size: 15px;
  font-weight: 500;
  margin: 0 4px;
}
:deep(.el-menu-item:hover) {
  color: #0e7c86 !important;
  background: rgba(14, 124, 134, 0.06) !important;
  border-radius: 8px;
}
:deep(.el-menu-item.is-active) {
  color: #0e7c86 !important;
  background: rgba(14, 124, 134, 0.08) !important;
  border-radius: 8px;
}
:deep(.el-menu-item.is-active .el-icon) {
  color: #0e7c86 !important;
}
:deep(.el-menu--horizontal > .el-menu-item.is-active)::after {
  display: none;
}
.user-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 10px 6px 6px;
  border-radius: 999px;
  background: #f7f4ee;
  border: 1px solid rgba(0, 0, 0, 0.04);
}
.user-name {
  color: #1f2937;
  font-size: 14px;
  font-weight: 500;
}
.container {
  width: 92%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px 0;
}
.outer-wrap {
    background: #fff;
    border-radius: 16px;
    padding: 28px;
    box-shadow: 0 4px 16px rgba(124,104,237,0.08);
}
.page-title h2 {
    margin: 0;
    font-size: 22px;
}
.page-title p {
    color: #888;
    margin: 6px 0 16px;
}
.divider {
    border: none;
    border-bottom: 1px solid #e5e7eb;
    margin: 0 0 24px 0;
}
.filter-card {
    background: #fff;
    padding: 20px 24px;
    border-radius: 16px;
    box-shadow: 0 4px 16px rgba(124,104,237,0.1);
    margin-bottom: 24px;
}
.filter-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 14px;
}
.status-tabs {
    display: flex;
    gap: 12px;
}
.status-tab {
    padding: 6px 18px;
    border-radius: 18px;
    background: #f5f6fa;
    color: #666;
    font-size: 14px;
    cursor: pointer;
    transition: all .2s;
}
.status-tab:hover {
    background: #eaf6f7;
    color: #0e7c86;
}
.status-tab.active {
    background: linear-gradient(135deg, #0e7c86, #149aa6);
    color: #fff;
}
.order-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}
.order-card {
    border: 1px solid #f0eef7;
    border-radius: 14px;
    background: #fff;
    box-shadow: 0 4px 18px rgba(15, 60, 70, 0.08);
    padding: 20px 24px;
    transition: 0.25s ease;
}
.order-card:hover {
    box-shadow:0 8px 24px rgba(124,104,237,0.14);
}
.order-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}
.order-id {
    font-size: 13px;
    color: #999;
}
.order-body {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 20px;
}
.ticket-name {
    font-size: 18px;
    font-weight: 600;
    color: #222;
    margin-bottom: 10px;
}
.meta {
    display: flex;
    flex-direction: column;
    gap: 6px;
    font-size: 13px;
    color: #666;
}
.order-price .amount {
    font-size: 26px;
    font-weight: 700;
    color: #ff3b30;
}
.order-foot {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 14px;
    border-top: 1px dashed #eee;
    padding-top: 12px;
}
.pagination-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    margin-top: 30px;
}
/* 底部 和门票完全一致 */
.footer-white {
  background: #ffffff;
  margin-top: 60px;
}
.footer-divider {
  width: 92%;
  max-width: 1400px;
  margin: 0 auto;
  border-top: 1px solid #e5e7eb;
}
.footer-simple {
  width: 92%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 22px 0 28px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
  font-size: 14px;
  color: #8898b2;
}
</style>
