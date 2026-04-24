import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

/**
 * 开发 / preview：浏览器访问 http://127.0.0.1:5174 ，API 为同源相对路径，
 * 由 Vite 转发到 gl-gateway（与 globalLogistic/gl-gateway 默认端口一致）。
 * Docker 内联调：VITE_PROXY_LOGI_GATEWAY=http://gl-gateway:8102
 * 宿主机网关映射为 8502 时：VITE_PROXY_LOGI_GATEWAY=http://127.0.0.1:8502
 */
const logiGatewayTarget = process.env.VITE_PROXY_LOGI_GATEWAY || 'http://127.0.0.1:8102'

const devProxy = {
  '/auth': { target: logiGatewayTarget, changeOrigin: true },
  '/admin': { target: logiGatewayTarget, changeOrigin: true },
  '/wms': { target: logiGatewayTarget, changeOrigin: true },
  '/tms': { target: logiGatewayTarget, changeOrigin: true },
  '/oms': { target: logiGatewayTarget, changeOrigin: true },
  '/system': { target: logiGatewayTarget, changeOrigin: true },
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
    port: 5174,
    strictPort: true,
    /** 监听 0.0.0.0，避免 Windows 上 localhost 走 ::1 而只绑了 127.0.0.1 时出现 net::ERR_CONNECTION_REFUSED */
    host: true,
    proxy: devProxy,
  },
  /** `vite preview` 默认不继承 server.proxy，需单独配置，否则 /admin/** 会 404 */
  preview: {
    port: 5174,
    strictPort: true,
    host: true,
    proxy: devProxy,
  },
})
