<template>
  <div class="home-page">
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
    <!-- Banner -->
    <div class="banner-wrap" @mouseenter="stopAutoPlay" @mouseleave="startAutoPlay">
      <div class="banner-main">
        <img
          v-for="(item, index) in activeBannerList"
          :key="item.id"
          :src="item.img"
          alt=""
          class="main-img"
          :class="{ show: currentIndex === index }"
        />
        <div class="banner-mask"></div>
        <div class="banner-text">
          <h1>{{ activeBanner?.subTitle }}</h1>
          <p>{{ activeBanner?.desc }}</p>
          <el-button class="discover-btn edge-beam edge-beam--cool" @click="handleBannerClick(activeBanner?.link)">
            Discover More
          </el-button>
        </div>
        <div class="arrow-left" @click="prevBanner">
          <el-icon><ArrowLeft /></el-icon>
        </div>
        <div class="arrow-right" @click="nextBanner">
          <el-icon><ArrowRight /></el-icon>
        </div>
        <div class="thumb-list">
          <div
            class="thumb-item"
            v-for="(item, index) in activeBannerList"
            :key="item.id"
            :class="{ active: currentIndex === index }"
            @click="currentIndex = index"
          >
            <img :src="item.img" alt="" />
          </div>
        </div>
      </div>
    </div>
    <!-- 搜索区 -->
    <div class="search-section">
      <div class="search-box">
        <el-select v-model="searchType" style="width:120px">
          <el-option label="景点" value="spot"></el-option>
          <el-option label="攻略" value="guide"></el-option>
        </el-select>
        <AutoCompleteInput
          v-model="searchKey"
          :type="searchType"
          placeholder="搜索目的地、景点、攻略..."
          class="search-input"
          @search="handleSearch" />        <el-button type="primary" class="search-btn" @click="handleSearch">搜索</el-button>
      </div>
    </div>
    <!-- 主体内容 -->
    <div class="container">
      <!-- 快速导航 -->
      <div class="section">
        <div class="section-head">
          <span class="section-icon">🚩</span>
          <h3>快速导航</h3>
        </div>
        <div class="nav-btn-group">
          <div class="nav-item nav-purple" @click="goSpotFilter('自然风光')">
            <div class="nav-item-bg"></div>
            <el-icon><Sunny /></el-icon>
            <div class="nav-item-title">自然风光</div>
            <small>山川湖海，自然奇趣</small>
          </div>
          <div class="nav-item nav-pink" @click="goSpotFilter('历史文化')">
            <div class="nav-item-bg"></div>
            <el-icon><House /></el-icon>
            <div class="nav-item-title">历史文化</div>
            <small>古迹名胜，文化传承</small>
          </div>
          <div class="nav-item nav-blue" @click="goSpotFilter('主题公园')">
            <div class="nav-item-bg"></div>
            <el-icon><Star /></el-icon>
            <div class="nav-item-title">主题公园</div>
            <small>欢乐体验，亲子时光</small>
          </div>
          <div class="nav-item nav-green" @click="goSpotFilter('城市地标')">
            <div class="nav-item-bg"></div>
            <el-icon><Location /></el-icon>
            <div class="nav-item-title">城市地标</div>
            <small>都市风光，现代建筑</small>
          </div>
          <div class="nav-item nav-orange" @click="goSpotFilter('乡村民俗')">
            <div class="nav-item-bg"></div>
            <el-icon><Food /></el-icon>
            <div class="nav-item-title">乡村民俗</div>
            <small>田园风情，民俗体验</small>
          </div>
        </div>
      </div>
      <!-- 精选景点 -->
      <div class="section">
        <div class="section-title">
          <div class="section-title-left">
            <span class="title-dot"></span>
            <h3>✨ 精选景点</h3>
          </div>
          <el-button text class="edge-beam edge-beam--pill" @click="$router.push('/spot')">查看全部 ></el-button>
        </div>
        <div class="card-list">
          <div class="card-item" v-for="spot in spotList" :key="spot.id">
            <div class="card-img-box">
              <span class="price-tag">¥{{ spot.price }}</span>
              <img :src="spot.img" alt="" />
            </div>
            <div class="card-body">
              <h4>{{ spot.name }}</h4>
              <p class="addr">
                <el-icon><MapLocation /></el-icon>
                {{ spot.location }}
              </p>
              <p class="desc">{{ spot.desc }}</p>
              <el-button
                type="primary"
                size="small"
                class="detail-btn"
                @click="goSpotDetail(spot.id)"
              >
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <!-- 旅游灵感 -->
      <div class="section">
        <div class="section-title">
          <div class="section-title-left">
            <span class="title-dot"></span>
            <h3>💡 旅游灵感</h3>
          </div>
          <el-button text class="edge-beam edge-beam--pill" @click="$router.push('/guide')">更多攻略 ></el-button>
        </div>
        <div class="guide-card-list">
          <div class="guide-card-item" v-for="guide in guideList" :key="guide.id">
            <div class="guide-card-img">
              <span class="view-badge">阅读 {{ guide.views }}</span>
              <img :src="guide.img" alt="" />
            </div>
            <div class="guide-card-info">
              <h4>{{ guide.title }}</h4>
              <div class="guide-meta">
                <span class="author">
                  <img :src="guide.avatar" alt="" class="author-avatar" />
                  {{ guide.author }}
                </span>
                <span class="date">{{ guide.createTime }}</span>
              </div>
              <el-button
                type="primary"
                size="small"
                class="detail-btn"
                @click="goGuideDetail(guide.id)"
              >
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ✅ 新增：主体和Footer之间的渐变分割线（和攻略页同款） -->
    <div class="home-footer-divider"></div>

    <!-- Footer -->
    <footer class="footer-wrap">
      <!-- 删掉原来footer内部那条线，这里注释保留 -->
      <!-- <div class="footer-top-accent"></div> -->
      <div class="footer-card">
        <div class="footer-container">
          <div class="footer-col">
            <div class="footer-brand">
              <div class="footer-logo-dot"></div>
              <div>
                <div class="footer-brand-name">旅游信息系统</div>
                <div class="footer-brand-sub">Travel Information System</div>
              </div>
            </div>
            <p class="footer-desc">
              旅游信息系统，提供景点查询、攻略分享、门票预订等一站式旅游服务，帮助用户轻松规划行程，发现更多目的地精彩。
            </p>
          </div>
          <div class="footer-col">
            <h3 class="footer-title">快速导航</h3>
            <ul class="footer-link-list">
              <li>
                <router-link to="/spot" class="footer-nav-link">
                  <span class="link-icon"><el-icon><MapLocation /></el-icon></span>
                  <span>景点推荐</span>
                </router-link>
              </li>
              <li>
                <router-link to="/guide" class="footer-nav-link">
                  <span class="link-icon"><el-icon><Document /></el-icon></span>
                  <span>旅游攻略</span>
                </router-link>
              </li>
              <li>
                <router-link to="/hotel" class="footer-nav-link">
                  <span class="link-icon"><el-icon><OfficeBuilding /></el-icon></span>
                  <span>周边住宿</span>
                </router-link>
              </li>
              <li>
                <router-link to="/ticket" class="footer-nav-link">
                  <span class="link-icon"><el-icon><Ticket /></el-icon></span>
                  <span>门票预订</span>
                </router-link>
              </li>
              <li>
                <router-link to="/order" class="footer-nav-link">
                  <span class="link-icon"><el-icon><List /></el-icon></span>
                  <span>我的订单</span>
                </router-link>
              </li>
            </ul>
          </div>
          <div class="footer-col">
            <h3 class="footer-title">帮助支持</h3>
            <ul class="footer-link-list">
              <li><router-link to="/faq">常见问题 FAQ</router-link></li>
              <li><router-link to="/book-notice">预订须知</router-link></li>
              <li><router-link to="/contact">联系我们</router-link></li>
              <li><router-link to="/privacy">隐私说明</router-link></li>
            </ul>
          </div>
          <div class="footer-col">
            <h3 class="footer-title">联系方式</h3>
            <div class="contact-card">
              <span class="contact-icon"><el-icon><Message /></el-icon></span>
              <div class="contact-info">
                <div class="contact-label">意见反馈</div>
                <div class="contact-email">demo_travel@163.com</div>
              </div>
            </div>
            <p class="footer-tip">如有意见或建议，欢迎随时联系我们。</p>
          </div>
        </div>
      </div>
      <div class="copyright">
        © 2026 旅游信息系统 · 版权所有
      </div>
    </footer>
  </div>
</template>
<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  Sunny,
  House,
  Star,
  Location,
  Food,
  MapLocation,
  Document,
  OfficeBuilding,
  Ticket,
  List,
  Setting,
  ArrowLeft,
  ArrowRight,
  Message
} from '@element-plus/icons-vue'
import { bannerList, getBannerPage } from '@/api/bannerData.js'
import { getGuidePageApi } from '@/api/guidedata'
import AutoCompleteInput from '@/components/AutoCompleteInput.vue'
import guide1 from '../assets/images/guide1.jpeg'
import guide2 from '../assets/images/guide2.jpeg'
import guide3 from '../assets/images/guide3.jpeg'
import avatar1 from '../assets/images/avatar1.jpeg'
import avatar2 from '../assets/images/avatar2.jpeg'
import avatar3 from '../assets/images/avatar3.jpeg'
const router = useRouter()
const route = useRoute()
const activeBannerList = computed(() => {
  return bannerList.value.filter(item => item.status === '启用')
})
const currentIndex = ref(0)
const activeBanner = computed(() => {
  if (activeBannerList.value.length === 0) return null
  return activeBannerList.value[currentIndex.value]
})
let timer = null
const startAutoPlay = () => {
  if (activeBannerList.value.length < 2) return
  timer = setInterval(() => {
    nextBanner()
  }, 4000)
}
const stopAutoPlay = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}
const prevBanner = () => {
  if (currentIndex.value <= 0) {
    currentIndex.value = activeBannerList.value.length - 1
  } else {
    currentIndex.value -= 1
  }
}
const nextBanner = () => {
  if (currentIndex.value >= activeBannerList.value.length - 1) {
    currentIndex.value = 0
  } else {
    currentIndex.value += 1
  }
}
const handleBannerClick = (link) => {
  if (link) {
    router.push(link)
  }
}
function preloadBannerImages(list) {
  list.forEach(item => {
    const img = new Image()
    img.src = item.img
  })
}
const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))
const searchKey = ref('')
const searchType = ref('spot')
const spotList = ref([])
const guideList = ref([])
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
const getSpotList = async () => {
  spotList.value = [
    { id: 1, name: '张家界', location: '湖南·张家界', desc: '奇峰三千，秀水八百，国家5A级景区', img: '/spot1.jpeg', price: '120' },
    { id: 2, name: '大理洱海', location: '云南·大理', desc: '苍山伴洱海，风花雪月的浪漫之地', img: '/spot2.jpeg', price: '80' },
    { id: 3, name: '九寨沟', location: '四川·阿坝', desc: '彩林碧水，被誉为人间童话世界', img: '/spot3.jpeg', price: '169' },
    { id: 4, name: '厦门鼓浪屿', location: '福建·厦门', desc: '海岛风情，万国建筑，文艺打卡胜地', img: '/spot4.jpeg', price: '90' }
  ]
}
const getGuideList = async () => {
  try {
    // 一次取全量攻略再排序，避免推荐攻略不在前 3 条时被漏掉
    const res = await getGuidePageApi(1, 100, {})
    const records = (res && res.records) ? res.records : []
    if (!records.length) throw new Error('empty')
    const sorted = [...records].sort((a, b) => {
      const recommendDiff = Number(b.isRecommend || 0) - Number(a.isRecommend || 0)
      if (recommendDiff !== 0) return recommendDiff
      return Number(b.id || 0) - Number(a.id || 0)
    })
    guideList.value = sorted.slice(0, 3).map((item) => ({
      id: item.id,
      title: item.title,
      author: item.author,
      createTime: formatHomeDate(item.date),
      views: item.views || 0,
      img: resolveGuideImage(item.img),
      avatar: resolveGuideImage(item.avatar)
    }))
  } catch (e) {
    // 后端异常时保留原来的静态兜底
    guideList.value = [
      { id: 1, title: '重庆洪崖洞三日游', author: '小李', createTime: '2025-06-02', views: 1568, img: guide1, avatar: avatar1 },
      { id: 2, title: '桂林阳朔二日游攻略', author: '小陈', createTime: '2025-06-05', views: 1024, img: guide2, avatar: avatar2 },
      { id: 3, title: '西安古城三日游', author: '小林', createTime: '2025-06-10', views: 1226, img: guide3, avatar: avatar3 }
    ]
  }
}

const resolveGuideImage = (name) => {
  if (!name) return guide1
  if (String(name).startsWith('http') || String(name).startsWith('/')) return name
  return new URL(`../assets/images/${name}`, import.meta.url).href
}

const formatHomeDate = (val) => {
  if (!val) return ''
  const d = new Date(val)
  if (isNaN(d.getTime())) return String(val).slice(0, 10)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}
const goSpotDetail = (id) => {
  router.push({ path: `/spotDetail/${id}` })
}
const goSpotFilter = (categoryName) => {
  router.push({
    path: '/spot',
    query: { category: categoryName }
  })
}
const handleSearch = () => {
  const keyword = searchKey.value.trim()
  if (!keyword) return
  if (searchType.value === 'spot') {
    router.push({
      path: '/spot',
      query: { searchName: keyword }
    })
  } else {
    router.push({
      path: '/guide',
      query: { searchName: keyword }
    })
  }
}
const goGuideDetail = (id) => {
  router.push({ path: `/guideDetail/${id}` })
}
onMounted(async () => {
  const res = await getBannerPage(1, 100, {})
  bannerList.value = res.records
  preloadBannerImages(bannerList.value)
  getSpotList()
  getGuideList()
  startAutoPlay()
})
onUnmounted(() => {
  stopAutoPlay()
})
</script>
<style scoped>
.home-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 8% 4%, rgba(14, 124, 134, 0.07), transparent 30%),
    radial-gradient(circle at 94% 14%, rgba(242, 140, 91, 0.06), transparent 28%),
    linear-gradient(180deg, #faf8f5 0%, #f7f4ee 100%);
}
/* 顶部导航 */
.nav-header {
  position: sticky;
  top: 0;
  z-index: 999;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: saturate(160%) blur(14px);
  -webkit-backdrop-filter: saturate(160%) blur(14px);
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 4px 18px rgba(15, 60, 70, 0.05);
}
.nav-container {
  width: 92%;
  max-width: 1200px;
  margin: 0 auto;
  height: 64px;
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
  box-shadow: 0 6px 16px rgba(14, 124, 134, 0.2);
}
.logo-text {
  font-size: 18px;
  font-weight: 600;
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
  color: #6b7280 !important;
  font-size: 14px;
  font-weight: 500;
  margin: 0 2px;
}
:deep(.el-menu-item:hover) {
  color: #0e7c86 !important;
  background: #f2fafa !important;
  border-radius: 8px;
}
:deep(.el-menu-item.is-active) {
  color: #0e7c86 !important;
  background: #e6f4f5 !important;
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
  background: #f2fafa;
  border: 1px solid #e6f4f5;
}
.user-name {
  color: #1f2937;
  font-size: 14px;
  font-weight: 500;
}
/* Banner */
.banner-wrap {
  width: 92%;
  max-width: 1200px;
  margin: 20px auto 0;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 24px 50px rgba(15, 60, 70, 0.16);
}
.banner-main {
  position: relative;
  height: 520px;
}
.main-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 0.8s ease-in-out;
  z-index: 1;
}
.main-img.show {
  opacity: 1;
  z-index: 2;
}
.banner-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(110deg, rgba(15, 35, 60, 0.55) 0%, rgba(15, 35, 60, 0.25) 40%, rgba(15, 35, 60, 0.08) 100%);
  z-index: 3;
}
.banner-text {
  position: absolute;
  left: 72px;
  top: 50%;
  transform: translateY(-50%);
  color: #fff;
  max-width: 520px;
  z-index: 4;
}
.banner-text h1 {
  font-size: 46px;
  margin: 0 0 14px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}
.banner-text p {
  font-size: 16px;
  margin: 0 0 24px;
  line-height: 1.7;
  opacity: 0.92;
  text-shadow: 0 4px 14px rgba(0, 0, 0, 0.3);
}
.discover-btn {
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.45);
  color: #fff;
  border-radius: 999px;
  padding: 12px 28px;
  font-size: 14px;
  backdrop-filter: blur(10px);
}
.arrow-left,
.arrow-right {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 46px;
  height: 46px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 4;
  border: 1px solid rgba(255, 255, 255, 0.35);
}
.arrow-left {
  left: 24px;
}
.arrow-right {
  right: 24px;
}
.thumb-list {
  position: absolute;
  right: 40px;
  bottom: 30px;
  display: flex;
  gap: 12px;
  z-index: 4;
}
.thumb-item {
  width: 96px;
  height: 128px;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid rgba(255, 255, 255, 0.35);
  transition: all 0.3s ease;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}
.thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.thumb-item.active {
  transform: translateY(-10px);
  border-color: #fff;
  box-shadow: 0 14px 34px rgba(0, 0, 0, 0.45);
}
/* 搜索区 */
.search-section {
  width: 92%;
  max-width: 1200px;
  margin: -26px auto 0;
  position: relative;
  z-index: 10;
}
.search-box {
  display: flex;
  justify-content: center;
  gap: 10px;
  align-items: center;
  padding: 12px;
  border-radius: 16px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 16px 36px rgba(15, 60, 70, 0.12);
}
.search-input {
  width: 520px;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 10px !important;
  background: #fff;
  box-shadow: 0 0 0 1px #e5e7eb inset !important;
}
.search-btn {
  border-radius: 10px !important;
  padding: 0 30px;
  height: 40px;
}
/* 主体 */
.container {
  width: 92%;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 80px;
}
.section {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 28px;
  margin-top: 24px;
  box-shadow: 0 8px 24px rgba(15, 60, 70, 0.06);
}
.section-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.section-head h3 {
  margin: 0;
  font-size: 20px;
  color: #1f2937;
}
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.section-title-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.section-title-left h3 {
  margin: 0;
  font-size: 20px;
  color: #1f2937;
}
.title-dot {
  width: 4px;
  height: 22px;
  border-radius: 4px;
  background: linear-gradient(180deg, #149aa6, #0e7c86);
}
/* 快速导航 */
.nav-btn-group {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}
.nav-item {
  position: relative;
  border-radius: 14px;
  padding: 24px 16px;
  text-align: center;
  color: #1f2937;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.25s ease;
  border: 1px solid #e5e7eb;
  background: #fff;
  box-shadow: 0 6px 18px rgba(15, 60, 70, 0.05);
}
.nav-item-bg {
  position: absolute;
  inset: 0;
  opacity: 0.55;
  transition: all 0.3s ease;
  z-index: 0;
}
.nav-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 30px rgba(15, 60, 70, 0.1);
}
.nav-item:hover .nav-item-bg {
  opacity: 0.78;
  transform: scale(1.03);
}
.nav-item :deep(.el-icon) {
  position: relative;
  z-index: 1;
  font-size: 26px;
  margin-bottom: 10px;
  color: #0e7c86;
}
.nav-item-title {
  position: relative;
  z-index: 1;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}
.nav-item small {
  position: relative;
  z-index: 1;
  font-size: 12px;
  color: #6b7280;
}
.nav-purple .nav-item-bg {
  background: linear-gradient(135deg, #e6f4f5, #f2fafa);
}
.nav-pink .nav-item-bg {
  background: linear-gradient(135deg, #f7efe3, #fdf8ef);
}
.nav-blue .nav-item-bg {
  background: linear-gradient(135deg, #e8f1f2, #f5fafa);
}
.nav-green .nav-item-bg {
  background: linear-gradient(135deg, #edf6ef, #f7fbf8);
}
.nav-orange .nav-item-bg {
  background: linear-gradient(135deg, #fdf0e9, #fff8f4);
}
/* 景点卡片 */
.card-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.card-item {
  border-radius: 14px;
  overflow: hidden;
  background: #fff;
  position: relative;
  padding-bottom: 46px;
  transition: all 0.25s ease;
  box-shadow: 0 6px 18px rgba(15, 60, 70, 0.05);
}
.card-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 30px rgba(15, 60, 70, 0.1);
}
.card-img-box {
  position: relative;
  height: 180px;
  overflow: hidden;
}
.card-img-box::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 60%;
  background: linear-gradient(to top, rgba(15, 35, 60, 0.45), transparent);
  pointer-events: none;
  z-index: 1;
}
.price-tag {
  position: absolute;
  top: 14px;
  left: 14px;
  background: rgba(255, 255, 255, 0.94);
  color: #0e7c86;
  padding: 5px 13px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 700;
  z-index: 2;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.14);
}
.card-img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.card-item:hover .card-img-box img {
  transform: scale(1.07);
}
.card-body {
  padding: 16px;
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
  margin-top: 8px;
}
.detail-btn {
  position: absolute;
  right: 14px;
  bottom: 14px;
  background: #0e7c86;
  border-color: #0e7c86;
  color: #fff;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 13px;
}
.card-item:hover .detail-btn {
  filter: brightness(1.06);
  box-shadow: 0 8px 20px rgba(14, 124, 134, 0.26);
}
/* 攻略卡片 */
.guide-card-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.guide-card-item {
  display: flex;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  height: 200px;
  transition: all 0.25s ease;
  box-shadow: 0 6px 18px rgba(15, 60, 70, 0.05);
}
.guide-card-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 30px rgba(15, 60, 70, 0.1);
}
.guide-card-img {
  position: relative;
  width: 42%;
  flex-shrink: 0;
  overflow: hidden;
}
.guide-card-img::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 60%;
  background: linear-gradient(to top, rgba(15, 35, 60, 0.45), transparent);
  pointer-events: none;
  z-index: 1;
}
.guide-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.guide-card-item:hover .guide-card-img img {
  transform: scale(1.07);
}
.view-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(15, 35, 60, 0.55);
  color: #fff;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  z-index: 2;
}
.guide-card-info {
  flex: 1;
  padding: 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.guide-card-info h4 {
  margin: 0 0 10px;
  font-size: 17px;
  color: #1f2937;
}
.guide-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 13px;
  color: #6b7280;
}
.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 6px;
  vertical-align: middle;
}
.author {
  display: inline-flex;
  align-items: center;
  color: #4b5563;
}
.date {
  color: #6b7280;
}
.guide-card-info .detail-btn {
  position: static;
  right: auto;
  bottom: auto;
  align-self: flex-start;
}

/* ✅ 新增分割线样式，和攻略页一模一样 */
.home-footer-divider {
height:2px;
  background: linear-gradient(to right, transparent,#0e7c86,transparent);
  margin:40px 0;
}

/* Footer */
.footer-wrap {
  margin-top: 0;
  background: transparent;
}
/* 隐藏原来footer内部那条线 */
.footer-top-accent {
  display: none;
}
.footer-card {
  width: 92%;
  max-width: 1400px;
  margin: 0 auto;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 20px 50px rgba(15, 60, 70, 0.12);
  overflow: hidden;
}
.footer-container {
  display: grid;
  grid-template-columns: 1.4fr 1fr 1fr 1.1fr;
  gap: 40px;
  padding: 44px 40px 36px;
}
.footer-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 18px;
  position: relative;
  padding-left: 12px;
}
.footer-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 14px;
  border-radius: 2px;
  background: linear-gradient(180deg, #0e7c86, #0e7c86);
}
.footer-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}
.footer-logo-dot {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  box-shadow: 0 6px 16px rgba(14, 124, 134, 0.35);
}
.footer-brand-name {
  font-size: 17px;
  font-weight: 700;
  color: #1f2937;
}
.footer-brand-sub {
  font-size: 11px;
  letter-spacing: 2px;
  color: #0e7c86;
  margin-top: 2px;
}
.footer-desc {
  line-height: 1.75;
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}
.footer-link-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.footer-link-list li {
  margin-bottom: 13px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.link-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: #f0f5ff;
  color: #0e7c86;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}
.footer-link-list a {
  color: #4b5563;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}
.footer-link-list a:hover {
  color: #0e7c86;
  transform: translateX(3px);
}
.footer-nav-link {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}
.contact-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: linear-gradient(135deg, #f5f8ff, #eef2ff);
  border: 1px solid #e1eaff;
  border-radius: 14px;
  margin-bottom: 14px;
}
.contact-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}
.contact-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 2px;
}
.contact-email {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}
.footer-tip {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}
.copyright {
  text-align: center;
  padding-top: 24px;
  font-size: 13px;
  color: #6b7280;
}
</style>
