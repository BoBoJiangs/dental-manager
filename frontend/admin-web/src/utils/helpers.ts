import dayjs from 'dayjs'

export function formatDateTime(value?: string | null, pattern = 'YYYY-MM-DD HH:mm') {
  if (!value) {
    return '-'
  }
  return dayjs(value).format(pattern)
}

export function formatDate(value?: string | null, pattern = 'YYYY-MM-DD') {
  if (!value) {
    return '-'
  }
  return dayjs(value).format(pattern)
}

export function buildTraceId() {
  return Math.random().toString(16).slice(2) + Date.now().toString(16)
}
