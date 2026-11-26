import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 全站深色主题（只有 html 上有 .dark 时才生效）
import './styles/theme-dark.css'
// 边缘流光效果（.edge-beam 工具类）
import './styles/edge-beam.css'
// 后台「悬浮岛」视觉补丁（只在 .admin-layout 内生效）
import './styles/admin-island.css'
// 全站品牌视觉基线（海岸旅行：深青 + 暖沙 + 日落橙）
import './styles/theme-brand.css'
// 全站绿色原生光标（前后台统一，输入框保留文本光标）
import './styles/cursor-brand.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from './router'
import axios from 'axios'
import { initTheme } from './composables/useTheme'
// 引入消息提示
import { ElMessage } from 'element-plus'

// 登录状态改为按标签页隔离，清除旧版共享登录，避免旧账号串到新标签页
localStorage.removeItem('userInfo')

// 先定主题再渲染，避免开屏白闪
initTheme()

const app = createApp(App)
// 全局注册图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(ElementPlus)
app.use(router)
// =====删掉axios.defaults.baseURL这一行！=====
app.config.globalProperties.$axios = axios
app.mount('#app')
