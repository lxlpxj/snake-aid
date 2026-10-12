// ==========================================
// api.js — 统一 API 地址管理（全项目唯一开关）
// H5 开发: 用相对路径 /api (走 vite proxy)
// 生产环境: Docker 前后端同域部署, /api 反代到 8080
// App 打包: 手机没有 nginx 转发, 必须写完整地址 → 本机 Docker 192.168.1.105
//   换服务器只改下面 APP-PLUS 这一行
// ==========================================

let _baseUrl = null

function detectBaseUrl() {
  // #ifdef H5
  // H5: 用相对路径, 由 vite dev server 或 Nginx proxy 转发
  return ''
  // #endif

  // #ifdef APP-PLUS
  // App 真机直连本机 Docker（固定 IP 192.168.1.105:8080）
  return 'http://192.168.1.105:8080'
  // #endif

  // #ifdef MP-WEIXIN
  return 'https://your-server.com'
  // #endif

  return 'http://192.168.1.105:8080'
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

// 用法: apiUrl('/user/login') → http://192.168.1.105:8080/user/login
export const API_BASE = getBaseUrl() + '/api'
