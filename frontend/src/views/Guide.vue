<template>
  <div class="guide-page">
    <!-- 顶部导航（和景点页完全一致） -->
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
          <!-- 管理员后台入口，管理员角色才显示 -->
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
      <!-- 攻略页顶部Banner【升级进阶渐变毛玻璃版本】 -->
      <div class="page-banner">
        <div class="banner-text">
          <h2>
            <el-icon><MapLocation /></el-icon>
            探索精彩旅游攻略
          </h2>
          <p>查看来自旅行者真实分享的行程、路线与旅行小贴士，开启你的旅途规划。</p>
        </div>
        <div class="banner-data">
          <div class="data-item">
            <div class="num">{{ total }}</div>
            <div class="label">篇攻略</div>
          </div>
          <div class="data-item">
            <div class="num">5</div>
            <div class="label">大分类</div>
          </div>
          <div class="data-item">
            <div class="num">24h</div>
            <div class="label">持续更新</div>
          </div>
        </div>
      </div>
      <!-- 搜索卡片【对齐景点搜索区样式，磨砂透明背景】 -->
      <div class="search-card">
        <div class="search-title" style="display:flex;align-items:center;gap:8px;margin-bottom:16px;">
          <el-icon><Search /></el-icon>
          <span>智能搜索</span>
        </div>
        <div class="search-row" style="display:flex;gap:12px;margin-bottom:16px;align-items:center;">
          <AutoCompleteInput v-model="searchName" type="guide" placeholder="搜索攻略标题、内容..." style="flex:1" @search="handleSearch" />
          <AutoCompleteInput v-model="searchArea" type="guideAuthor" placeholder="作者筛选" style="flex:1" @search="handleSearch" />
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button style="background:#67c23a;border-color:#67c23a;color:#fff;border-radius:999px;"
            @click="openAddDialog">
            发布攻略
          </el-button>
        </div>
        <div style="margin-bottom:8px;color:#666;display:flex;align-items:center;gap:6px;">
          <el-icon><Grid /></el-icon>
          攻略分类
        </div>
        <div class="tag-row" style="display:flex;gap:8px;">
          <el-tag effect="light" :class="{ 'is-active': currentCategory === 'all' }"
            @click="currentCategory = 'all'">全部</el-tag>
          <el-tag effect="light" :class="{ 'is-active': currentCategory === '自然风光' }"
            @click="currentCategory = '自然风光'">自然风光</el-tag>
          <el-tag effect="light" :class="{ 'is-active': currentCategory === '古城古镇' }"
            @click="currentCategory = '古城古镇'">古城古镇</el-tag>
          <el-tag effect="light" :class="{ 'is-active': currentCategory === '城市人文' }"
            @click="currentCategory = '城市人文'">城市人文</el-tag>
          <el-tag effect="light" :class="{ 'is-active': currentCategory === '滨海休闲' }"
            @click="currentCategory = '滨海休闲'">滨海休闲</el-tag>
        </div>
      </div>
      <!-- ========== 【改动点：标题栏（全部攻略 + 共xx篇放在同一行相邻） ========== -->
      <div class="page-title-row">
        <div class="title-left">
          <div class="blue-line"></div>
          <h2>全部攻略</h2>
          <div class="title-tip">共{{ total }}篇攻略文章</div>
        </div>
      </div>
      <!-- 攻略卡片列表【重点：卡片移除左上角分类标签，结构对齐景点卡片】 -->
      <div class="guide-card-list">
        <div class="guide-card" v-for="item in pageList" :key="item.id">
          <div class="card-img-box">
            <img :src="getImgUrl(item.img)" alt="">
          </div>
          <div class="card-body">
            <h4>{{ item.title }}</h4>
            <p class="author">
              <img :src="getImgUrl(item.avatar)" alt="" class="author-avatar" />
              {{ item.author }}
            </p>
            <p class="time">{{ formatTime(item.date) }}</p>
            <div class="card-btn-group">
              <el-button type="primary" size="small" class="detail-btn" @click="$router.push({
                  path: `/guideDetail/${item.id}`,
                  query: {
                    pageNum: currentPage,
                    fromPage: 'guide'
                  }
                })">查看详情</el-button>
              <template v-if="item.isFixed === 0">
                <el-button type="warning" size="small" @click="openEditDialog(item)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDelete(item.id)">删除</el-button>
              </template>
            </div>
          </div>
        </div>
      </div>
      <div class="pagination-wrap" style="margin-top:24px;display:flex;justify-content:center;">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total"
          layout="prev, pager, next" />
      </div>
      <!-- ===== 新增：渐变分割线 ===== -->
      <div class="guide-divider"></div>
    </div>
    <!-- 攻略页面专属Footer（替换为专题专属Footer） -->
    <div class="guide-footer">
      <div class="footer-card">
        <div class="footer-section-title">
          <span>攻略专题推荐</span>
        </div>
        <!-- 专题卡片区域 -->
        <div class="footer-topics">
          <div class="topic-card" @click="$router.push('/guide?currentCategory=自然风光')">
            <div class="topic-cover topic-cover-1">
              <div class="topic-mask"></div>
              <div class="topic-icon">🏔</div>
            </div>
            <div class="topic-body">
              <div class="topic-title">自然风光</div>
              <div class="topic-desc">山川、湖泊、海岸与自然奇观</div>
            </div>
          </div>
          <div class="topic-card" @click="$router.push('/guide?currentCategory=古城古镇')">
            <div class="topic-cover topic-cover-2">
              <div class="topic-mask"></div>
              <div class="topic-icon">🏯</div>
            </div>
            <div class="topic-body">
              <div class="topic-title">古城古镇</div>
              <div class="topic-desc">老街、古建与慢旅行路线</div>
            </div>
          </div>
          <div class="topic-card" @click="$router.push('/guide?currentCategory=城市人文')">
            <div class="topic-cover topic-cover-3">
              <div class="topic-mask"></div>
              <div class="topic-icon">🌆</div>
            </div>
            <div class="topic-body">
              <div class="topic-title">城市人文</div>
              <div class="topic-desc">城市地标、博物馆与街区漫游</div>
            </div>
          </div>
          <div class="topic-card" @click="$router.push('/guide?currentCategory=滨海休闲')">
            <div class="topic-cover topic-cover-4">
              <div class="topic-mask"></div>
              <div class="topic-icon">🌊</div>
            </div>
            <div class="topic-body">
              <div class="topic-title">滨海休闲</div>
              <div class="topic-desc">海岛、沙滩与海滨度假攻略</div>
            </div>
          </div>
        </div>
        <!-- 底部版权信息（修改为居中） -->
        <div class="footer-copyright-bar">
          <span>旅游信息系统 · 攻略板块</span>
          <span class="dot">·</span>
          <span>© 2026 All Rights Reserved</span>
        </div>
      </div>
    </div>
    <!-- 发布/编辑攻略弹窗（业务逻辑完全保留原样，无改动） -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑攻略' : '发布攻略'" width="550px">
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
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="填写攻略行程介绍"></el-input>
        </el-form-item>
        <el-form-item label="攻略图片">
          <el-radio-group v-model="form.img">
            <el-radio v-for="pic in imgOptions" :key="pic.value" :label="pic.value">
              <img :src="pic.value"
                style="width:100px;height:60px;object-fit:cover;border-radius:4px;margin-right:6px" />
              {{ pic.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="作者头像">
          <el-radio-group v-model="form.avatar">
            <el-radio v-for="pic in avatarOptions" :key="pic.value" :label="pic.value">
              <img :src="pic.value"
                style="width:100px;height:60px;object-fit:cover;border-radius:4px;margin-right:6px" />
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
  </div>
</template>
<script setup>
import { ref, watch, onMounted, watchEffect } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, House, MapLocation, Document, OfficeBuilding, Ticket, Search, Grid, List, Setting } from '@element-plus/icons-vue'
// 图片资源
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
// 接口
import { getGuidePageApi, addGuideApi, updateGuideApi, deleteGuideApi } from '@/api/guideData'
import AutoCompleteInput from '@/components/AutoCompleteInput.vue'
const router = useRouter()
const route = useRoute()
const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))
const imgMap = {
  'guide1.jpeg': guide1,
  'guide2.jpeg': guide2,
  'guide3.jpeg': guide3,
  'guide4.jpeg': guide4,
  'guide5.jpeg': guide5,
  'guide6.jpeg': guide6,
  'guide7.jpeg': guide7,
  'guide8.jpeg': guide8,
  'guide9.png': guide9,
  'avatar1.jpeg': avatar1,
  'avatar2.jpeg': avatar2,
  'avatar3.jpeg': avatar3,
  'avatar4.jpeg': avatar4,
  'avatar5.jpeg': avatar5,
  'avatar6.jpeg': avatar6,
  'avatar7.jpeg': avatar7,
  'avatar8.jpeg': avatar8,
}
function getImgUrl(filename) {
  if(!filename) return ''
  return imgMap[filename] || filename
}
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
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)
const tableData = ref([])
const currentCategory = ref('all')
const searchName = ref('')
const searchArea = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const form = ref({
  title: '',
  author: '',
  category: '',
  content: '',
  img: '',
  avatar: '',
  isFixed: 0
})
const imgOptions = [
  { label: 'guide1洪崖洞', value: guide1 },
  { label: 'guide2阳朔', value: guide2 },
  { label: 'guide3西安', value: guide3 },
  { label: 'guide4荔波小七孔', value: guide4 },
  { label: 'guide5乌镇', value: guide5 },
  { label: 'guide6大连', value: guide6 },
  { label: 'guide7鸣沙山', value: guide7 },
  { label: 'guide8德天瀑布', value: guide8 },
  { label: 'guide9老君山', value: guide9 },
]
const avatarOptions = [
  { label: 'avatar1', value: avatar1 },
  { label: 'avatar2', value: avatar2 },
  { label: 'avatar3', value: avatar3 },
  { label: 'avatar4', value: avatar4 },
  { label: 'avatar5', value: avatar5 },
  { label: 'avatar6', value: avatar6 },
  { label: 'avatar7', value: avatar7 },
  { label: 'avatar8', value: avatar8 },
]
async function loadData() {
  const queryObj = {}
  if(currentCategory.value !== 'all'){
    queryObj.category = currentCategory.value
  }
  if(searchName.value.trim()){
    queryObj.title = searchName.value.trim()
  }
  if(searchArea.value.trim()){
    queryObj.author = searchArea.value.trim()
  }
  const res = await getGuidePageApi(currentPage.value, pageSize.value, queryObj)
  tableData.value = res.records
  total.value = res.total
}
// ✅ 改成 function，有函数提升，前面watchEffect可以直接调用
function handleSearch() {
  currentPage.value = 1
  loadData()
}
function handleReset() {
  searchName.value = ''
  searchArea.value = ''
  currentCategory.value = 'all'
  currentPage.value = 1
  loadData()
}
watch(currentPage, () => {
  loadData()
})
watch(currentCategory, () => {
  currentPage.value = 1
  loadData()
})
// 和景点页保持一致的路由监听
watchEffect(() => {
  if (route.query.currentCategory) {
    currentCategory.value = route.query.currentCategory
    currentPage.value = 1
    loadData()
  }
  if (route.query.searchName) {
    searchName.value = route.query.searchName
    handleSearch()
  }
})
onMounted(() => {
  loadData()
  if (route.query.page) {
    currentPage.value = Number(route.query.page)
  }
})
const formatTime = (dateStr) => {
  const date = new Date(dateStr)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const mm = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${hh}:${mm}`
}
const openAddDialog = () => {
  isEdit.value = false
  editId.value = null
  form.value = {
    title: '',
    author: '',
    category: '',
    content: '',
    img: '',
    avatar: '',
    isFixed: 0
  }
  dialogVisible.value = true
}
const openEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}
const submitForm = async () => {
  if (isEdit.value) {
    form.value.id = editId.value
    await updateGuideApi(form.value)
    ElMessage.success("修改成功")
  } else {
    await addGuideApi(form.value)
    ElMessage.success("新增成功")
  }
  dialogVisible.value = false
  currentPage.value = 1
  loadData()
}
const handleDelete = async (id) => {
  await deleteGuideApi(id)
  ElMessage.success("删除成功")
  currentPage.value = 1
  loadData()
}
const pageList = tableData
</script>

<style scoped>
.guide-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 12% 12%, rgba(14, 124, 134, 0.12), transparent 28%),
    radial-gradient(circle at 88% 18%, rgba(242, 140, 91, 0.10), transparent 32%),
    linear-gradient(180deg, #faf8f5 0%, #f7f4ee 45%, #f3f0e9 100%);
}
/* ============ 导航栏（和景点页完全一致） ============ */
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
/* ============ 左文右图横幅 + 深青蒙版 + 暖沙数据卡 ============ */
.page-banner {
  width: 100%;
  height: 240px;
  border-radius: 20px;
  background: linear-gradient(120deg, #0a4f56 0%, #0e7c86 58%, #149aa6 100%);
  margin: 20px 0;
  padding: 32px 36px;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 18px 40px rgba(10, 79, 86, 0.18);
  overflow: hidden;
  position: relative;
}
/* 右侧实景图，内缩成一张圆角图片卡 */
.page-banner::before {
  content: '';
  position: absolute;
  width: 42%;
  top: 12px;
  right: 12px;
  bottom: 12px;
  border-radius: 14px;
  background:
    linear-gradient(90deg, rgba(10, 79, 86, 0.58) 0%, rgba(10, 79, 86, 0.16) 48%, rgba(10, 79, 86, 0) 100%),
    url('@/assets/images/guide2.jpeg') center / cover no-repeat;
  border: 1px solid rgba(232, 213, 183, 0.30);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.20);
  pointer-events: none;
}
.banner-text {
  animation: fadeLeft 0.8s ease-out;
  max-width: 52%;
  position: relative;
  z-index: 2;
}
.banner-text h2 {
  font-size: 34px;
  margin: 0 0 10px;
  font-weight: 700;
  color: #ffffff;
  display: flex;
  align-items: center;
  gap: 12px;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
}
.banner-text p {
  margin: 0;
  color: rgba(255, 255, 255, 0.78);
  letter-spacing: 1px;
  font-size: 15px;
  line-height: 1.6;
}
.banner-data {
  display: flex;
  gap: 12px;
  position: absolute;
  right: 36px;
  bottom: 24px;
  z-index: 3;
}
.data-item {
  text-align: center;
  background: rgba(10, 79, 86, 0.62);
  padding: 12px 8px;
  width: 96px;
  border-radius: 12px;
  backdrop-filter: blur(16px);
  border: 1px solid rgba(232, 213, 183, 0.38);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.14), 0 8px 20px rgba(0, 0, 0, 0.16);
  transition: 0.3s ease;
  position: relative;
  animation: popCard 0.6s ease-out backwards;
}
/* 卡片顶部日落橙细装饰条 */
.data-item::before {
  content: "";
  position: absolute;
  top: 0;
  left: 15%;
  right: 15%;
  height: 3px;
  background: #f28c5b;
  border-radius: 0 0 6px 6px;
}
/* 三个卡片延迟依次弹出 */
.data-item:nth-child(1){animation-delay:0.2s;}
.data-item:nth-child(2){animation-delay:0.4s;}
.data-item:nth-child(3){animation-delay:0.6s;}
.data-item:hover {
  transform: translateY(-4px);
  background: rgba(10, 79, 86, 0.76);
  border-color: rgba(232, 213, 183, 0.58);
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.22);
}
.data-item .num {
  font-size: 30px;
  font-weight: 700;
  line-height: 1;
  color: #e8d5b7;
}
.data-item .label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.78);
  margin-top: 6px;
}
/* 载入动画 */
@keyframes fadeLeft {
  from{opacity:0;transform: translateX(-30px);}
  to{opacity:1;transform: translateX(0);}
}
@keyframes popCard {
  from{opacity:0;transform: translateY(20px);}
  to{opacity:1;transform: translateY(0);}
}
/* ========= 标题栏样式（全部攻略 + 蓝色竖线 + 共xx篇放在相邻位置） ========= */
.page-title-row {
  /* 去掉 justify-content: space-between; */
  margin: 24px 0 16px;
}
.title-left {
  display: flex;
  align-items: center;
  gap:10px;
}
.blue-line {
  width: 6px;
  height: 20px;
  border-radius: 99px;
  background: linear-gradient(180deg, #0e7c86, #0e7c86);
}
.title-left h2 {
  font-size: 20px;
  font-weight:700;
  color:#1f2937;
  margin:0;
}
.title-tip {
  font-size:16px;
  color:#6b82a2;
  background:rgba(14, 124, 134,0.08);
  padding:6px 18px;
  border-radius:999px;
}
/* ============ 主体容器 ============ */
.container {
  width: 92%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 10px 0 30px;
}
/* ============ 搜索卡片：修改为磨砂透明效果，和景点页统一 ============ */
.search-card {
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border-radius: 20px;
  box-shadow:
    0 10px 24px rgba(15, 60, 70, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  padding: 28px;
  margin: 20px 0;
}
.search-title{
  font-size:16px;
  font-weight:600;
  color:#1f2937;
}
.tag-row :deep(.el-tag) {
  background-color: #fff;
  color: #4b5563;
  border: 1px solid #e5e7eb;
  border-radius: 999px;
  padding: 7px 18px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.25s ease;
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
.search-row :deep(.el-input__wrapper) {
  background-color: #ffffff;
  border-radius: 999px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.04);
}
.search-row :deep(.el-input__wrapper:hover),
.search-row :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #0e7c86 inset;
}
.search-row :deep(.el-button--primary) {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  border-color: transparent;
  color: #fff;
  border-radius: 999px;
}
.search-row :deep(.el-button--primary:hover) {
  filter: brightness(1.08);
  box-shadow: 0 10px 24px rgba(14, 124, 134, 0.35);
}
.search-row :deep(.el-button:not(.el-button--primary)) {
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  color: #4b5563;
  border-radius: 999px;
}
.search-row :deep(.el-button:not(.el-button--primary):hover) {
  color: #0e7c86;
  border-color: #0e7c86;
}
/* 发布攻略按钮美化 */
.search-row :deep(.el-button[style*="background:#67c23a"]){
  transition: 0.3s ease;
}
.search-row :deep(.el-button[style*="background:#67c23a"]:hover){
  background:#76d848 !important;
  box-shadow:0 8px 20px rgba(103, 194, 58,0.25);
}
.guide-card-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 28px;
}
.guide-card {
  border: none;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 24px rgba(15, 60, 70, 0.08);
  position: relative;
  padding-bottom: 54px;
  background: #fff;
  transition: all 0.35s ease;
}
.guide-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 22px 44px rgba(15, 60, 70, 0.16);
}
.card-img-box {
  position: relative;
  height: 210px;
  overflow: hidden;
}
.card-img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
.guide-card:hover .card-img-box img {
  transform: scale(1.06);
}
.card-body {
  padding: 20px;
}
.card-body h4 {
  margin: 0 0 10px;
  font-size: 17px;
  font-weight:600;
  color: #1f2937;
  /* 多行省略，修复VS Code黄色波浪警告，功能不变 */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  /*! autoprefixer: off */
  -webkit-box-orient: vertical;
  /*! autoprefixer: on */
}
.author {
  color: #6b7280;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 6px 0;
}
.author-avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}
.time {
  font-size: 12px;
  color: #6b7280;
  margin: 6px 0 12px;
}
.card-btn-group {
  position: absolute;
  right: 18px;
  bottom: 18px;
  display: flex;
  gap: 8px;
}
.detail-btn {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  border-color: transparent;
  color: #fff;
  border-radius: 10px;
}
.pagination-wrap :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  color: #fff;
}
/* 分页全局美化 */
.pagination-wrap :deep(.el-pagination){
  --el-color-primary: #0e7c86;
}
/* ===== 新增分割线样式 ===== */
.guide-divider{
  height:2px;
  background: linear-gradient(to right, transparent,#0e7c86,transparent);
  margin:40px 0;
}
/* =========攻略页底部：专题推荐 ========= */
.guide-footer {
  margin-top: 0;
  width: 100%;
}
.footer-card {
  background: linear-gradient(180deg, #ffffff 0%, #f7faff 100%);
  border-radius: 24px;
  padding: 40px 36px 28px;
  box-shadow: 0 10px 30px rgba(15, 60, 70, 0.08);
  position: relative;
  overflow: hidden;
}
.footer-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #0e7c86, #0e7c86);
}
/* 顶部标题（修改为居中，去掉圆点和副标题） */
.footer-section-title {
  text-align: center;
  margin-bottom: 24px;
}
.footer-section-title > span {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 1px;
}
/* 专题卡片区域 */
.footer-topics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}
.topic-card {
  background: #ffffff;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: 0.35s ease;
  box-shadow: 0 6px 18px rgba(15, 60, 70, 0.06);
}
.topic-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 32px rgba(15, 60, 70, 0.18);
}
.topic-cover {
  height: 96px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.topic-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.92);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  z-index: 2;
}
.topic-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255,255,255,0.18), rgba(15, 60, 70, 0.35));
  z-index: 1;
}
.topic-cover-1 {
  background: linear-gradient(135deg, #e8d5b7, #f28c5b);
}
.topic-cover-2 {
  background: linear-gradient(135deg, #ffd89b, #f6a623);
}
.topic-cover-3 {
  background: linear-gradient(135deg, #7cc4ca, #0e7c86);
}
.topic-cover-4 {
  background: linear-gradient(135deg, #7cc4ca, #149aa6);
}
.topic-body {
  padding: 16px 18px 18px;
}
.topic-title {
  font-size: 15px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 6px;
}
.topic-desc {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.5;
}
/* 底部版权栏【修改为居中】 */
.footer-copyright-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap:12px;
  padding: 18px 20px 0;
  border-top: 1px dashed #e4ecf7;
  font-size: 12px;
  color: #9aa9c0;
}
/* 响应式：屏幕窄时专题卡片改为2列 */
@media (max-width: 900px) {
  .footer-topics {
    grid-template-columns: repeat(2, 1fr);
  }
  .page-banner {
    height: auto;
    min-height: 0;
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
    padding: 28px 24px;
  }
  .page-banner::before {
    display: none;
  }
  .banner-text {
    max-width: 100%;
  }
  .banner-data {
    position: static;
    width: 100%;
    justify-content: flex-start;
    padding-right: 0;
  }
}

@media (max-width: 768px) {
  .page-banner {
    height: auto;
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
    padding: 28px 22px;
  }
  .banner-text h2 {
    font-size: 28px;
  }
  .banner-text p {
    font-size: 14px;
    letter-spacing: 0;
  }
  .banner-data {
    width: 100%;
    gap: 12px;
  }
  .data-item {
    flex: 1 1 0;
    width: auto;
    min-width: 88px;
  }
}
</style>
