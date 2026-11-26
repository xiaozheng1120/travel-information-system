<template>
  <div class="hotel-page">
    <!-- 顶部导航（和首页/景点/攻略页完全统一，原样不动） -->
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
    <!-- Hero标题区 美化版本（纯渐变+装饰圆点，和攻略页风格统一，不使用外部图片） -->
    <div class="page-hero">
      <div class="hero-decor dot1"></div>
      <div class="hero-decor dot2"></div>
      <div class="hero-decor dot3"></div>
      <div class="hero-content">
        <div class="hero-tag">精选住宿</div>
        <h1>甄选周边住宿</h1>
        <p>舒适居所，伴你旅途，找到离景点最近的理想住处</p>
      </div>
    </div>
    <div class="container">
      <!-- 搜索筛选卡片 -->
      <div class="search-card">
        <div class="search-wrap">
          <div class="search-title">
            <el-icon><Search /></el-icon>
            <span>智能筛选</span>
          </div>
          <div class="search-row1">
            <AutoCompleteInput v-model="searchName" type="hotel" placeholder="搜索住宿名称或地址..." @search="handleSearch" />
            <el-select v-model="selectedSpot" placeholder="选择景点" clearable>
              <el-option label="全部景点" value="" />
              <el-option label="张家界" value="张家界" />
              <el-option label="大理洱海" value="大理洱海" />
              <el-option label="九寨沟" value="九寨沟" />
              <el-option label="厦门鼓浪屿" value="厦门鼓浪屿" />
              <el-option label="黄山" value="黄山" />
              <el-option label="杭州西湖" value="杭州西湖" />
              <el-option label="西安城墙" value="西安城墙" />
              <el-option label="上海迪士尼乐园" value="上海迪士尼乐园" />
              <el-option label="广州塔" value="广州塔" />
              <el-option label="宏村" value="宏村" />
              <el-option label="故宫博物院" value="故宫博物院" />
              <el-option label="芜湖方特东方神画" value="芜湖方特东方神画" />
            </el-select>
            <el-button type="primary" class="btn-search" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
          <hr class="search-divider" />
          <div class="search-row2">
            <div class="filter-item">
              <label>住宿类型</label>
              <el-select v-model="selectedType" placeholder="选择类型" clearable style="width:260px">
                <el-option label="全部" value="" />
                <el-option label="酒店" value="酒店" />
                <el-option label="民宿" value="民宿" />
                <el-option label="客栈" value="客栈" />
                <el-option label="度假村" value="度假村" />
                <el-option label="青旅" value="青旅" />
              </el-select>
            </div>
            <div class="filter-item">
              <label>价格区间</label>
              <div class="price-range">
                <el-input v-model="minPrice" placeholder="最低价" type="number" style="width:100px" />
                <span>-</span>
                <el-input v-model="maxPrice" placeholder="最高价" type="number" style="width:100px" />
              </div>
            </div>
            <div class="filter-item rate-item">
              <label>最低评分</label>
              <div class="rate-wrap">
                <span v-for="n in 5" :key="n" class="custom-rate-star" :class="{ active: n <= minScore }"
                  @click="minScore = n">
                  ★
                </span>
                <span class="rate-text">{{ minScore }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 标题+统计 -->
      <div class="list-head">
        <h2>全部住宿 <span class="total-text">共 {{ filterList.length }} 家住宿</span></h2>
      </div>
      <!-- 住宿卡片列表 -->
      <div class="hotel-card-list">
        <div class="hotel-card" v-for="item in pageHotelList" :key="item.id">
          <div class="card-img">
            <img :src="item.img" alt="">
            <div class="card-tags">
              <span class="tag type-tag">{{ item.type }}</span>
              <span class="tag price-tag">{{ item.priceRange }}</span>
            </div>
          </div>
          <div class="card-content">
            <h4>{{ item.name }}</h4>
            <p class="addr">
              <el-icon><MapLocation /></el-icon>
              {{ item.address }}
            </p>
            <p class="distance">
              <el-icon><MapLocation /></el-icon>
              <span class="distance-text">靠近{{ item.nearSpot }}</span>
              <span class="distance-num">{{ item.distance }}</span>
            </p>
            <p class="desc">{{ item.desc }}</p>
            <div class="card-bottom">
              <div class="score-line">
                <el-rate :model-value="item.score" disabled size="small" />
                <span class="score-text">{{ item.score }}分</span>
              </div>
              <el-button type="primary" class="btn-detail" size="small"
                @click="$router.push({ path: '/hotelDetail', query: { id: item.id, pageNum: currentPage } })">
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="filterList.length"
          layout="prev, pager, next" />
      </div>
    </div>
    <!-- 修改：白色底部容器 + 顶部分割线，和攻略页样式一致 -->
    <div class="footer-white">
      <div class="footer-divider"></div>
      <div class="footer-simple">
        <span>旅游信息系统 · 周边住宿板块</span>
        <span>©2026 旅游信息系统 All Rights Reserved</span>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  House,
  MapLocation,
  Document,
  OfficeBuilding,
  Ticket,
  Search, List, Setting
} from '@element-plus/icons-vue'
import { getHotelPageApi } from '@/api/hotelData.js'
import AutoCompleteInput from '@/components/AutoCompleteInput.vue'
const router = useRouter()
const route = useRoute()
const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))
const hotelList = ref([])
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
const logout = () => {
  sessionStorage.removeItem('userInfo')
  ElMessage.success('已退出登录')
  router.push('/login')
}
const searchName = ref('')
const selectedSpot = ref('')
const selectedType = ref('')
const minPrice = ref('')
const maxPrice = ref('')
const minScore = ref(0)
const currentPage = ref(1)
const pageSize = 12
const loadHotelData = async () => {
  const res = await getHotelPageApi(1, 1000, {})
  hotelList.value = res.records
}
const handleSearch = () => {
  currentPage.value = 1
}
const handleReset = () => {
  searchName.value = ''
  selectedSpot.value = ''
  selectedType.value = ''
  minPrice.value = ''
  maxPrice.value = ''
  minScore.value = 0
  currentPage.value = 1
}
const filterList = computed(() => {
  let list = hotelList.value
  if (searchName.value) {
    list = list.filter(item =>
      item.name.includes(searchName.value) ||
      item.address.includes(searchName.value)
    )
  }
  if (selectedSpot.value) {
    list = list.filter(item => item.nearSpot === selectedSpot.value)
  }
  if (selectedType.value) {
    list = list.filter(item => item.type === selectedType.value)
  }
  if (minScore.value > 0) {
    list = list.filter(item => item.score >= minScore.value)
  }
  if (minPrice.value || maxPrice.value) {
    list = list.filter(item => {
      const priceArr = item.priceRange.split('-')
      const itemMin = Number(priceArr[0])
      const itemMax = Number(priceArr[1])
      const inputMin = Number(minPrice.value)
      const inputMax = Number(maxPrice.value)
      let pass = true
      if (!isNaN(inputMin)) {
        pass = pass && itemMin >= inputMin
      }
      if (!isNaN(inputMax)) {
        pass = pass && itemMax <= inputMax
      }
      return pass
    })
  }
  return list
})
const pageHotelList = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filterList.value.slice(start, start + pageSize)
})
onMounted(async () => {
  await loadHotelData()
  if (route.query.pageNum) {
    currentPage.value = Number(route.query.pageNum)
  }
})
</script>
<style scoped>
.hotel-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 12% 12%, rgba(14, 124, 134, 0.12), transparent 28%),
    radial-gradient(circle at 88% 18%, rgba(242, 140, 91, 0.10), transparent 32%),
    linear-gradient(180deg, #faf8f5 0%, #f7f4ee 45%, #f3f0e9 100%);
}
/* ========== 导航栏（完全保留原样 ========== */
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
/* Hero头部横幅 美化后的样式，和攻略页风格统一，增加装饰圆点、标签层级 */
/* Hero头部横幅：压暗后更高级 */
.page-hero {
  width: 92%;
  max-width: 1200px;
  height: 240px;
  box-sizing: border-box;
  margin: 24px auto 0;
  border-radius: 20px;
  padding: 0 36px;
  color: #fff;
  background: linear-gradient(120deg, #0a4f56 0%, #0e7c86 58%, #149aa6 100%);
  box-shadow: 0 20px 46px rgba(10, 79, 86, 0.20);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
}
/* 右侧实景图，内缩成一张圆角图片卡 */
.page-hero::after {
  content: '';
  position: absolute;
  width: 42%;
  top: 12px;
  right: 12px;
  bottom: 12px;
  border-radius: 14px;
  background:
    linear-gradient(90deg, rgba(10, 79, 86, 0.58) 0%, rgba(10, 79, 86, 0.14) 48%, rgba(10, 79, 86, 0) 100%),
    url('/h13.jpeg') center / cover no-repeat;
  border: 1px solid rgba(232, 213, 183, 0.32);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.20);
  pointer-events: none;
}
.hero-decor {
  display: none;
}
.hero-content {
  position: relative;
  z-index: 2;
  max-width: 52%;
}
.dot1 {
  width: 160px;
  height: 160px;
  top: -60px;
  right: 80px;
}
.dot2 {
  width: 90px;
  height: 90px;
  bottom: 20px;
  right: 240px;
}
.dot3 {
  width: 60px;
  height: 60px;
  top: 40px;
  left: 50%;
}
.hero-tag {
  display: inline-block;
  padding: 6px 14px;
  background: #e8d5b7;
  border: 1px solid rgba(255, 255, 255, 0.30);
  border-radius: 999px;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 14px;
  width: fit-content;
  color: #0a4f56;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}
.hero-content h1 {
  margin: 0 0 10px;
  font-size: 34px;
  font-weight: 700;
  letter-spacing: 1px;
}
.hero-content p {
  margin: 0;
  font-size: 15px;
  opacity: 0.9;
  max-width: 540px;
  line-height: 1.7;
}
.container {
  width: 92%;
  max-width: 1400px;
  margin: 28px auto 0;
}
/* 搜索卡片 */
.search-card {
  background: rgba(255,255,255,0.82);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.95);
  border-radius: 24px;
  padding: 28px;
  margin-bottom:24px;
  box-shadow: 0 10px 30px rgba(15, 60, 70, 0.08);
}
.search-title {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 14px;
  font-size: 18px;
  color: #1f2937;
  font-weight:600;
}
.search-row1 {
  display: flex;
  gap: 12px;
  align-items: center;
}
.search-row1 :deep(.el-input),
.search-row1 :deep(.el-select) {
  flex: 1;
}
.search-row1 :deep(.el-input),
.search-row1 :deep(.el-select),
.search-row1 :deep(.el-button) {
  height: 38px;
}
.btn-search {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  border-color: transparent;
  border-radius: 999px;
}
.search-divider {
  margin: 18px 0;
  border: none;
  border-bottom: 1px solid #e5e7eb;
}
.search-row2 {
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  gap: 20px;
  flex-wrap: nowrap;
}
.filter-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.filter-item label {
  font-size: 14px;
  color: #333;
}
.rate-item {
  min-width: 230px;
  flex-shrink: 0;
}
.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
}
.price-range :deep(.el-input) {
  width: 100px;
}
.rate-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}
.custom-rate-star {
  font-size: 20px;
  color: #c0c4cc;
  cursor: pointer;
  margin-right: 2px;
  line-height: 1;
  transition: color 0.2s;
}
.custom-rate-star:hover {
  color: #f7ba2a;
}
.custom-rate-star.active {
  color: #ff9900;
}
.rate-text {
  margin-left: 8px;
  font-size: 14px;
  color: #606266;
}
/* 列表标题+统计 */
.list-head{
  display:flex;
  align-items:center;
  gap:10px;
  margin-bottom:20px;
}
.list-head h2{
  font-size:22px;
  color:#1f2937;
  margin:0;
}
.total-text{
  font-size:15px;
  color:#667894;
  font-weight:400;
}
/* 住宿卡片列表 */
.hotel-card-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}
.hotel-card {
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 22px rgba(15, 60, 70, 0.07);
  background: #fff;
  display: flex;
  flex-direction: column;
  transition: all 0.35s ease;
}
.hotel-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 22px 44px rgba(15, 60, 70, 0.14);
}
.card-img {
  position: relative;
  height: 210px;
  overflow:hidden;
}
.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.hotel-card:hover .card-img img {
  transform: scale(1.07);
}
.card-tags {
  position: absolute;
  left: 12px;
  top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #fff;
  backdrop-filter: blur(2px);
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.type-tag {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
}
.price-tag {
  background: rgba(230, 67, 64, 0.75);
}
.card-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.card-content h4 {
  margin: 0 0 10px;
  font-size: 17px;
  font-weight: 600;
  color: #1f2937;
}
.addr {
  font-size: 13px;
  color: #6b7280;
  margin: 4px 0;
  display: flex;
  align-items: center;
  gap: 4px;
}
.distance {
  font-size: 13px;
  color: #555;
  margin: 8px 0;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}
.distance-text {
  color: #555;
}
.distance-num {
  color: #0e7c86;
  font-weight: 700;
  font-size: 14px;
}
.desc {
  font-size: 13px;
  color: #6b7280;
  margin: 8px 0 16px;
  line-height: 1.6;
  flex: 1;
  display:-webkit-box;
  -webkit-box-orient:vertical;
  overflow:hidden;
}
.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.score-line {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.score-text {
  color: #ff9900;
  font-weight: 600;
}
.btn-detail {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  border: none;
  border-radius: 10px;
  font-size: 13px;
  padding: 7px 16px;
}
.pagination-wrap {
  margin-top: 36px;
  margin-bottom:40px;
  display: flex;
  justify-content: center;
}
.pagination-wrap :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  color: #fff;
}
/* 白色底部区域 + 分割线，和攻略页统一 */
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

@media (max-width: 900px) {
  .page-hero {
    height: auto;
    padding: 30px 24px;
  }
  .page-hero::after {
    display: none;
  }
  .hero-content {
    max-width: 100%;
  }
  .hero-content h1 {
    font-size: 28px;
  }
}
</style>