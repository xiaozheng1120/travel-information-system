<template>
  <div class="ticket-page">
    <!-- 导航（和住宿完全一致） -->
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
      <!-- 标题区，和住宿页Hero下面的标题风格对齐 -->
      <div class="page-title-block">
        <div class="title-icon-wrap">
          <el-icon style="font-size:22px;"><Ticket /></el-icon>
        </div>
        <div class="title-text">
          <h2>精选门票预订</h2>
          <p>探索各地热门景点，预订优惠门票，开启美好旅程</p>
        </div>
      </div>
      <!-- 智能筛选卡片，和住宿筛选卡片样式统一 -->
      <div class="search-card">
        <div class="search-title">
          <el-icon><Search /></el-icon>
          <span>智能筛选</span>
        </div>
        <div class="search-row1">
          <AutoCompleteInput v-model="searchName" type="ticket" placeholder="搜索门票名称或景点..." @search="handleSearch" />
          <el-select v-model="filterSpot" placeholder="选择景点" clearable>
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
          <el-button type="primary" class="btn-search" @click="handleSearch">搜索门票</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </div>
        <hr class="search-divider" />
        <div class="search-row2">
          <div class="filter-item">
            <label>门票类型</label>
            <el-select v-model="filterType" placeholder="选择类型" clearable style="width:260px">
              <el-option label="全部" value="" />
              <el-option label="成人票" value="成人票"></el-option>
              <el-option label="学生票" value="学生票"></el-option>
              <el-option label="儿童票" value="儿童票"></el-option>
              <el-option label="老人票" value="老人票"></el-option>
              <el-option label="联票" value="联票"></el-option>
            </el-select>
          </div>
          <!-- 【新增回来：选择景点筛选项，和截图布局保持一致】 -->
          <div class="filter-item">
            <label>选择景点</label>
            <el-select v-model="filterSpot" placeholder="选择景点" clearable style="width:260px">
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
          </div>
        </div>
      </div>
      <!-- 列表标题+统计 -->
      <div class="list-head">
        <h2>全部门票 <span class="total-text">共 {{ filterList.length }} 张门票</span></h2>
      </div>
      <!-- 门票卡片列表，圆角、阴影对齐住宿卡片 -->
      <div class="ticket-list">
        <div class="ticket-card" v-for="item in pageList" :key="item.id">
          <div class="card-top-block">
            <span class="card-tag">{{ item.type }}</span>
            <span class="card-ticket-icon">
              <el-icon style="font-size:20px;"><Ticket /></el-icon>
            </span>
          </div>
          <div class="card-content">
            <h3 class="ticket-name">{{ item.name }}</h3>
            <div class="price-line">
              <div class="price-left">
                <span class="now-price">¥{{ item.priceNow }}</span>
                <span class="old-price">¥{{ item.priceOld }}</span>
              </div>
              <el-tag size="small" type="danger">特惠</el-tag>
            </div>
            <p class="valid-text">🗓 {{ item.validDesc }}</p>
            <p class="desc">{{ item.desc }}</p>
            <div class="card-bottom-row">
              <span class="stock" :class="{ 'stock-zero': item.stock <=0 }">🪪 余票 {{ item.stock }} 张</span>
              <el-button class="buy-btn" :disabled="item.stock <=0" @click="openBookDialog(item)">
                {{ item.stock <=0 ? '已售罄' : '立即预订' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <!-- 分页，和住宿分页对齐 -->
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" layout="prev, pager, next"
          :total="total" @current-change="pageChange" />
      </div>
    </div>
    <!-- 底部：白色容器 + 分割线，和住宿页完全一致 -->
    <div class="footer-white">
      <div class="footer-divider"></div>
      <div class="footer-simple">
        <span>旅游信息系统 · 门票预订板块</span>
        <span>©2026 旅游信息系统 All Rights Reserved</span>
      </div>
    </div>
    <el-dialog v-model="bookDialogVisible" title="门票预订" width="460px" :close-on-click-modal="true">
      <el-form :model="bookForm" :rules="bookRules" label-width="85px" ref="bookFormRef">
        <el-form-item label="门票名称">
          <el-input v-model="bookForm.ticketName" disabled></el-input>
        </el-form-item>
        <el-form-item label="单价">
          <el-input v-model="bookForm.price" disabled></el-input>
        </el-form-item>
        <el-form-item label="出行日期" prop="visitDate">
          <WheelDatePicker v-model="bookForm.visitDate" placeholder="选择出行日期" :min-date="todayStr" @change="onVisitDateChange" />
        </el-form-item>
        <el-form-item label="购票数量" prop="num">
          <NumberStepper v-model="bookForm.num" :min="1" :max="bookForm.maxStock > 0 ? bookForm.maxStock : 99" />
        </el-form-item>
        <el-form-item label="出行人姓名" prop="userName">
          <el-input v-model="bookForm.userName" placeholder="填写出行人姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系手机号" prop="phone">
          <el-input v-model="bookForm.phone" placeholder="填写接收订单的手机号"></el-input>
        </el-form-item>
        <el-form-item label="订单总价">
          <el-input v-model="totalPrice" disabled></el-input>
        </el-form-item>
      </el-form>
      <div class="tip-text">
        <p>💡提示：请准确填写出行人信息，入园需核验身份；购票后请留意门票有效期。</p>
      </div>
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBook" :loading="submitLoading">确认预订</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { House, MapLocation, Document, OfficeBuilding, Ticket, List, Setting, Search, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ticketAllList, loadAllTicketFromBackend } from '@/api/ticketData.js'
import { addOrder } from '@/api/orderData.js'
import AutoCompleteInput from '@/components/AutoCompleteInput.vue'
import WheelDatePicker from '@/components/WheelDatePicker.vue'
import NumberStepper from '@/components/NumberStepper.vue'
const route = useRoute()
const router = useRouter()
const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))

// 今天（YYYY-MM-DD），出行日期最早只能选今天
const pad2 = (n) => String(n).padStart(2, '0')
const now = new Date()
const todayStr = `${now.getFullYear()}-${pad2(now.getMonth() + 1)}-${pad2(now.getDate())}`

// 选完日期后顺手把校验红字清掉
const onVisitDateChange = () => {
  bookFormRef.value?.validateField?.('visitDate').catch(() => {})
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
const searchName = ref('')
const filterType = ref('')
const filterSpot = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const bookDialogVisible = ref(false)
const bookFormRef = ref(null)
const submitLoading = ref(false)
const currentTicket = ref(null)
const bookForm = ref({
  ticketName: '',
  price: '',
  visitDate: '',
  num: 1,
  userName: '',
  phone: '',
  maxStock: 0
})
const bookRules = ref({
  visitDate: [{ required: true, message: '请选择出行日期', trigger: 'blur' }],
  num: [{ required: true, message: '请选择购票数量', trigger: 'blur' }],
  userName: [{ required: true, message: '请填写出行人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请填写手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确11位手机号', trigger: 'blur' }
  ]
})
const totalPrice = ref('¥0.00')
const calcTotalPrice = () => {
  totalPrice.value = '¥' + (Number(bookForm.value.price) * Number(bookForm.value.num)).toFixed(2)
}
watch(() => bookForm.value.num, (newVal) => {
  const n = Number(newVal)
  const maxStock = bookForm.value.maxStock
  if (!isNaN(n) && n > maxStock) {
    ElMessage.warning(`超出余票！当前仅剩${maxStock}张，已自动调整为最大可购数量`)
    bookForm.value.num = maxStock
  }
  calcTotalPrice()
})
const disabledPastDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}
const filterList = computed(() => {
  let arr = ticketAllList.value
  if (searchName.value) {
    arr = arr.filter(item => item.name.includes(searchName.value))
  }
  if (filterType.value) {
    arr = arr.filter(item => item.type === filterType.value)
  }
  if (filterSpot.value) {
    arr = arr.filter(item => item.spot === filterSpot.value)
  }
  return arr
})
const total = computed(() => filterList.value.length)
const pageList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filterList.value.slice(start, end)
})
const handleSearch = () => {
  currentPage.value = 1
}
const resetFilter = () => {
  searchName.value = ''
  filterType.value = ''
  filterSpot.value = ''
  currentPage.value = 1
}
const pageChange = () => { }
const openBookDialog = (item) => {
  if(item.stock <= 0){
    ElMessage.warning("该门票已售罄！")
    return
  }
  currentTicket.value = item
  bookForm.value.ticketName = item.name
  bookForm.value.price = item.priceNow
  bookForm.value.visitDate = ''
  bookForm.value.num = 1
  bookForm.value.userName = ''
  bookForm.value.phone = ''
  bookForm.value.maxStock = item.stock
  calcTotalPrice()
  bookDialogVisible.value = true
}
const submitBook = async () => {
  if (Number(bookForm.value.num) > bookForm.value.maxStock) {
    ElMessage.warning(`超出余票！当前仅剩${bookForm.value.maxStock}张，无法预订`)
    bookForm.value.num = bookForm.value.maxStock
    return
  }
  try {
    await bookFormRef.value.validate()
  } catch (e) {
    return
  }

  // 不在这里直接下单，先把订单草稿放进 sessionStorage，跳去收银台支付
  const num = Number(bookForm.value.num)
  const price = Number(bookForm.value.price)
  const amount = Number((price * num).toFixed(2))
  const visitDate = new Date(bookForm.value.visitDate).toISOString().split('T')[0]

  const payload = {
    orderNo: 'DD' + Date.now(),
    userId: userInfo.value.id,
    ticketId: currentTicket.value.id,
    orderType: 1,
    hotelId: null,
    count: num,
    status: 1,
    totalPrice: amount,
    travelDate: visitDate,
    personName: bookForm.value.userName,
    phone: bookForm.value.phone
  }

  sessionStorage.setItem('travel-pay-draft', JSON.stringify({
    orderType: 1,
    productName: bookForm.value.ticketName,
    subtitle: currentTicket.value.validDesc,
    dateLabel: visitDate,
    qtyLabel: `${num} 张 × ¥${price}`,
    amount,
    personName: bookForm.value.userName,
    phone: bookForm.value.phone,
    payload
  }))

  bookDialogVisible.value = false
  router.push('/pay')
}
const logout = () => {
  sessionStorage.removeItem('userInfo')
  ElMessage.success('已退出登录')
  router.push('/login')
}
onMounted(async () => {
  await loadAllTicketFromBackend()
})
</script>
<style scoped>
.ticket-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 12% 12%, rgba(14, 124, 134, 0.12), transparent 28%),
    radial-gradient(circle at 88% 18%, rgba(242, 140, 91, 0.10), transparent 32%),
    linear-gradient(180deg, #faf8f5 0%, #f7f4ee 45%, #f3f0e9 100%);
}
/* 导航 完全和住宿一致 */
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
/* 杂志式票券横幅：左暖沙文字 + 右整幅实景图 + 撕票口 */
.page-title-block {
  position: relative;
  display: flex;
  align-items: center;
  gap: 18px;
  height: 240px;
  box-sizing: border-box;
  padding: 32px 36px;
  margin-bottom: 24px;
  border-radius: 20px;
  background: linear-gradient(120deg, #f7efe3 0%, #f3e6d5 100%);
  border: 1px solid rgba(10, 79, 86, 0.10);
  box-shadow: 0 16px 38px rgba(10, 79, 86, 0.10);
  overflow: hidden;
}
/* 右侧整幅景点图，左侧用暖沙色轻叠，和文字区自然衔接 */
.page-title-block::after {
  content: '';
  position: absolute;
  left: 57.5%;
  top: 12px;
  right: 12px;
  bottom: 12px;
  z-index: 0;
  border-radius: 14px;
  background:
    linear-gradient(90deg, rgba(247, 239, 227, 0.55) 0%, rgba(247, 239, 227, 0) 22%, rgba(232, 213, 183, 0.10) 100%),
    url('/ticket-hero.jpg') center / cover no-repeat;
  pointer-events: none;
}
/* 中间虚线撕票口 + 上下两个半圆缺口 */
.page-title-block::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 2;
  background-image:
    radial-gradient(circle at 57.5% 0, #faf8f5 0 9px, transparent 10px),
    radial-gradient(circle at 57.5% 100%, #faf8f5 0 9px, transparent 10px),
    repeating-linear-gradient(to bottom, rgba(10, 79, 86, 0.24) 0 5px, transparent 5px 11px);
  background-position: 0 0, 0 100%, 57.5% 18px;
  background-size: 100% 100%, 100% 100%, 1px calc(100% - 36px);
  background-repeat: no-repeat;
  pointer-events: none;
}
.title-icon-wrap {
  position: relative;
  z-index: 3;
  width: 44px;
  height: 44px;
  flex: 0 0 44px;
  border-radius: 50%;
  background: #0a4f56;
  color: #e8d5b7;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 16px rgba(10, 79, 86, 0.18);
}
.title-text {
  position: relative;
  z-index: 3;
  max-width: 46%;
}
.title-text h2 {
  margin: 0;
  font-size: 34px;
  font-weight: 600;
  color: #0a4f56;
  letter-spacing: 0.5px;
}
.title-text p {
  margin: 9px 0 0;
  color: #5b6b70;
  font-size: 15px;
  line-height: 1.7;
  max-width: 460px;
}
/* 副标题下一道日落橙短线，作为整块唯一亮色 */
.title-text::after {
  content: '';
  display: block;
  width: 42px;
  height: 2px;
  margin-top: 18px;
  border-radius: 2px;
  background: #f28c5b;
}
/* 筛选卡片 和住宿完全统一 */
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
  justify-content: center;
  align-items: flex-start;
  gap: 60px;
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
/* 列表标题 */
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
.ticket-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}
.ticket-card {
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  background: #fff;
  box-shadow: 0 8px 22px rgba(15, 60, 70, 0.07);
  transition: all 0.35s ease;
  display: flex;
  flex-direction: column;
}
.ticket-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 22px 44px rgba(15, 60, 70, 0.14);
}
.card-top-block {
  height: 72px;
  background: #eaf6f7;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
}
.card-tag {
  background: #0e7c86;
  color: #fff;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
}
.card-ticket-icon {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #0e7c86;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.card-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.ticket-name {
  font-size: 18px;
  font-weight: 600;
  color: #222;
  margin: 0 0 12px;
  text-align: center;
}
.price-line {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 10px;
}
.price-left {
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.now-price {
  font-size: 26px;
  color: #ff3b30;
  font-weight: 700;
}
.old-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}
.valid-text {
  font-size: 13px;
  color: #666;
  margin: 0 0 10px;
}
.desc {
  font-size: 13px;
  color: #555;
  line-height: 1.5;
  margin: 0 0 auto 0;
}
.card-bottom-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
}
.stock {
  font-size: 13px;
  color: #777;
}
.stock-zero{
  color: red;
  font-weight: bold;
}
.buy-btn {
  min-width: 110px;
  height: 40px;
  background: linear-gradient(135deg, #0e7c86, #0e7c86) !important;
  border: none !important;
  color: #fff !important;
  font-size: 15px;
  font-weight: 600;
  border-radius: 20px !important;
}
.buy-btn:hover {
  background: linear-gradient(135deg, #149aa6, #0e7c86) !important;
}
.buy-btn:disabled{
  background: #c0c4cc !important;
  cursor: not-allowed;
}
.pagination-wrap {
  margin-top: 36px;
  display: flex;
  justify-content: center;
}
.pagination-wrap :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
  color: #fff;
}
:deep(.el-dialog__footer .el-button--primary) {
  background: #eef4ff !important;
  border-color: #dce8ff !important;
  color: #409EFF !important;
  font-weight: 500;
}
:deep(.el-dialog__footer .el-button--primary):hover {
  background: #e1ecff !important;
  border-color: #c7dcff !important;
  color: #0e7c86 !important;
}
.tip-text {
  color: #888;
  font-size: 13px;
  padding: 0 10px 10px;
}
/* 底部 白色div，和住宿一模一样 */
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
  .page-title-block {
    height: auto;
    min-height: 0;
    padding: 28px 24px;
    gap: 14px;
  }
  .page-title-block::after,
  .page-title-block::before {
    display: none;
  }
  .title-text {
    max-width: calc(100% - 58px);
  }
  .title-text h2 {
    font-size: 28px;
  }
  .title-text p {
    font-size: 14px;
  }
  .title-text::after {
    margin-top: 14px;
  }
}
</style>