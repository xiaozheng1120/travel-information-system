<template>
  <div class="admin-layout">
    <!-- 悬浮岛式侧边栏：不贴边，四周留白，圆角卡片 + 柔和投影 -->
    <aside class="ai-sidebar">
      <div class="ai-brand">
        <img src="/logo.jpeg" alt="logo" class="ai-logo" />
        <div class="ai-brand-text">
          <strong>旅游信息系统</strong>
          <span>ADMIN CONSOLE</span>
        </div>
      </div>

      <nav class="ai-nav">
        <router-link
          v-for="(item, index) in menus"
          :key="item.path"
          :to="item.path"
          class="ai-item ripple-host"
          :class="{ 'is-active': isActive(item.path) }"
          :style="arcStyle(index)"
        >
          <el-icon class="ai-item-icon"><component :is="item.icon" /></el-icon>
          <span class="ai-item-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="ai-sidebar-foot">
        <img src="/admin-avatar.jpeg" alt="管理员头像" class="ai-avatar" />
        <div class="ai-user-meta">
          <strong>{{ adminName }}</strong>
          <span>管理员</span>
        </div>
        <button type="button" class="ai-exit" @click="logout">退出</button>
      </div>
    </aside>

    <section class="ai-main">
      <header class="ai-topbar">
        <div class="ai-crumb">
          <span>后台</span>
          <i>/</i>
          <strong>{{ pageTitle }}</strong>
        </div>
        <div class="ai-top-right">
          <button type="button" class="ai-ghost" @click="goFront">返回前台</button>
          <span class="ai-gap"></span>
          <span class="ai-top-user">{{ adminName }}</span>
          <img src="/admin-avatar.jpeg" alt="管理员头像" class="ai-top-avatar" />
        </div>
      </header>

      <div class="ai-body">
        <router-view v-slot="{ Component }">
          <transition name="ai-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  House,
  User,
  Picture,
  Grid,
  MapLocation,
  Document,
  OfficeBuilding,
  DocumentCopy,
  Ticket
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const menus = [
  { path: '/admin/home', label: '首页', icon: House },
  { path: '/admin/user', label: '用户管理', icon: User },
  { path: '/admin/banner', label: '轮播图管理', icon: Picture },
  { path: '/admin/category', label: '分类管理', icon: Grid },
  { path: '/admin/spot', label: '景点管理', icon: MapLocation },
  { path: '/admin/guide', label: '攻略管理', icon: Document },
  { path: '/admin/hotel', label: '住宿管理', icon: OfficeBuilding },
  { path: '/admin/order', label: '订单管理', icon: DocumentCopy },
  { path: '/admin/ticket', label: '门票管理', icon: Ticket }
]

const activeIndex = computed(() => menus.findIndex((item) => route.path === item.path))
const isActive = (path) => route.path === path
const pageTitle = computed(() => route.meta?.title || '后台首页')

const adminInfo = (() => {
  try {
    return JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  } catch (e) {
    return {}
  }
})()
const adminName = computed(() => adminInfo.username || adminInfo.nickname || 'admin')

// 弧形滚轮：越远离当前项，越向外偏移、缩小、变淡
const arcStyle = (index) => {
  if (activeIndex.value < 0) return {}
  const d = index - activeIndex.value
  const dist = Math.min(Math.abs(d), 4)
  return {
    transform: `translateX(${dist * 5}px) scale(${1 - dist * 0.012})`,
    opacity: d === 0 ? 1 : Math.max(0.56, 1 - dist * 0.11)
  }
}

const goFront = () => {
  router.push('/home')
}

const logout = () => {
  sessionStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style scoped>
/* 整体配色与前台统一：暖白底 + 白卡片 + #0e7c86 主色，侧栏换成深青墨色 */
.admin-layout {
  height: 100vh;
  box-sizing: border-box;
  display: flex;
  gap: 14px;
  padding: 14px;
  background-color: #f5f7fa;
  background-image:
    radial-gradient(rgba(14, 124, 134, 0.08) 1px, transparent 1px),
    radial-gradient(circle at 12% 8%, rgba(14, 124, 134, 0.08), transparent 32%),
    radial-gradient(circle at 92% 90%, rgba(242, 140, 91, 0.06), transparent 30%);
  background-size: 18px 18px, 100% 100%, 100% 100%;
  font-family: 'PingFang SC', 'Microsoft YaHei', system-ui, sans-serif;
  overflow: hidden;
}

/* ============ 悬浮岛侧边栏（深青墨） ============ */
.ai-sidebar {
  position: relative;
  flex: 0 0 232px;
  width: 232px;
  display: flex;
  flex-direction: column;
  padding: 16px 12px 12px;
  border-radius: 20px;
  background: linear-gradient(165deg, #0d4a52 0%, #0a3b42 52%, #082f35 100%);
  box-shadow: 0 18px 40px rgba(8, 40, 46, 0.24);
  overflow: hidden;
}

.ai-sidebar::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  pointer-events: none;
  background:
    radial-gradient(120% 60% at 82% -12%, rgba(124, 196, 202, 0.2), transparent 62%),
    radial-gradient(90% 50% at 0% 110%, rgba(20, 154, 166, 0.14), transparent 60%);
}

.ai-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 8px 16px;
  border-bottom: 1px solid rgba(124, 196, 202, 0.14);
}

.ai-logo {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  object-fit: contain;
  background: rgba(255, 255, 255, 0.94);
  padding: 3px;
}

.ai-brand-text {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.ai-brand-text strong {
  font-size: 14px;
  font-weight: 700;
  color: #eaf7f7;
  letter-spacing: 0.3px;
}

.ai-brand-text span {
  font-size: 9px;
  letter-spacing: 2px;
  color: rgba(170, 214, 218, 0.5);
}

.ai-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-top: 14px;
  flex: 1;
  overflow-y: auto;
}

.ai-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  height: 42px;
  padding: 0 12px;
  border-radius: 12px;
  color: rgba(214, 236, 238, 0.66);
  font-size: 13.5px;
  text-decoration: none;
  transition: background 0.22s ease, color 0.22s ease, opacity 0.3s ease, transform 0.3s ease;
}

.ai-item-icon {
  font-size: 16px;
}

.ai-item:hover {
  background: rgba(124, 196, 202, 0.12);
  color: #eaf7f7;
}

.ai-item.is-active {
  color: #ffffff;
  font-weight: 600;
  background: linear-gradient(90deg, rgba(20, 154, 166, 0.32), rgba(20, 154, 166, 0.06));
  box-shadow: inset 0 0 0 1px rgba(124, 196, 202, 0.24);
}

.ai-item.is-active::before {
  content: '';
  position: absolute;
  left: 5px;
  top: 12px;
  bottom: 12px;
  width: 3px;
  border-radius: 3px;
  background: linear-gradient(180deg, #7cc4ca, #149aa6);
}

.ai-sidebar-foot {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 8px 4px;
  border-top: 1px solid rgba(124, 196, 202, 0.14);
}

.ai-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 1.5px solid rgba(124, 196, 202, 0.45);
}

.ai-user-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.ai-user-meta strong {
  font-size: 12.5px;
  color: #eaf7f7;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 86px;
}

.ai-user-meta span {
  font-size: 10.5px;
  color: rgba(186, 222, 226, 0.55);
}

.ai-exit {
  margin-left: auto;
  padding: 5px 10px;
  font-size: 11.5px;
  border-radius: 8px;
  border: 1px solid rgba(124, 196, 202, 0.24);
  background: transparent;
  color: rgba(214, 236, 238, 0.78);
  cursor: pointer;
  transition: all 0.2s ease;
}

.ai-exit:hover {
  background: rgba(124, 196, 202, 0.14);
  color: #ffffff;
  border-color: rgba(124, 196, 202, 0.55);
}

/* ============ 右侧主区域（暖白，与前台一致） ============ */
.ai-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  border-radius: 20px;
  border: 1px solid #e5e7eb;
  background: #f7f8fa;
  box-shadow: 0 18px 40px rgba(15, 60, 70, 0.08);
  overflow: hidden;
}

.ai-topbar {
  flex: 0 0 auto;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 22px;
  border-bottom: 1px solid #edf0f2;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}

.ai-crumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #8a94a3;
}

.ai-crumb i {
  color: #cbd5e1;
  font-style: normal;
}

.ai-crumb strong {
  font-size: 14.5px;
  font-weight: 600;
  color: #1f2937;
  letter-spacing: 0.2px;
}

.ai-top-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-ghost {
  padding: 6px 12px;
  border-radius: 9px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  color: #5a6b78;
  font-size: 12.5px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.ai-ghost:hover {
  border-color: #0e7c86;
  color: #0e7c86;
  background: #f2fafa;
}

.ai-gap {
  width: 1px;
  height: 16px;
  background: #e5e7eb;
}

.ai-top-user {
  font-size: 12.5px;
  color: #5a6b78;
}

.ai-top-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e6f4f5;
}

.ai-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 20px 22px 26px;
  background:
    radial-gradient(circle at 8% 0%, rgba(14, 124, 134, 0.04), transparent 34%),
    #f5f7fa;
}

/* 路由切换淡入 */
.ai-fade-enter-active,
.ai-fade-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.ai-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.ai-fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

@media (prefers-reduced-motion: reduce) {
  .ai-fade-enter-active,
  .ai-fade-leave-active {
    transition: none;
  }
}
</style>
