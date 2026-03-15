# Dashboard Override

> Overrides `design-system/dental-manager-pc-web/MASTER.md` for the PC workbench dashboard.

## Purpose

- 面向门诊管理员、前台、财务的日常运营首页
- 重点突出今日数据、待办事项、预约与候诊流转

## Layout Rules

- 使用 `sidebar + topbar + content` 三栏布局
- 左侧导航固定宽度 `240px`
- 内容区分为 `概览卡片 / 今日预约 / 候诊 / 收费提醒`
- 关键数据卡片采用 4 列网格，最小宽度 `240px`

## Visual Priorities

- 今日到诊、待接诊、应收金额使用高对比卡片
- 风险类信息只使用琥珀/红色点缀，不整卡染色
- 表格行高保持 `52px+`

## Components

- `SummaryCard`
- `AppointmentTable`
- `QueueList`
- `TaskPanel`

## Avoid

- 不使用营销型 hero 结构
- 不使用 landing page CTA 风格
- 不使用过多插画
