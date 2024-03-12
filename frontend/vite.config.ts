import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0', // for docker (connect from the outside of docker container)
    proxy: {
      // 代理配置
      '/api': {
                
        target: 'http://60.205.253.222:8080',
        changeOrigin: true,//允许跨域
        // secure:false,//解决自签名证书错误
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  }
})
