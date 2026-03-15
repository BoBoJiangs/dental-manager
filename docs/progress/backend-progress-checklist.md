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
- [x] 当前登录用户接口 `/api/auth/me`
- [x] 刷新 Token 接口 `/api/auth/refresh`
- [x] 退出登录接口 `/api/auth/logout`
- [x] 当前权限元数据接口 `/api/auth/permissions`
- [x] 登录态携带门诊范围 `clinicIds`
- [x] 登录态携带角色数据范围 `dataScopes`
- [x] 预约/病历/收费/患者查询已接入基础数据范围控制（ALL/CLINIC/SELF_PATIENT）
- [x] 菜单/按钮权限矩阵已接入认证上下文
- [x] 写接口已接入方法级权限控制

### 3.7 通用能力抽象
- [x] 业务编号接口 `BizNoGenerator`
- [x] Redis 编号实现 `RedisBizNoGenerator`
- [x] Redis 不可用时本地回退生成
- [x] 文件存储接口 `FileStorageService`
- [x] MinIO 文件存储实现 `MinioFileStorageService`
- [x] 附件查询接口 `GET /api/files/attachments`
- [x] 附件上传接口 `POST /api/files/attachments/upload`
- [x] 文件附件实体 `FileAttachmentEntity`
- [x] 文件附件查询请求 `FileAttachmentQuery`
- [x] 文件附件上传请求 `FileAttachmentUploadRequest`
- [x] 文件附件 VO `FileAttachmentVO`
- [x] 文件附件 Mapper `FileAttachmentMapper`
- [x] 文件附件查询 XML `FileAttachmentMapper.xml`
- [x] 文件附件服务接口 `FileAttachmentService`
- [x] 文件附件服务实现 `FileAttachmentServiceImpl`
- [x] 文件附件控制器 `FileAttachmentController`
- [x] 附件列表支持按业务、患者、病历过滤
- [x] 附件上传时自动写入 `file_attachment`
- [x] 附件上传时走现有 MinIO 存储骨架
- [x] 短信接口 `SmsService`
- [x] 短信请求对象 `SmsSendRequest`
- [x] 短信结果对象 `SmsSendResult`
- [x] 阿里云短信实现 `AliyunSmsService`

### 3.8 系统测试接口
- [x] 受保护接口 `GET /api/system/me`
- [x] 工作台概览接口 `GET /api/system/workbench`
- [x] 已验证 JWT 生效后可访问

### 3.9 患者模块 patient
#### 已完成接口
- [x] `GET /api/patients` 分页查询患者
- [x] `GET /api/patients/{id}` 查询患者详情
- [x] `GET /api/patients/{id}/visit-records` 查询患者历史就诊记录
- [x] `GET /api/patients/{id}/images` 查询患者影像列表
- [x] `GET /api/patients/{id}/member` 查询患者会员信息
- [x] `POST /api/patients` 新建患者
- [x] `PUT /api/patients/{id}` 编辑患者
- [x] `PUT /api/patients/{id}/status` 更新患者状态
- [x] `DELETE /api/patients/{id}` 删除患者
- [x] `GET /api/patients/tag-options` 查询标签选项
- [x] `PUT /api/patients/{id}/tags` 更新患者标签
- [x] `GET /api/patients/doctor-options` 查询医生选项
- [x] `PUT /api/patients/{id}/primary-doctors` 更新主治医生

#### 已完成能力
- [x] 患者主档实体 `PatientEntity`
- [x] 患者主治医生关系实体 `PatientDoctorRelEntity`
- [x] 患者扩展档案实体 `PatientProfileEntity`
- [x] 患者标签实体 `PatientTagEntity`
- [x] 患者标签关系实体 `PatientTagRelEntity`
- [x] 患者 Mapper `PatientMapper`
- [x] 患者主治医生关系 Mapper `PatientDoctorRelMapper`
- [x] 患者扩展档案 Mapper `PatientProfileMapper`
- [x] 患者标签 Mapper `PatientTagMapper`
- [x] 患者标签关系 Mapper `PatientTagRelMapper`
- [x] 分页查询请求 `PatientPageQuery`
- [x] 建档请求 `PatientCreateRequest`
- [x] 主治医生更新请求 `PatientPrimaryDoctorUpdateRequest`
- [x] 建档扩展请求 `PatientProfileCreateRequest`
- [x] 患者分页 VO `PatientPageItemVO`
- [x] 患者详情 VO `PatientDetailVO`
- [x] 患者主治医生 VO `PatientDoctorVO`
- [x] 患者历史就诊记录 VO `PatientVisitRecordVO`
- [x] 患者会员信息 VO `PatientMemberInfoVO`
- [x] 患者会员余额流水 VO `PatientMemberBalanceRecordVO`
- [x] 患者会员积分流水 VO `PatientMemberPointsRecordVO`
- [x] 患者扩展档案 VO `PatientProfileVO`
- [x] 患者标签 VO `PatientTagVO`
- [x] 患者服务接口 `PatientService`
- [x] 患者服务实现 `PatientServiceImpl`
- [x] 患者控制器 `PatientController`
- [x] 患者编辑请求 `PatientUpdateRequest`
- [x] 患者状态更新请求 `PatientStatusUpdateRequest`
- [x] 患者标签更新请求 `PatientTagUpdateRequest`
- [x] 患者查询按当前登录用户 `orgId` 做数据范围限制
- [x] 建档时自动生成 `patientCode`
- [x] 建档时可同时写入 `patient_profile`
- [x] 编辑时可同时更新/补建 `patient_profile`
- [x] 患者详情聚合返回标签信息
- [x] 患者详情聚合返回主治医生信息
- [x] 患者历史就诊记录支持按患者聚合返回
- [x] 患者影像列表支持按患者聚合返回
- [x] 患者会员信息支持聚合返回账户、等级、余额/积分流水
- [x] 建档/编辑时可同时维护患者标签
- [x] 支持单独替换患者标签关系
- [x] 支持单独替换主治医生关系
- [x] 支持患者停用/启用状态切换
- [x] 支持患者逻辑删除
- [x] 建档时自动写入初诊/最近就诊时间
- [x] 建档时做手机号查重
- [x] 建档时做证件号查重

### 3.10 预约模块 appointment
#### 已完成接口
- [x] `GET /api/appointments` 分页查询预约
- [x] `GET /api/appointments/{id}` 查询预约详情
- [x] `POST /api/appointments` 创建预约
- [x] `PUT /api/appointments/{id}/reschedule` 改约
- [x] `PUT /api/appointments/{id}/cancel` 取消预约
- [x] `PUT /api/appointments/{id}/check-in` 到诊签到
- [x] `GET /api/appointments/schedules` 查询医生排班
- [x] `POST /api/appointments/schedules` 创建医生排班
- [x] `GET /api/appointments/queues` 查询候诊列表
- [x] `PUT /api/appointments/queues/{id}/status` 更新候诊状态

#### 已完成能力
- [x] 预约实体 `AppointmentEntity`
- [x] 医生排班实体 `DoctorScheduleEntity`
- [x] 预约日志实体 `AppointmentLogEntity`
- [x] 候诊记录实体 `QueueRecordEntity`
- [x] 预约分页查询请求 `AppointmentPageQuery`
- [x] 医生排班查询请求 `DoctorScheduleQuery`
- [x] 候诊查询请求 `QueueRecordQuery`
- [x] 预约创建请求 `AppointmentCreateRequest`
- [x] 医生排班创建请求 `DoctorScheduleCreateRequest`
- [x] 预约改约请求 `AppointmentRescheduleRequest`
- [x] 预约取消请求 `AppointmentCancelRequest`
- [x] 预约签到请求 `AppointmentCheckInRequest`
- [x] 候诊状态更新请求 `QueueStatusUpdateRequest`
- [x] 预约详情 VO `AppointmentDetailVO`
- [x] 预约分页 VO `AppointmentPageItemVO`
- [x] 医生排班 VO `DoctorScheduleVO`
- [x] 候诊 VO `QueueRecordVO`
- [x] 预约 Mapper `AppointmentMapper`
- [x] 医生排班 Mapper `DoctorScheduleMapper`
- [x] 预约日志 Mapper `AppointmentLogMapper`
- [x] 候诊记录 Mapper `QueueRecordMapper`
- [x] 候诊查询 Mapper `QueueRecordQueryMapper`
- [x] 预约查询 XML `AppointmentMapper.xml`
- [x] 医生排班查询 XML `DoctorScheduleMapper.xml`
- [x] 候诊查询 XML `QueueRecordQueryMapper.xml`
- [x] 预约服务接口 `AppointmentService`
- [x] 医生排班服务接口 `DoctorScheduleService`
- [x] 候诊服务接口 `QueueRecordService`
- [x] 预约服务实现 `AppointmentServiceImpl`
- [x] 医生排班服务实现 `DoctorScheduleServiceImpl`
- [x] 候诊服务实现 `QueueRecordServiceImpl`
- [x] 预约控制器 `AppointmentController`
- [x] 医生排班控制器 `DoctorScheduleController`
- [x] 候诊控制器 `QueueRecordController`
- [x] 预约查询按当前登录用户 `orgId` 做数据范围限制
- [x] 预约分页支持按日期、门诊、医生、状态、关键字过滤
- [x] 预约分页聚合返回患者姓名/手机号、医生姓名、门诊名称
- [x] 预约创建时自动生成 `appointmentNo`
- [x] 预约创建/改约时校验患者、门诊、医生有效性
- [x] 预约改约/取消/签到时记录 `appointment_log`
- [x] 到诊签到时自动生成/更新 `queue_record`
- [x] 预约详情聚合返回候诊信息
- [x] 医生排班支持按日期、门诊、医生、状态过滤
- [x] 排班创建时校验门诊、医生、诊室有效性
- [x] 候诊列表支持按日期、门诊、医生、状态过滤
- [x] 候诊状态流转支持 `CALLING/IN_TREATMENT/COMPLETED/SKIPPED`

### 3.11 已完成验证
- [x] `mvn -q -DskipTests compile` 编译通过
- [x] Spring Boot 可启动
- [x] Swagger 页面可打开
- [x] `/api/auth/login` 可返回 JWT
- [x] 未带 JWT 访问受保护接口返回 401
- [x] 带 JWT 访问 `/api/system/me` 返回当前用户
- [x] 患者分页接口可返回初始化数据
- [x] 患者详情接口可返回 `patient + patient_profile`
- [x] 患者建档成功写入数据库

### 3.12 病历模块 emr
#### 已完成接口
- [x] `GET /api/emr/records` 分页查询病历
- [x] `GET /api/emr/records/{id}` 查询病历详情
- [x] `POST /api/emr/records` 新建病历
- [x] `GET /api/emr/treatment-plans` 分页查询治疗计划
- [x] `GET /api/emr/treatment-plans/{id}` 查询治疗计划详情
- [x] `POST /api/emr/treatment-plans` 新建治疗计划
- [x] `PUT /api/emr/treatment-plans/{id}/status` 更新治疗计划状态
- [x] `GET /api/emr/print-templates` 查询打印/知情同意书模板
- [x] `GET /api/emr/consent-forms` 查询知情同意书列表
- [x] `GET /api/emr/consent-forms/{id}` 查询知情同意书详情
- [x] `POST /api/emr/consent-forms` 创建知情同意书记录
- [x] `GET /api/emr/signatures` 查询电子签名列表
- [x] `POST /api/emr/signatures` 创建电子签名记录

#### 已完成能力
- [x] 病历实体 `MedicalRecordEntity`
- [x] 诊断记录实体 `DiagnosisRecordEntity`
- [x] 病历分页查询请求 `MedicalRecordPageQuery`
- [x] 病历创建请求 `MedicalRecordCreateRequest`
- [x] 诊断创建请求 `DiagnosisRecordCreateRequest`
- [x] 病历分页 VO `MedicalRecordPageItemVO`
- [x] 病历详情 VO `MedicalRecordDetailVO`
- [x] 诊断记录 VO `DiagnosisRecordVO`
- [x] 病历 Mapper `MedicalRecordMapper`
- [x] 诊断记录 Mapper `DiagnosisRecordMapper`
- [x] 病历查询 XML `MedicalRecordMapper.xml`
- [x] 病历服务接口 `MedicalRecordService`
- [x] 病历服务实现 `MedicalRecordServiceImpl`
- [x] 病历控制器 `MedicalRecordController`
- [x] 治疗计划实体 `TreatmentPlanEntity`
- [x] 治疗项目实体 `TreatmentItemEntity`
- [x] 治疗计划分页请求 `TreatmentPlanPageQuery`
- [x] 治疗计划创建请求 `TreatmentPlanCreateRequest`
- [x] 治疗计划状态更新请求 `TreatmentPlanStatusUpdateRequest`
- [x] 治疗项目创建请求 `TreatmentItemCreateRequest`
- [x] 治疗计划分页 VO `TreatmentPlanPageItemVO`
- [x] 治疗计划详情 VO `TreatmentPlanDetailVO`
- [x] 治疗项目 VO `TreatmentItemVO`
- [x] 治疗计划 Mapper `TreatmentPlanMapper`
- [x] 治疗项目 Mapper `TreatmentItemMapper`
- [x] 治疗计划查询 XML `TreatmentPlanMapper.xml`
- [x] 治疗计划服务接口 `TreatmentPlanService`
- [x] 治疗计划服务实现 `TreatmentPlanServiceImpl`
- [x] 治疗计划控制器 `TreatmentPlanController`
- [x] 打印模板实体 `PrintTemplateEntity`
- [x] 知情同意书实体 `ConsentFormRecordEntity`
- [x] 电子签名实体 `ElectronicSignatureEntity`
- [x] 打印模板查询请求 `PrintTemplateQuery`
- [x] 知情同意书查询请求 `ConsentFormQuery`
- [x] 知情同意书创建请求 `ConsentFormCreateRequest`
- [x] 电子签名查询请求 `ElectronicSignatureQuery`
- [x] 电子签名创建请求 `ElectronicSignatureCreateRequest`
- [x] 打印模板 VO `PrintTemplateVO`
- [x] 知情同意书 VO `ConsentFormVO`
- [x] 电子签名 VO `ElectronicSignatureVO`
- [x] 打印模板 Mapper `PrintTemplateMapper`
- [x] 知情同意书 Mapper `ConsentFormRecordMapper`
- [x] 电子签名 Mapper `ElectronicSignatureMapper`
- [x] 打印模板查询 XML `PrintTemplateMapper.xml`
- [x] 知情同意书查询 XML `ConsentFormRecordMapper.xml`
- [x] 电子签名查询 XML `ElectronicSignatureMapper.xml`
- [x] 知情同意书服务接口 `ConsentFormService`
- [x] 电子签名服务接口 `ElectronicSignatureService`
- [x] 知情同意书服务实现 `ConsentFormServiceImpl`
- [x] 电子签名服务实现 `ElectronicSignatureServiceImpl`
- [x] 知情同意书控制器 `ConsentFormController`
- [x] 电子签名控制器 `ElectronicSignatureController`
- [x] 病历查询按当前登录用户 `orgId` 做数据范围限制
- [x] 病历分页支持按患者、医生、门诊、状态、就诊日期、关键字过滤
- [x] 新建病历时自动生成 `recordNo`
- [x] 新建病历时校验门诊/患者/医生/预约有效性
- [x] 新建病历时支持同步写入诊断记录
- [x] 病历详情聚合返回诊断列表
- [x] 治疗计划查询支持按患者、病历、医生、门诊、状态过滤
- [x] 新建治疗计划时自动生成 `planNo`
- [x] 新建治疗计划时自动汇总项目应收金额
- [x] 新建治疗计划时校验病历、患者、门诊一致性
- [x] 治疗计划详情聚合返回治疗项目列表
- [x] 打印模板支持按模板类型、门诊过滤
- [x] 知情同意书创建时自动保存模板内容快照
- [x] 知情同意书查询按当前登录用户数据范围限制
- [x] 电子签名支持按患者、病历、签署人类型过滤
- [x] 医生签署病历时自动回写病历签署标记

### 3.13 牙位图模块 dentalchart
#### 已完成接口
- [x] `GET /api/dentalcharts/medical-records/{medicalRecordId}` 按病历查询牙位图
- [x] `PUT /api/dentalcharts/medical-records/{medicalRecordId}` 按病历保存牙位图

#### 已完成能力
- [x] 牙位图实体 `DentalChartEntity`
- [x] 牙位图明细实体 `DentalChartDetailEntity`
- [x] 牙位图保存请求 `DentalChartSaveRequest`
- [x] 牙位图明细保存请求 `DentalChartDetailSaveRequest`
- [x] 牙位图 VO `DentalChartVO`
- [x] 牙位图明细 VO `DentalChartDetailVO`
- [x] 牙位图 Mapper `DentalChartMapper`
- [x] 牙位图明细 Mapper `DentalChartDetailMapper`
- [x] 牙位图查询 XML `DentalChartMapper.xml`
- [x] 牙位图服务接口 `DentalChartService`
- [x] 牙位图服务实现 `DentalChartServiceImpl`
- [x] 牙位图控制器 `DentalChartController`
- [x] 牙位图查询按当前登录用户 `orgId` 做数据范围限制
- [x] 保存牙位图时校验病历/门诊/患者有效性
- [x] 保存牙位图时支持整图明细替换
- [x] 二次保存时自动递增 `chartVersion`
- [x] 牙位图详情聚合返回病历号、患者名、门诊名和明细列表

### 3.14 收费模块 billing
#### 已完成接口
- [x] `GET /api/billing/charge-orders` 分页查询收费单
- [x] `GET /api/billing/charge-orders/{id}` 查询收费单详情
- [x] `POST /api/billing/charge-orders` 创建收费单
- [x] `PUT /api/billing/charge-orders/{id}/payments` 登记支付
- [x] `PUT /api/billing/charge-orders/{id}/refunds` 登记退款
- [x] `GET /api/billing/cashier-shifts` 分页查询交班记录
- [x] `GET /api/billing/cashier-shifts/{id}` 查询交班详情
- [x] `POST /api/billing/cashier-shifts` 开班
- [x] `PUT /api/billing/cashier-shifts/{id}/close` 关班

#### 已完成能力
- [x] 收费单实体 `ChargeOrderEntity`
- [x] 收费明细实体 `ChargeItemEntity`
- [x] 收费单创建请求 `ChargeOrderCreateRequest`
- [x] 收费明细创建请求 `ChargeItemCreateRequest`
- [x] 收费单分页请求 `ChargeOrderPageQuery`
- [x] 收费单分页 VO `ChargeOrderPageItemVO`
- [x] 收费单详情 VO `ChargeOrderDetailVO`
- [x] 收费明细 VO `ChargeItemVO`
- [x] 支付记录实体 `PaymentRecordEntity`
- [x] 退款记录实体 `RefundRecordEntity`
- [x] 应收记录实体 `ReceivableRecordEntity`
- [x] 收银交班实体 `CashierShiftRecordEntity`
- [x] 支付请求 `PaymentCreateRequest`
- [x] 退款请求 `RefundCreateRequest`
- [x] 开班请求 `CashierShiftOpenRequest`
- [x] 关班请求 `CashierShiftCloseRequest`
- [x] 交班分页请求 `CashierShiftQuery`
- [x] 支付记录 VO `PaymentRecordVO`
- [x] 退款记录 VO `RefundRecordVO`
- [x] 交班 VO `CashierShiftVO`
- [x] 收费单 Mapper `ChargeOrderMapper`
- [x] 收银交班 Mapper `CashierShiftRecordMapper`
- [x] 收费明细 Mapper `ChargeItemMapper`
- [x] 支付记录 Mapper `PaymentRecordMapper`
- [x] 退款记录 Mapper `RefundRecordMapper`
- [x] 应收记录 Mapper `ReceivableRecordMapper`
- [x] 收费单查询 XML `ChargeOrderMapper.xml`
- [x] 收银交班查询 XML `CashierShiftRecordMapper.xml`
- [x] 收费服务接口 `ChargeOrderService`
- [x] 收银交班服务接口 `CashierShiftService`
- [x] 收费服务实现 `ChargeOrderServiceImpl`
- [x] 收银交班服务实现 `CashierShiftServiceImpl`
- [x] 收费控制器 `ChargeOrderController`
- [x] 收银交班控制器 `CashierShiftController`
- [x] 收费查询按当前登录用户 `orgId` 做数据范围限制
- [x] 收费分页支持按患者、门诊、状态、收费日期、关键字过滤
- [x] 创建收费单时自动生成 `chargeNo`
- [x] 创建收费单时自动汇总总额、优惠、应收、欠费
- [x] 创建收费单时校验门诊/患者/病历有效性
- [x] 收费单详情聚合返回收费明细列表
- [x] 创建收费单时自动生成应收记录
- [x] 登记支付时自动更新收费状态、已付金额、欠费金额
- [x] 登记退款时自动更新收费状态、已付金额、欠费金额
- [x] 收费单详情聚合返回支付记录与退款记录
- [x] 开班时自动生成 `shiftNo`
- [x] 关班时自动汇总现金/微信/支付宝/银行卡/余额/退款金额
- [x] 交班列表支持按门诊、收银员、日期、状态过滤

### 3.15 影像模块 imaging
#### 已完成接口
- [x] `GET /api/imaging/patient-images` 查询患者影像列表
- [x] `POST /api/imaging/patient-images` 新建患者影像记录
- [x] `GET /api/imaging/photo-compare-groups` 查询术前术后对比组
- [x] `POST /api/imaging/photo-compare-groups` 创建术前术后对比组

#### 已完成能力
- [x] 患者影像实体 `PatientImageEntity`
- [x] 术前术后对比组实体 `PhotoCompareGroupEntity`
- [x] 患者影像创建请求 `PatientImageCreateRequest`
- [x] 术前术后对比组创建请求 `PhotoCompareGroupCreateRequest`
- [x] 患者影像查询请求 `PatientImageQuery`
- [x] 术前术后对比组查询请求 `PhotoCompareGroupQuery`
- [x] 患者影像 VO `PatientImageVO`
- [x] 术前术后对比组 VO `PhotoCompareGroupVO`
- [x] 患者影像 Mapper `PatientImageMapper`
- [x] 术前术后对比组 Mapper `PhotoCompareGroupMapper`
- [x] 患者影像查询 XML `PatientImageMapper.xml`
- [x] 术前术后对比组查询 XML `PhotoCompareGroupMapper.xml`
- [x] 患者影像服务接口 `PatientImageService`
- [x] 术前术后对比组服务接口 `PhotoCompareGroupService`
- [x] 患者影像服务实现 `PatientImageServiceImpl`
- [x] 术前术后对比组服务实现 `PhotoCompareGroupServiceImpl`
- [x] 患者影像控制器 `PatientImageController`
- [x] 术前术后对比组控制器 `PhotoCompareGroupController`
- [x] 影像查询按当前登录用户 `orgId` 做数据范围限制
- [x] 影像列表支持按患者、病历、影像类型、分组类型过滤
- [x] 新建影像记录时校验门诊/患者/病历/文件有效性
- [x] 影像列表聚合返回附件文件名、URL、MIME 类型
- [x] 术前术后对比组支持按患者、病历过滤
- [x] 对比组创建时校验前后图片属于同一患者/病历

### 3.16 会员模块 member
#### 已完成接口
- [x] `GET /api/members` 分页查询会员账户
- [x] `GET /api/members/{id}` 查询会员账户详情
- [x] `POST /api/members` 开通会员账户
- [x] `GET /api/members/level-options` 查询会员等级选项

#### 已完成能力
- [x] 会员账户实体 `MemberAccountEntity`
- [x] 会员等级实体 `MemberLevelEntity`
- [x] 会员账户创建请求 `MemberAccountCreateRequest`
- [x] 会员账户分页请求 `MemberAccountPageQuery`
- [x] 会员等级选项 VO `MemberLevelOptionVO`
- [x] 会员账户分页 VO `MemberAccountPageItemVO`
- [x] 会员账户详情 VO `MemberAccountDetailVO`
- [x] 会员账户 Mapper `MemberAccountMapper`
- [x] 会员等级 Mapper `MemberLevelMapper`
- [x] 会员账户查询 XML `MemberAccountMapper.xml`
- [x] 会员查询 Mapper `MemberAccountQueryMapper`
- [x] 会员服务接口 `MemberAccountService`
- [x] 会员服务实现 `MemberAccountServiceImpl`
- [x] 会员控制器 `MemberAccountController`
- [x] 会员查询按当前登录用户 `orgId` 做数据范围限制
- [x] 会员分页支持按患者、等级、状态、关键字过滤
- [x] 开通会员时自动生成 `memberNo`
- [x] 开通会员时校验患者与会员等级有效性
- [x] 开通会员后自动回写患者 `member_status`
- [x] 会员详情聚合返回最近余额/积分流水

### 3.17 报表模块 report
#### 已完成接口
- [x] `GET /api/reports/clinic-overview` 查询门诊经营概览
- [x] `GET /api/reports/doctor-performance` 查询医生业绩概览

#### 已完成能力
- [x] 门诊经营概览查询请求 `ClinicOverviewQuery`
- [x] 医生业绩查询请求 `DoctorPerformanceQuery`
- [x] 门诊经营概览 VO `ClinicOverviewVO`
- [x] 医生业绩 VO `DoctorPerformanceVO`
- [x] 报表 Mapper `ClinicReportMapper`
- [x] 报表查询 XML `ClinicReportMapper.xml`
- [x] 报表服务接口 `ClinicReportService`
- [x] 报表服务实现 `ClinicReportServiceImpl`
- [x] 报表控制器 `ClinicReportController`
- [x] 经营概览按当前登录用户 `orgId` 做数据范围限制
- [x] 支持按统计日期、门诊过滤预约与收费核心指标
- [x] 汇总返回预约量、到诊量、初诊/复诊量、收费单量、应收/实收/欠费
- [x] 医生业绩支持按统计日期、门诊、医生过滤
- [x] 医生业绩汇总预约量、到诊量、初复诊量、接诊量、业绩金额

### 3.18 短信模块 sms
#### 已完成接口
- [x] `GET /api/sms/templates` 查询短信模板
- [x] `GET /api/sms/tasks` 分页查询短信任务
- [x] `GET /api/sms/tasks/{id}` 查询短信任务详情
- [x] `POST /api/sms/tasks` 创建短信任务

#### 已完成能力
- [x] 短信模板实体 `SmsTemplateEntity`
- [x] 短信任务实体 `SmsTaskEntity`
- [x] 短信发送记录实体 `SmsSendRecordEntity`
- [x] 短信任务创建请求 `SmsTaskCreateRequest`
- [x] 短信任务分页请求 `SmsTaskPageQuery`
- [x] 短信模板 VO `SmsTemplateVO`
- [x] 短信任务分页 VO `SmsTaskPageItemVO`
- [x] 短信任务详情 VO `SmsTaskDetailVO`
- [x] 短信发送记录 VO `SmsSendRecordVO`
- [x] 短信模板 Mapper `SmsTemplateMapper`
- [x] 短信任务 Mapper `SmsTaskMapper`
- [x] 短信发送记录 Mapper `SmsSendRecordMapper`
- [x] 短信任务查询 XML `SmsTaskMapper.xml`
- [x] 短信服务接口 `SmsTaskService`
- [x] 短信服务实现 `SmsTaskServiceImpl`
- [x] 短信控制器 `SmsTaskController`
- [x] 短信模板查询按当前登录用户 `orgId` 做数据范围限制
- [x] 短信任务分页支持按门诊、业务类型、患者、状态、手机号过滤
- [x] 创建短信任务时校验模板与患者有效性
- [x] 计划时间为空或已到时自动调用 `SmsService` 发送
- [x] 发送完成后自动写入 `sms_send_record`

### 3.19 组织基础设置模块 org/system
#### 已完成接口
- [x] `GET /api/org/profile` 查询组织信息
- [x] `GET /api/org/clinics` 查询门诊列表
- [x] `GET /api/org/departments` 查询科室列表
- [x] `GET /api/org/rooms` 查询诊室列表
- [x] `GET /api/org/staff` 查询员工列表
- [x] `GET /api/org/roles` 查询角色列表

#### 已完成能力
- [x] 组织实体 `OrgEntity`
- [x] 门诊实体 `ClinicEntity`
- [x] 科室实体 `DepartmentEntity`
- [x] 诊室实体 `RoomEntity`
- [x] 组织基础查询请求 `OrgBaseQuery`
- [x] 组织信息 VO `OrgProfileVO`
- [x] 门诊 VO `ClinicVO`
- [x] 科室 VO `DepartmentVO`
- [x] 诊室 VO `RoomVO`
- [x] 组织 Mapper `OrgMapper`
- [x] 门诊 Mapper `ClinicMapper`
- [x] 科室 Mapper `DepartmentMapper`
- [x] 诊室 Mapper `RoomMapper`
- [x] 组织基础设置服务接口 `OrgSettingService`
- [x] 组织基础设置服务实现 `OrgSettingServiceImpl`
- [x] 组织基础设置控制器 `OrgSettingController`
- [x] 基础设置查询按当前登录用户 `orgId` 做数据范围限制
- [x] 科室、诊室支持按门诊筛选
- [x] 员工列表支持按门诊、岗位、状态、关键字过滤
- [x] 角色列表支持按组织返回角色编码、名称、数据范围

## 4. 尚未完成清单

### 4.1 患者模块后续待做
- [x] 患者删除后的业务约束细化（会员、预约、病历、治疗计划、收费、影像、签名、同意书关联保护）

### 4.2 认证与权限后续待做
- [x] 更完整的角色权限模型
- [x] 菜单/按钮权限
- [x] 数据权限细化（按门诊、医生、岗位）
- [x] 自定义 `UserDetailsService`，去掉默认开发密码提示

### 4.3 基础设施后续待做
- [x] Flyway 迁移脚本正式接入
- [x] Redis 业务号规则完善
- [x] MinIO 真正上传实现
- [x] 阿里云短信真实发送实现
- [x] RabbitMQ 消息生产/消费骨架
- [x] 全局日志 traceId / requestId
- [x] 统一审计日志
- [x] 多环境配置拆分优化

### 4.4 业务模块后续待做
- [x] `appointment` 预约模块骨架
- [x] `emr` 病历模块骨架
- [x] `dentalchart` 牙位图模块骨架
- [x] `imaging` 影像资料模块骨架
- [x] `billing` 收费模块骨架
- [x] `member` 会员模块骨架
- [x] `sms` 业务短信模块骨架
- [x] `report` 统计报表模块骨架
- [x] `org/system` 基础设置模块深化

### 4.5 工程质量与交付后续待做
- [x] 单元测试
- [x] 集成测试
- [x] 本地启动说明文档
- [x] API 调试示例整理
- [x] Docker / docker-compose 本地依赖编排
- [x] CI 构建脚本

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

### 预约模块相关
- `src/main/java/com/company/dental/modules/appointment/controller/AppointmentController.java`
- `src/main/java/com/company/dental/modules/appointment/service/AppointmentService.java`
- `src/main/java/com/company/dental/modules/appointment/service/impl/AppointmentServiceImpl.java`
- `src/main/java/com/company/dental/modules/appointment/mapper/AppointmentMapper.java`
- `src/main/resources/mapper/appointment/AppointmentMapper.xml`

### 病历模块相关
- `src/main/java/com/company/dental/modules/emr/controller/MedicalRecordController.java`
- `src/main/java/com/company/dental/modules/emr/service/MedicalRecordService.java`
- `src/main/java/com/company/dental/modules/emr/service/impl/MedicalRecordServiceImpl.java`
- `src/main/java/com/company/dental/modules/emr/mapper/MedicalRecordMapper.java`
- `src/main/resources/mapper/emr/MedicalRecordMapper.xml`

### 牙位图模块相关
- `src/main/java/com/company/dental/modules/dentalchart/controller/DentalChartController.java`
- `src/main/java/com/company/dental/modules/dentalchart/service/DentalChartService.java`
- `src/main/java/com/company/dental/modules/dentalchart/service/impl/DentalChartServiceImpl.java`
- `src/main/java/com/company/dental/modules/dentalchart/mapper/DentalChartMapper.java`
- `src/main/resources/mapper/dentalchart/DentalChartMapper.xml`

### 收费模块相关
- `src/main/java/com/company/dental/modules/billing/controller/ChargeOrderController.java`
- `src/main/java/com/company/dental/modules/billing/service/ChargeOrderService.java`
- `src/main/java/com/company/dental/modules/billing/service/impl/ChargeOrderServiceImpl.java`
- `src/main/java/com/company/dental/modules/billing/mapper/ChargeOrderMapper.java`
- `src/main/resources/mapper/billing/ChargeOrderMapper.xml`

### 影像模块相关
- `src/main/java/com/company/dental/modules/imaging/controller/PatientImageController.java`
- `src/main/java/com/company/dental/modules/imaging/service/PatientImageService.java`
- `src/main/java/com/company/dental/modules/imaging/service/impl/PatientImageServiceImpl.java`
- `src/main/java/com/company/dental/modules/imaging/mapper/PatientImageMapper.java`
- `src/main/resources/mapper/imaging/PatientImageMapper.xml`

### 会员模块相关
- `src/main/java/com/company/dental/modules/member/controller/MemberAccountController.java`
- `src/main/java/com/company/dental/modules/member/service/MemberAccountService.java`
- `src/main/java/com/company/dental/modules/member/service/impl/MemberAccountServiceImpl.java`
- `src/main/java/com/company/dental/modules/member/mapper/MemberAccountMapper.java`
- `src/main/resources/mapper/member/MemberAccountMapper.xml`

### 报表模块相关
- `src/main/java/com/company/dental/modules/report/controller/ClinicReportController.java`
- `src/main/java/com/company/dental/modules/report/service/ClinicReportService.java`
- `src/main/java/com/company/dental/modules/report/service/impl/ClinicReportServiceImpl.java`
- `src/main/java/com/company/dental/modules/report/mapper/ClinicReportMapper.java`
- `src/main/resources/mapper/report/ClinicReportMapper.xml`

### 短信模块相关
- `src/main/java/com/company/dental/modules/sms/controller/SmsTaskController.java`
- `src/main/java/com/company/dental/modules/sms/service/SmsTaskService.java`
- `src/main/java/com/company/dental/modules/sms/service/impl/SmsTaskServiceImpl.java`
- `src/main/java/com/company/dental/modules/sms/mapper/SmsTaskMapper.java`
- `src/main/resources/mapper/sms/SmsTaskMapper.xml`

### 组织基础设置相关
- `src/main/java/com/company/dental/modules/org/controller/OrgSettingController.java`
- `src/main/java/com/company/dental/modules/org/service/OrgSettingService.java`
- `src/main/java/com/company/dental/modules/org/service/impl/OrgSettingServiceImpl.java`
- `src/main/java/com/company/dental/modules/org/mapper/OrgMapper.java`
- `src/main/java/com/company/dental/modules/org/mapper/ClinicMapper.java`
- `src/main/java/com/company/dental/modules/org/mapper/StaffMapper.java`

### 收费模块相关
- `src/main/java/com/company/dental/modules/billing/controller/ChargeOrderController.java`
- `src/main/java/com/company/dental/modules/billing/service/ChargeOrderService.java`
- `src/main/java/com/company/dental/modules/billing/service/impl/ChargeOrderServiceImpl.java`
- `src/main/java/com/company/dental/modules/billing/mapper/ChargeOrderMapper.java`
- `src/main/resources/mapper/billing/ChargeOrderMapper.xml`

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
   - `GET /api/auth/me`
   - `GET /api/auth/permissions`
   - `POST /api/auth/refresh`
   - `POST /api/auth/logout`
   - `/api/system/me`
   - `GET /api/system/workbench`
   - `/api/patients`
   - `/api/patients/{id}`
   - `GET /api/patients/{id}/visit-records`
   - `GET /api/patients/{id}/images`
   - `GET /api/patients/{id}/member`
   - `POST /api/patients`
   - `PUT /api/patients/{id}`
   - `PUT /api/patients/{id}/status`
   - `DELETE /api/patients/{id}`
   - `GET /api/patients/tag-options`
   - `PUT /api/patients/{id}/tags`
   - `GET /api/patients/doctor-options`
   - `PUT /api/patients/{id}/primary-doctors`
   - `GET /api/appointments`
   - `GET /api/appointments/{id}`
   - `POST /api/appointments`
   - `PUT /api/appointments/{id}/reschedule`
   - `PUT /api/appointments/{id}/cancel`
   - `PUT /api/appointments/{id}/check-in`
   - `GET /api/appointments/schedules`
   - `POST /api/appointments/schedules`
   - `GET /api/appointments/queues`
   - `PUT /api/appointments/queues/{id}/status`
   - `GET /api/emr/records`
   - `GET /api/emr/records/{id}`
   - `POST /api/emr/records`
   - `GET /api/emr/treatment-plans`
   - `GET /api/emr/treatment-plans/{id}`
   - `POST /api/emr/treatment-plans`
   - `PUT /api/emr/treatment-plans/{id}/status`
   - `GET /api/emr/print-templates`
   - `GET /api/emr/consent-forms`
   - `GET /api/emr/consent-forms/{id}`
   - `POST /api/emr/consent-forms`
   - `GET /api/emr/signatures`
   - `POST /api/emr/signatures`
   - `GET /api/dentalcharts/medical-records/{medicalRecordId}`
   - `PUT /api/dentalcharts/medical-records/{medicalRecordId}`
   - `GET /api/files/attachments`
   - `POST /api/files/attachments/upload`
   - `GET /api/billing/charge-orders`
   - `GET /api/billing/charge-orders/{id}`
   - `POST /api/billing/charge-orders`
   - `PUT /api/billing/charge-orders/{id}/payments`
   - `PUT /api/billing/charge-orders/{id}/refunds`
   - `GET /api/billing/cashier-shifts`
   - `GET /api/billing/cashier-shifts/{id}`
   - `POST /api/billing/cashier-shifts`
   - `PUT /api/billing/cashier-shifts/{id}/close`
   - `GET /api/imaging/patient-images`
   - `POST /api/imaging/patient-images`
   - `GET /api/imaging/photo-compare-groups`
   - `POST /api/imaging/photo-compare-groups`
   - `GET /api/members`
   - `GET /api/members/{id}`
   - `POST /api/members`
   - `GET /api/members/level-options`
   - `GET /api/reports/clinic-overview`
   - `GET /api/reports/doctor-performance`
   - `GET /api/sms/templates`
   - `GET /api/sms/tasks`
   - `GET /api/sms/tasks/{id}`
   - `POST /api/sms/tasks`
   - `GET /api/org/profile`
   - `GET /api/org/clinics`
   - `GET /api/org/departments`
   - `GET /api/org/rooms`
   - `GET /api/org/staff`
   - `GET /api/org/roles`

## 8. 建议下一步开发顺序

推荐按下面顺序继续：
1. 单元测试 / 集成测试补齐
2. 支付 / 退款 / 会员联动深化
3. 全局日志与审计能力
4. Docker / CI 进一步完善

如果希望从最小可用闭环继续推进，建议下一步先做：
- 单元测试 / 集成测试补齐

## 9. 本文件位置

本文件路径：
- `docs/progress/backend-progress-checklist.md`

后续每完成一个模块，建议同步更新本文件，避免换设备后丢失上下文。
