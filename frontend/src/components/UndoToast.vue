<template>
    <div class="undo-stack" v-if="undoItems.length">
        <transition-group name="undo-slide">
            <div class="undo-bar" v-for="item in undoItems" :key="item.id">
                <span class="undo-icon">
                    <el-icon><Delete /></el-icon>
                </span>
                <span class="undo-text">{{ item.message }}</span>
                <button type="button" class="undo-btn" @click="undoItem(item.id)">撤销</button>
                <span class="undo-progress" :style="{ animationDuration: item.duration + 'ms' }"></span>
            </div>
        </transition-group>
    </div>
</template>

<script setup>
import { Delete } from '@element-plus/icons-vue'
import { useUndo } from '@/composables/useUndo'

const { undoItems, undoItem } = useUndo()
</script>

<style scoped>
.undo-stack {
    position: fixed;
    left: 50%;
    bottom: 30px;
    transform: translateX(-50%);
    z-index: 5000;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    pointer-events: none;
}

.undo-bar {
    position: relative;
    display: flex;
    align-items: center;
    gap: 12px;
    min-width: 260px;
    max-width: min(86vw, 520px);
    padding: 12px 14px 14px;
    border-radius: 12px;
    background: #1e2329;
    color: #f2f5f9;
    box-shadow: 0 14px 34px rgba(10, 18, 30, 0.35);
    pointer-events: auto;
    overflow: hidden;
}

.undo-icon {
    width: 22px;
    height: 22px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
    color: #ffc76a;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    flex-shrink: 0;
}

.undo-text {
    flex: 1;
    font-size: 13px;
    line-height: 1.5;
    color: #eaf0f8;
    word-break: break-all;
}

.undo-btn {
    flex-shrink: 0;
    padding: 5px 14px;
    border-radius: 8px;
    border: 1.5px solid #ffc76a;
    background: transparent;
    color: #ffc76a;
    font-size: 13px;
    font-weight: 600;
    letter-spacing: 0.5px;
    cursor: pointer;
    transition: background 0.18s ease, color 0.18s ease, transform 0.18s ease;
}

.undo-btn:hover {
    background: #ffc76a;
    color: #23282f;
}

.undo-btn:active {
    transform: scale(0.96);
}

/* 底部倒计时进度条，看得见还剩多少时间 */
.undo-progress {
    position: absolute;
    left: 0;
    bottom: 0;
    height: 2.5px;
    width: 100%;
    background: linear-gradient(90deg, #ffc76a, #ff9a3c);
    transform-origin: left center;
    animation: undo-countdown linear forwards;
}

@keyframes undo-countdown {
    from {
        transform: scaleX(1);
    }
    to {
        transform: scaleX(0);
    }
}

.undo-slide-enter-active,
.undo-slide-leave-active {
    transition: all 0.26s cubic-bezier(0.34, 1.4, 0.44, 1);
}
.undo-slide-enter-from {
    opacity: 0;
    transform: translateY(14px) scale(0.96);
}
.undo-slide-leave-to {
    opacity: 0;
    transform: translateY(10px) scale(0.96);
}

@media (prefers-reduced-motion: reduce) {
    .undo-progress {
        animation: none;
    }
    .undo-slide-enter-active,
    .undo-slide-leave-active {
        transition: none;
    }
}
</style>
