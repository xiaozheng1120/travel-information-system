<template>
    <div class="stp" :class="{ 'is-success': success, 'is-disabled': disabled || loading }">
        <div class="stp-track" ref="trackRef" :style="{ '--stp-progress': progress }">
            <div class="stp-fill" :style="{ width: fillWidth }"></div>
            <div class="stp-label">
                <span v-if="success">支付成功</span>
                <span v-else-if="loading">正在处理…</span>
                <span v-else>{{ text }}<i class="stp-chevrons"><b>›</b><b>›</b><b>›</b></i></span>
            </div>
            <div class="stp-thumb" :style="{ transform: `translateX(${x}px)` }" @pointerdown="onDown"
                @pointermove="onMove" @pointerup="onUp" @pointercancel="onUp">
                <el-icon v-if="success" class="stp-icon check"><Select /></el-icon>
                <el-icon v-else-if="loading" class="stp-icon loading"><Loading /></el-icon>
                <el-icon v-else class="stp-icon"><Right /></el-icon>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { Right, Select, Loading } from '@element-plus/icons-vue'

const props = defineProps({
    text: { type: String, default: '右滑确认支付' },
    disabled: { type: Boolean, default: false },
    loading: { type: Boolean, default: false }
})

const emit = defineEmits(['success'])

const PAD = 6
const THUMB = 44

const trackRef = ref(null)
const x = ref(0)
const maxX = ref(0)
const dragging = ref(false)
const success = ref(false)

let startClientX = 0
let startX = 0

const progress = computed(() => {
    if (!maxX.value) return 0
    return Math.min(1, x.value / maxX.value)
})

const fillWidth = computed(() => (x.value + THUMB + PAD * 2) + 'px')

const clamp = (n, min, max) => Math.min(max, Math.max(min, n))

const onDown = (e) => {
    if (props.disabled || props.loading || success.value) return
    const rect = trackRef.value.getBoundingClientRect()
    maxX.value = Math.max(1, rect.width - THUMB - PAD * 2)
    dragging.value = true
    startClientX = e.clientX
    startX = x.value
    e.currentTarget.setPointerCapture?.(e.pointerId)
}

const onMove = (e) => {
    if (!dragging.value) return
    x.value = clamp(startX + (e.clientX - startClientX), 0, maxX.value)
}

const onUp = () => {
    if (!dragging.value) return
    dragging.value = false
    // 不到 92% 就松手：回弹到起点
    if (progress.value >= 0.92) {
        x.value = maxX.value
        success.value = true
        emit('success')
    } else {
        x.value = 0
    }
}

// 支付失败时由外部调用，让滑块复位
const reset = () => {
    success.value = false
    dragging.value = false
    x.value = 0
}

defineExpose({ reset })
</script>

<style scoped>
.stp {
    width: 100%;
}

.stp-track {
    position: relative;
    display: flex;
    align-items: center;
    height: 56px;
    padding: 6px;
    border-radius: 999px;
    background: rgba(233, 240, 251, 0.9);
    border: 1px solid rgba(14, 124, 134, 0.22);
    overflow: hidden;
    user-select: none;
    touch-action: pan-y;
}

/* 随滑块位置变化的填充底色 */
.stp-fill {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    border-radius: 999px;
    background: linear-gradient(90deg, rgba(14, 124, 134, 0.24), rgba(242, 140, 91, 0.34));
    transition: width 0.16s linear;
    pointer-events: none;
}

.stp.is-success .stp-fill {
    width: 100% !important;
    background: linear-gradient(90deg, #0e7c86, #0e7c86);
}

.stp-label {
    position: absolute;
    left: 0;
    right: 0;
    text-align: center;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 0.5px;
    color: #3c5a80;
    pointer-events: none;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    transition: color 0.2s ease;
}

.stp.is-success .stp-label {
    color: #fff;
}

/* 三个渐隐箭头，做“右滑”的方向暗示 */
.stp-chevrons {
    display: inline-flex;
    gap: 1px;
    margin-left: 2px;
    font-style: normal;
}
.stp-chevrons b {
    font-weight: 600;
    color: #7cc4ca;
    animation: stp-chevron 1.4s infinite;
}
.stp-chevrons b:nth-child(2) {
    animation-delay: 0.18s;
}
.stp-chevrons b:nth-child(3) {
    animation-delay: 0.36s;
}
@keyframes stp-chevron {
    0%, 100% { opacity: 0.25; }
    50% { opacity: 1; }
}

/* 圆形滑块 */
.stp-thumb {
    position: relative;
    z-index: 2;
    width: 44px;
    height: 44px;
    border-radius: 50%;
    background: #fff;
    box-shadow: 0 6px 16px rgba(20, 40, 80, 0.18);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: grab;
    transition: transform 0.28s cubic-bezier(0.34, 1.4, 0.44, 1);
}
.stp-thumb:active {
    cursor: grabbing;
}

.stp-icon {
    font-size: 18px;
    color: #0e7c86;
}

.stp.is-success .stp-thumb {
    background: #fff;
    transition: transform 0.28s cubic-bezier(0.34, 1.4, 0.44, 1);
}
.stp.is-success .stp-icon.check {
    color: #31c48d;
    font-size: 20px;
}
.stp.is-success .stp-icon.loading {
    color: #0e7c86;
}

.stp.is-disabled {
    opacity: 0.65;
    pointer-events: none;
}
</style>
