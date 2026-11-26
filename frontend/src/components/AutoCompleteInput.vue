<template>
    <div class="ac-root" ref="rootRef">
        <el-input
            :model-value="modelValue"
            :placeholder="placeholder"
            :size="size"
            :clearable="clearable"
            @update:model-value="handleInput"
            @focus="handleFocus"
            @blur="handleBlur"
            @keydown="handleKeydown">
            <template #prefix v-if="$slots.prefix">
                <slot name="prefix" />
            </template>
            <template #suffix v-if="$slots.suffix">
                <slot name="suffix" />
            </template>
        </el-input>

        <transition name="ac-fade">
            <div v-if="visible" class="ac-panel">
                <ul v-if="list.length" class="ac-list">
                    <li
                        v-for="(item, i) in list"
                        :key="item + i"
                        class="ac-item"
                        :class="{ 'is-active': i === activeIndex }"
                        @mouseenter="activeIndex = i"
                        @mousedown.prevent="choose(item)">
                        <el-icon class="ac-icon"><Search /></el-icon>
                        <span class="ac-text" v-html="highlight(item)"></span>
                        <el-icon class="ac-enter" v-if="i === activeIndex"><Right /></el-icon>
                    </li>
                </ul>
                <div v-else class="ac-state">
                    {{ loading ? '正在匹配…' : '没有匹配的结果，换个词试试' }}
                </div>
            </div>
        </transition>
    </div>
</template>

<script setup>
import { ref, watch, onBeforeUnmount } from 'vue'
import { Search, Right } from '@element-plus/icons-vue'
import { getSuggestApi } from '@/api/suggestData'

const props = defineProps({
    modelValue: { type: String, default: '' },
    // 联想类型：spot / spotArea / guide / guideAuthor / hotel / ticket
    type: { type: String, default: 'spot' },
    placeholder: { type: String, default: '请输入关键词' },
    size: { type: String, default: 'default' },
    clearable: { type: Boolean, default: true },
    limit: { type: Number, default: 8 }
})

const emit = defineEmits(['update:modelValue', 'select', 'search'])

const rootRef = ref(null)
const list = ref([])
const visible = ref(false)
const loading = ref(false)
const activeIndex = ref(-1)

let timer = null
let seq = 0

// 请求联想词，丢弃过期响应，避免打字太快时旧结果覆盖新结果
const fetchSuggest = async (keyword) => {
    const current = ++seq
    loading.value = true
    try {
        const res = await getSuggestApi(props.type, keyword)
        if (current !== seq) return
        const arr = Array.isArray(res) ? res : (res && Array.isArray(res.data) ? res.data : [])
        list.value = arr.slice(0, props.limit)
        visible.value = true
        activeIndex.value = list.value.length ? 0 : -1
    } catch (e) {
        if (current !== seq) return
        list.value = []
        visible.value = false
    } finally {
        if (current === seq) loading.value = false
    }
}

// 输入防抖，避免每个字符都打一次后端
const scheduleFetch = (keyword) => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fetchSuggest(keyword), 260)
}

const handleFocus = () => {
    // 聚焦先给推荐（关键词为空时后端返回默认前 10 条）
    scheduleFetch(props.modelValue || '')
}

const handleInput = (val) => {
    emit('update:modelValue', val)
    scheduleFetch(val || '')
}

const handleBlur = () => {
    // 延迟关闭，保证点击联想项时能先触发选择
    setTimeout(() => {
        visible.value = false
        activeIndex.value = -1
    }, 120)
}

const choose = (item) => {
    emit('update:modelValue', item)
    emit('select', item)
    visible.value = false
    activeIndex.value = -1
}

const handleKeydown = (e) => {
    if (!visible.value || !list.value.length) {
        if (e.key === 'Enter') emit('search', props.modelValue)
        return
    }
    if (e.key === 'ArrowDown') {
        e.preventDefault()
        activeIndex.value = (activeIndex.value + 1) % list.value.length
    } else if (e.key === 'ArrowUp') {
        e.preventDefault()
        activeIndex.value = (activeIndex.value - 1 + list.value.length) % list.value.length
    } else if (e.key === 'Enter') {
        e.preventDefault()
        if (activeIndex.value >= 0) choose(list.value[activeIndex.value])
        else emit('search', props.modelValue)
    } else if (e.key === 'Escape') {
        visible.value = false
        activeIndex.value = -1
    }
}

// 切换联想类型（比如首页的景点 / 攻略切换）时，重新拉一次
watch(() => props.type, () => {
    if (visible.value) scheduleFetch(props.modelValue || '')
})

onBeforeUnmount(() => {
    if (timer) clearTimeout(timer)
})

const escapeHtml = (text) => String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')

// 把命中的那段文字高亮
const highlight = (text) => {
    const kw = (props.modelValue || '').trim()
    const safe = escapeHtml(text)
    if (!kw) return safe
    const idx = String(text).toLowerCase().indexOf(kw.toLowerCase())
    if (idx < 0) return safe
    return escapeHtml(text.slice(0, idx))
        + '<mark class="ac-mark">' + escapeHtml(text.slice(idx, idx + kw.length)) + '</mark>'
        + escapeHtml(text.slice(idx + kw.length))
}
</script>

<style scoped>
.ac-root {
    position: relative;
    width: 100%;
    flex: 1 1 auto;
}
.ac-root :deep(.el-input) {
    width: 100%;
}

/* 联想面板 */
.ac-panel {
    position: absolute;
    top: calc(100% + 8px);
    left: 0;
    right: 0;
    z-index: 30;
    padding: 6px;
    max-height: 268px;
    overflow-y: auto;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(220, 230, 244, 0.9);
    border-radius: 14px;
    box-shadow: 0 14px 32px rgba(20, 40, 80, 0.16);
}

.ac-list {
    margin: 0;
    padding: 0;
    list-style: none;
}

.ac-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 9px 12px;
    border-radius: 10px;
    font-size: 14px;
    color: #3a4a61;
    cursor: pointer;
    transition: background 0.18s ease, color 0.18s ease;
}
.ac-item.is-active,
.ac-item:hover {
    background: #f1f7ff;
    color: #22364f;
}

.ac-icon {
    flex-shrink: 0;
    font-size: 13px;
    color: #a7b6c9;
}

.ac-text {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.ac-enter {
    flex-shrink: 0;
    font-size: 12px;
    color: #0e7c86;
}

/* 命中部分高亮：只变色加粗，不铺底色，避免视觉噪音 */
.ac-mark {
    background: transparent;
    color: #0e7c86;
    font-weight: 600;
    padding: 0;
}

.ac-state {
    padding: 14px 12px;
    text-align: center;
    font-size: 13px;
    color: #a7b6c9;
}

.ac-fade-enter-active,
.ac-fade-leave-active {
    transition: opacity 0.18s ease, transform 0.18s ease;
}
.ac-fade-enter-from,
.ac-fade-leave-to {
    opacity: 0;
    transform: translateY(-4px);
}
</style>
