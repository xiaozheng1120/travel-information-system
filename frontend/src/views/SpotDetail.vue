<template>
  <div class="spot-detail">
    <div v-if="loading" class="loading-text">加载中...</div>
    <div v-else-if="spot" class="detail-card">
      <!-- 返回按钮 -->
      <el-button @click="$router.back()" class="back-btn">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <!-- 顶部大图 -->
      <div class="detail-hero">
        <img v-if="spot.img" :src="spot.img" alt="景点图片" class="hero-img" />
        <div v-else class="hero-empty">暂无图片</div>
        <div class="hero-mask"></div>
        <div class="hero-info">
          <span class="hero-tag">景点详情</span>
          <h1 class="hero-title">{{ spot.name }}</h1>
          <div class="hero-meta">
            <span class="meta-item">
              <el-icon><MapLocation /></el-icon>
              {{ spot.area }}
            </span>
            <span class="meta-item">
              <el-icon><Ticket /></el-icon>
              门票 ¥{{ spot.price }}
            </span>
          </div>
        </div>
      </div>
      <!-- 中部信息卡 -->
      <div class="info-grid">
        <div class="info-card">
          <div class="info-card-icon blue">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="info-card-content">
            <div class="info-card-label">开放时间</div>
            <div class="info-card-value">{{ spot.openTime || '全天开放' }}</div>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon purple">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="info-card-content">
            <div class="info-card-label">建议游玩时长</div>
            <div class="info-card-value">{{ spot.playTime || '半天' }}</div>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon orange">
            <el-icon><Star /></el-icon>
          </div>
          <div class="info-card-content">
            <div class="info-card-label">景点等级</div>
            <div class="info-card-value">{{ spot.level || '暂无评级' }}</div>
          </div>
        </div>
        <div class="info-card">
          <div class="info-card-icon green">
            <el-icon><PriceTag /></el-icon>
          </div>
          <div class="info-card-content">
            <div class="info-card-label">门票价格</div>
            <div class="info-card-value">{{ spot.price > 0 ? '¥' + spot.price : '免费' }}</div>
          </div>
        </div>
      </div>
      <!-- 标签与操作区 -->
      <div class="action-bar">
        <div class="tag-list">
          <el-tag
            v-for="(tag, index) in tagList"
            :key="tag"
            :type="tagTypes[index % tagTypes.length]"
            effect="light"
            size="large"
          >
            {{ tag }}
          </el-tag>
        </div>
        <!-- 【修改处】增加点击跳转，携带景点ID传给门票页面 -->
        <div class="action-right">
          <FavoriteButton
            target-type="spot"
            :target-id="spot.id"
            :target-name="spot.name"
            :target-img="spot.img"
          />
          <el-button type="primary" size="large" class="book-btn" @click="$router.push({path:'/ticket', query:{spotId: spot.id}})">
            <el-icon><Ticket /></el-icon>
            立即预订门票
          </el-button>
        </div>
      </div>
      <!-- 景点介绍 -->
      <div class="desc-wrap">
        <div class="desc-title">
          <span class="title-dot"></span>
          <h3>景点介绍</h3>
        </div>
        <p>{{ spot.description }}</p>
      </div>
    </div>
    <div v-else class="empty-tip">暂无数据</div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  ArrowLeft,
  MapLocation,
  Ticket,
  Clock,
  Timer,
  Star,
  PriceTag
} from '@element-plus/icons-vue'
import { getSpotByIdApi } from '@/api/spotData'
import FavoriteButton from '@/components/FavoriteButton.vue'
const route = useRoute()
const spot = ref(null)
const loading = ref(true)
// 标签从后端 tags 字段读取（逗号分隔），没配就退回景点分类
const tagTypes = ['primary', 'success', 'warning', 'info']
const tagList = computed(() => {
  const raw = (spot.value && spot.value.tags) || ''
  const arr = raw.split(/[,，]/).map(v => v.trim()).filter(Boolean)
  if (arr.length) return arr
  const cat = spot.value && spot.value.categoryName
  return cat ? [cat] : []
})
const loadDetail = async () => {
  try {
    const id = route.params.id
    console.log('路由拿到的id：', id)
    const res = await getSpotByIdApi(id)
    console.log('接口返回完整res：', res)
    spot.value = res
    console.log('赋值后的spot：', spot.value)
  } catch (err) {
    console.error('请求报错：', err)
  } finally {
    loading.value = false
  }
}
onMounted(() => {
  loadDetail()
})
</script>
<style scoped>
.spot-detail {
  padding: 40px 24px;
  background:
    radial-gradient(circle at 10% 10%, rgba(14, 124, 134, 0.08), transparent 26%),
    radial-gradient(circle at 90% 20%, rgba(242, 140, 91, 0.08), transparent 28%),
    #f0f4fc;
  min-height: 100vh;
}
.detail-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 0;
  max-width: 1180px;
  margin: 0 auto;
  overflow: hidden;
  box-shadow:
    0 20px 50px rgba(15, 60, 70, 0.12),
    0 8px 20px rgba(31, 58, 95, 0.08);
  transition: all 0.35s ease;
}
.back-btn {
  margin: 28px 0 0 28px;
  border-radius: 10px;
}
:deep(.back-btn:hover) {
  background-color: #ecf5ff !important;
  color: #0e7c86 !important;
}
/* 顶部大图区域 */
.detail-hero {
  position: relative;
  width: 100%;
  height: 420px;
  margin-top: 20px;
  overflow: hidden;
}
.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}
.detail-card:hover .hero-img {
  transform: scale(1.03);
}
.hero-empty {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eef2f8;
  color: #9ca3af;
  font-size: 18px;
}
.hero-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    rgba(0, 0, 0, 0) 40%,
    rgba(0, 0, 0, 0.45) 100%
  );
}
.hero-info {
  position: absolute;
  left: 40px;
  bottom: 36px;
  z-index: 2;
  color: #fff;
}
.hero-tag {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.35);
  font-size: 12px;
  letter-spacing: 2px;
  margin-bottom: 14px;
  backdrop-filter: blur(10px);
}
.hero-title {
  font-size: 40px;
  margin: 0 0 14px;
  font-weight: 700;
  text-shadow: 0 10px 25px rgba(0, 0, 0, 0.35);
}
.hero-meta {
  display: flex;
  gap: 24px;
  font-size: 15px;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(0, 0, 0, 0.25);
  padding: 8px 16px;
  border-radius: 999px;
  backdrop-filter: blur(10px);
}
/* 中部信息卡片 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 32px 40px 0;
}
.info-card {
  background: #f7faff;
  border: 1px solid #e6edf8;
  border-radius: 18px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: all 0.3s ease;
}
.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 16px 34px rgba(14, 124, 134, 0.18);
}
.info-card-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
}
.info-card-icon.blue {
  background: linear-gradient(135deg, #0e7c86, #0e7c86);
}
.info-card-icon.purple {
  background: linear-gradient(135deg, #f28c5b, #0e7c86);
}
.info-card-icon.orange {
  background: linear-gradient(135deg, #e8d5b7, #f59e0b);
}
.info-card-icon.green {
  background: linear-gradient(135deg, #2f9d6c, #7cc4ca);
}
.info-card-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-card-label {
  font-size: 12px;
  color: #6b7280;
}
.info-card-value {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}
/* 标签和操作区 */
.action-bar {
  margin: 28px 40px 0;
  padding: 24px;
  background: linear-gradient(135deg, #f7faff, #eef2ff);
  border: 1px solid #e6edf8;
  border-radius: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}
.tag-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.action-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}
.book-btn {
  padding: 12px 28px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.25s ease;
}
.book-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 18px rgba(14, 124, 134,0.3);
}
/* 介绍区 */
.desc-wrap {
  margin: 32px 40px 40px;
  padding: 28px;
  background: #fafbff;
  border: 1px solid #eef2ff;
  border-radius: 20px;
}
.desc-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}
.title-dot {
  width: 6px;
  height: 22px;
  border-radius: 6px;
  background: linear-gradient(180deg, #0e7c86, #0e7c86);
}
.desc-title h3 {
  margin: 0;
  font-size: 20px;
  color: #1f2937;
}
.desc-wrap p {
  font-size: 16px;
  color: #4e5969;
  line-height: 2;
  text-indent: 2em;
  margin: 0;
}
.loading-text,
.empty-tip {
  text-align: center;
  padding: 80px 20px;
  font-size: 18px;
  color: #666;
}
/* 平板适配 */
@media (max-width: 900px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
    padding: 24px 20px 0;
  }
  .action-bar {
    margin: 24px 20px 0;
  }
  .desc-wrap {
    margin: 24px 20px 28px;
  }
  .hero-info {
    left: 24px;
    bottom: 24px;
  }
  .hero-title {
    font-size: 28px;
  }
}
/* 手机适配 */
@media (max-width: 600px) {
  .detail-hero {
    height: 300px;
  }
  .hero-title {
    font-size: 24px;
  }
  .hero-meta {
    flex-direction: column;
    gap: 10px;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
  .action-bar {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>