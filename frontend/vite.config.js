import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      // 开发环境将 /auth 转发到 gl-auth（默认 8602）；若网关已配置路由可改为 8502
      '/auth': {
        target: 'http://127.0.0.1:8602',
        changeOrigin: true,
      },
      // 字典等 /admin/sys/** 直连 logi-admin（默认 8601）
      '/admin': {
        target: 'http://127.0.0.1:8601',
        changeOrigin: true,
      },
    },
  },
})
