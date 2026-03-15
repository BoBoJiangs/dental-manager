# Home Override

> Overrides `design-system/dental-manager-mini-program/MASTER.md` for patient mini program home.

## Purpose

- 面向患者和家属
- 入口以预约、门店选择、会员和就诊记录为主

## Layout Rules

- 首页采用移动端单列卡片布局
- 顶部放置门店选择与快捷预约按钮
- 中部放下一次预约、会员卡片、最近消息
- 底部保留 tab 导航

## Visual Priorities

- 预约按钮和下一次到诊卡片优先级最高
- 会员权益采用柔和渐变，不使用强刺激色
- 表单和按钮满足单手操作区

## Components

- `ClinicSelector`
- `PrimaryAppointmentCard`
- `MemberCard`
- `QuickEntryGrid`
- `MessageCard`

## Avoid

- 不使用 waitlist/countdown 结构
- 不使用复杂轮播
- 不用过多阴影层级
