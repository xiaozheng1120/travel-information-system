<template>
  <div class="hotel-detail-page">
    <div class="container">
      <el-button @click="goBack" class="back-btn">
        <el-icon>
          <ArrowLeft />
        </el-icon> 返回
      </el-button>
      <!-- 增加判断，找不到数据显示提示 -->
      <div v-if="hotelInfo" class="detail-wrap">
        <div class="img-box">
          <img :src="hotelInfo.img" alt="">
        </div>
        <div class="info-box">
          <h2>{{ hotelInfo.name }}</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">地址</span>
              <span class="info-value">{{ hotelInfo.address }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">靠近景点</span>
              <span class="info-value">{{ hotelInfo.nearSpot }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">距离</span>
              <span class="info-value distance-num">{{ hotelInfo.distance }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">类型</span>
              <span class="info-value">{{ hotelInfo.type }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">价格区间</span>
              <span class="info-value price-text">{{ hotelInfo.priceRange }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">评分</span>
              <span class="info-value">
                <el-rate v-model="hotelInfo.score" disabled show-score />
              </span>
            </div>
          </div>
          <div class="hotel-tags" v-if="hotelInfo.tags && hotelInfo.tags.length">
            <span class="tag-item" v-for="(tag, idx) in hotelInfo.tags" :key="idx">
              {{ tag }}
            </span>
          </div>
          <div class="desc">
            <h4>住宿介绍</h4>
            <p>{{ hotelInfo.desc }}</p>
          </div>
          <div class="action-bar">
            <FavoriteButton
              target-type="hotel"
              :target-id="hotelInfo.id"
              :target-name="hotelInfo.name"
              :target-img="hotelInfo.img"
            />
            <el-button type="primary" class="btn-book edge-beam edge-beam--cool" @click="openBookDialog">预订酒店</el-button>
            <el-button class="btn-contact" @click="openContactDialog">联系酒店</el-button>
          </div>
        </div>
      </div>
      <div v-else style="text-align:center;padding:80px 0;font-size:18px;color:#666">
        未找到该住宿信息
      </div>
    </div>
    <!-- 预订弹窗 -->
    <el-dialog v-model="bookDialogVisible" title="酒店预订" width="420px">
      <el-form :model="bookForm" label-width="80px">
        <el-form-item label="入住日期">
          <WheelDatePicker v-model="bookForm.checkIn" placeholder="选择入住日期" :min-date="todayStr" />
        </el-form-item>
        <el-form-item label="退房日期">
          <WheelDatePicker v-model="bookForm.checkOut" placeholder="选择退房日期" :min-date="bookForm.checkIn || todayStr" />
        </el-form-item>
        <el-form-item label="入住人数">
          <NumberStepper v-model="bookForm.personNum" :min="1" :max="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBook">确认预订</el-button>
      </template>
    </el-dialog>
    <!-- 联系酒店弹窗 -->
    <el-dialog v-model="contactDialogVisible" title="酒店联系方式" width="360px">
      <p>酒店名称：{{ hotelInfo.name }}</p>
      <p>联系电话：0744-12345678</p>
      <p>酒店地址：{{ hotelInfo.address }}</p>
      <template #footer>
        <el-button @click="contactDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElRate, ElMessage } from 'element-plus'
import { getHotelDetailApi } from '@/api/hotelData.js'
import WheelDatePicker from '@/components/WheelDatePicker.vue'
import NumberStepper from '@/components/NumberStepper.vue'
import FavoriteButton from '@/components/FavoriteButton.vue'
const route = useRoute()
const router = useRouter()
const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || '{}'))

// 今天（YYYY-MM-DD），作为入住日期的最小可选值
const pad2 = (n) => String(n).padStart(2, '0')
const now = new Date()
const todayStr = `${now.getFullYear()}-${pad2(now.getMonth() + 1)}-${pad2(now.getDate())}`
const hotelInfo = ref(null)
const backPage = ref(1)
// 弹窗变量
const bookDialogVisible = ref(false)
const contactDialogVisible = ref(false)
const bookForm = ref({
  checkIn: '',
  checkOut: '',
  personNum: 1
})
const openBookDialog = () => {
  bookDialogVisible.value = true
}
const submitBook = () => {
  if (!bookForm.value.checkIn || !bookForm.value.checkOut || !bookForm.value.personNum) {
    return ElMessage.warning('请完整填写预订信息！')
  }
  const nights = Math.round((new Date(bookForm.value.checkOut) - new Date(bookForm.value.checkIn)) / 86400000)
  if (nights <= 0) {
    return ElMessage.warning('退房日期需要晚于入住日期')
  }
  // 房价区间形如 260-480，按区间最低价/晚估算
  const priceText = String(hotelInfo.value?.priceRange || '')
  const priceMatch = priceText.match(/(\d+(\.\d+)?)/)
  const nightly = priceMatch ? Number(priceMatch[1]) : 0
  if (!nightly) {
    return ElMessage.warning('该酒店暂未提供房价，无法支付')
  }
  const amount = Number((nightly * nights).toFixed(2))
  const guestName = userInfo.value.name || userInfo.value.username || '游客'
  const phone = userInfo.value.phone || ''

  const payload = {
    orderNo: 'DD' + Date.now(),
    userId: userInfo.value.id,
    ticketId: null,
    orderType: 2,
    hotelId: hotelInfo.value.id,
    count: nights,
    status: 1,
    totalPrice: amount,
    travelDate: bookForm.value.checkIn,
    personName: guestName,
    phone
  }

  sessionStorage.setItem('travel-pay-draft', JSON.stringify({
    orderType: 2,
    productName: hotelInfo.value.name,
    subtitle: [hotelInfo.value.type, hotelInfo.value.nearSpot].filter(Boolean).join(' · '),
    dateLabel: `${bookForm.value.checkIn} 至 ${bookForm.value.checkOut} · ${nights} 晚`,
    qtyLabel: `¥${nightly}/晚 × ${nights} 晚`,
    amount,
    personName: guestName,
    phone,
    payload
  }))

  bookDialogVisible.value = false
  router.push('/pay')
}
const openContactDialog = () => {
  contactDialogVisible.value = true
}
// 加载酒店详情函数
async function loadHotelData() {
  hotelInfo.value = null
  // 兼容两种传参方式，params / query
  const id = Number(route.params.id || route.query.id)
  console.log("解析得到id：", id)
  backPage.value = Number(route.query.pageNum) || 1
  if (!id) return
  try {
    const res = await getHotelDetailApi(id)
    console.log("axios完整res：", res)
    console.log("res.data：", res.data)
    // =========这里改！数据直接是res，不是res.data=========
    hotelInfo.value = res
    console.log("赋值后hotelInfo：", hotelInfo.value)
  } catch (err) {
    console.error("获取酒店详情失败", err)
    ElMessage.error("加载酒店信息失败")
  }
}
onMounted(() => {
  loadHotelData()
})
// 监听路由变化，浏览器前进后退也会重新拉取数据
watch(() => [route.params, route.query], () => {
  loadHotelData()
})
const goBack = () => {
  router.push({ path: '/hotel', query: { pageNum: backPage.value } })
}
</script>
<style scoped>
.hotel-detail-page {
  min-height: 100vh;
  background: #f7f8fc;
}
.container {
  width: 90%;
  margin: 0 auto;
  padding: 30px 0;
}
.back-btn {
  margin-bottom: 20px;
}
.detail-wrap {
  display: flex;
  gap: 30px;
  background: #fff;
  padding: 24px;
  border-radius: 10px;
}
.img-box {
  width: 50%;
  height: 420px;
}
.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}
.info-box {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.info-box h2 {
  margin: 0 0 16px 0;
  font-size: 22px;
}
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
  margin-bottom: 16px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-label {
  font-size: 13px;
  color: #888;
}
.info-value {
  font-size: 14px;
  color: #333;
}
.distance-num {
  color: #0e7c86;
  font-weight: 700;
}
.price-text {
  color: #e64340;
  font-weight: 600;
}
.info-box :deep(.el-rate) {
  margin-left: 0;
}
.hotel-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}
.tag-item {
  padding: 4px 12px;
  background: #f0f7ff;
  color: #0e7c86;
  border-radius: 16px;
  font-size: 13px;
  border: 1px solid #e6f4f5;
}
.desc {
  margin-top: 4px;
  margin-bottom: 24px;
}
.desc h4 {
  margin-bottom: 8px;
  font-size: 16px;
}
.desc p {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: #444;
}
.action-bar {
  margin-top: auto;
  display: flex;
  gap: 12px;
}
.btn-book {
  background-color: #0e7c86;
  border-color: #0e7c86;
}
.btn-contact {
  border-color: #0e7c86;
  color: #0e7c86;
}
</style>