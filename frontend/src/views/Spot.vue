<template>
  <div class="spot-page">
    <!-- 顶部导航 -->
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
    <!-- 页头Banner区【升级修改】 -->
    <div class="spot-banner">
      <div class="spot-banner-inner">
        <!-- 新增装饰元素 -->
        <div class="spot-banner-glow"></div>
        <div class="spot-banner-orbit orbit-1"></div>
        <div class="spot-banner-orbit orbit-2"></div>
        <div class="spot-banner-text">
          <span class="spot-banner-tag">Travel Collection</span>
          <h1>探索精彩景点</h1>
          <p class="spot-banner-desc">发现世界各地的美丽风景和文化遗产，开启你的下一次旅行。</p>
          <!-- 新增统计数据 -->
          <div class="spot-banner-stats">
            <div class="stat-item">
              <span class="stat-num">{{ totalCount }}+</span>
              <span class="stat-label">精选景点</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">6</span>
              <span class="stat-label">分类导航</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">24h</span>
              <span class="stat-label">行程推荐</span>
            </div>
          </div>
        </div>
        <!-- 右侧悬浮玻璃卡片 -->
        <div class="spot-banner-side">
          <div class="floating-card card-1">
            <div class="mini-img img-1"></div>
            <div class="floating-text">
              <span class="f-title">山岳景观</span>
              <span class="f-sub">云海 · 日出 · 森林</span>
            </div>
          </div>
          <div class="floating-card card-2">
            <div class="mini-img img-2"></div>
            <div class="floating-text">
              <span class="f-title">湖泊胜地</span>
              <span class="f-sub">蓝天 · 倒影 · 自然</span>
            </div>
          </div>
          <div class="floating-card card-3">
            <div class="mini-img img-3"></div>
            <div class="floating-text">
              <span class="f-title">古城文化</span>
              <span class="f-sub">历史 · 建筑 · 风情</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 主体内容：两侧留白 -->
    <div class="container">
      <!-- 搜索卡片 -->
      <div class="search-card">
        <div class="search-card-head">
          <span class="search-icon-wrap">
            <el-icon><Search /></el-icon>
          </span>
          <div>
            <h3>智能搜索</h3>
            <p>按名称、地区或分类快速找到你想去的景点</p>
          </div>
        </div>
        <div class="search-form">
          <div class="search-input-item">
            <AutoCompleteInput v-model="searchName" type="spot" placeholder="搜索景点名称、描述..." @search="handleSearch" />
          </div>
          <div class="search-input-item">
            <AutoCompleteInput v-model="searchArea" type="spotArea" placeholder="地点筛选" @search="handleSearch" />
          </div>
          <div class="search-btn-group">
            <el-button type="primary" class="btn-search" @click="handleSearch">搜索</el-button>
            <el-button class="btn-reset" @click="handleReset">重置</el-button>
          </div>
        </div>
        <div class="category-area">
          <div class="category-title">
            <el-icon><Grid /></el-icon>
            <span>景点分类</span>
          </div>
          <div class="tag-row">
            <el-tag
              effect="light"
              :class="{ 'is-active': currentCategory === 'all' }"
              @click="selectCategory('all')"
            >
              全部
            </el-tag>
            <el-tag
              v-for="item in categoryList.filter(c => c.status === '启用')"
              :key="item.id"
              effect="light"
              :class="{ 'is-active': currentCategory === item.id }"
              @click="selectCategory(item.id)"
            >
              {{ item.name }}
            </el-tag>
          </div>
        </div>
      </div>
      <!-- 结果标题 -->
      <div class="result-head">
        <div class="result-title">
          <span class="title-dot"></span>
          <h3>全部景点</h3>
          <span class="result-count">共 {{ totalCount }} 个目的地</span>
        </div>
      </div>
      <!-- 景点卡片列表 -->
      <div class="spot-card-list">
        <div class="spot-card" v-for="item in pageSpotList" :key="item.id">
          <div class="card-img-box">
            <div class="card-tag">{{ item.categoryName }}</div>
            <span class="price-tag">¥{{ item.price }}</span>
            <img :src="item.img" alt="">
            <div class="card-img-mask"></div>
          </div>
          <div class="card-body">
            <h4>{{ item.name }}</h4>
            <p class="addr">
              <el-icon><MapLocation /></el-icon>
              {{ item.area }}
            </p>
            <p class="desc">{{ item.desc }}</p>
            <el-button
              type="primary"
              size="small"
              class="detail-btn"
              @click="$router.push({ path: `/spotDetail/${item.id}` })"
            >
              查看详情
            </el-button>
          </div>
        </div>
      </div>
      <!-- 空状态 -->
      <div v-if="pageSpotList.length === 0" class="empty-state">
        <el-icon><Picture /></el-icon>
        <p>暂无符合条件的景点</p>
      </div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :total="totalCount"
          layout="prev, pager, next, jumper"
          :page-size="pageSize"
          jumper-text="前往"
          @change="loadSpotData"
        />
      </div>
    </div>
    <!-- 和攻略页一模一样的渐变分割线 -->
    <div class="page-divider"></div>
    <!-- Footer：样式1:1复刻攻略页，内部内容、点击筛选逻辑完全保留不变 -->
    <footer class="footer-wrap">
      <div class="footer-card">
        <h3 class="footer-title">热门目的地</h3>
        <div class="city-list">
          <div class="city-item" v-for="city in cityList" :key="city" @click="selectCity(city)">
            {{city}}
          </div>
        </div>
        <div class="footer-copyright">
          旅游信息系统 · 景点板块 &nbsp;&nbsp;© 2026 All Rights Reserved
        </div>
      </div>
    </footer>
  </div>
</template>
<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  House,
  MapLocation,
  Document,
  OfficeBuilding,
  Ticket,
  Search,
  Picture,
  Grid,
  List,
  Setting,
  Message
} from '@element-plus/icons-vue'
import { getFrontSpotListApi } from '@/api/spotData'
import { getCategoryListApi } from '@/api/categoryData'
import AutoCompleteInput from '@/components/AutoCompleteInput.vue'
const router = useRouter()
const route = useRoute()
const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))
const cityList = ref(['厦门','黄山','杭州','北京','桂林','成都','西安','大理','青岛','苏州','张家界','丽江','重庆','长沙'])
// 点击城市，填入搜索框并筛选
const selectCity = (city) => {
  searchArea.value = city
  handleSearch()
}
function getAvatarSrc(user) {
  if (user?.role === '管理员') {
    return '/admin-avatar.jpeg'
  }
  if (user?.avatar) {
    if (user.avatar.startsWith('http') || user.avatar.startsWith('/')) {
      return user.avatar
    }
    return new URL(`../assets/images/${user.avatar}`, import.meta.url).href
  }
  return new URL(`../assets/images/default-avatar.png`, import.meta.url).href
}
const logout = () => {
  sessionStorage.removeItem('userInfo')
  ElMessage.success('已退出登录')
  router.push('/login')
}
const currentCategory = ref('all')
const searchName = ref('')
const searchArea = ref('')
const currentPage = ref(1)
const pageSize = 6
const totalCount = ref(0)
const allSpotData = ref([])
const pageSpotList = ref([])
const categoryList = ref([])
const selectCategory = (val) => {
  currentCategory.value = val
  handleSearch()
  syncCategoryQuery(val)
}
async function loadSpotData() {
  const res = await getFrontSpotListApi()
  // 前台只展示启用状态的景点，后台禁用后立即不再出现
  allSpotData.value = res.filter(item => Number(item.status) === 1)
  filterAndSlice()
}
async function loadCategory() {
  const res = await getCategoryListApi()
  categoryList.value = res
}
function filterAndSlice() {
  let list = [...allSpotData.value]
  if (currentCategory.value !== 'all') {
    list = list.filter(item => item.categoryId === currentCategory.value)
  }
  if (searchName.value.trim()) {
    const keyword = searchName.value.trim().toLowerCase()
    list = list.filter(item =>
      item.name.toLowerCase().includes(keyword) ||
      item.desc.toLowerCase().includes(keyword)
    )
  }
  if (searchArea.value.trim()) {
    const areaKey = searchArea.value.trim().toLowerCase()
    list = list.filter(item => item.area.toLowerCase().includes(areaKey))
  }
  totalCount.value = list.length
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  pageSpotList.value = list.slice(start, end)
}
const handleSearch = () => {
  currentPage.value = 1
  filterAndSlice()
}
const handleReset = () => {
  searchName.value = ''
  searchArea.value = ''
  currentCategory.value = 'all'
  currentPage.value = 1
  filterAndSlice()
  if (Object.keys(route.query).length) {
    router.replace({ path: '/spot' })
  }
}

// 分类点击后同步到地址栏，刷新/回退也能保持当前分类
const syncCategoryQuery = (val) => {
  const name = categoryList.value.find(c => c.id === val)?.name
  const nextCategory = name || ''
  if ((route.query.category || '') === nextCategory) return
  const query = { ...route.query }
  if (name) {
    query.category = name
  } else {
    delete query.category
  }
  router.replace({ path: '/spot', query })
}

// 只跟随地址栏参数，不再把 currentCategory 当成依赖，避免把用户刚点的分类弹回去
const applyCategoryQuery = () => {
  const cateName = route.query.category
  if (!cateName) {
    if (currentCategory.value !== 'all') {
      currentCategory.value = 'all'
      currentPage.value = 1
      filterAndSlice()
    }
    return
  }
  const findItem = categoryList.value.find(c => c.name === cateName)
  const nextId = findItem ? findItem.id : 'all'
  if (currentCategory.value !== nextId) {
    currentCategory.value = nextId
    currentPage.value = 1
    filterAndSlice()
  }
}

const applySearchQuery = () => {
  const keyword = route.query.searchName
  if (keyword === undefined) return
  if (keyword !== searchName.value) {
    searchName.value = keyword
    handleSearch()
  }
}

watch(() => route.query.category, applyCategoryQuery)
watch(() => route.query.searchName, applySearchQuery)
onMounted(async () => {
  await loadCategory()
  await loadSpotData()
  applyCategoryQuery()
  applySearchQuery()
})
</script>
<style scoped>
.spot-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 12% 12%, rgba(14, 124, 134, 0.12), transparent 28%),
    radial-gradient(circle at 88% 18%, rgba(242, 140, 91, 0.10), transparent 32%),
    linear-gradient(180deg, #faf8f5 0%, #f7f4ee 45%, #f3f0e9 100%);
}
/* 顶部导航 */
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
/* ================= 【全新升级Banner CSS，替换原来旧banner样式】 ================= */
.spot-banner {
  width: 92%;
  max-width: 1200px;
  margin: 20px auto 0;
  border-radius: 20px;
  overflow: hidden;
  height: 240px;
  position: relative;
  box-shadow:
    0 20px 50px rgba(15, 60, 70, 0.18),
    0 8px 20px rgba(10, 79, 86, 0.12);
  background: none;
}
.spot-banner-inner {
  width: 100%;
  height: 100%;
  position: relative;
  background:
    linear-gradient(120deg, rgba(10, 79, 86, 0.55), rgba(242, 140, 91, 0.45), rgba(20, 154, 166, 0.55)),
    url("/mountain-cloud.jpeg") center / cover no-repeat;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 44px;
  box-sizing: border-box;
}
/* 左侧光晕 */
.spot-banner-glow {
  position: absolute;
  left: -80px;
  top: 50%;
  transform: translateY(-50%);
  width: 260px;
  height: 260px;
  background: radial-gradient(circle, rgba(242, 140, 91, 0.35), transparent 70%);
  filter: blur(20px);
  pointer-events: none;
}
/* 右侧装饰虚线圆环 */
.spot-banner-orbit {
  position: absolute;
  border-radius: 50%;
  border: 1px dashed rgba(255, 255, 255, 0.25);
  pointer-events: none;
}
.spot-banner-orbit.orbit-1 {
  right: 180px;
  top: 50%;
  transform: translateY(-50%);
  width: 260px;
  height: 260px;
}
.spot-banner-orbit.orbit-2 {
  right: 120px;
  top: 50%;
  transform: translateY(-50%);
  width: 380px;
  height: 380px;
}
/* 左侧文字区域 */
.spot-banner-text {
  position: relative;
  z-index: 3;
  color: #fff;
  max-width: 480px;
}
.spot-banner-tag {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.35);
  font-size: 12px;
  letter-spacing: 2px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 14px;
  backdrop-filter: blur(10px);
}
.spot-banner-text h1 {
  font-size: 34px;
  margin: 0 0 10px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 10px 25px rgba(0, 0, 0, 0.35);
}
.spot-banner-desc {
  font-size: 14px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.85);
  margin: 0 0 22px;
}
/* 底部数据统计栏 */
.spot-banner-stats {
  display: flex;
  gap: 28px;
}
.stat-item {
  display: flex;
  flex-direction: column;
}
.stat-num {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
}
.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
  margin-top: 2px;
}
/* 右侧悬浮玻璃卡片容器 */
.spot-banner-side {
  position: relative;
  z-index: 3;
  width: 340px;
  height: 100%;
}
.floating-card {
  position: absolute;
  right: 0;
  width: 200px;
  padding: 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(14px);
  display: flex;
  gap: 12px;
  align-items: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.18);
}
.card-1 {
  top: 24px;
  right: 40px;
}
.card-2 {
  top: 50%;
  transform: translateY(-50%);
  right: 120px;
}
.card-3 {
  bottom: 24px;
  right: 20px;
}
.mini-img {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  flex-shrink: 0;
  background-size: cover;
  background-position: center;
  border: 1px solid rgba(255, 255, 255, 0.4);
}
.img-1 {
  background-image: linear-gradient(135deg, #149aa6, #f28c5b);
}
.img-2 {
  background-image: linear-gradient(135deg, #2f9d6c, #7cc4ca);
}
.img-3 {
  background-image: linear-gradient(135deg, #f28c5b, #e8d5b7);
}
.floating-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.f-title {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}
.f-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
}
/* ====================================== */
/* 主体容器 */
.container {
  width: 92%;
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 80px;
}
/* 搜索卡片 */
.search-card {
  margin-top: 24px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.9);
  border-radius: 24px;
  padding: 28px;
  box-shadow: 0 10px 30px rgba(15, 60, 70, 0.08);
}
.search-card-head {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 20px;
}
.search-icon-wrap {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}
.search-card-head h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}
.search-card-head p {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
}
.search-form {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 14px;
  margin-bottom: 24px;
}
.search-input-item :deep(.el-input__wrapper) {
  border-radius: 999px;
  background: #fff;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.04);
}
.search-btn-group {
  display: flex;
  gap: 10px;
}
.btn-search {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  border-color: transparent;
  border-radius: 999px;
  padding: 0 28px;
}
.btn-search:hover {
  filter: brightness(1.08);
  box-shadow: 0 10px 24px rgba(14, 124, 134, 0.35);
}
.btn-reset {
  background: #fff;
  border: 1px solid #e5e7eb;
  color: #4b5563;
  border-radius: 999px;
  padding: 0 22px;
}
.btn-reset:hover {
  color: #0e7c86;
  border-color: #0e7c86;
}
/* 分类区 */
.category-area {
  padding-top: 20px;
  border-top: 1px dashed #e5e7eb;
}
.category-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #4b5563;
  margin-bottom: 14px;
}
.tag-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.tag-row :deep(.el-tag) {
  background-color: #fff;
  color: #4b5563;
  border: 1px solid #e5e7eb;
  border-radius: 999px;
  padding: 6px 16px;
  font-size: 13px;
  cursor: pointer;
}
.tag-row :deep(.el-tag:hover) {
  color: #0e7c86;
  border-color: #0e7c86;
}
.tag-row :deep(.el-tag.is-active) {
  color: #fff;
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  border-color: transparent;
}
/* 结果标题 */
.result-head {
  margin-top: 30px;
  margin-bottom: 18px;
}
.result-title {
  display: flex;
  align-items: center;
  gap: 12px;
}
.result-title h3 {
  margin: 0;
  font-size: 20px;
  color: #1f2937;
}
.title-dot {
  width: 8px;
  height: 26px;
  border-radius: 6px;
  background: linear-gradient(180deg, #0e7c86, #0e7c86);
}
.result-count {
  font-size: 13px;
  color: #6b7280;
  background: rgba(14, 124, 134, 0.1);
  padding: 4px 12px;
  border-radius: 999px;
}
/* 卡片列表 */
.spot-card-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}
.spot-card {
  border-radius: 20px;
  overflow: hidden;
  background: #fff;
  position: relative;
  padding-bottom: 46px;
  transition: all 0.35s ease;
  box-shadow: 0 10px 24px rgba(15, 60, 70, 0.08);
}
.spot-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(15, 60, 70, 0.14);
}
.card-img-box {
  position: relative;
  height: 220px;
  overflow: hidden;
}
.card-img-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0) 40%, rgba(0, 0, 0, 0.35) 100%);
  pointer-events: none;
}
.card-tag {
  position: absolute;
  top: 14px;
  left: 14px;
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  color: #fff;
  font-size: 12px;
  padding: 5px 14px;
  border-radius: 999px;
  z-index: 2;
}
.price-tag {
  position: absolute;
  top: 50px;
  left: 14px;
  background: rgba(255, 255, 255, 0.92);
  color: #1f2937;
  padding: 5px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  z-index: 2;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.18);
}
.card-img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.spot-card:hover .card-img-box img {
  transform: scale(1.07);
}
.card-body {
  padding: 18px;
}
.card-body h4 {
  margin: 0 0 8px;
  font-size: 17px;
  color: #1f2937;
}
.addr {
  color: #6b7280;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.6;
  margin: 10px 0;
}
.detail-btn {
  position: absolute;
  right: 16px;
  bottom: 16px;
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  border-color: transparent;
  color: #fff;
  border-radius: 10px;
  padding: 8px 18px;
  font-size: 13px;
}
.spot-card:hover .detail-btn {
  filter: brightness(1.08);
  box-shadow: 0 10px 24px rgba(14, 124, 134, 0.35);
}
/* 空状态 */
.empty-state {
  padding: 60px 0;
  text-align: center;
  color: #6b7280;
}
.empty-state :deep(.el-icon) {
  font-size: 46px;
  margin-bottom: 10px;
  color: #9ca3af;
}
/* 分页 */
.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  color: #fff;
}
/* ========== 分割线（和攻略页完全同款渐变线） ========== */
.page-divider {
 height:2px;
  background: linear-gradient(to right, transparent,#0e7c86,transparent);
  margin:40px 0;
}
/* ========== Footer 样式1:1复刻攻略页，只改外壳，内部内容逻辑不动 ========== */
.footer-wrap {
  width: 100%;
  padding: 0 20px 40px;
  box-sizing: border-box;
}
.footer-card {
  width: 92%;
  max-width:1400px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 24px;
  padding: 40px 32px;
  box-shadow: 0 4px 20px rgba(14,124,134,0.08);
}
.footer-title {
  text-align: center;
  font-size: 22px;
  color: #1f2937;
  font-weight: 600;
  margin: 0 0 28px;
}
.city-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
  margin-bottom: 32px;
}
.city-item {
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #fff;
  color: #4a5568;
  padding:14px 22px;
  transition: all 0.25s ease;
  cursor: pointer;
}
.city-item:hover {
  border-color: #0e7c86;
  color: #0e7c86;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(14,124,134,0.12);
}
.footer-copyright {
  text-align: center;
  font-size: 14px;
  color: #9ca3af;
}
@media (max-width: 900px) {
  .spot-banner {
    height: auto;
  }
  .spot-banner::before {
    display: none;
  }
  .spot-banner-inner {
    display: block;
    padding: 28px 24px;
  }
  .spot-banner-text {
    max-width: 100%;
  }
  .spot-banner-stats {
    position: static;
    margin-top: 20px;
  }
  .spot-banner-text h1 {
    font-size: 28px;
  }
}
</style>
