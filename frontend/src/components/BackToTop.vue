<template>
    <button
        v-if="visible"
        class="back-top"
        :class="{ 'is-dark': isDark }"
        type="button"
        title="返回顶部"
        @click="scrollToTop">
        <!-- 环形进度：滚得越深，环越满 -->
        <svg class="bt-ring" viewBox="0 0 48 48" aria-hidden="true">
            <circle class="bt-track" cx="24" cy="24" r="21" />
            <circle
                class="bt-progress"
                cx="24"
                cy="24"
                r="21"
                :style="{ strokeDashoffset: dashOffset }" />
        </svg>
        <span class="bt-arrow">
            <el-icon><Top /></el-icon>
        </span>
    </button>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Top } from '@element-plus/icons-vue'
import { useTheme } from '@/composables/useTheme'

const { isDark } = useTheme()
const route = useRoute()

const MAX_SCROLL = 240          // 滚过这么多才出现，避免轻微滑动就弹按钮
const CIRCUMFERENCE = 2 * Math.PI * 21

const visible = ref(false)
const progress = ref(0)
const scrollerEl = ref(null)

let raf = 0
let pendingY = 0
let boundScrollTarget = null

const dashOffset = computed(() => (1 - progress.value) * CIRCUMFERENCE)

const isAdmin = () => route.path.startsWith('/admin')

const activeScroller = () => {
    if (isAdmin()) {
        return document.querySelector('.ai-body') || document.scrollingElement
    }
    return document.scrollingElement || document.documentElement
}

const measure = () => {
    const target = activeScroller()
    if (!target) return
    const y = window.scrollY || document.documentElement.scrollTop || target.scrollTop || 0
    pendingY = y
    if (raf) return
    raf = requestAnimationFrame(() => {
        raf = 0
        const st = pendingY
        visible.value = st > MAX_SCROLL
        const scrollable = Math.max(1, target.scrollHeight - target.clientHeight)
        progress.value = Math.min(1, Math.max(0, st / scrollable))
    })
}

const bindScroll = () => {
    // 顶栏是窗口滚动的话监听 window，后台是容器滚动的话监听那个容器
    const target = isAdmin() ? document.querySelector('.ai-body') : null
    if (boundScrollTarget) {
        boundScrollTarget.removeEventListener('scroll', measure)
        boundScrollTarget = null
    }
    window.removeEventListener('scroll', measure)
    if (target) {
        boundScrollTarget = target
        target.addEventListener('scroll', measure, { passive: true })
    }
    window.addEventListener('scroll', measure, { passive: true })
    measure()
}

const scrollToTop = () => {
    const target = activeScroller()
    if (!target) return
    const reduce = window.matchMedia?.('(prefers-reduced-motion: reduce)').matches
    if (isAdmin() && target.scrollTo) {
        target.scrollTo({ top: 0, behavior: reduce ? 'auto' : 'smooth' })
        return
    }
    window.scrollTo({ top: 0, behavior: reduce ? 'auto' : 'smooth' })
}

onMounted(() => {
    // 等路由视图挂载完再找滚动容器（后台是 .ai-body 内滚）
    setTimeout(bindScroll, 0)
    setTimeout(bindScroll, 260)
    window.addEventListener('resize', measure, { passive: true })
})

watch(
    () => route.path,
    () => {
        // 等新页面 DOM 渲染完再找滚动容器（后台是 .ai-body）
        setTimeout(bindScroll, 0)
        setTimeout(bindScroll, 260)
    }
)

onBeforeUnmount(() => {
    cancelAnimationFrame(raf)
    window.removeEventListener('scroll', measure)
    window.removeEventListener('resize', measure)
    if (boundScrollTarget) boundScrollTarget.removeEventListener('scroll', measure)
})
</script>

<style scoped>
.back-top {
    position: fixed;
    right: 26px;
    bottom: 86px;
    z-index: 3000;
    width: 48px;
    height: 48px;
    padding: 0;
    border: 1px solid rgba(14, 124, 134, 0.28);
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.82);
    backdrop-filter: blur(14px) saturate(150%);
    -webkit-backdrop-filter: blur(14px) saturate(150%);
    box-shadow: 0 10px 26px rgba(20, 40, 80, 0.16);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    transition: box-shadow 0.3s ease, transform 0.25s cubic-bezier(0.34, 1.4, 0.44, 1), background 0.35s ease,
        border-color 0.35s ease;
    animation: back-top-in 0.32s cubic-bezier(0.34, 1.4, 0.44, 1);
}

@keyframes back-top-in {
    from {
        opacity: 0;
        transform: translateY(10px) scale(0.86);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

.back-top:hover {
    box-shadow: 0 14px 32px rgba(20, 40, 80, 0.22);
    transform: translateY(-2px);
}

.back-top:active {
    transform: scale(0.94);
}

.back-top.is-dark {
    background: rgba(22, 34, 58, 0.86);
    border-color: rgba(124, 196, 202, 0.35);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.45);
}

/* 环形进度条 */
.bt-ring {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    transform: rotate(-90deg);
    pointer-events: none;
}

.bt-track {
    fill: none;
    stroke: rgba(14, 124, 134, 0.2);
    stroke-width: 2;
}

.bt-progress {
    fill: none;
    stroke: #0e7c86;
    stroke-width: 2;
    stroke-linecap: round;
    stroke-dasharray: 131.95;
    transition: stroke-dashoffset 0.18s linear;
}

.back-top.is-dark .bt-track {
    stroke: rgba(124, 196, 202, 0.16);
}

.back-top.is-dark .bt-progress {
    stroke: #7cc4ca;
}

/* 箭头：悬停时向上小跳 */
.bt-arrow {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 19px;
    color: #0e7c86;
    transition: color 0.35s ease, transform 0.28s cubic-bezier(0.34, 1.4, 0.44, 1);
}

.back-top:hover .bt-arrow {
    transform: translateY(-2px);
}

.back-top.is-dark .bt-arrow {
    color: #e6f4f5;
}

@media (prefers-reduced-motion: reduce) {
    .back-top {
        animation: none;
    }
    .bt-progress {
        transition: none;
    }
}
</style>
