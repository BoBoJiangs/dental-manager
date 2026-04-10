# admin-web

PC 管理后台工程，技术栈：

- `Vue 3`
- `Vite`
- `Element Plus`
- `Pinia`
- `Vue Router`
- `Axios`

## 启动

```bash
cd frontend/admin-web
npm install
npm run dev
```

默认开发端口：

- `http://127.0.0.1:5173`

## 冒烟测试

```bash
cd frontend/admin-web
npm run test:e2e
```

说明：

- 该命令要求前端服务已启动在 `5173`，并且后端接口可用（默认走 `/api` 代理）
- 如果要一键拉起依赖 + 后端 + 前端 + E2E，可在仓库根目录执行：
  - `powershell -ExecutionPolicy Bypass -File .\scripts\smoke-local.ps1`

## 环境变量

参考：

- `frontend/admin-web/.env.example`

当前主要变量：

- `VITE_API_BASE_URL`
- `VITE_APP_TITLE`

## 联调契约

- 前后端联调基线文档：`docs/api-contract-baseline.md`

## 当前页面

- 登录
- 工作台
- 患者中心
- 患者详情独立路由
- 预约中心
- 预约日历视图
- 门诊接诊
- 影像资料
- 收费财务
- 会员中心
- 短信中心
- 统计分析
- 基础设置

## 已补能力

- 菜单权限显隐
- 按钮权限显隐
- 业务选择器（患者、病历、附件、治疗计划等）
- 患者详情独立详情页
- 预约列表 / 日历双视图
- 基础设置可维护：组织、门诊、科室、诊室、员工
- 收费财务成品化：筛选、分页、详情、支付、退款、交班
- 会员运营化：分页、详情、充值、积分调整、状态维护
- 短信任务运营化：筛选、分页、详情、定时发送
- 全局 `401/403` 前端兜底跳转
