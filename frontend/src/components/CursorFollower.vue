<template>
    <span class="ripple-anchor" aria-hidden="true"></span>
</template>

<script setup>
import { onBeforeUnmount, onMounted } from 'vue'

// 跟随箭头已移除，改为浏览器原生品牌光标；这里只保留按钮按压波纹。
const isFinePointer = () => window.matchMedia('(hover: hover) and (pointer: fine)').matches
const prefersReduced = () => window.matchMedia('(prefers-reduced-motion: reduce)').matches

let rippleBound = false

const isDarkBg = (el) => {
    const bg = window.getComputedStyle(el).backgroundColor
    const m = bg.match(/rgba?\(([^)]+)\)/)
    if (!m) return true
    const parts = m[1].split(',').map((v) => parseFloat(v))
    const [r, g, b] = parts
    const a = parts.length > 3 ? parts[3] : 1
    if (a < 0.2) return false
    return (0.299 * r + 0.587 * g + 0.114 * b) < 150
}

const spawnRipple = (e) => {
    const host = e.target.closest('button, .el-button, .ripple-host')
    if (!host) return
    if (host.classList.contains('edge-beam')) return

    const rect = host.getBoundingClientRect()
    if (!rect.width || !rect.height) return

    const x = e.clientX - rect.left
    const y = e.clientY - rect.top
    const radius = Math.max(
        Math.hypot(x, y),
        Math.hypot(rect.width - x, y),
        Math.hypot(x, rect.height - y),
        Math.hypot(rect.width - x, rect.height - y)
    )

    const prevPosition = host.style.position
    const prevOverflow = host.style.overflow
    const prevRight = host.style.right
    const prevBottom = host.style.bottom
    const prevLeft = host.style.left
    const prevTop = host.style.top
    const computed = window.getComputedStyle(host)
    if (computed.position === 'static') {
        host.style.position = 'relative'
        if (computed.right !== 'auto') host.style.right = 'auto'
        if (computed.bottom !== 'auto') host.style.bottom = 'auto'
        if (computed.left !== 'auto') host.style.left = 'auto'
        if (computed.top !== 'auto') host.style.top = 'auto'
    }
    host.style.overflow = 'hidden'

    const ink = document.createElement('span')
    ink.className = 'click-ripple'
    ink.style.left = `${x}px`
    ink.style.top = `${y}px`
    ink.style.width = `${radius * 2}px`
    ink.style.height = `${radius * 2}px`
    ink.style.background = isDarkBg(host)
        ? 'rgba(255, 255, 255, 0.42)'
        : 'rgba(14, 124, 134, 0.24)'
    host.appendChild(ink)

    const cleanup = () => {
        ink.remove()
        host.style.position = prevPosition
        host.style.overflow = prevOverflow
        host.style.right = prevRight
        host.style.bottom = prevBottom
        host.style.left = prevLeft
        host.style.top = prevTop
    }
    ink.addEventListener('animationend', cleanup, { once: true })
    setTimeout(cleanup, 900)
}

const onRippleDown = (e) => {
    if (e.button === 0) spawnRipple(e)
}

const mountRipple = () => {
    if (rippleBound) return
    rippleBound = true
    window.addEventListener('pointerdown', onRippleDown)
}

const unmountRipple = () => {
    if (!rippleBound) return
    rippleBound = false
    window.removeEventListener('pointerdown', onRippleDown)
}

onMounted(() => {
    if (isFinePointer() && !prefersReduced()) mountRipple()
})

onBeforeUnmount(unmountRipple)
</script>

<style scoped>
.ripple-anchor {
    display: none;
}
</style>

<style>
.click-ripple {
    position: absolute;
    border-radius: 50%;
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
    pointer-events: none;
    z-index: 0;
    animation: click-ripple-expand 0.62s cubic-bezier(0.18, 0.65, 0.25, 1) forwards;
}

@keyframes click-ripple-expand {
    0% {
        transform: translate(-50%, -50%) scale(0);
        opacity: 1;
    }
    70% {
        opacity: 0.45;
    }
    100% {
        transform: translate(-50%, -50%) scale(1);
        opacity: 0;
    }
}

@media (prefers-reduced-motion: reduce) {
    .click-ripple {
        animation: none;
    }
}
</style>
