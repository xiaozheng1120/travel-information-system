<template>
  <div class="app-shell">
    <!-- 登录/注册共用的背景与遮罩：固定在最底层，切换页面时完全不动 -->
    <div v-if="isAuthPage" class="auth-bg">
      <div class="auth-mask"></div>
    </div>

    <!-- 只有登录、注册页带 auth 过渡，其他页面不加任何动效 -->
    <router-view v-slot="{ Component, route: currentRoute }">
  <transition v-if="currentRoute.meta.transition === 'auth'" name="auth">
    <component :is="Component" :key="currentRoute.path" />
  </transition>
  <component v-else :is="Component" :key="currentRoute.fullPath" />
</router-view>

    <!-- 右下角主题切换：太阳旋转变月亮，配色以按钮为圆心扩散切换 -->
    <ThemeToggle />

    <!-- 返回顶部：滚过一屏才出现，外圈环显示阅读进度 -->
    <BackToTop />

    <!-- 按钮按压波纹：原生绿色光标之外，保留点击反馈 -->
    <CursorFollower />

    <!-- 撤销提示条：删除/取消先生效，5 秒内可撤销 -->
    <UndoToast />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import ThemeToggle from '@/components/ThemeToggle.vue'
import BackToTop from '@/components/BackToTop.vue'
import CursorFollower from '@/components/CursorFollower.vue'
import UndoToast from '@/components/UndoToast.vue'

const route = useRoute()
const isAuthPage = computed(() => route.path === '/login' || route.path === '/register')
</script>

<style>
.app-shell {
  position: relative;
  min-height: 100vh;
}

/*
 * 背景图 + 遮罩固定成一层，过渡期间始终是同一张不动的画面，
 * 所以无论怎么淡化都不会出现变亮/发白，只有卡片内容在交换。
 */
.auth-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  /* 兜底底色：图片未解码时也不会露出白色 */
  background-color: #0d1424;
  pointer-events: none;
  overflow: hidden;
}

/*
 * 照片本体单独一层：略压暗、略提饱和、轻微柔化，
 * 让背景退到“幕后”，卡片才像是浮在这片景里而不是贴上去的。
 */
.auth-bg::before {
  content: '';
  position: absolute;
  inset: -12px;
  background: url('/ffz0lFj7d.jpeg') no-repeat center center;
  background-size: cover;
  filter: blur(2px) brightness(0.94) saturate(1.08);
}

/*
 * 暗角 + 上下渐深：四周压暗、中央稍亮，
 * 视线自然落到中间的卡片上，边缘也不会抢戏。
 */
.auth-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(
      120% 95% at 50% 48%,
      rgba(6, 12, 22, 0.10) 0%,
      rgba(6, 12, 22, 0.30) 65%,
      rgba(4, 9, 18, 0.44) 100%
    ),
    linear-gradient(
      180deg,
      rgba(8, 14, 26, 0.34) 0%,
      rgba(8, 14, 26, 0.14) 42%,
      rgba(8, 14, 26, 0.38) 100%
    );
}

/* 登录 ↔ 注册：交叉淡化，底层背景恒定，因此不会闪白 */
.auth-enter-active,
.auth-leave-active {
  transition: opacity 0.32s ease;
  will-change: opacity;
}

.auth-enter-from,
.auth-leave-to {
  opacity: 0;
}

/* 离场页脱离文档流铺满，不挤压新页面 */
.auth-leave-active {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
}

/* 入场页在上层，与离场页交叉淡化 */
.auth-enter-active {
  position: relative;
  z-index: 2;
}

/* 其他页面：不加任何过渡 */
.no-transition-enter-active,
.no-transition-leave-active {
  transition: none;
}
</style>
