import http from './http'
import { ElMessage } from 'element-plus'
import type { ApiResponse, LoginResponse, PageResult, PermissionSnapshot } from '@/types'

const SUCCESS_CODE = 0

export class ApiBusinessError extends Error {
  code: number

  constructor(code: number, message: string) {
    super(message || '业务请求失败')
    this.name = 'ApiBusinessError'
    this.code = code
  }
}

function assertBusinessSuccess<T>(payload: ApiResponse<T>) {
  if (payload.code !== SUCCESS_CODE) {
    ElMessage.error(payload.message || '业务请求失败')
    throw new ApiBusinessError(payload.code, payload.message)
  }
  return payload
}

function get<T>(url: string, params?: Record<string, unknown>) {
  return http.get<ApiResponse<T>>(url, { params }).then((response) => assertBusinessSuccess(response.data))
}

function post<T>(url: string, data?: unknown, config?: Record<string, unknown>) {
  return http.post<ApiResponse<T>>(url, data, config).then((response) => assertBusinessSuccess(response.data))
}

function put<T>(url: string, data?: unknown) {
  return http.put<ApiResponse<T>>(url, data).then((response) => assertBusinessSuccess(response.data))
}

function del<T>(url: string) {
  return http.delete<ApiResponse<T>>(url).then((response) => assertBusinessSuccess(response.data))
}

export const api = {
  auth: {
    login: (payload: { username: string; password: string }) => post<LoginResponse>('/api/auth/login', payload),
    me: () => get<LoginResponse>('/api/auth/me'),
    permissions: () => get<PermissionSnapshot>('/api/auth/permissions'),
    logout: () => post<string>('/api/auth/logout'),
  },
  system: {
    workbench: () => get<Record<string, number>>('/api/system/workbench'),
  },
  patient: {
    page: (params: Record<string, unknown>) => get<PageResult<any>>('/api/patients', params),
    detail: (id: number) => get<any>(`/api/patients/${id}`),
    create: (payload: Record<string, unknown>) => post<any>('/api/patients', payload),
    update: (id: number, payload: Record<string, unknown>) => put<any>(`/api/patients/${id}`, payload),
    tags: (id: number, payload: Record<string, unknown>) => put<any>(`/api/patients/${id}/tags`, payload),
    primaryDoctors: (id: number, payload: Record<string, unknown>) => put<any>(`/api/patients/${id}/primary-doctors`, payload),
    visitRecords: (id: number) => get<any[]>(`/api/patients/${id}/visit-records`),
    images: (id: number) => get<any[]>(`/api/patients/${id}/images`),
    member: (id: number) => get<any>(`/api/patients/${id}/member`),
    tagOptions: () => get<any[]>('/api/patients/tag-options'),
    doctorOptions: () => get<any[]>('/api/patients/doctor-options'),
    remove: (id: number) => del<string>(`/api/patients/${id}`),
  },
  appointment: {
    page: (params: Record<string, unknown>) => get<PageResult<any>>('/api/appointments', params),
    detail: (id: number) => get<any>(`/api/appointments/${id}`),
    create: (payload: Record<string, unknown>) => post<any>('/api/appointments', payload),
    reschedule: (id: number, payload: Record<string, unknown>) => put<any>(`/api/appointments/${id}/reschedule`, payload),
    cancel: (id: number, payload: Record<string, unknown>) => put<any>(`/api/appointments/${id}/cancel`, payload),
    checkIn: (id: number, payload: Record<string, unknown>) => put<any>(`/api/appointments/${id}/check-in`, payload),
    schedules: (params: Record<string, unknown>) => get<any[]>('/api/appointments/schedules', params),
    createSchedule: (payload: Record<string, unknown>) => post<any>('/api/appointments/schedules', payload),
    queues: (params: Record<string, unknown>) => get<any[]>('/api/appointments/queues', params),
    updateQueue: (id: number, payload: Record<string, unknown>) => put<any>(`/api/appointments/queues/${id}/status`, payload),
  },
  emr: {
    records: (params: Record<string, unknown>) => get<PageResult<any>>('/api/emr/records', params),
    recordDetail: (id: number) => get<any>(`/api/emr/records/${id}`),
    createRecord: (payload: Record<string, unknown>) => post<any>('/api/emr/records', payload),
    updateRecord: (id: number, payload: Record<string, unknown>) => put<any>(`/api/emr/records/${id}`, payload),
    treatmentPlans: (params: Record<string, unknown>) => get<PageResult<any>>('/api/emr/treatment-plans', params),
    treatmentPlanDetail: (id: number) => get<any>(`/api/emr/treatment-plans/${id}`),
    createTreatmentPlan: (payload: Record<string, unknown>) => post<any>('/api/emr/treatment-plans', payload),
    updateTreatmentPlanStatus: (id: number, payload: Record<string, unknown>) => put<any>(`/api/emr/treatment-plans/${id}/status`, payload),
    templates: (params: Record<string, unknown>) => get<any[]>('/api/emr/print-templates', params),
    consentForms: (params: Record<string, unknown>) => get<any[]>('/api/emr/consent-forms', params),
    consentFormDetail: (id: number) => get<any>(`/api/emr/consent-forms/${id}`),
    createConsentForm: (payload: Record<string, unknown>) => post<any>('/api/emr/consent-forms', payload),
    updateConsentForm: (id: number, payload: Record<string, unknown>) => put<any>(`/api/emr/consent-forms/${id}`, payload),
    signatures: (params: Record<string, unknown>) => get<any[]>('/api/emr/signatures', params),
    createSignature: (payload: Record<string, unknown>) => post<any>('/api/emr/signatures', payload),
  },
  dentalChart: {
    getByRecord: (recordId: number) => get<any>(`/api/dentalcharts/medical-records/${recordId}`),
    saveByRecord: (recordId: number, payload: Record<string, unknown>) => put<any>(`/api/dentalcharts/medical-records/${recordId}`, payload),
  },
  file: {
    attachments: (params: Record<string, unknown>) => get<any[]>('/api/files/attachments', params),
    uploadAttachment: (formData: FormData) =>
      post<any>('/api/files/attachments/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      }),
  },
  imaging: {
    images: (params: Record<string, unknown>) => get<any[]>('/api/imaging/patient-images', params),
    createImage: (payload: Record<string, unknown>) => post<any>('/api/imaging/patient-images', payload),
    compareGroups: (params: Record<string, unknown>) => get<any[]>('/api/imaging/photo-compare-groups', params),
    createCompareGroup: (payload: Record<string, unknown>) => post<any>('/api/imaging/photo-compare-groups', payload),
  },
  billing: {
    chargeOrders: (params: Record<string, unknown>) => get<PageResult<any>>('/api/billing/charge-orders', params),
    chargeDetail: (id: number) => get<any>(`/api/billing/charge-orders/${id}`),
    createCharge: (payload: Record<string, unknown>) => post<any>('/api/billing/charge-orders', payload),
    pay: (id: number, payload: Record<string, unknown>) => put<any>(`/api/billing/charge-orders/${id}/payments`, payload),
    refund: (id: number, payload: Record<string, unknown>) => put<any>(`/api/billing/charge-orders/${id}/refunds`, payload),
    shifts: (params: Record<string, unknown>) => get<PageResult<any>>('/api/billing/cashier-shifts', params),
    shiftDetail: (id: number) => get<any>(`/api/billing/cashier-shifts/${id}`),
    openShift: (payload: Record<string, unknown>) => post<any>('/api/billing/cashier-shifts', payload),
    closeShift: (id: number, payload: Record<string, unknown>) => put<any>(`/api/billing/cashier-shifts/${id}/close`, payload),
  },
  member: {
    page: (params: Record<string, unknown>) => get<PageResult<any>>('/api/members', params),
    detail: (id: number) => get<any>(`/api/members/${id}`),
    create: (payload: Record<string, unknown>) => post<any>('/api/members', payload),
    recharge: (id: number, payload: Record<string, unknown>) => put<any>(`/api/members/${id}/recharge`, payload),
    points: (id: number, payload: Record<string, unknown>) => put<any>(`/api/members/${id}/points`, payload),
    status: (id: number, payload: Record<string, unknown>) => put<any>(`/api/members/${id}/status`, payload),
    levels: () => get<any[]>('/api/members/level-options'),
  },
  sms: {
    templates: () => get<any[]>('/api/sms/templates'),
    tasks: (params: Record<string, unknown>) => get<PageResult<any>>('/api/sms/tasks', params),
    taskDetail: (id: number) => get<any>(`/api/sms/tasks/${id}`),
    createTask: (payload: Record<string, unknown>) => post<any>('/api/sms/tasks', payload),
  },
  report: {
    clinicOverview: (params: Record<string, unknown>) => get<any>('/api/reports/clinic-overview', params),
    doctorPerformance: (params: Record<string, unknown>) => get<any[]>('/api/reports/doctor-performance', params),
  },
  org: {
    profile: () => get<any>('/api/org/profile'),
    updateProfile: (payload: Record<string, unknown>) => put<any>('/api/org/profile', payload),
    clinics: () => get<any[]>('/api/org/clinics'),
    createClinic: (payload: Record<string, unknown>) => post<any>('/api/org/clinics', payload),
    updateClinic: (id: number, payload: Record<string, unknown>) => put<any>(`/api/org/clinics/${id}`, payload),
    departments: (params?: Record<string, unknown>) => get<any[]>('/api/org/departments', params),
    createDepartment: (payload: Record<string, unknown>) => post<any>('/api/org/departments', payload),
    updateDepartment: (id: number, payload: Record<string, unknown>) => put<any>(`/api/org/departments/${id}`, payload),
    rooms: (params?: Record<string, unknown>) => get<any[]>('/api/org/rooms', params),
    createRoom: (payload: Record<string, unknown>) => post<any>('/api/org/rooms', payload),
    updateRoom: (id: number, payload: Record<string, unknown>) => put<any>(`/api/org/rooms/${id}`, payload),
    staff: (params?: Record<string, unknown>) => get<any[]>('/api/org/staff', params),
    createStaff: (payload: Record<string, unknown>) => post<any>('/api/org/staff', payload),
    updateStaff: (id: number, payload: Record<string, unknown>) => put<any>(`/api/org/staff/${id}`, payload),
    roles: () => get<any[]>('/api/org/roles'),
    createRole: (payload: Record<string, unknown>) => post<any>('/api/org/roles', payload),
    updateRole: (id: number, payload: Record<string, unknown>) => put<any>(`/api/org/roles/${id}`, payload),
  },
}
