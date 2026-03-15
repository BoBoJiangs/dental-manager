import {
  Calendar,
  ChatDotRound,
  CreditCard,
  DataAnalysis,
  Document,
  Files,
  FirstAidKit,
  HomeFilled,
  PictureFilled,
  Setting,
  User,
} from '@element-plus/icons-vue'

export interface MenuItem {
  path: string
  title: string
  icon: unknown
  permission: string
}

export const menuItems: MenuItem[] = [
  { path: '/dashboard', title: '工作台', icon: HomeFilled, permission: 'WORKBENCH_VIEW' },
  { path: '/patients', title: '患者中心', icon: User, permission: 'PATIENT_VIEW' },
  { path: '/appointments', title: '预约中心', icon: Calendar, permission: 'APPOINTMENT_VIEW' },
  { path: '/emr', title: '门诊接诊', icon: FirstAidKit, permission: 'EMR_VIEW' },
  { path: '/imaging', title: '影像资料', icon: PictureFilled, permission: 'IMAGING_VIEW' },
  { path: '/billing', title: '收费财务', icon: CreditCard, permission: 'BILLING_VIEW' },
  { path: '/members', title: '会员中心', icon: Files, permission: 'MEMBER_VIEW' },
  { path: '/sms', title: '短信中心', icon: ChatDotRound, permission: 'SMS_VIEW' },
  { path: '/reports', title: '统计分析', icon: DataAnalysis, permission: 'REPORT_VIEW' },
  { path: '/settings', title: '基础设置', icon: Setting, permission: 'ORG_VIEW' },
]
