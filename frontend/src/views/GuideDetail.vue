<template>
  <div class="guide-detail-page">
    <div class="container">
      <div class="outer-wrap">
        <el-button @click="goBack" class="back-btn">
          <el-icon>
            <ArrowLeft />
          </el-icon> 返回
        </el-button>
        <div v-if="guideInfo" class="detail-content">
          <!-- 顶部大图Banner -->
          <div class="hero-banner">
            <img v-if="guideInfo.img" :src="getAssetImg(guideInfo.img)" alt="攻略图片" class="banner-img">
            <div v-else class="banner-empty">暂无图片</div>
            <div class="banner-mask"></div>
            <div class="banner-info">
              <div class="tag">攻略详情</div>
              <h2 class="title">{{ guideInfo.title }}</h2>
              <div class="meta-info">
                <span class="author-line">
                  <img v-if="guideInfo.avatar" :src="getAssetImg(guideInfo.avatar)" class="detail-avatar" />
                  <el-icon v-else>
                    <User />
                  </el-icon>
                  作者：{{ guideInfo.author }}
                </span>
                <span>
                  <el-icon>
                    <Calendar />
                  </el-icon>发布时间：{{ formatDate(guideInfo.date) }}
                </span>
              </div>
              <!-- 浏览量、推荐指数标签：读取接口真实数据 -->
              <div class="banner-tags">
                <span class="tag-item"><el-icon><View /></el-icon> 浏览 {{ guideInfo.views || 0 }}</span>
                <span class="tag-item"
                  ><el-icon><Star /></el-icon>
                  {{ Number(guideInfo.rating || 0).toFixed(1) }}</span
                >
                <FavoriteButton
                  tone="overlay"
                  target-type="guide"
                  :target-id="guideInfo.id"
                  :target-name="guideInfo.title"
                  :target-img="getAssetImg(guideInfo.img)"
                />
              </div>
            </div>
          </div>

          <!-- 攻略正文（富文本） -->
          <div class="article">
            <div class="article-title">
              <span class="line"></span>
              <span>攻略介绍</span>
            </div>
            <div class="article-html" v-html="articleHtml"></div>
          </div>

          <!-- 评论区 -->
          <div class="comment-section">
            <div class="comment-title-row">
              <div class="article-title" style="margin-bottom:0">
                <span class="line"></span>
                <span>旅行者评论</span>
              </div>
              <div class="comment-stats">
                <span>共 {{ commentStats.count }} 条</span>
                <span v-if="commentStats.count > 0">平均 {{ Number(commentStats.avgRating || 0).toFixed(1) }} 分</span>
              </div>
            </div>

            <div v-if="userInfo" class="comment-form">
              <div class="comment-form-head">
                <span>写下你的真实体验</span>
                <el-rate v-model="formRating" :max="5" allow-half show-score score-template="{value} 分" />
              </div>
              <el-input
                v-model="formContent"
                type="textarea"
                :rows="3"
                maxlength="500"
                show-word-limit
                placeholder="说说这条攻略哪里有用、哪里需要补充…"
              />
              <div class="comment-form-foot">
                <el-button type="primary" :loading="submitting" @click="submitComment">发布评论</el-button>
              </div>
            </div>
            <div v-else class="comment-login-tip">
              <router-link to="/login" class="login-link">登录</router-link> 后可以发表评论
            </div>

            <div v-if="commentLoading" class="comment-empty">评论加载中…</div>
            <div v-else-if="commentList.length === 0" class="comment-empty">还没有评论，来做第一个分享体验的人吧</div>
            <div v-else class="comment-list">
              <div v-for="item in commentList" :key="item.id" class="comment-item">
                <div class="comment-avatar">
                  <img v-if="item.avatar" :src="getUserAvatar(item.avatar)" alt="avatar" />
                  <el-icon v-else><User /></el-icon>
                </div>
                <div class="comment-main">
                  <div class="comment-head">
                    <span class="comment-user">{{ item.username || '旅行者' }}</span>
                    <el-rate
                      v-if="item.rating"
                      :model-value="Number(item.rating)"
                      disabled
                      size="small"
                      style="display:inline-flex;margin-left:8px"
                    />
                    <span class="comment-time">{{ formatDate(item.createTime) }}</span>
                  </div>
                  <p class="comment-content">{{ item.content }}</p>
                </div>
                <button
                  v-if="userInfo && Number(item.userId) === Number(userInfo.id)"
                  type="button"
                  class="comment-delete"
                  @click="removeComment(item)"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty">
          <p>未找到该攻略信息</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, ArrowLeft, Calendar, View, Star } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGuideDetailApi } from '@/api/guideData'
import { getCommentListApi, getCommentStatsApi, addCommentApi, deleteCommentApi } from '@/api/commentData.js'
import FavoriteButton from '@/components/FavoriteButton.vue'

const route = useRoute()
const router = useRouter()
const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || 'null'))
const guideInfo = ref(null)

// 评论
const commentList = ref([])
const commentStats = ref({ count: 0, avgRating: 0 })
const commentLoading = ref(false)
const formContent = ref('')
const formRating = ref(5)
const submitting = ref(false)

// 富文本正文：去掉 script / on* 事件，避免注入
const articleHtml = computed(() => {
    const raw = guideInfo.value?.content || ''
    // 老数据是纯文本，转成段落保留换行
    if (!/<[a-z][\s\S]*>/i.test(raw)) {
        return String(raw)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/\n/g, '<br />')
    }
    return String(raw)
        .replace(/<script[\s\S]*?<\/script>/gi, '')
        .replace(/\son[a-z]+\s*=\s*"[^"]*"/gi, '')
        .replace(/\son[a-z]+\s*=\s*'[^']*'/gi, '')
        .replace(/javascript:/gi, '')
})

const getUserAvatar = (avatar) => {
    if (!avatar) return ''
    if (avatar.startsWith('http') || avatar.startsWith('/')) return avatar
    return new URL(`../assets/images/${avatar}`, import.meta.url).href
}

const getAssetImg = (imgName) => {
  if (!imgName) return ''
  const name = String(imgName).trim()
  if (name.startsWith('http') || name.startsWith('/')) {
    return name
  }
  return `/assets/images/${name}`
}

const formatDate = (dateVal) => {
  if (!dateVal) return ''
  const d = new Date(dateVal)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

const goBack = () => {
  const fromPage = route.query.fromPage
  if (fromPage === 'guide') {
    router.push({
      path: '/guide',
      query: { page: route.query.pageNum }
    })
  } else if (fromPage === 'home') {
    router.push('/home')
  } else {
    router.back()
  }
}

async function loadGuideData() {
  const id = Number(route.params.id || route.query.id)
  console.log('拿到的攻略ID：', id)
  if (!id) {
    guideInfo.value = null
    return
  }
  const res = await getGuideDetailApi(id)
  console.log('axios返回全部数据：', res)
  guideInfo.value = res
  loadComments(id)
}

async function loadComments(guideId) {
  commentLoading.value = true
  try {
    const [list, stats] = await Promise.all([
      getCommentListApi(guideId),
      getCommentStatsApi(guideId)
    ])
    commentList.value = list || []
    commentStats.value = stats || { count: 0, avgRating: 0 }
  } catch (e) {
    console.error('评论加载失败', e)
    commentList.value = []
  } finally {
    commentLoading.value = false
  }
}

async function submitComment() {
  if (!userInfo.value) {
    ElMessage.warning('请先登录再评论')
    return
  }
  if (!formContent.value.trim()) {
    ElMessage.warning('请先写点什么再发布')
    return
  }
  submitting.value = true
  try {
    const res = await addCommentApi({
      guideId: Number(route.params.id || route.query.id),
      userId: userInfo.value.id,
      content: formContent.value.trim(),
      rating: formRating.value
    })
    if (res && res.code === 200) {
      ElMessage.success('评论发布成功')
      formContent.value = ''
      formRating.value = 5
      await loadComments(Number(route.params.id || route.query.id))
    } else {
      ElMessage.error((res && res.msg) || '评论失败')
    }
  } catch (e) {
    ElMessage.error('评论失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

async function removeComment(item) {
  try {
    await ElMessageBox.confirm('确定删除这条评论吗？', '删除评论', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  try {
    const res = await deleteCommentApi(item.id, userInfo.value.id)
    if (res && res.code === 200) {
      ElMessage.success('评论已删除')
      await loadComments(Number(route.params.id || route.query.id))
    } else {
      ElMessage.error((res && res.msg) || '删除失败')
    }
  } catch (e) {
    ElMessage.error('删除失败，请稍后重试')
  }
}

onMounted(() => {
  loadGuideData()
})

watch(() => route.params, () => {
  loadGuideData()
})
</script>

<style scoped>
.guide-detail-page {
  min-height: 100vh;
  background-color: #f7f9ff;
}

.container {
  width: 90%;
  margin: 0 auto;
  padding: 30px 0 40px;
}

.outer-wrap {
  max-width: 1100px;
  margin: 0 auto;
}

.back-btn {
  margin-bottom: 16px;
  border-radius: 8px;
}

.hero-banner {
  position: relative;
  height: 380px;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 24px;
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-empty {
  width: 100%;
  height: 100%;
  background: #e8edf7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #888;
}

.banner-mask {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.75), rgba(0, 0, 0, 0.15));
}

.banner-info {
  position: absolute;
  left: 32px;
  bottom: 32px;
  color: #fff;
}

.tag {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.22);
  border-radius: 20px;
  font-size: 13px;
  margin-bottom: 12px;
}

.title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 14px;
}

.meta-info {
  display: flex;
  gap: 24px;
  font-size: 15px;
  opacity: 0.92;
  align-items: center;
  margin-bottom: 12px;
}

.author-line {
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}

.banner-tags {
  display: flex;
  gap: 16px;
}

.tag-item {
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.article {
  background: #fff;
  border-radius: 16px;
  padding: 28px 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.article-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: #222;
  margin-bottom: 18px;
}

.article-title .line {
  width: 5px;
  height: 20px;
  background: #4096ff;
  border-radius: 3px;
}

.article-html {
  font-size: 15px;
  line-height: 1.9;
  color: #555;
  word-break: break-word;
}
.article-html p {
  margin: 0 0 12px;
  text-indent: 2em;
}
.article-html h2 {
  font-size: 20px;
  color: #1f3350;
  margin: 20px 0 10px;
  font-weight: 700;
}
.article-html h3 {
  font-size: 17px;
  color: #1f3350;
  margin: 16px 0 8px;
  font-weight: 600;
}
.article-html ul,
.article-html ol {
  padding-left: 22px;
  margin: 10px 0;
}
.article-html li {
  margin: 4px 0;
}
.article-html blockquote {
  margin: 14px 0;
  padding: 10px 14px;
  background: #f5f9ff;
  border-left: 3px solid #7cc4ca;
  color: #475569;
  border-radius: 6px;
}
.article-html img {
  max-width: 100%;
  border-radius: 10px;
  display: block;
  margin: 14px auto;
}

.comment-section {
  margin-top: 20px;
  background: #fff;
  border-radius: 16px;
  padding: 24px 28px 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.comment-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}
.comment-stats {
  display: flex;
  gap: 14px;
  font-size: 12.5px;
  color: #8ba0ba;
}
.comment-form {
  padding: 14px;
  border-radius: 12px;
  background: #f7fbff;
  border: 1px solid #e4effb;
  margin-bottom: 18px;
}
.comment-form-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: #4d6a8f;
  margin-bottom: 10px;
  flex-wrap: wrap;
  gap: 8px;
}
.comment-form-foot {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
.comment-login-tip {
  font-size: 13px;
  color: #8ba0ba;
  padding: 12px 14px;
  background: #f8fbff;
  border-radius: 10px;
  margin-bottom: 16px;
}
.login-link {
  color: #0e7c86;
  text-decoration: none;
}
.comment-empty {
  padding: 26px 0;
  text-align: center;
  font-size: 13px;
  color: #9db0c7;
}
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.comment-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px dashed #e8eff8;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  overflow: hidden;
  background: #eef4fd;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #93a5bb;
  flex-shrink: 0;
}
.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.comment-main {
  flex: 1;
  min-width: 0;
}
.comment-head {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
  font-size: 12.5px;
  color: #93a5bb;
}
.comment-user {
  font-weight: 600;
  color: #2f4b73;
  font-size: 13px;
}
.comment-time {
  margin-left: auto;
}
.comment-content {
  margin: 6px 0 0;
  font-size: 14px;
  line-height: 1.75;
  color: #33475f;
  white-space: pre-wrap;
}
.comment-delete {
  align-self: flex-start;
  border: none;
  background: transparent;
  color: #c0ccda;
  font-size: 12px;
  cursor: pointer;
}
.comment-delete:hover {
  color: #e15b5b;
}

.empty {
  text-align: center;
  padding: 80px 0;
  color: #999;
  font-size: 18px;
}

@media (max-width: 768px) {
  .hero-banner {
    height: 260px;
  }

  .title {
    font-size: 26px;
  }

  .banner-tags {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
