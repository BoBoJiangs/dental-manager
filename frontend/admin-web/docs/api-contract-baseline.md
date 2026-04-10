# 前端联调契约基线（FE-01）

本文档用于约束 `frontend/admin-web` 与后端接口联调时的最小契约，减少“页面能跑但字段错位”的隐性问题。

## 1. 通用响应契约

- 所有接口统一返回结构：
  - `code: number`
  - `message: string`
  - `data: T`
  - `timestamp: string`
- 约定 `code === 0` 为成功；非 `0` 为业务失败。
- 分页接口统一 `data` 结构：
  - `records: T[]`
  - `total: number`
  - `current: number`
  - `size: number`

## 2. 前端执行约束

- API 请求统一走 `src/api/service.ts`，禁止页面内手写 `axios`。
- `service.ts` 统一做业务码检查（`code !== 0` 直接抛错）。
- `401/403` 由 `src/api/http.ts` 统一处理跳转，页面不重复处理。
- 权限响应统一结构：
  - `menuPermissions: string[]`
  - `buttonPermissions: string[]`

## 3. 核心联调链路（一期）

### 3.1 患者链路

- `GET /api/patients`
- `POST /api/patients`
- `PUT /api/patients/{id}`
- `PUT /api/patients/{id}/status`
- `PUT /api/patients/{id}/tags`
- `PUT /api/patients/{id}/primary-doctors`

### 3.2 预约链路

- `GET /api/appointments`
- `POST /api/appointments`
- `PUT /api/appointments/{id}/reschedule`
- `PUT /api/appointments/{id}/cancel`
- `PUT /api/appointments/{id}/check-in`

### 3.3 收费链路

- `GET /api/billing/charge-orders`
- `POST /api/billing/charge-orders`
- `PUT /api/billing/charge-orders/{id}/payments`
- `PUT /api/billing/charge-orders/{id}/refunds`

## 4. FE-01 验收标准

- 页面中不再出现 `Record<string, any>` 形式的权限对象读写。
- API 层能统一拦截业务失败（非 `0`）并抛出标准错误对象。
- 至少完成一轮前端构建验证：`npm run build`。
