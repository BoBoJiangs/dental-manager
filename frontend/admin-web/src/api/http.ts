import axios from 'axios'
import { ElMessage } from 'element-plus'
import { buildTraceId } from '@/utils/helpers'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/',
  timeout: 20000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('dental_admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  config.headers['X-Trace-Id'] = buildTraceId()
  config.headers['X-Request-Id'] = buildTraceId()
  return config
})

http.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      localStorage.removeItem('dental_admin_token')
      localStorage.removeItem('dental_admin_user')
      if (!window.location.pathname.startsWith('/login')) {
        const redirect = encodeURIComponent(`${window.location.pathname}${window.location.search}`)
        window.location.href = `/login?redirect=${redirect}`
      }
    }
    if (status === 403 && !window.location.pathname.startsWith('/forbidden')) {
      window.location.href = '/forbidden'
    }
    ElMessage.error(error.response?.data?.message || error.message || '网络异常')
    return Promise.reject(error)
  },
)

export default http
