import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 不需要 token 的路径
const NO_TOKEN_PATHS = [
  '/admin/auth/login', '/admin/auth/encode',
  '/app/auth/login', '/app/auth/register', '/app/auth/send-sms'
]
request.interceptors.request.use(config => {
  const url = config.url || ''
  const needToken = !NO_TOKEN_PATHS.some(p => url.includes(p))
  const token = localStorage.getItem('snake_admin_token')
  if (needToken && token) config.headers.Authorization = 'Bearer ' + token
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) return res.data
    if (res.code === 401) {
      localStorage.removeItem('snake_admin_token')
      localStorage.removeItem('snake_admin_user')
      if (window.location.hash !== '#/login') {
        window.location.hash = '#/login'
      }
      return Promise.reject(new Error('登录已过期'))
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message))
  },
  error => {
    const msg = error.response?.data?.message || error.message
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
