import { createRouter, createWebHistory } from 'vue-router'
import { menuItems } from '@/config/menu'
import { usePermission } from '@/composables/usePermission'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/LoginView.vue'),
      meta: { title: '登录' },
    },
    {
      path: '/',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', name: 'dashboard', component: () => import('@/views/dashboard/DashboardView.vue'), meta: { title: '工作台', permission: 'WORKBENCH_VIEW' } },
        { path: 'patients', name: 'patients', component: () => import('@/views/patients/PatientsView.vue'), meta: { title: '患者中心', permission: 'PATIENT_VIEW' } },
        { path: 'patients/:id', name: 'patient-detail', component: () => import('@/views/patients/PatientDetailView.vue'), meta: { title: '患者详情', permission: 'PATIENT_VIEW' } },
        { path: 'appointments', name: 'appointments', component: () => import('@/views/appointments/AppointmentsView.vue'), meta: { title: '预约中心', permission: 'APPOINTMENT_VIEW' } },
        { path: 'emr', name: 'emr', component: () => import('@/views/emr/EmrView.vue'), meta: { title: '门诊接诊', permission: 'EMR_VIEW' } },
        { path: 'imaging', name: 'imaging', component: () => import('@/views/imaging/ImagingView.vue'), meta: { title: '影像资料', permission: 'IMAGING_VIEW' } },
        { path: 'billing', name: 'billing', component: () => import('@/views/billing/BillingView.vue'), meta: { title: '收费财务', permission: 'BILLING_VIEW' } },
        { path: 'members', name: 'members', component: () => import('@/views/members/MembersView.vue'), meta: { title: '会员中心', permission: 'MEMBER_VIEW' } },
        { path: 'sms', name: 'sms', component: () => import('@/views/sms/SmsView.vue'), meta: { title: '短信中心', permission: 'SMS_VIEW' } },
        { path: 'reports', name: 'reports', component: () => import('@/views/reports/ReportsView.vue'), meta: { title: '统计分析', permission: 'REPORT_VIEW' } },
        { path: 'settings', name: 'settings', component: () => import('@/views/settings/SettingsView.vue'), meta: { title: '基础设置', permission: 'ORG_VIEW' } },
      ],
    },
    {
      path: '/forbidden',
      name: 'forbidden',
      component: () => import('@/views/system/ForbiddenView.vue'),
      meta: { title: '无权限' },
    },
  ],
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  const { hasMenuPermission } = usePermission()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && authStore.isAuthenticated) {
    return { path: '/dashboard' }
  }
  if (authStore.isAuthenticated && !authStore.permissions) {
    try {
      await authStore.loadPermissions()
    } catch {
      authStore.clearAuth()
      return { path: '/login' }
    }
  }
  if (to.meta.permission && !hasMenuPermission(String(to.meta.permission))) {
    const firstAllowed = menuItems.find((item) => hasMenuPermission(item.permission))
    return firstAllowed ? { path: firstAllowed.path } : { path: '/forbidden' }
  }
  document.title = `${(to.meta.title as string) || '管理后台'} - ${import.meta.env.VITE_APP_TITLE || '牙科系统管理后台'}`
  return true
})

export default router
