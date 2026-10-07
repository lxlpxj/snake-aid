import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/doc.html': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/swagger-ui.html': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
