import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

/** 开发 / preview 共用：将浏览器同源 API 前缀转发到本机服务（避免 5173 上直接请求 /admin 得到 404） */
const logiAdminTarget = process.env.VITE_PROXY_LOGI_ADMIN || 'http://127.0.0.1:8601'
const glAuthTarget = process.env.VITE_PROXY_GL_AUTH || 'http://127.0.0.1:8602'
const logiWmsTarget = process.env.VITE_PROXY_LOGI_WMS || 'http://127.0.0.1:8721'

const devProxy = {
  '/auth': {
    target: glAuthTarget,
    changeOrigin: true,
  },
  '/admin': {
    target: logiAdminTarget,
    changeOrigin: true,
  },
  '/wms': {
    target: logiWmsTarget,
    changeOrigin: true,
  },
}

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: devProxy,
  },
  /** `vite preview` 默认不继承 server.proxy，需单独配置，否则 /admin/** 会 404 */
  preview: {
    proxy: devProxy,
  },
})
