import { ref } from 'vue'

const STORAGE_KEY = 'travel-theme'

// 全局共享同一份主题状态（所有页面、所有组件读到的是同一个值）
const isDark = ref(false)

const applyClass = (dark) => {
  const root = document.documentElement
  root.classList.toggle('dark', dark)
  root.dataset.theme = dark ? 'dark' : 'light'
}

// 初始化：优先读本地缓存，没有缓存就跟随系统
export function initTheme() {
  let saved = ''
  try {
    saved = localStorage.getItem(STORAGE_KEY) || ''
  } catch (e) {
    saved = ''
  }
  if (saved === 'dark' || saved === 'light') {
    isDark.value = saved === 'dark'
  } else {
    try {
      isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
    } catch (e) {
      isDark.value = false
    }
  }
  applyClass(isDark.value)
}

// 只改状态和 class，不做动画（给扩散动画的中间帧调用）
export function setTheme(dark) {
  isDark.value = dark
  applyClass(dark)
  try {
    localStorage.setItem(STORAGE_KEY, dark ? 'dark' : 'light')
  } catch (e) {
    /* 隐身模式下可能写不了，忽略即可 */
  }
}

/**
 * 以点击位置为圆心做扩散切换
 * 用浏览器原生的 View Transitions，新旧两帧叠在一起，新配色从按钮处圆形铺开
 */
export function toggleThemeWithReveal(event) {
  const next = !isDark.value
  const root = document.documentElement

  const x = event && event.clientX ? event.clientX : window.innerWidth - 60
  const y = event && event.clientY ? event.clientY : window.innerHeight - 60
  // 半径要足以盖住离点击点最远的那个角
  const radius = Math.hypot(
    Math.max(x, window.innerWidth - x),
    Math.max(y, window.innerHeight - y)
  )

  root.style.setProperty('--tt-x', x + 'px')
  root.style.setProperty('--tt-y', y + 'px')
  root.style.setProperty('--tt-r', radius + 'px')

  // 浏览器不支持或者用户开了“减少动效”，就直接切，不留白屏
  const reduce = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches
  if (!document.startViewTransition || reduce) {
    setTheme(next)
    return
  }

  root.classList.add('theme-revealing')
  const transition = document.startViewTransition(() => setTheme(next))
  transition.finished.finally(() => {
    root.classList.remove('theme-revealing')
  })
}

export function useTheme() {
  return { isDark, setTheme, toggleThemeWithReveal }
}
