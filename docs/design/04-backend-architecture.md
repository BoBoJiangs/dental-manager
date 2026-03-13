# Spring Boot 后端项目结构与基础骨架

## 1. 总体架构

一期后端建议采用：

- Spring Boot 单应用
- 模块化单体
- 按业务域拆包
- 应用服务层 + 领域规则层 + 基础设施层

推荐技术栈：
- Java 21
- Spring Boot 3.x
- Spring Security + JWT
- MyBatis-Plus
- MySQL 8
- Redis
- RabbitMQ
- MinIO
- Flyway
- OpenAPI

## 2. 包结构建议

```text
com.company.dental
├─ DentalApplication.java
├─ common
├─ config
├─ framework
├─ modules
└─ integration
```

### common
- ApiResponse
- PageResult
- BaseEntity
- BusinessException
- 常量、枚举、工具类

### config
- MybatisPlusConfig
- MinioConfig
- RedisConfig
- OpenApiConfig
- ThreadPoolConfig

### framework
- security：JWT、SecurityConfig、AuthContext
- exception：全局异常处理
- mybatis：自动填充
- audit：操作审计
- web：TraceId、拦截器

### modules
- auth
- org
- patient
- appointment
- emr
- dentalchart
- imaging
- billing
- member
- sms
- system
- report

### integration
- file
- sms
- insurance
- invoice
- imagingdevice

## 3. 单模块标准结构

```text
modules/patient
├─ controller
├─ dto
├─ query
├─ vo
├─ entity
├─ mapper
├─ service
├─ domain
├─ convert
└─ enums
```

## 4. 基础骨架建议

### 4.1 统一返回结构
- `ApiResponse<T>`
- `PageResult<T>`

### 4.2 基础异常
- `BusinessException`
- `ErrorCode`
- `GlobalExceptionHandler`

### 4.3 基础实体
- `BaseEntity`
- 审计字段自动填充
- 逻辑删除

### 4.4 安全骨架
- `JwtProperties`
- `JwtTokenProvider`
- `JwtAuthenticationFilter`
- `SecurityConfig`
- `LoginUser`
- `AuthContext`

### 4.5 业务编号生成
- `BizNoGenerator`
- `RedisBizNoGenerator`

### 4.6 文件服务抽象
- `FileStorageService`
- `MinioFileStorageService`

### 4.7 短信服务抽象
- `SmsService`
- `AliyunSmsService`

## 5. 推荐接口分端

### 管理后台
- `/api/admin/*`

### 医生 App / H5
- `/api/app/*`

### 患者小程序
- `/api/mini/*`

## 6. 推荐调用关系

```text
Controller -> Service -> Domain / Mapper -> DB
```

不建议直接 `Controller -> Mapper`。

## 7. 事务边界建议

事务主要放在 Service 层，典型场景：
- 新建患者并绑定标签
- 预约创建并生成短信任务
- 病历保存并写牙位图
- 收费并生成支付/欠费记录
- 退款并更新收费状态

## 8. 第一批优先落地文件

- `DentalApplication.java`
- `application.yml`
- `ApiResponse.java`
- `PageResult.java`
- `BusinessException.java`
- `GlobalExceptionHandler.java`
- `BaseEntity.java`
- `MybatisPlusConfig.java`
- `AuditMetaObjectHandler.java`
- `JwtProperties.java`
- `JwtTokenProvider.java`
- `JwtAuthenticationFilter.java`
- `SecurityConfig.java`
- `AuthContext.java`
- `LoginUser.java`
- `AuthController.java`
- `AuthService.java`
- `AuthServiceImpl.java`

## 9. 后端开发顺序建议

1. 启动项目与基础配置
2. 跑通数据库、Swagger、统一异常
3. 跑通登录与 JWT
4. 跑通患者基础查询
5. 再进入预约、病历、收费模块

## 10. 相关 SQL 文件

- `sql/01_schema.sql`
- `sql/02_init_data.sql`
