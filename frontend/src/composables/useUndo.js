import { ref } from 'vue'

/**
 * 「能撤销就别二次确认」
 * 删除 / 取消这类动作先在界面生效，底部弹一条 5 秒内可撤销的提示；
 * 5 秒内点「撤销」→ 完全不请求后端，界面回滚；
 * 5 秒后自动提交 → 才真正调用后端接口。
 */
const undoItems = ref([])
const tasks = new Map()
let seq = 0

const removeItem = (id) => {
    undoItems.value = undoItems.value.filter((item) => item.id !== id)
}

const finish = (id, commit) => {
    const rec = tasks.get(id)
    if (!rec) return
    clearTimeout(rec.timer)
    tasks.delete(id)
    removeItem(id)
    if (commit) {
        try {
            const r = rec.onCommit?.()
            if (r && typeof r.catch === 'function') r.catch(() => {})
        } catch (e) {
            /* 提交失败由各页面自己提示 */
        }
    } else {
        rec.onUndo?.()
    }
}

export const pushUndo = ({ message, duration = 5000, onCommit, onUndo }) => {
    const id = ++seq
    undoItems.value.push({ id, message, duration })
    const timer = setTimeout(() => finish(id, true), duration)
    tasks.set(id, { timer, onCommit, onUndo })
    return id
}

export const undoItem = (id) => finish(id, false)

export const useUndo = () => ({ undoItems, pushUndo, undoItem })
