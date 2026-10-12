// ==========================================
// api.js — 统一 API 地址管理（全项目唯一开关）
// H5 开发: 用相对路径 /api (走 vite proxy)
// 生产环境: Docker 前后端同域部署, /api 反代到 8080
// App 打包: 手机没有 nginx 转发, 必须写完整地址 → 本机 Docker localhost:8080
//   真机调试时 localhost 指手机自己, 需改成电脑的局域网 IP（如 192.168.1.x）
//   换服务器只改下面 APP-PLUS 这两行
// ==========================================

let _baseUrl = null

function detectBaseUrl() {
  // #ifdef H5
  // H5: 用相对路径, 由 vite dev server 或 Nginx proxy 转发
  return ''
  // #endif

  // #ifdef APP-PLUS
  // App 直连后端（默认本机 Docker；真机访问需改为电脑局域网 IP）
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

// 用法: apiUrl('/user/login') → http://localhost:8080/user/login
export const API_BASE = getBaseUrl() + '/api'
