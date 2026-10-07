// ==========================================
// api.js — 统一 API 地址管理
// H5 开发: 用相对路径 /api (走 vite proxy)
// 生产环境: Docker 前后端同域部署, /api 反代到 8080
// App 打包: 用 http://localhost:8080/api
// ==========================================

let _baseUrl = null

function detectBaseUrl() {
  // #ifdef H5
  // H5: 用相对路径, 由 vite dev server 或 Nginx proxy 转发
  return ''
  // #endif
  
  // #ifdef APP-PLUS
  // App: Docker 跑在本机 8080
  return 'http://localhost:8080'
  // #endif
  
  // #ifdef MP-WEIXIN
  return 'https://your-server.com'
  // #endif
  
  return 'http://localhost:8080'
}

export function getBaseUrl() {
  if (!_baseUrl) _baseUrl = detectBaseUrl()
  return _baseUrl
}

export function apiUrl(path) {
  const base = getBaseUrl()
  if (path.startsWith('/')) return base + path
  return base + '/' + path
}

// 直接替换: 'http://192.168.2.15:8080/api' => apiUrl('') + path
export const API_BASE = getBaseUrl() + '/api'
