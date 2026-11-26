<template>
  <div class="profile-page">
    <!-- 导航 -->
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
            :src="avatarSrc"
            style="width: 34px; height: 34px; border-radius: 50%; object-fit: cover"
            fit="cover"
          />
          <div class="user-name">{{ userInfo?.username }}</div>
          <el-button text @click="logout">退出登录</el-button>
        </div>
      </div>
    </div>

    <div class="container">
      <!-- 用户卡片 -->
      <div class="hero-card">
        <div class="hero-glow"></div>
        <div class="hero-avatar">
          <img :src="avatarSrc" alt="头像" />
        </div>
        <div class="hero-info">
          <h2>{{ profile.name || profile.username || '游客' }}</h2>
          <p class="hero-sub">
            <span class="role-pill">{{ profile.role || '普通用户' }}</span>
            <span>账号：{{ profile.username }}</span>
            <span v-if="profile.phone">· {{ profile.phone }}</span>
          </p>
        </div>
        <div class="hero-stats">
          <div class="hero-stat">
            <strong>{{ myOrders.length }}</strong>
            <span>我的订单</span>
          </div>
          <div class="hero-stat">
            <strong>{{ favorites.length }}</strong>
            <span>我的收藏</span>
          </div>
        </div>
      </div>

      <!-- 页签 -->
      <div class="tab-bar">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          type="button"
          class="tab-btn"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
          <el-icon size="15"><component :is="tab.icon" /></el-icon>
          {{ tab.label }}
          <span v-if="tab.key === 'orders' && myOrders.length" class="tab-count">{{ myOrders.length }}</span>
          <span v-else-if="tab.key === 'favorites' && favorites.length" class="tab-count">{{ favorites.length }}</span>
        </button>
      </div>

      <!-- 我的订单 -->
      <div v-if="activeTab === 'orders'" class="panel">
        <div v-if="loading.orders" class="panel-empty">订单加载中…</div>
        <div v-else-if="myOrders.length === 0" class="panel-empty">
          还没有订单，<router-link to="/ticket" class="link">去预订门票</router-link> 或
          <router-link to="/hotel" class="link">找一间住宿</router-link>
        </div>
        <div v-else class="order-list">
          <div v-for="order in myOrders" :key="order.id" class="order-card">
            <div class="order-cover" :class="orderTypeOf(order) === 2 ? 'hotel' : 'ticket'">
              <el-icon size="22"><component :is="orderTypeOf(order) === 2 ? OfficeBuilding : Ticket" /></el-icon>
              <span>{{ orderTypeOf(order) === 2 ? '住宿' : '门票' }}</span>
            </div>
            <div class="order-main">
              <div class="order-title-row">
                <h3>{{ displayName(order) }}</h3>
                <span class="status-tag" :class="statusClass(order)">{{ statusText(order) }}</span>
              </div>
              <p class="order-meta">
                <span>{{ orderTypeOf(order) === 2 ? '入住' : '出行' }}：{{ formatDate(order.travelDate) || '未指定' }}</span>
                <span>{{ order.count }}{{ orderTypeOf(order) === 2 ? ' 晚' : ' 张' }}</span>
                <span>下单：{{ formatDateTime(order.createTime) }}</span>
              </p>
              <p class="order-no">订单号 {{ order.orderNo }}</p>
            </div>
            <div class="order-right">
              <p class="order-price">¥{{ money(order.totalPrice) }}</p>
              <router-link :to="`/orderDetail/${order.id}`" class="order-detail-link">查看详情</router-link>
              <button
                v-if="Number(order.status) === 1 && !isOrderExpired(order)"
                type="button"
                class="order-cancel"
                @click="cancelOrder(order)"
              >
                取消订单
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 我的收藏 -->
      <div v-if="activeTab === 'favorites'" class="panel">
        <div v-if="loading.favorites" class="panel-empty">收藏加载中…</div>
        <div v-else-if="favorites.length === 0" class="panel-empty">
          还没有收藏，去<router-link to="/spot" class="link">景点</router-link>或<router-link to="/guide" class="link">攻略</router-link>页面点心形按钮收藏吧
        </div>
        <div v-else class="fav-grid">
          <div v-for="fav in favorites" :key="fav.id" class="fav-card">
            <div class="fav-img">
              <img v-if="fav.targetImg" :src="getFavImg(fav.targetImg)" :alt="fav.targetName" />
              <div v-else class="fav-img-placeholder">
                <el-icon size="22"><component :is="favIcon(fav.targetType)" /></el-icon>
              </div>
              <span class="fav-type">{{ favTypeText(fav.targetType) }}</span>
            </div>
            <div class="fav-body">
              <h4>{{ fav.targetName || '未命名' }}</h4>
              <p>收藏于 {{ formatDate(fav.createTime) }}</p>
              <div class="fav-actions">
                <button type="button" class="mini-btn primary" @click="goTarget(fav)">去看看</button>
                <button type="button" class="mini-btn" @click="removeFavorite(fav)">取消收藏</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 资料设置 -->
      <div v-if="activeTab === 'profile'" class="panel">
        <div class="form-wrap">
          <el-form :model="form" label-width="86px" class="profile-form">
            <el-form-item label="账号">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="form.name" placeholder="给自己取个昵称" maxlength="20" show-word-limit />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
            <el-form-item label="角色">
              <el-input v-model="form.role" disabled />
            </el-form-item>
            <el-form-item label="修改密码">
              <el-input v-model="form.password" type="password" placeholder="不修改请留空" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
              <el-button @click="resetForm">还原</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, House, MapLocation, Document, OfficeBuilding, Ticket, List, Setting,
  Star, Collection
} from '@element-plus/icons-vue'
import { getUserByIdApi, updateUserInfoApi } from '@/api/userData.js'
import { getFavoriteListApi, removeFavoriteApi } from '@/api/favoriteData.js'
import { getOrderPageApi, cancelOrder as cancelOrderApi } from '@/api/orderData.js'
import { getOrderDisplayStatus, isOrderExpired } from '@/utils/orderStatus.js'
import { useUndo } from '@/composables/useUndo'

const { pushUndo } = useUndo()
const router = useRouter()

const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))
const profile = ref({ ...userInfo.value })
// 与其它前台页面使用同一套头像规则：管理员用固定头像，普通用户用自己上传/配置的头像
function getAvatarSrc(user) {
  if (user?.role === '管理员') {
    return '/admin-avatar.jpeg'
  }
  if (user?.avatar) {
    // 上传接口返回的是完整 URL，不能再拼 assets 路径
    if (user.avatar.startsWith('http') || user.avatar.startsWith('/')) {
      return user.avatar
    }
    return new URL(`../assets/images/${user.avatar}`, import.meta.url).href
  }
  return new URL(`../assets/images/default-avatar.png`, import.meta.url).href
}
const avatarSrc = computed(() => getAvatarSrc(profile.value))

const getFavImg = (src) => {
  if (!src) return ''
  const path = String(src).trim()
  if (path.startsWith('http') || path.startsWith('/')) return path
  return `/assets/images/${path}`
}
const form = reactive({
  username: '',
  name: '',
  phone: '',
  role: '',
  password: ''
})

const tabs = [
  { key: 'orders', label: '我的订单', icon: List },
  { key: 'favorites', label: '我的收藏', icon: Star },
  { key: 'profile', label: '资料设置', icon: User }
]
const activeTab = ref('orders')

const myOrders = ref([])
const favorites = ref([])
const saving = ref(false)
const loading = reactive({ orders: true, favorites: true })

const money = (v) => Number(v || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
const orderTypeOf = (row) => Number(row.orderType || (row.hotelId ? 2 : 1))
const displayName = (row) => row.hotelName || row.ticketName || '未知商品'
const statusText = (order) => getOrderDisplayStatus(order)
const statusClass = (order) => {
  const text = getOrderDisplayStatus(order)
  if (text === '待出行') return 'pending'
  if (text === '已完成') return 'done'
  if (text === '已取消') return 'cancel'
  if (text === '已过期') return 'expired'
  return ''
}
const formatDate = (v) => {
  if (!v) return ''
  const d = new Date(v)
  if (Number.isNaN(d.getTime())) return ''
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
const formatDateTime = (v) => {
  if (!v) return ''
  const d = new Date(v)
  if (Number.isNaN(d.getTime())) return ''
  return `${formatDate(v)} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
const favTypeText = (t) => (t === 'guide' ? '攻略' : t === 'hotel' ? '住宿' : '景点')
const favIcon = (t) => (t === 'guide' ? Document : t === 'hotel' ? OfficeBuilding : MapLocation)

const loadProfile = async () => {
  if (!userInfo.value.id) return
  try {
    const res = await getUserByIdApi(userInfo.value.id)
    if (res?.data?.code === 200 && res.data.data) {
      profile.value = res.data.data
      Object.assign(form, {
        username: res.data.data.username || '',
        name: res.data.data.name || '',
        phone: res.data.data.phone || '',
        role: res.data.data.role || '',
        password: ''
      })
    }
  } catch (err) {
    console.error('加载个人资料失败', err)
    Object.assign(form, {
      username: profile.value.username || '',
      name: profile.value.name || '',
      phone: profile.value.phone || '',
      role: profile.value.role || ''
    })
  }
}

const loadOrders = async () => {
  if (!userInfo.value.id) {
    loading.orders = false
    return
  }
  try {
    const res = await getOrderPageApi(1, 50, { userId: userInfo.value.id })
    myOrders.value = res?.records || []
  } catch (err) {
    console.error('加载我的订单失败', err)
    myOrders.value = []
  } finally {
    loading.orders = false
  }
}

const loadFavorites = async () => {
  if (!userInfo.value.id) {
    loading.favorites = false
    return
  }
  try {
    const res = await getFavoriteListApi(userInfo.value.id)
    favorites.value = Array.isArray(res) ? res : []
  } catch (err) {
    console.error('加载收藏失败', err)
    favorites.value = []
  } finally {
    loading.favorites = false
  }
}

const cancelOrder = (order) => {
  const prevStatus = order.status
  order.status = 3
  pushUndo({
    message: `已取消订单「${displayName(order)}」`,
    onUndo: () => {
      order.status = prevStatus
    },
    onCommit: async () => {
      try {
        const res = await cancelOrderApi(order.id)
        if (res) ElMessage.success('订单已取消，库存已归还')
        else ElMessage.error('取消失败，仅待出行订单可取消')
      } catch (err) {
        ElMessage.error('取消失败，请重试')
      }
      loadOrders()
    }
  })
}

const removeFavorite = async (fav) => {
  try {
    const res = await removeFavoriteApi(userInfo.value.id, fav.targetType, fav.targetId)
    if (res) {
      favorites.value = favorites.value.filter((item) => item.id !== fav.id)
      ElMessage.success('已取消收藏')
    } else {
      ElMessage.error('取消收藏失败')
    }
  } catch (err) {
    ElMessage.error('取消收藏失败，请稍后再试')
  }
}

const goTarget = (fav) => {
  if (fav.targetType === 'guide') {
    router.push(`/guideDetail/${fav.targetId}`)
  } else if (fav.targetType === 'hotel') {
    router.push({ path: '/hotelDetail', query: { id: fav.targetId } })
  } else {
    router.push(`/spotDetail/${fav.targetId}`)
  }
}

const saveProfile = async () => {
  if (!form.name.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  if (form.phone && !/^1\d{10}$/.test(form.phone)) {
    ElMessage.warning('手机号格式不太对')
    return
  }
  saving.value = true
  try {
    const payload = {
      id: userInfo.value.id,
      name: form.name.trim(),
      phone: form.phone.trim()
    }
    if (form.password) payload.password = form.password
    const res = await updateUserInfoApi(payload)
    if (res?.data?.code === 200) {
      ElMessage.success('资料已更新')
      profile.value = { ...profile.value, name: payload.name, phone: payload.phone }
      const stored = { ...userInfo.value, name: payload.name, phone: payload.phone }
      sessionStorage.setItem('userInfo', JSON.stringify(stored))
      userInfo.value = stored
      form.password = ''
    } else {
      ElMessage.error(res?.data?.msg || '保存失败')
    }
  } catch (err) {
    ElMessage.error('保存失败，请稍后再试')
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  Object.assign(form, {
    username: profile.value.username || '',
    name: profile.value.name || '',
    phone: profile.value.phone || '',
    role: profile.value.role || '',
    password: ''
  })
}

const logout = () => {
  sessionStorage.removeItem('userInfo')
  window.location.href = '/login'
}

onMounted(() => {
  loadProfile()
  loadOrders()
  loadFavorites()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding-bottom: 40px;
  background:
    radial-gradient(circle at 12% 12%, rgba(14, 124, 134, 0.12), transparent 28%),
    radial-gradient(circle at 88% 18%, rgba(242, 140, 91, 0.1), transparent 32%),
    linear-gradient(180deg, #faf8f5 0%, #f7f4ee 45%, #f3f0e9 100%);
}
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

/* 用户卡片 */
.hero-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 24px 26px;
  border-radius: 20px;
  background: linear-gradient(135deg, #f3f8ff, #ffffff);
  border: 1px solid #e4eefb;
  box-shadow: 0 12px 32px rgba(15, 60, 70, 0.08);
  overflow: hidden;
}
.hero-glow {
  position: absolute;
  right: -60px;
  top: -70px;
  width: 240px;
  height: 240px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(14, 124, 134, 0.18), transparent 68%);
  pointer-events: none;
}
.hero-avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #ffffff;
  box-shadow: 0 6px 16px rgba(15, 60, 70, 0.18);
  flex-shrink: 0;
}
.hero-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.hero-info {
  flex: 1;
  min-width: 0;
}
.hero-info h2 {
  margin: 0 0 8px;
  font-size: 21px;
  color: #1f2a3d;
}
.hero-sub {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12.5px;
  color: #7f93ad;
  flex-wrap: wrap;
}
.role-pill {
  padding: 3px 10px;
  border-radius: 999px;
  background: #ecf5ff;
  border: 1px solid #e6f4f5;
  color: #0e7c86;
  font-weight: 600;
}
.hero-stats {
  display: flex;
  gap: 26px;
}
.hero-stat {
  text-align: center;
}
.hero-stat strong {
  display: block;
  font-size: 24px;
  color: #0e7c86;
  line-height: 1.1;
}
.hero-stat span {
  font-size: 11.5px;
  color: #9ca3af;
}

/* 页签 */
.tab-bar {
  display: flex;
  gap: 8px;
  margin: 20px 0 16px;
  padding: 6px;
  border-radius: 14px;
  background: #ffffff;
  border: 1px solid #e8f0fb;
  width: fit-content;
}
.tab-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #6b7280;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.tab-btn.active {
  background: linear-gradient(135deg, #149aa6, #0e7c86);
  color: #fff;
  font-weight: 600;
  box-shadow: 0 6px 14px rgba(14, 124, 134, 0.24);
}
.tab-count {
  padding: 0 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.28);
  font-size: 10.5px;
}
.tab-btn:not(.active) .tab-count {
  background: #eef5ff;
  color: #0e7c86;
}

.panel {
  min-height: 220px;
}
.panel-empty {
  padding: 60px 0;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
  background: #fff;
  border: 1px solid #e8f0fb;
  border-radius: 16px;
}
.link {
  color: #0e7c86;
  text-decoration: none;
}
.link:hover {
  text-decoration: underline;
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.order-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 18px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #e8f0fb;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}
.order-card:hover {
  box-shadow: 0 12px 26px rgba(15, 60, 70, 0.1);
  transform: translateY(-2px);
}
.order-cover {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 11px;
  flex-shrink: 0;
}
.order-cover.ticket {
  background: #ecf5ff;
  color: #0e7c86;
}
.order-cover.hotel {
  background: #fdf6ec;
  color: #b88230;
}
.order-main {
  flex: 1;
  min-width: 0;
}
.order-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.order-title-row h3 {
  margin: 0;
  font-size: 15px;
  color: #1f2a3d;
}
.status-tag {
  padding: 2px 9px;
  border-radius: 999px;
  font-size: 10.5px;
}
.status-tag.pending {
  background: #ecf5ff;
  color: #0e7c86;
}
.status-tag.done {
  background: #f0f9eb;
  color: #529b2e;
}
.status-tag.cancel {
  background: #fef0f0;
  color: #c45656;
}
.status-tag.expired {
  background: #fdf6ec;
  color: #b88230;
}
.order-meta {
  margin: 6px 0 0;
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  font-size: 12px;
  color: #7f93ad;
}
.order-no {
  margin: 4px 0 0;
  font-size: 11px;
  color: #b6c4d8;
  font-family: Consolas, monospace;
}
.order-right {
  text-align: right;
  flex-shrink: 0;
}
.order-price {
  margin: 0 0 6px;
  font-size: 17px;
  font-weight: 700;
  color: #1f2a3d;
}
.order-cancel {
  padding: 4px 10px;
  border-radius: 8px;
  border: 1px solid #fbd5d5;
  background: #fef4f4;
  color: #d9534f;
  font-size: 11.5px;
  cursor: pointer;
}
.order-cancel:hover {
  background: #fdeaea;
}
.order-detail-link {
  display: inline-block;
  margin-bottom: 6px;
  font-size: 11.5px;
  color: #0e7c86;
  text-decoration: none;
}
.order-detail-link:hover {
  text-decoration: underline;
}

/* 收藏网格 */
.fav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 14px;
}
.fav-card {
  border-radius: 16px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e8f0fb;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.fav-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 14px 28px rgba(15, 60, 70, 0.12);
}
.fav-img {
  position: relative;
  height: 130px;
  background: #eef5ff;
}
.fav-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.fav-img-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #149aa6;
}
.fav-type {
  position: absolute;
  left: 10px;
  top: 10px;
  padding: 2px 9px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  color: #0e7c86;
  font-size: 10.5px;
  font-weight: 600;
}
.fav-body {
  padding: 12px 14px 14px;
}
.fav-body h4 {
  margin: 0 0 4px;
  font-size: 14px;
  color: #1f2a3d;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.fav-body p {
  margin: 0 0 10px;
  font-size: 11.5px;
  color: #9ca3af;
}
.fav-actions {
  display: flex;
  gap: 8px;
}
.mini-btn {
  padding: 4px 10px;
  border-radius: 8px;
  border: 1px solid #e2ebf9;
  background: #fff;
  color: #6b7280;
  font-size: 11.5px;
  cursor: pointer;
  transition: all 0.18s ease;
}
.mini-btn:hover {
  border-color: #0e7c86;
  color: #0e7c86;
}
.mini-btn.primary {
  border-color: transparent;
  background: linear-gradient(135deg, #149aa6, #0e7c86);
  color: #fff;
}
.mini-btn.primary:hover {
  box-shadow: 0 4px 10px rgba(14, 124, 134, 0.26);
}

/* 资料表单 */
.form-wrap {
  background: #fff;
  border: 1px solid #e8f0fb;
  border-radius: 16px;
  padding: 22px 26px;
  max-width: 560px;
}
.profile-form :deep(.el-form-item) {
  margin-bottom: 16px;
}
.profile-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #dbe7f8 inset;
  background: #fbfdff;
}

@media (max-width: 860px) {
  .nav-menu {
    display: none;
  }
  .hero-card {
    flex-wrap: wrap;
  }
  .order-card {
    flex-wrap: wrap;
  }
  .order-right {
    width: 100%;
    text-align: left;
    display: flex;
    align-items: center;
    gap: 12px;
  }
}
</style>
