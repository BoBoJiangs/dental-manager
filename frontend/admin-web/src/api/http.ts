import axios, { AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import { buildTraceId } from '@/utils/helpers'
import type { ApiResponse } from '@/types'

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
  config.headers['X-Timezone'] = Intl.DateTimeFormat().resolvedOptions().timeZone
  return config
})

function resolveErrorMessage(error: AxiosError<ApiResponse<unknown>>) {
  return error.response?.data?.message || error.message || '网络异常'
}

http.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (axios.isCancel(error)) {
      return Promise.reject(error)
    }
    const status = error.response?.status
    const message = resolveErrorMessage(error)
    let shouldShowMessage = true

    if (status === 401) {
      localStorage.removeItem('dental_admin_token')
      localStorage.removeItem('dental_admin_user')
      if (!window.location.pathname.startsWith('/login')) {
        const redirect = encodeURIComponent(`${window.location.pathname}${window.location.search}`)
        window.location.href = `/login?redirect=${redirect}`
      } else {
        shouldShowMessage = false
      }
    }
    if (status === 403 && !window.location.pathname.startsWith('/forbidden')) {
      window.location.href = '/forbidden'
      shouldShowMessage = false
    }
    if (shouldShowMessage) {
      ElMessage.error(message)
    }
    return Promise.reject(error)
  },
)

export default http
