import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173, //固定前端端口为5173，不会自动跳到5174
    proxy: {
      // 只代理后端接口前缀 /api
      // 原来的 /guide、/hotel 前缀会把前端路由 /guide、/guideDetail、/hotelDetail 一起转给后端，导致 Whitelabel 404
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})