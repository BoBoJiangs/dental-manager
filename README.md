# 牙科收银系统后端

Spring Boot 3 + Java 21 的牙科管理系统后端骨架，当前已覆盖认证、患者、预约、病历、牙位图、影像、收费、会员、短信、报表、组织基础设置等一期核心模块的最小可用接口。

## 技术栈

- Java 21
- Spring Boot 3
- Spring Security + JWT
- MyBatis-Plus
- MySQL 8
- Redis
- RabbitMQ
- MinIO
- Springdoc OpenAPI

## 本地启动

### 1. 准备依赖

- JDK 21
- Maven 3.9+
- MySQL 8.x
- Redis
- RabbitMQ
- MinIO

### 2. 初始化数据库

按顺序执行：

1. `sql/01_schema.sql`
2. `sql/02_init_data.sql`

数据库名使用 `dental_mgmt`。

如果你想改用 Flyway 初始化，可直接启动空库后执行：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,flyway
```

对应迁移脚本位置：

- `src/main/resources/db/migration/V1__init_schema.sql`
- `src/main/resources/db/migration/V2__init_seed_data.sql`

### 3. 检查配置

主要配置文件：

- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-test.yml`
- `src/main/resources/application-prod.yml`

至少确认以下配置正确：

- MySQL 地址、账号、密码
- Redis 地址
- RabbitMQ 地址
- MinIO 地址
- JWT Secret

当前已拆分：

- `application.yml`：公共配置
- `application-dev.yml`：本地开发配置
- `application-test.yml`：测试环境配置
- `application-prod.yml`：生产环境配置

当前响应会自动附带：

- `X-Trace-Id`
- `X-Request-Id`

日志也会打印这两个字段，方便排查请求链路和异常。

如需启用 RabbitMQ 异步短信派发，可增加：

```yaml
dental:
  messaging:
    sms-enabled: true
```

### 3.1 使用 Docker Compose 启动依赖

```bash
docker compose up -d
```

默认会启动：

- MySQL 8
- Redis 7
- RabbitMQ 3（含管理后台）
- MinIO（含 bucket 初始化）

### 4. 编译与运行

```bash
mvn -q -DskipTests compile
mvn spring-boot:run
```

如果本机仍是 JDK 17，可临时验证编译：

```bash
mvn -o -q -DskipTests -Djava.version=17 compile
```

如果端口冲突，可临时指定：

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=18084"
```

### 5. 一键本地冒烟（Windows）

在仓库根目录执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\smoke-local.ps1
```

默认流程：

1. `docker compose up -d` 启动依赖
2. 后端编译（跳过测试）
3. 启动后端（`8080`）
4. 启动前端（`5173`）
5. 运行 Playwright 冒烟测试

可选参数：

- `-SkipDocker`：跳过 Docker 依赖启动
- `-SkipBackendCompile`：跳过后端编译
- `-SkipFrontendInstall`：跳过前端 `npm ci` 检查

## 初始化账号

- 用户名：`admin`
- 密码：`Admin@123456`

以上账号来自 `sql/02_init_data.sql`，当前使用 `{noop}` 密码格式，仅用于本地联调。

## 已落地模块

- 认证：登录、JWT、受保护接口
- 患者：档案、标签、主治医生、历史就诊、影像、会员聚合
- 预约：分页、详情、创建、改约、取消、签到
- 病历：分页、详情、新建、诊断记录
- 牙位图：按病历查询与保存
- 影像：患者影像列表、影像记录创建
- 收费：收费单分页、详情、开单
- 会员：会员分页、详情、开卡、等级选项
- 短信：模板列表、短信任务分页/详情/创建
- 报表：门诊经营概览
- 组织设置：组织信息、门诊、科室、诊室

## 前端 UI 参考

- 多端 UI 参考总览：`frontend/ui-reference/index.html`
- 页面清单：`frontend/ui-reference/routes.md`
- 原型目录：`frontend/ui-reference/`
- 设计系统目录：`design-system/`

## PC Web 工程

- 工程目录：`frontend/admin-web`
- 技术栈：`Vue 3 + Vite + Element Plus + Pinia + Vue Router`
- 启动说明：`frontend/admin-web/README.md`

常用命令：

```bash
cd frontend/admin-web
npm install
npm run dev
npm run build
```

## 关键接口

- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/auth/permissions`
- `GET /api/system/me`
- `GET /api/patients`
- `GET /api/appointments`
- `GET /api/appointments/schedules`
- `GET /api/appointments/queues`
- `GET /api/emr/records`
- `GET /api/emr/treatment-plans`
- `GET /api/emr/print-templates`
- `GET /api/emr/consent-forms`
- `GET /api/emr/signatures`
- `GET /api/dentalcharts/medical-records/{medicalRecordId}`
- `GET /api/files/attachments`
- `POST /api/files/attachments/upload`
- `GET /api/billing/charge-orders`
- `GET /api/billing/cashier-shifts`
- `GET /api/imaging/patient-images`
- `GET /api/imaging/photo-compare-groups`
- `GET /api/members`
- `GET /api/sms/tasks`
- `GET /api/reports/clinic-overview`
- `GET /api/reports/doctor-performance`
- `GET /api/org/profile`
- `GET /api/org/roles`

## 接口文档

启动后访问：

- `http://127.0.0.1:8080/swagger-ui.html`

## CI 工作流

- `ci`：后端编译+测试，前端构建
- `e2e-smoke`：启动依赖与服务后执行 Playwright 冒烟（支持手动触发与定时触发）

后端覆盖率报告（JaCoCo）：

- CI 中后端测试使用 `-Pcoverage` profile，会产出覆盖率报告并上传构建产物
- 本地如需生成覆盖率报告，可执行：`mvn -Pcoverage test`

## 进度文档

当前开发进度与续开发建议见：

- `docs/progress/backend-progress-checklist.md`

接口联调示例见：

- `docs/api-debug-examples.md`
