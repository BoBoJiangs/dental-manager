# 多端 UI 参考

本目录用于承接牙科系统多端前端的首版 UI 参考，不直接绑定某个前端框架，实现目标是：

- 统一三端设计语言
- 让产品、开发、测试先对页面结构达成一致
- 为后续正式前端工程提供视觉和交互基线

## 端侧划分

- `pc-web/`：PC 管理后台
- `mini-program/`：患者小程序
- `doctor-app/`：医生/助理移动 App
- `shared/`：共享设计令牌与基础样式

## 建议正式技术栈

- `PC Web`：React + Vite + Tailwind CSS + React Router
- `Mini Program`：Taro + React
- `Doctor App`：React Native

## 当前可直接打开

- `frontend/ui-reference/index.html`
- `frontend/ui-reference/pc-web/dashboard.html`
- `frontend/ui-reference/mini-program/home.html`
- `frontend/ui-reference/doctor-app/workbench.html`

## 对应设计系统

- `design-system/dental-manager-pc-web/MASTER.md`
- `design-system/dental-manager-pc-web/pages/dashboard.md`
- `design-system/dental-manager-mini-program/MASTER.md`
- `design-system/dental-manager-mini-program/pages/home.md`
- `design-system/dental-manager-doctor-app/MASTER.md`
- `design-system/dental-manager-doctor-app/pages/workbench.md`
