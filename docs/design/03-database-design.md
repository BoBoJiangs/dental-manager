# 一期数据库设计说明

## 1. 设计约定

- 表名、字段名统一使用 `snake_case`
- 主键统一 `bigint`
- 核心业务表统一包含审计字段：`created_by`、`created_at`、`updated_by`、`updated_at`
- 逻辑删除字段统一为 `is_deleted`
- 金额统一 `decimal(18,2)`
- 时间统一 `datetime`
- 一期不强依赖数据库外键，业务层保证数据一致性

## 2. 组织模型

核心数据统一带：
- `org_id`
- `clinic_id`

其中患者主表为集团统一患者池。

## 3. 核心表分组

### 3.1 组织与权限
- `org`
- `clinic`
- `department`
- `room`
- `staff`
- `user_account`
- `sys_role`
- `sys_user_role`

### 3.2 患者中心
- `patient`
- `patient_profile`
- `patient_tag`
- `patient_tag_rel`
- `patient_doctor_rel`

### 3.3 预约排班
- `doctor_schedule`
- `appointment`
- `appointment_log`
- `queue_record`

### 3.4 病历与治疗
- `medical_record`
- `diagnosis_record`
- `treatment_plan`
- `treatment_item`
- `medical_record_ext_ortho`
- `medical_record_ext_implant`

### 3.5 牙位图
- `dental_chart`
- `dental_chart_detail`

### 3.6 文件、影像、签名
- `file_attachment`
- `patient_image`
- `photo_compare_group`
- `electronic_signature`
- `consent_form_record`
- `print_template`

### 3.7 收费财务
- `charge_order`
- `charge_item`
- `payment_record`
- `receivable_record`
- `refund_record`
- `cashier_shift_record`

### 3.8 会员
- `member_level`
- `member_account`
- `member_balance_record`
- `member_points_record`

### 3.9 短信
- `sms_template`
- `sms_task`
- `sms_send_record`

### 3.10 统计
- `clinic_stat_daily`
- `doctor_stat_daily`

### 3.11 预留集成
- `invoice_record`
- `insurance_settlement_record`
- `integration_log`

## 4. 关键业务规则

### 4.1 集团统一患者池
- 同一患者跨门诊共用主档案
- 查重优先规则：手机号 > 证件号 > 姓名+生日

### 4.2 医生患者可见范围
医生只能看：
- 当前主治患者
- 历史接诊患者
- 当前预约接诊患者
- 治疗计划负责人关联患者

### 4.3 病历规则
- 病历不可物理删除
- 已签名病历不可随意修改
- 同意书必须保存签署内容快照

### 4.4 财务规则
- 一个收费单可对应多次支付
- 欠费单独管理
- 退款记录独立存档
- 收费、退款、支付均需保留状态流转

### 4.5 牙位图规则
- 牙位图必须结构化存储
- 支持恒牙/乳牙
- 支持牙位与治疗项目关联

## 5. 推荐建表顺序

1. 组织权限
2. 患者中心
3. 预约排班
4. 病历与牙位图
5. 文件影像签字
6. 收费会员短信
7. 统计与预留集成

## 6. 对应 SQL 文件

- 建表：`sql/01_schema.sql`
- 初始化：`sql/02_init_data.sql`
