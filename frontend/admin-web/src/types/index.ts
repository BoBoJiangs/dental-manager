export interface LoginResponse {
  accessToken: string
  tokenType: string
  userId: number
  orgId: number
  staffId?: number
  username: string
  accountType: string
  clinicId?: number
  clinicIds?: number[]
  roles?: string[]
  dataScopes?: string[]
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: string
}

export interface PermissionSnapshot {
  menuPermissions: string[]
  buttonPermissions: string[]
}

export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

export interface OptionItem {
  label: string
  value: number | string
}
