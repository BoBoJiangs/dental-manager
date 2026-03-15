# Workbench Override

> Overrides `design-system/dental-manager-doctor-app/MASTER.md` for doctor mobile workbench.

## Purpose

- 面向医生和助理
- 快速查看今日预约、候诊队列、接诊进度、患者入口

## Layout Rules

- 顶部为医生信息和日期
- 第一屏展示今日统计与进行中任务
- 第二屏展示候诊队列和今日预约
- 底部 tab 固定：工作台 / 患者 / 记录 / 我的

## Visual Priorities

- 待接诊、进行中、已完成状态必须有明确区分
- 列表项优先显示患者、时间、主诉、状态
- 操作按钮至少 `44x44`

## Components

- `DoctorHeader`
- `DailyStatCard`
- `QueueItem`
- `AppointmentItem`
- `BottomTabBar`

## Avoid

- 不使用 App Store 落地页结构
- 不使用强营销视觉
- 不把关键操作藏进二级菜单
