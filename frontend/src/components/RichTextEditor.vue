<template>
  <div class="rich-editor">
    <div class="rte-toolbar">
      <button type="button" class="rte-btn" title="撤销" @click="exec('undo')">↶</button>
      <button type="button" class="rte-btn" title="重做" @click="exec('redo')">↷</button>
      <span class="rte-sep"></span>
      <button type="button" class="rte-btn" title="加粗" @click="exec('bold')"><b>B</b></button>
      <button type="button" class="rte-btn" title="斜体" @click="exec('italic')"><i>I</i></button>
      <button type="button" class="rte-btn" title="下划线" @click="exec('underline')"><u>U</u></button>
      <span class="rte-sep"></span>
      <button type="button" class="rte-btn" title="大标题" @click="exec('formatBlock', '<h2>')">H2</button>
      <button type="button" class="rte-btn" title="小标题" @click="exec('formatBlock', '<h3>')">H3</button>
      <button type="button" class="rte-btn" title="正文" @click="exec('formatBlock', '<p>')">正文</button>
      <span class="rte-sep"></span>
      <button type="button" class="rte-btn" title="无序列表" @click="exec('insertUnorderedList')">• 列表</button>
      <button type="button" class="rte-btn" title="有序列表" @click="exec('insertOrderedList')">1. 列表</button>
      <button type="button" class="rte-btn" title="引用" @click="exec('formatBlock', '<blockquote>')">引用</button>
      <span class="rte-sep"></span>
      <button type="button" class="rte-btn" title="插入图片" :disabled="uploading" @click="pickImage">
        {{ uploading ? '上传中…' : '插入图片' }}
      </button>
      <button type="button" class="rte-btn" title="清除格式" @click="clearFormat">清除格式</button>
      <span class="rte-spacer"></span>
      <button type="button" class="rte-btn" :class="{ active: sourceMode }" @click="toggleSource">
        {{ sourceMode ? '可视化' : 'HTML' }}
      </button>
      <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="onFileChange" />
    </div>

    <div
      v-show="!sourceMode"
      ref="editorRef"
      class="rte-body"
      contenteditable="true"
      :data-placeholder="placeholder"
      @input="handleInput"
      @paste="handlePaste"
      @blur="handleInput"
    ></div>

    <textarea
      v-show="sourceMode"
      class="rte-source"
      :value="innerHtml"
      @input="handleSourceInput"
    ></textarea>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadImageApi } from '@/api/uploadData.js'

const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '在这里写攻略正文，可以加粗、分点、插入图片…' }
})
const emit = defineEmits(['update:modelValue'])

const editorRef = ref(null)
const fileInput = ref(null)
const innerHtml = ref('')
const sourceMode = ref(false)
const uploading = ref(false)
let lastEmitted = ''

function sanitize(html) {
  if (!html) return ''
  return html
    .replace(/<script[\s\S]*?<\/script>/gi, '')
    .replace(/<style[\s\S]*?<\/style>/gi, '')
    .replace(/\son[a-z]+\s*=\s*"[^"]*"/gi, '')
    .replace(/\son[a-z]+\s*=\s*'[^']*'/gi, '')
    .replace(/javascript:/gi, '')
}

function syncFromDom() {
  if (!editorRef.value) return
  innerHtml.value = editorRef.value.innerHTML
  if (innerHtml.value !== lastEmitted) {
    lastEmitted = innerHtml.value
    emit('update:modelValue', innerHtml.value)
  }
}

function renderFromModel() {
  innerHtml.value = props.modelValue || ''
  if (editorRef.value && !sourceMode.value) {
    const html = sanitize(props.modelValue || '')
    if (editorRef.value.innerHTML !== html) {
      editorRef.value.innerHTML = html
    }
  }
}

function handleInput() {
  syncFromDom()
}

function handleSourceInput(e) {
  const val = sanitize(e.target.value)
  innerHtml.value = val
  lastEmitted = val
  emit('update:modelValue', val)
  if (editorRef.value) {
    editorRef.value.innerHTML = val
  }
}

function exec(cmd, value) {
  if (sourceMode.value) {
    ElMessage.warning('HTML 模式下请直接编辑源码')
    return
  }
  editorRef.value?.focus()
  document.execCommand(cmd, false, value || null)
  syncFromDom()
}

function clearFormat() {
  editorRef.value?.focus()
  document.execCommand('removeFormat', false, null)
  document.execCommand('formatBlock', false, '<p>')
  syncFromDom()
}

function toggleSource() {
  sourceMode.value = !sourceMode.value
  if (!sourceMode.value) {
    nextTick(() => {
      if (editorRef.value) editorRef.value.innerHTML = sanitize(innerHtml.value)
    })
  }
}

function pickImage() {
  fileInput.value?.click()
}

async function onFileChange(e) {
  const file = e.target.files && e.target.files[0]
  e.target.value = ''
  if (!file) return
  uploading.value = true
  try {
    const res = await uploadImageApi(file)
    if (res && res.url) {
      const html = `<img src="${res.url}" style="max-width:100%;border-radius:8px;display:block;margin:10px 0;" />`
      if (sourceMode.value) {
        handleSourceInput({ target: { value: innerHtml.value + html } })
      } else {
        editorRef.value?.focus()
        document.execCommand('insertHTML', false, html)
        syncFromDom()
      }
      ElMessage.success('图片已插入正文')
    } else {
      ElMessage.error((res && res.msg) || '图片上传失败')
    }
  } catch (err) {
    ElMessage.error('图片上传失败，请检查后端上传接口')
  } finally {
    uploading.value = false
  }
}

function handlePaste(e) {
  const html = e.clipboardData?.getData('text/html')
  if (html) {
    e.preventDefault()
    document.execCommand('insertHTML', false, sanitize(html))
    syncFromDom()
  }
}

watch(() => props.modelValue, (val) => {
  if (val === lastEmitted) return
  renderFromModel()
})

onMounted(() => {
  renderFromModel()
})
</script>

<style scoped>
.rich-editor {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
}
.rte-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px;
  padding: 6px 8px;
  background: #faf8f5;
  border-bottom: 1px solid #e8edf5;
}
.rte-btn {
  min-width: 30px;
  height: 28px;
  padding: 0 8px;
  border: 1px solid transparent;
  border-radius: 6px;
  background: transparent;
  color: #4a5d75;
  font-size: 12px;
  cursor: pointer;
}
.rte-btn:hover {
  background: #eaf2ff;
  border-color: #e6f4f5;
  color: #0e7c86;
}
.rte-btn.active {
  background: #eaf2ff;
  color: #0e7c86;
}
.rte-sep {
  width: 1px;
  height: 18px;
  background: #e5e7eb;
  margin: 0 4px;
}
.rte-spacer {
  flex: 1;
}
.rte-body {
  min-height: 180px;
  max-height: 460px;
  overflow-y: auto;
  padding: 14px 16px;
  font-size: 14px;
  line-height: 1.8;
  color: #333;
  outline: none;
}
.rte-body:empty::before {
  content: attr(data-placeholder);
  color: #b6c2d2;
}
.rte-body img {
  max-width: 100%;
  border-radius: 8px;
  display: block;
  margin: 10px 0;
}
.rte-body h2 {
  font-size: 20px;
  margin: 14px 0 8px;
}
.rte-body h3 {
  font-size: 16px;
  margin: 12px 0 6px;
}
.rte-body blockquote {
  margin: 10px 0;
  padding: 8px 12px;
  border-left: 3px solid #7cc4ca;
  background: #f5f9ff;
  color: #475569;
}
.rte-source {
  display: block;
  width: 100%;
  min-height: 220px;
  border: none;
  outline: none;
  padding: 12px 14px;
  font-size: 13px;
  line-height: 1.7;
  color: #334155;
  font-family: Consolas, Menlo, monospace;
  resize: vertical;
  box-sizing: border-box;
}
</style>
