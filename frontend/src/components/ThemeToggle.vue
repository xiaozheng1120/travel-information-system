<template>
    <button
        class="theme-toggle"
        :class="{ 'is-dark': isDark }"
        type="button"
        :title="isDark ? '切换到浅色' : '切换到深色'"
        @click="handleClick"
        @pointerdown="pressed = true"
        @pointerup="pressed = false"
        @pointerleave="pressed = false">
        <span class="tt-glow"></span>
        <span class="tt-icons" :class="{ pressed }">
            <el-icon class="tt-sun"><Sunny /></el-icon>
            <el-icon class="tt-moon"><Moon /></el-icon>
        </span>
    </button>
</template>

<script setup>
import { ref } from 'vue'
import { Sunny, Moon } from '@element-plus/icons-vue'
import { useTheme } from '@/composables/useTheme'

const { isDark, toggleThemeWithReveal } = useTheme()
const pressed = ref(false)

const handleClick = (event) => {
    toggleThemeWithReveal(event)
}
</script>

<style scoped>
.theme-toggle {
    position: fixed;
    right: 26px;
    bottom: 26px;
    z-index: 3000;
    width: 48px;
    height: 48px;
    padding: 0;
    border: 1px solid rgba(14, 124, 134, 0.28);
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.82);
    backdrop-filter: blur(14px) saturate(150%);
    -webkit-backdrop-filter: blur(14px) saturate(150%);
    box-shadow: 0 10px 26px rgba(15, 60, 70, 0.16);
    cursor: pointer;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background 0.35s ease, border-color 0.35s ease, box-shadow 0.35s ease;
}

.theme-toggle:hover {
    box-shadow: 0 14px 32px rgba(15, 60, 70, 0.22);
}

.theme-toggle:active {
    transform: scale(0.96);
}

/* 深色态：按钮自己也变成夜色玻璃 */
.theme-toggle.is-dark {
    background: rgba(18, 26, 42, 0.9);
    border-color: rgba(124, 196, 202, 0.35);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.45);
}

/* 点击时按钮下方扩散的一圈光，暗示“扩散切换”的来源 */
.tt-glow {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    background: radial-gradient(circle at 50% 50%, rgba(14, 124, 134, 0.55), transparent 65%);
    opacity: 0;
    transform: scale(0.4);
    transition: opacity 0.45s ease, transform 0.45s ease;
    pointer-events: none;
}
.theme-toggle.is-dark .tt-glow {
    opacity: 0.35;
    transform: scale(1);
}

/* 太阳 ↔ 月亮：整组一起旋转 180°，太阳缩没、月亮转进来 */
.tt-icons {
    position: relative;
    width: 22px;
    height: 22px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.5s cubic-bezier(0.34, 1.3, 0.4, 1), scale 0.2s ease;
    transform: rotate(0deg);
}
.tt-icons.pressed {
    scale: 0.92;
}
.theme-toggle.is-dark .tt-icons {
    transform: rotate(180deg);
}

.tt-sun,
.tt-moon {
    position: absolute;
    font-size: 19px;
    transition: opacity 0.35s ease, transform 0.45s cubic-bezier(0.34, 1.3, 0.4, 1);
}
.tt-sun {
    color: #0e7c86;
    opacity: 1;
    transform: scale(1) translateY(0);
}
.tt-moon {
    color: #e6f4f5;
    opacity: 0;
    transform: scale(0.5) translateY(6px);
}

.theme-toggle.is-dark .tt-sun {
    opacity: 0;
    transform: scale(0.4) translateY(-6px);
}
.theme-toggle.is-dark .tt-moon {
    opacity: 1;
    transform: scale(1) translateY(0);
}
</style>
