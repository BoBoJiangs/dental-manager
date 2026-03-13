# 后端开发完成清单与续开发指引

## 1. 文档目的

该文件用于记录当前仓库后端工程的已完成内容、未完成内容、当前可运行状态与建议续开发顺序，方便后续换电脑后快速恢复开发。

## 2. 当前工程状态

当前仓库已经从纯方案/SQL仓库，落成了一个可运行的 Spring Boot 3 + Maven 后端基础工程。

当前已经具备：
- 可编译
- 可启动
- 可连接 MySQL
- 可查看 Swagger/OpenAPI
- 可进行用户名密码登录
- 可返回 JWT
- 可访问受保护接口
- 已落地患者模块最小闭环的一部分

当前主要联调端口（最近一次启动验证）：
- `18084`

Swagger 地址：
- `http://127.0.0.1:18084/swagger-ui.html`

## 3. 已完成清单

### 3.1 基础工程骨架
- [x] 创建 `pom.xml`
- [x] 创建 Spring Boot 启动类 `DentalApplication`
- [x] 建立标准包结构：`common` / `config` / `framework` / `integration` / `modules`
- [x] 建立后续模块预留目录：
  - [x] `modules/org`
  - [x] `modules/patient`
  - [x] `modules/appointment`
  - [x] `modules/emr`
  - [x] `modules/dentalchart`
  - [x] `modules/imaging`
  - [x] `modules/billing`
  - [x] `modules/member`
  - [x] `modules/sms`
  - [x] `modules/system`
  - [x] `modules/report`
  - [x] `integration/insurance`
  - [x] `integration/invoice`
  - [x] `integration/imagingdevice`

### 3.2 Maven / 依赖
- [x] Spring Boot 3
- [x] Spring Web
- [x] Spring Validation
- [x] Spring Security
- [x] MyBatis-Plus
- [x] MySQL Driver
- [x] Redis
- [x] RabbitMQ
- [x] Flyway（依赖已接入，当前未启用迁移）
- [x] JWT
- [x] MinIO
- [x] Springdoc OpenAPI / Swagger UI
- [x] MapStruct
- [x] Lombok

### 3.3 通用基础层
- [x] 统一返回：`ApiResponse`
- [x] 分页返回：`PageResult`
- [x] 基础实体：`BaseEntity`
- [x] 统一业务异常：`BusinessException`
- [x] 错误码枚举：`ErrorCode`

### 3.4 配置层与基础设施
- [x] MyBatis-Plus 配置
- [x] OpenAPI 配置
- [x] MinIO 配置
- [x] 审计字段自动填充 `AuditMetaObjectHandler`
- [x] MinIO 属性类 `MinioProperties`
- [x] `application.yml`
- [x] `application-dev.yml`
- [x] Mapper 资源目录 `src/main/resources/mapper`

### 3.5 安全与认证骨架
- [x] JWT 配置类 `JwtProperties`
- [x] 登录态对象 `LoginUser`
- [x] 认证上下文 `AuthContext`
- [x] JWT 生成/解析 `JwtTokenProvider`
- [x] JWT 过滤器 `JwtAuthenticationFilter`
- [x] 安全配置 `SecurityConfig`
- [x] 全局异常处理 `GlobalExceptionHandler`
- [x] 放行 `/api/auth/**`
- [x] 放行 Swagger/OpenAPI 相关路径
- [x] 其他接口默认需要认证

### 3.6 认证模块 auth
- [x] 登录请求 DTO `LoginRequest`
- [x] 登录响应 VO `LoginResponse`
- [x] 用户实体 `AuthUserEntity`
- [x] 用户 Mapper `AuthUserMapper`
- [x] 认证服务接口 `AuthService`
- [x] 认证服务实现 `AuthServiceImpl`
- [x] 认证控制器 `AuthController`
- [x] 用户名密码登录
- [x] 从 `user_account` 表读取账号
- [x] 从 `sys_user_role + sys_role` 读取角色编码
- [x] 返回 JWT
- [x] 登录后更新最近登录时间与 IP
- [x] 认证健康检查接口 `/api/auth/health`

### 3.7 通用能力抽象
- [x] 业务编号接口 `BizNoGenerator`
- [x] Redis 编号实现 `RedisBizNoGenerator`
- [x] Redis 不可用时本地回退生成
- [x] 文件存储接口 `FileStorageService`
- [x] MinIO 文件存储骨架 `MinioFileStorageService`
- [x] 短信接口 `SmsService`
- [x] 短信请求对象 `SmsSendRequest`
- [x] 短信结果对象 `SmsSendResult`
- [x] 阿里云短信骨架 `AliyunSmsService`

### 3.8 系统测试接口
- [x] 受保护接口 `GET /api/system/me`
- [x] 已验证 JWT 生效后可访问

### 3.9 患者模块 patient
#### 已完成接口
- [x] `GET /api/patients` 分页查询患者
- [x] `GET /api/patients/{id}` 查询患者详情
- [x] `POST /api/patients` 新建患者

#### 已完成能力
- [x] 患者主档实体 `PatientEntity`
- [x] 患者扩展档案实体 `PatientProfileEntity`
- [x] 患者 Mapper `PatientMapper`
- [x] 患者扩展档案 Mapper `PatientProfileMapper`
- [x] 分页查询请求 `PatientPageQuery`
- [x] 建档请求 `PatientCreateRequest`
- [x] 建档扩展请求 `PatientProfileCreateRequest`
- [x] 患者分页 VO `PatientPageItemVO`
- [x] 患者详情 VO `PatientDetailVO`
- [x] 患者扩展档案 VO `PatientProfileVO`
- [x] 患者服务接口 `PatientService`
- [x] 患者服务实现 `PatientServiceImpl`
- [x] 患者控制器 `PatientController`
- [x] 患者查询按当前登录用户 `orgId` 做数据范围限制
- [x] 建档时自动生成 `patientCode`
- [x] 建档时可同时写入 `patient_profile`
- [x] 建档时自动写入初诊/最近就诊时间
- [x] 建档时做手机号查重
- [x] 建档时做证件号查重

### 3.10 已完成验证
- [x] `mvn -q -DskipTests compile` 编译通过
- [x] Spring Boot 可启动
- [x] Swagger 页面可打开
- [x] `/api/auth/login` 可返回 JWT
- [x] 未带 JWT 访问受保护接口返回 401
- [x] 带 JWT 访问 `/api/system/me` 返回当前用户
- [x] 患者分页接口可返回初始化数据
- [x] 患者详情接口可返回 `patient + patient_profile`
- [x] 患者建档成功写入数据库

## 4. 尚未完成清单

### 4.1 患者模块后续待做
- [ ] `PUT /api/patients/{id}` 编辑患者
- [ ] 患者标签/标签关系
- [ ] 主治医生关系维护
- [ ] 患者历史就诊记录汇总接口
- [ ] 患者影像列表接口
- [ ] 患者会员信息聚合接口
- [ ] 患者删除/停用策略明确化

### 4.2 认证与权限后续待做
- [ ] 更完整的角色权限模型
- [ ] 菜单/按钮权限
- [ ] 数据权限细化（按门诊、医生、岗位）
- [ ] 登出接口
- [ ] Token 刷新机制
- [ ] 用户详情接口 `/api/auth/me`
- [ ] 自定义 `UserDetailsService`，去掉默认开发密码提示

### 4.3 基础设施后续待做
- [ ] Flyway 迁移脚本正式接入
- [ ] Redis 业务号规则完善
- [ ] MinIO 真正上传实现
- [ ] 阿里云短信真实发送实现
- [ ] RabbitMQ 消息生产/消费骨架
- [ ] 全局日志 traceId / requestId
- [ ] 统一审计日志
- [ ] 多环境配置拆分优化

### 4.4 业务模块后续待做
- [ ] `appointment` 预约模块骨架
- [ ] `emr` 病历模块骨架
- [ ] `dentalchart` 牙位图模块骨架
- [ ] `imaging` 影像资料模块骨架
- [ ] `billing` 收费模块骨架
- [ ] `member` 会员模块骨架
- [ ] `sms` 业务短信模块骨架
- [ ] `report` 统计报表模块骨架
- [ ] `org/system` 基础设置模块深化

### 4.5 工程质量与交付后续待做
- [ ] 单元测试
- [ ] 集成测试
- [ ] 本地启动说明文档
- [ ] API 调试示例整理
- [ ] Docker / docker-compose 本地依赖编排
- [ ] CI 构建脚本

## 5. 当前关键文件索引

### 基础与配置
- `pom.xml`
- `src/main/java/com/company/dental/DentalApplication.java`
- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`

### 认证相关
- `src/main/java/com/company/dental/framework/security/SecurityConfig.java`
- `src/main/java/com/company/dental/framework/security/JwtTokenProvider.java`
- `src/main/java/com/company/dental/modules/auth/controller/AuthController.java`
- `src/main/java/com/company/dental/modules/auth/service/impl/AuthServiceImpl.java`

### 患者模块相关
- `src/main/java/com/company/dental/modules/patient/controller/PatientController.java`
- `src/main/java/com/company/dental/modules/patient/service/PatientService.java`
- `src/main/java/com/company/dental/modules/patient/service/impl/PatientServiceImpl.java`
- `src/main/java/com/company/dental/modules/patient/entity/PatientEntity.java`
- `src/main/java/com/company/dental/modules/patient/entity/PatientProfileEntity.java`

## 6. 数据库准备方式

当前本地验证使用以下 SQL 初始化数据库：
- `sql/01_schema.sql`
- `sql/02_init_data.sql`

导入顺序：
1. 先导入 `01_schema.sql`
2. 再导入 `02_init_data.sql`

登录初始账号依赖 `02_init_data.sql`：
- 用户名：`admin`
- 密码：`Admin@123456`

说明：
- 初始化密码采用 `{noop}` 前缀方案，当前登录逻辑已兼容该格式。

## 7. 换电脑后建议恢复步骤

### 第一步：准备环境
- 安装 JDK 21
- 安装 Maven 3.9+
- 安装 MySQL 8.x
- 准备 Redis（可选，当前即使不可用业务号也能本地回退）

### 第二步：导入数据库
执行：
- `sql/01_schema.sql`
- `sql/02_init_data.sql`

确保数据库名为：
- `dental_mgmt`

### 第三步：检查配置
重点检查：
- `src/main/resources/application-dev.yml`

至少确认：
- MySQL 地址/账号/密码
- Redis 地址
- RabbitMQ 地址
- MinIO 地址
- JWT secret

### 第四步：编译并启动
```bash
mvn -q -DskipTests compile
mvn spring-boot:run
```

如果 8080 端口被占用，可临时指定：
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=18084"
```

### 第五步：验证接口
1. 打开 Swagger
2. 调用 `/api/auth/login`
3. 复制 JWT
4. 调用：
   - `/api/system/me`
   - `/api/patients`
   - `/api/patients/{id}`
   - `POST /api/patients`

## 8. 建议下一步开发顺序

推荐按下面顺序继续：
1. `PUT /api/patients/{id}` 患者编辑
2. 患者标签 / 患者扩展关系完善
3. 预约模块 `appointment` 骨架与首个接口
4. 患者 -> 预约 -> 到诊签到 的一期业务链路
5. 病历 / 收费 / 影像模块逐步补齐

如果希望从最小可用闭环继续推进，建议下一步先做：
- `PUT /api/patients/{id}`

## 9. 本文件位置

本文件路径：
- `docs/progress/backend-progress-checklist.md`

后续每完成一个模块，建议同步更新本文件，避免换设备后丢失上下文。
