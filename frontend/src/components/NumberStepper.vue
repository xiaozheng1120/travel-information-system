<template>
    <div class="ns" :class="{ 'is-disabled': disabled }">
        <button type="button" class="ns-btn" :disabled="isDisabledMin" aria-label="减少" @click="step(-1)">
            <el-icon><Minus /></el-icon>
        </button>
        <input v-if="editing" ref="inputRef" v-model="draft" class="ns-value ns-input" type="text"
            inputmode="numeric" @blur="commit" @keydown.enter.prevent="commit" @keydown.esc.prevent="cancel" />
        <span v-else class="ns-value" @click="startEdit">{{ value }}</span>
        <button type="button" class="ns-btn" :disabled="isDisabledMax" aria-label="增加" @click="step(1)">
            <el-icon><Plus /></el-icon>
        </button>
    </div>
</template>

<script setup>
import { computed, nextTick, ref } from 'vue'
import { Minus, Plus } from '@element-plus/icons-vue'

const props = defineProps({
    modelValue: { type: [Number, String], default: 1 },
    min: { type: Number, default: 1 },
    max: { type: Number, default: 99 },
    step: { type: Number, default: 1 },
    disabled: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'change'])

const editing = ref(false)
const draft = ref('')
const inputRef = ref(null)

const value = computed(() => {
    const n = Number(props.modelValue)
    if (!Number.isFinite(n)) return props.min
    return Math.min(props.max, Math.max(props.min, n))
})

const isDisabledMin = computed(() => props.disabled || value.value <= props.min)
const isDisabledMax = computed(() => props.disabled || value.value >= props.max)

const apply = (next) => {
    const clamped = Math.min(props.max, Math.max(props.min, next))
    if (clamped === value.value) return
    emit('update:modelValue', clamped)
    emit('change', clamped)
}

const step = (dir) => {
    if (props.disabled) return
    apply(value.value + dir * props.step)
}

// 点数字可以直接改，方便后台填大一点的库存
const startEdit = () => {
    if (props.disabled) return
    editing.value = true
    draft.value = String(value.value)
    nextTick(() => inputRef.value?.focus())
}

const commit = () => {
    const n = Number(draft.value)
    editing.value = false
    if (Number.isFinite(n)) apply(Math.round(n))
}

const cancel = () => {
    editing.value = false
}
</script>

<style scoped>
.ns {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 4px;
    background: #fff;
    border: 1px solid rgba(196, 214, 240, 0.75);
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(31, 58, 95, 0.05);
}

.ns-btn {
    width: 30px;
    height: 30px;
    flex: 0 0 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 9px;
    border: 1px solid rgba(196, 214, 240, 0.75);
    background: #fbfdff;
    color: #35557d;
    font-size: 14px;
    cursor: pointer;
    transition: background 0.18s ease, border-color 0.18s ease, color 0.18s ease,
        transform 0.18s ease;
}

.ns-btn:hover:not(:disabled) {
    background: #eef6ff;
    border-color: #7cc4ca;
    color: #2f7fe0;
}

.ns-btn:active:not(:disabled) {
    transform: scale(0.94);
}

.ns-btn:disabled {
    opacity: 0.4;
    cursor: not-allowed;
}

.ns-value {
    min-width: 44px;
    height: 30px;
    padding: 0 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 9px;
    border: 1px solid rgba(196, 214, 240, 0.6);
    background: #f2f7fd;
    color: #1f2937;
    font-size: 14px;
    font-weight: 600;
    font-variant-numeric: tabular-nums;
    box-sizing: border-box;
    cursor: text;
}

.ns-input {
    width: 48px;
    min-width: 48px;
    outline: none;
    text-align: center;
    font-family: inherit;
}

.ns-input:focus {
    border-color: #7cc4ca;
    background: #fbfdff;
    box-shadow: 0 0 0 2px rgba(14, 124, 134, 0.12);
}

.ns.is-disabled {
    opacity: 0.7;
}
</style>
