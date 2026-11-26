<!-- 通用收藏按钮：景点 / 攻略 / 住宿详情页共用 -->
<template>
  <button
    type="button"
    class="fav-btn"
    :class="{ active: favorited, overlay: tone === 'overlay', loading: busy }"
    :disabled="busy"
    @click.stop="toggle"
  >
    <el-icon size="15">
      <StarFilled v-if="favorited" />
      <Star v-else />
    </el-icon>
    <span>{{ favorited ? '已收藏' : '收藏' }}</span>
  </button>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { checkFavoriteApi, addFavoriteApi, removeFavoriteApi } from '@/api/favoriteData'

const props = defineProps({
  targetType: { type: String, required: true },
  targetId: { type: [Number, String], required: true },
  targetName: { type: String, default: '' },
  targetImg: { type: String, default: '' },
  tone: { type: String, default: 'light' }
})

const favorited = ref(false)
const busy = ref(false)
const user = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

const refresh = async () => {
  if (!user.id || !props.targetId) {
    favorited.value = false
    return
  }
  try {
    const res = await checkFavoriteApi(user.id, props.targetType, props.targetId)
    favorited.value = !!res
  } catch (err) {
    favorited.value = false
  }
}

const toggle = async () => {
  if (!user.id) {
    ElMessage.warning('登录后才能收藏哦')
    return
  }
  if (!props.targetId || busy.value) return
  busy.value = true
  try {
    if (favorited.value) {
      const res = await removeFavoriteApi(user.id, props.targetType, props.targetId)
      if (res) {
        favorited.value = false
        ElMessage.success('已取消收藏')
      } else {
        ElMessage.error('取消收藏失败')
      }
    } else {
      const res = await addFavoriteApi({
        userId: user.id,
        targetType: props.targetType,
        targetId: Number(props.targetId),
        targetName: props.targetName,
        targetImg: props.targetImg
      })
      if (res) {
        favorited.value = true
        ElMessage.success('收藏成功，可在个人中心查看')
      } else {
        ElMessage.error('收藏失败')
      }
    }
  } catch (err) {
    ElMessage.error('操作失败，请稍后再试')
  } finally {
    busy.value = false
  }
}

onMounted(refresh)
watch(() => props.targetId, refresh)
</script>

<style scoped>
.fav-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  border-radius: 999px;
  border: 1px solid #dbe7f8;
  background: #ffffff;
  color: #6b7280;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.fav-btn:hover {
  border-color: #0e7c86;
  color: #0e7c86;
  background: #eef5ff;
}
.fav-btn.active {
  border-color: #f7c46c;
  background: #fffaf0;
  color: #d48806;
}
.fav-btn.active .el-icon {
  color: #f7ba2a;
}
.fav-btn.loading {
  opacity: 0.65;
  pointer-events: none;
}
/* 覆盖在大图上的浅色版本 */
.fav-btn.overlay {
  background: rgba(255, 255, 255, 0.16);
  border-color: rgba(255, 255, 255, 0.5);
  color: #ffffff;
  backdrop-filter: blur(6px);
}
.fav-btn.overlay:hover {
  background: rgba(255, 255, 255, 0.28);
  border-color: #ffffff;
  color: #ffffff;
}
.fav-btn.overlay.active {
  background: rgba(255, 255, 255, 0.9);
  border-color: #ffffff;
  color: #d48806;
}
</style>
