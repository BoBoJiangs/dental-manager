import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [vue()],
    server: {
      host: '0.0.0.0',
      port: 5173,
      proxy: {
        '/api': {
          target: env.VITE_PROXY_TARGET || 'http://127.0.0.1:18084',
          changeOrigin: true,
        },
      },
    },
    build: {
      chunkSizeWarningLimit: 800,
      rollupOptions: {
        output: {
          manualChunks(id) {
            if (!id.includes('node_modules')) {
              return
            }
            if (id.includes('@element-plus/icons-vue')) {
              return 'element-icons'
            }
            if (id.includes('element-plus')) {
              const componentMatch = id.match(/element-plus\/es\/components\/([^/]+)/)
              if (componentMatch?.[1]) {
                return `element-${componentMatch[1]}`
              }
              if (id.includes('/hooks/') || id.includes('/utils/') || id.includes('/tokens/')) {
                return 'element-core'
              }
              return 'element-core'
            }
            if (id.includes('/vue-router/') || id.includes('/pinia/') || id.includes('/vue/')) {
              return 'vue-core'
            }
            if (id.includes('/axios/') || id.includes('/dayjs/')) {
              return 'utils'
            }
            return 'vendor'
          },
        },
      },
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
  }
})
