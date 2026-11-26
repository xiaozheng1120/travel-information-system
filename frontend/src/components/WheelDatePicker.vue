<template>
    <div class="wd-root" ref="rootRef" :style="{ width: width }">
        <div class="wd-trigger" :class="{ 'is-open': open, 'is-empty': !displayText }" @click="toggle" tabindex="0"
            @keydown.enter.prevent="toggle">
            <el-icon class="wd-cal"><Calendar /></el-icon>
            <span class="wd-value">{{ displayText || placeholder }}</span>
            <el-icon class="wd-arrow"><ArrowDown /></el-icon>
        </div>

        <Teleport to="body">
            <transition name="wd-pop">
                <div v-if="open" class="wd-panel" ref="panelRef" :style="panelStyle" @mousedown.stop>
                    <div class="wd-head">
                        <span class="wd-title">选择日期</span>
                        <span class="wd-preview">{{ previewText }}</span>
                    </div>

                    <div class="wd-wheels">
                        <div class="wd-col" v-for="col in columns" :key="col.key">
                            <div class="wd-scroll" :ref="(el) => setColRef(col.key, el)" @scroll="onScroll(col.key)">
                                <div class="wd-pad"></div>
                                <div v-for="(opt, i) in col.options" :key="opt.value" class="wd-item"
                                    :class="{ 'is-active': i === col.index }" @click="pick(col.key, i)">
                                    {{ opt.label }}
                                </div>
                                <div class="wd-pad"></div>
                            </div>
                            <div class="wd-fade top"></div>
                            <div class="wd-fade bottom"></div>
                            <div class="wd-band"></div>
                            <div class="wd-label">{{ col.unit }}</div>
                        </div>
                    </div>

                    <div class="wd-actions">
                        <button type="button" class="wd-btn ghost" @click="close">取消</button>
                        <button type="button" class="wd-btn primary" @click="confirm">确定</button>
                    </div>
                </div>
            </transition>
        </Teleport>
    </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { Calendar, ArrowDown } from '@element-plus/icons-vue'

const props = defineProps({
    modelValue: { type: String, default: '' },
    placeholder: { type: String, default: '请选择日期' },
    width: { type: String, default: '100%' },
    // 最早 / 最晚可选日期（YYYY-MM-DD），不传则按 今天 到 今年+6 年
    minDate: { type: String, default: '' },
    maxDate: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'change'])

const ITEM_H = 40
const VISIBLE = 3

const rootRef = ref(null)
const panelRef = ref(null)
const open = ref(false)
const panelStyle = ref({})

const today = new Date()
const currentYear = today.getFullYear()

const clamp = (n, min, max) => Math.min(max, Math.max(min, n))

const parseDate = (text) => {
    const m = /^(\d{4})-(\d{1,2})-(\d{1,2})/.exec(String(text || ''))
    if (!m) return null
    return { y: Number(m[1]), m: Number(m[2]), d: Number(m[3]) }
}

const minBound = computed(() => parseDate(props.minDate) || { y: currentYear, m: 1, d: 1 })
const maxBound = computed(() => parseDate(props.maxDate) || { y: currentYear + 6, m: 12, d: 31 })
const minYear = computed(() => Math.min(minBound.value.y, currentYear))
const maxYear = computed(() => Math.max(maxBound.value.y, currentYear + 1))

// 当前面板里正在编辑的年月日（未点确定前不写回 v-model）
const temp = ref({ y: currentYear, m: today.getMonth() + 1, d: today.getDate() })

const daysInMonth = (y, m) => new Date(y, m, 0).getDate()

const displayText = computed(() => {
    const p = parseDate(props.modelValue)
    return p ? `${p.y}-${String(p.m).padStart(2, '0')}-${String(p.d).padStart(2, '0')}` : ''
})

const previewText = computed(() =>
    `${temp.value.y} 年 ${temp.value.m} 月 ${temp.value.d} 日`
)

const years = computed(() => {
    const list = []
    for (let y = minYear.value; y <= maxYear.value; y++) list.push(y)
    return list
})

const columns = computed(() => {
    const dy = daysInMonth(temp.value.y, temp.value.m)
    const dayCount = clamp(temp.value.d, 1, dy)

    return [
        {
            key: 'y', unit: '年',
            options: years.value.map(y => ({ value: y, label: String(y) })),
            index: years.value.indexOf(temp.value.y)
        },
        {
            key: 'm', unit: '月',
            options: Array.from({ length: 12 }, (_, i) => ({ value: i + 1, label: String(i + 1).padStart(2, '0') })),
            index: temp.value.m - 1
        },
        {
            key: 'd', unit: '日',
            options: Array.from({ length: dy }, (_, i) => ({ value: i + 1, label: String(i + 1).padStart(2, '0') })),
            index: dayCount - 1
        }
    ]
})

const colRefs = {}
const setColRef = (key, el) => {
    if (el) colRefs[key] = el
}
const scrollTimers = {}

const syncScroll = (smooth = false) => {
    columns.value.forEach((col) => {
        const el = colRefs[col.key]
        if (!el) return
        const top = Math.max(0, col.index) * ITEM_H
        if (smooth) el.scrollTo({ top, behavior: 'smooth' })
        else el.scrollTop = top
    })
}

const onScroll = (key) => {
    if (scrollTimers[key]) clearTimeout(scrollTimers[key])
    scrollTimers[key] = setTimeout(() => {
        const el = colRefs[key]
        if (!el) return
        const idx = clamp(Math.round(el.scrollTop / ITEM_H), 0, (columns.value.find(c => c.key === key)?.options.length || 1) - 1)
        if (key === 'y') {
            const y = years.value[idx]
            if (y && y !== temp.value.y) {
                temp.value.y = y
                temp.value.d = clamp(temp.value.d, 1, daysInMonth(y, temp.value.m))
            }
        } else if (key === 'm') {
            const m = idx + 1
            if (m !== temp.value.m) {
                temp.value.m = m
                temp.value.d = clamp(temp.value.d, 1, daysInMonth(temp.value.y, m))
            }
        } else if (key === 'd') {
            temp.value.d = idx + 1
        }
    }, 90)
}

const pick = (key, index) => {
    const col = columns.value.find(c => c.key === key)
    if (!col) return
    if (key === 'y') temp.value.y = years.value[index]
    if (key === 'm') temp.value.m = index + 1
    if (key === 'd') temp.value.d = index + 1
    temp.value.d = clamp(temp.value.d, 1, daysInMonth(temp.value.y, temp.value.m))
    nextTick(() => syncScroll(true))
}

const clampToBounds = (date) => {
    const value = date.y * 10000 + date.m * 100 + date.d
    const min = minBound.value.y * 10000 + minBound.value.m * 100 + minBound.value.d
    const max = maxBound.value.y * 10000 + maxBound.value.m * 100 + maxBound.value.d
    if (value < min) return { ...minBound.value }
    if (value > max) return { ...maxBound.value }
    return date
}

const updatePanelPos = () => {
    if (!rootRef.value) return
    const rect = rootRef.value.getBoundingClientRect()
    const panelW = 300
    const panelH = 300
    const left = clamp(rect.left, 10, window.innerWidth - panelW - 10)
    let top = rect.bottom + 8
    if (top + panelH > window.innerHeight - 10) {
        top = Math.max(10, rect.top - panelH - 8)
    }
    panelStyle.value = { left: left + 'px', top: top + 'px', width: panelW + 'px' }
}

const openPanel = () => {
    const parsed = parseDate(props.modelValue)
    temp.value = clampToBounds(parsed || { y: currentYear, m: today.getMonth() + 1, d: today.getDate() })
    open.value = true
    updatePanelPos()
    nextTick(() => syncScroll(false))
}
const close = () => {
    open.value = false
}
const toggle = () => (open.value ? close() : openPanel())

const confirm = () => {
    const { y, m, d } = temp.value
    const text = `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    emit('update:modelValue', text)
    emit('change', text)
    open.value = false
}

const onDocPointerDown = (e) => {
    if (!open.value) return
    const inRoot = rootRef.value && rootRef.value.contains(e.target)
    const inPanel = panelRef.value && panelRef.value.contains(e.target)
    if (!inRoot && !inPanel) close()
}
const onKeydown = (e) => {
    if (e.key === 'Escape' && open.value) close()
}
const onWinChange = () => {
    if (open.value) updatePanelPos()
}

watch(open, (val) => {
    if (val) {
        document.addEventListener('mousedown', onDocPointerDown, true)
        document.addEventListener('keydown', onKeydown, true)
        window.addEventListener('resize', onWinChange)
        window.addEventListener('scroll', onWinChange, true)
    } else {
        document.removeEventListener('mousedown', onDocPointerDown, true)
        document.removeEventListener('keydown', onKeydown, true)
        window.removeEventListener('resize', onWinChange)
        window.removeEventListener('scroll', onWinChange, true)
    }
})

// 外部值变化（比如表单重置）时同步到面板
watch(() => props.modelValue, (val) => {
    if (open.value) return
    const parsed = parseDate(val)
    if (parsed) temp.value = parsed
})

onBeforeUnmount(() => {
    document.removeEventListener('mousedown', onDocPointerDown, true)
    document.removeEventListener('keydown', onKeydown, true)
    window.removeEventListener('resize', onWinChange)
    window.removeEventListener('scroll', onWinChange, true)
})
</script>

<style scoped>
.wd-root {
    position: relative;
}

/* 触发器：和项目里其他输入框同样的胶囊质感 */
.wd-trigger {
    display: flex;
    align-items: center;
    gap: 8px;
    height: 38px;
    padding: 0 14px;
    background: #fff;
    border: 1px solid rgba(14, 124, 134, 0.22);
    border-radius: 12px;
    cursor: pointer;
    transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
    user-select: none;
}

.wd-trigger:hover {
    border-color: rgba(14, 124, 134, 0.55);
}

.wd-trigger.is-open {
    border-color: #0e7c86;
    box-shadow: 0 0 0 3px rgba(14, 124, 134, 0.14);
}

.wd-cal {
    font-size: 15px;
    color: #0e7c86;
}

.wd-value {
    flex: 1;
    font-size: 14px;
    color: #22364f;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.wd-trigger.is-empty .wd-value {
    color: #9fb0c6;
}

.wd-arrow {
    font-size: 12px;
    color: #a7b6c9;
    transition: transform 0.25s ease;
}

.wd-trigger.is-open .wd-arrow {
    transform: rotate(180deg);
}

/* 弹出面板 */
.wd-panel {
    position: fixed;
    z-index: 3000;
    padding: 14px 16px 16px;
    background: #fff;
    border-radius: 18px;
    border: 1px solid rgba(220, 230, 244, 0.9);
    box-shadow: 0 18px 44px rgba(20, 40, 80, 0.18);
}

.wd-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
}

.wd-title {
    font-size: 14px;
    font-weight: 600;
    color: #1f2937;
}

.wd-preview {
    font-size: 12px;
    color: #6b7280;
}

/* 三列滚轮 */
.wd-wheels {
    display: flex;
    gap: 8px;
}

.wd-col {
    position: relative;
    flex: 1;
    height: 120px;
}

.wd-scroll {
    height: 120px;
    overflow-y: auto;
    scroll-snap-type: y mandatory;
    scrollbar-width: none;
    -ms-overflow-style: none;
}
.wd-scroll::-webkit-scrollbar {
    display: none;
}

.wd-pad {
    height: 40px;
}

.wd-item {
    height: 40px;
    line-height: 40px;
    text-align: center;
    font-size: 15px;
    color: #6b7280;
    scroll-snap-align: center;
    cursor: pointer;
    transition: color 0.18s ease, transform 0.18s ease;
}

.wd-item.is-active {
    color: #1f2937;
    font-weight: 700;
    font-size: 17px;
}

/* 上下渐隐，做出滚轮的边缘感 */
.wd-fade {
    position: absolute;
    left: 0;
    right: 0;
    height: 32px;
    pointer-events: none;
    z-index: 4;
}
.wd-fade.top {
    top: 0;
    background: linear-gradient(180deg, #fff 10%, rgba(255, 255, 255, 0));
}
.wd-fade.bottom {
    bottom: 0;
    background: linear-gradient(0deg, #fff 10%, rgba(255, 255, 255, 0));
}

/* 中间那条选项行的柔和高亮条 */
.wd-band {
    position: absolute;
    left: 2px;
    right: 2px;
    top: 40px;
    height: 40px;
    border-radius: 10px;
    background: linear-gradient(180deg, rgba(14, 124, 134, 0.10), rgba(14, 124, 134, 0.16));
    pointer-events: none;
    z-index: 1;
}

.wd-scroll {
    position: relative;
    z-index: 3;
}

.wd-label {
    position: absolute;
    left: 0;
    right: 0;
    bottom: -20px;
    text-align: center;
    font-size: 12px;
    color: #9fb0c6;
}

/* 按钮 */
.wd-actions {
    display: flex;
    gap: 10px;
    margin-top: 30px;
}

.wd-btn {
    flex: 1;
    height: 36px;
    border-radius: 999px;
    border: 1px solid transparent;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.wd-btn.ghost {
    background: #f2f6fc;
    border-color: rgba(14, 124, 134, 0.18);
    color: #6b7280;
}
.wd-btn.ghost:hover {
    background: #e8f1fd;
    color: #22364f;
}

.wd-btn.primary {
    background: linear-gradient(135deg, #0e7c86, #0e7c86);
    color: #fff;
    box-shadow: 0 8px 18px rgba(14, 124, 134, 0.28);
}
.wd-btn.primary:hover {
    filter: brightness(1.05);
}
.wd-btn:active {
    transform: scale(0.97);
}

/* 弹入弹出手感 */
.wd-pop-enter-active,
.wd-pop-leave-active {
    transition: opacity 0.18s ease, transform 0.22s cubic-bezier(0.34, 1.3, 0.4, 1);
}
.wd-pop-enter-from,
.wd-pop-leave-to {
    opacity: 0;
    transform: translateY(-6px) scale(0.98);
}
</style>
