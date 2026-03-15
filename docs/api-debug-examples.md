# API 调试示例

本文档提供当前后端骨架的 `curl` 调试示例，适合本地联调、冒烟验证和接口回归。

## 1. 前置条件

- 服务已启动
- Swagger 可访问：`http://127.0.0.1:8080/swagger-ui.html`
- 数据库已初始化：`sql/01_schema.sql` + `sql/02_init_data.sql`

默认登录账号：

- 用户名：`admin`
- 密码：`Admin@123456`

## 2. 通用变量

```bash
export BASE_URL="http://127.0.0.1:8080"
export USERNAME="admin"
export PASSWORD="Admin@123456"
```

## 3. 登录并保存 Token

```bash
export TOKEN=$(
  curl -s -X POST "${BASE_URL}/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
      "username": "'"${USERNAME}"'",
      "password": "'"${PASSWORD}"'"
    }' | jq -r '.data.accessToken'
)

echo "${TOKEN}"
```

如果本机没有 `jq`，也可以先直接打印响应，再手工复制 `accessToken`。

## 4. 认证检查

```bash
curl -s "${BASE_URL}/api/auth/me" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/auth/permissions" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/system/me" \
  -H "Authorization: Bearer ${TOKEN}"
```

## 5. 组织基础设置

```bash
curl -s "${BASE_URL}/api/org/profile" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/org/clinics" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/org/departments?clinicId=1" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/org/rooms?clinicId=1" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/org/roles" \
  -H "Authorization: Bearer ${TOKEN}"
```

## 6. 患者链路

### 6.1 查询标签与医生选项

```bash
curl -s "${BASE_URL}/api/patients/tag-options" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/patients/doctor-options" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 6.2 创建患者

```bash
curl -s -X POST "${BASE_URL}/api/patients" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "patientName": "测试患者A",
    "gender": 1,
    "birthday": "1995-05-20",
    "mobile": "13912345678",
    "sourceCode": "FRONT_DESK",
    "firstClinicId": 1,
    "memberStatus": 0,
    "patientStatus": 1,
    "tagIds": [1, 2],
    "remark": "接口调试创建",
    "profile": {
      "bloodType": "A",
      "allergyHistory": "无",
      "pastHistory": "无",
      "address": "深圳市南山区"
    }
  }'
```

记录响应中的患者 `id`：

```bash
export PATIENT_ID=1
```

### 6.3 查询与更新患者

```bash
curl -s "${BASE_URL}/api/patients/${PATIENT_ID}" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s -X PUT "${BASE_URL}/api/patients/${PATIENT_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "patientName": "测试患者A-更新",
    "gender": 1,
    "birthday": "1995-05-20",
    "mobile": "13912345678",
    "sourceCode": "FRONT_DESK",
    "firstClinicId": 1,
    "memberStatus": 1,
    "patientStatus": 1,
    "tagIds": [1],
    "remark": "更新后的患者信息"
  }'

curl -s -X PUT "${BASE_URL}/api/patients/${PATIENT_ID}/tags" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"tagIds":[1,3]}'

curl -s -X PUT "${BASE_URL}/api/patients/${PATIENT_ID}/primary-doctors" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"doctorIds":[4]}'
```

### 6.4 患者聚合接口

```bash
curl -s "${BASE_URL}/api/patients/${PATIENT_ID}/visit-records" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/patients/${PATIENT_ID}/images" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/patients/${PATIENT_ID}/member" \
  -H "Authorization: Bearer ${TOKEN}"
```

## 7. 预约链路

### 7.1 创建预约

```bash
curl -s -X POST "${BASE_URL}/api/appointments" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "patientId": '"${PATIENT_ID}"',
    "doctorId": 4,
    "roomId": 3,
    "sourceType": "PC",
    "appointmentType": "FIRST",
    "appointmentDate": "2026-03-13",
    "startTime": "2026-03-13T09:30:00",
    "endTime": "2026-03-13T10:00:00",
    "treatmentItemName": "正畸初诊",
    "remark": "接口调试预约"
  }'
```

记录返回的预约 `id`：

```bash
export APPOINTMENT_ID=1
```

### 7.2 预约后续操作

```bash
curl -s "${BASE_URL}/api/appointments/${APPOINTMENT_ID}" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s -X PUT "${BASE_URL}/api/appointments/${APPOINTMENT_ID}/reschedule" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "doctorId": 4,
    "roomId": 3,
    "appointmentDate": "2026-03-13",
    "startTime": "2026-03-13T10:00:00",
    "endTime": "2026-03-13T10:30:00",
    "treatmentItemName": "正畸改约",
    "remark": "改约测试"
  }'

curl -s -X PUT "${BASE_URL}/api/appointments/${APPOINTMENT_ID}/check-in" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"remark":"签到测试"}'
```

### 7.3 医生排班与候诊

```bash
curl -s "${BASE_URL}/api/appointments/schedules?scheduleDate=2026-03-13&clinicId=1" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s -X POST "${BASE_URL}/api/appointments/schedules" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "doctorId": 4,
    "roomId": 3,
    "scheduleDate": "2026-03-13",
    "startTime": "13:00:00",
    "endTime": "18:00:00",
    "scheduleType": "OUTPATIENT",
    "maxAppointmentCount": 8,
    "status": 1,
    "remark": "下午加号门诊"
  }'

curl -s "${BASE_URL}/api/appointments/queues?queueDate=2026-03-13&clinicId=1" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s -X PUT "${BASE_URL}/api/appointments/queues/1/status" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "queueStatus": "CALLING",
    "remark": "开始叫号"
  }'
```

## 8. 病历与牙位图

### 8.1 创建病历

```bash
curl -s -X POST "${BASE_URL}/api/emr/records" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "patientId": '"${PATIENT_ID}"',
    "appointmentId": '"${APPOINTMENT_ID}"',
    "doctorId": 4,
    "visitType": "FIRST",
    "visitDate": "2026-03-13T10:30:00",
    "chiefComplaint": "牙齿不齐",
    "presentIllness": "近两年逐渐明显",
    "oralExamination": "上前牙拥挤",
    "preliminaryDiagnosis": "牙列不齐",
    "recordStatus": "DRAFT",
    "diagnoses": [
      {
        "diagnosisType": "INITIAL",
        "diagnosisName": "牙列不齐",
        "toothPosition": "11,12,21,22"
      }
    ]
  }'
```

记录病历 `id`：

```bash
export RECORD_ID=1
```

### 8.2 保存牙位图

```bash
curl -s -X PUT "${BASE_URL}/api/dentalcharts/medical-records/${RECORD_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "patientId": '"${PATIENT_ID}"',
    "chartType": "PERMANENT",
    "chartStatus": "DRAFT",
    "details": [
      {
        "toothNo": "11",
        "toothSurface": "LABIAL",
        "toothStatus": "ORTHO",
        "diagnosisFlag": 1,
        "treatmentFlag": 0,
        "notes": "前牙拥挤"
      }
    ]
  }'

curl -s "${BASE_URL}/api/dentalcharts/medical-records/${RECORD_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 8.3 创建治疗计划

```bash
curl -s -X POST "${BASE_URL}/api/emr/treatment-plans" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "patientId": '"${PATIENT_ID}"',
    "medicalRecordId": '"${RECORD_ID}"',
    "doctorId": 4,
    "planName": "正畸一期治疗计划",
    "planStatus": "CONFIRMED",
    "startDate": "2026-03-13",
    "remark": "接诊后生成治疗计划",
    "items": [
      {
        "itemCode": "ORTHO001",
        "itemName": "正畸检查",
        "itemCategory": "ORTHO",
        "toothPosition": "11,12,21,22",
        "unitPrice": 200.00,
        "quantity": 1,
        "discountAmount": 0.00,
        "sortNo": 1
      },
      {
        "itemCode": "ORTHO002",
        "itemName": "托槽粘接",
        "itemCategory": "ORTHO",
        "unitPrice": 5000.00,
        "quantity": 1,
        "discountAmount": 500.00,
        "sortNo": 2
      }
    ]
  }'

curl -s "${BASE_URL}/api/emr/treatment-plans?patientId=${PATIENT_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 8.4 查询模板并创建知情同意书

```bash
curl -s "${BASE_URL}/api/emr/print-templates?templateType=CONSENT_FORM&clinicId=1" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s -X POST "${BASE_URL}/api/emr/consent-forms" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "patientId": '"${PATIENT_ID}"',
    "medicalRecordId": '"${RECORD_ID}"',
    "templateId": 2,
    "formStatus": "DRAFT"
  }'

curl -s "${BASE_URL}/api/emr/consent-forms?patientId=${PATIENT_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 8.5 创建电子签名

```bash
curl -s -X POST "${BASE_URL}/api/emr/signatures" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "patientId": '"${PATIENT_ID}"',
    "medicalRecordId": '"${RECORD_ID}"',
    "signerName": "张医生",
    "signerType": "DOCTOR",
    "signatureFileId": '"${FILE_ID}"',
    "signedAt": "2026-03-13T11:10:00"
  }'

curl -s "${BASE_URL}/api/emr/signatures?patientId=${PATIENT_ID}&medicalRecordId=${RECORD_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

## 9. 文件与影像

### 9.1 上传附件

```bash
curl -s -X POST "${BASE_URL}/api/files/attachments/upload" \
  -H "Authorization: Bearer ${TOKEN}" \
  -F "clinicId=1" \
  -F "bizType=MEDICAL_RECORD" \
  -F "bizId=${RECORD_ID}" \
  -F "patientId=${PATIENT_ID}" \
  -F "medicalRecordId=${RECORD_ID}" \
  -F "uploadSource=PC" \
  -F "file=@/tmp/test-image.jpg"

curl -s "${BASE_URL}/api/files/attachments?patientId=${PATIENT_ID}&medicalRecordId=${RECORD_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 9.2 新建影像记录

```bash
export FILE_ID=1

curl -s -X POST "${BASE_URL}/api/imaging/patient-images" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "patientId": '"${PATIENT_ID}"',
    "medicalRecordId": '"${RECORD_ID}"',
    "fileId": '"${FILE_ID}"',
    "imageType": "INTRAORAL",
    "imageGroupType": "NORMAL",
    "shotTime": "2026-03-13T10:45:00",
    "toothPosition": "11",
    "sortNo": 1,
    "remark": "口内照"
  }'

curl -s "${BASE_URL}/api/imaging/patient-images?patientId=${PATIENT_ID}&medicalRecordId=${RECORD_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 9.3 创建术前术后对比组

```bash
curl -s -X POST "${BASE_URL}/api/imaging/photo-compare-groups" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": '"${PATIENT_ID}"',
    "medicalRecordId": '"${RECORD_ID}"',
    "groupName": "正畸一期前后对比",
    "preImageId": 1,
    "postImageId": 2,
    "compareDesc": "前后牙列变化记录"
  }'

curl -s "${BASE_URL}/api/imaging/photo-compare-groups?patientId=${PATIENT_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

## 10. 收费

```bash
curl -s -X POST "${BASE_URL}/api/billing/charge-orders" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "patientId": '"${PATIENT_ID}"',
    "medicalRecordId": '"${RECORD_ID}"',
    "chargeTime": "2026-03-13T11:00:00",
    "remark": "接口调试收费",
    "items": [
      {
        "itemCode": "ORTHO001",
        "itemName": "正畸检查",
        "itemCategory": "ORTHO",
        "toothPosition": "11,12,21,22",
        "unitPrice": 300.00,
        "quantity": 1,
        "discountAmount": 20.00,
        "doctorId": 4
      }
    ]
  }'
```

记录收费单 `id`：

```bash
export CHARGE_ORDER_ID=1

curl -s "${BASE_URL}/api/billing/charge-orders/${CHARGE_ORDER_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 10.1 收银开班 / 关班

```bash
curl -s -X POST "${BASE_URL}/api/billing/cashier-shifts" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "cashierId": 3,
    "shiftDate": "2026-03-13",
    "startTime": "2026-03-13T08:30:00",
    "remark": "早班开班"
  }'

curl -s "${BASE_URL}/api/billing/cashier-shifts?clinicId=1&shiftDate=2026-03-13" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s -X PUT "${BASE_URL}/api/billing/cashier-shifts/1/close" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "endTime": "2026-03-13T18:00:00",
    "remark": "正常关班"
  }'
```

## 11. 会员

### 11.1 查询等级

```bash
curl -s "${BASE_URL}/api/members/level-options" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 11.2 开通会员

```bash
curl -s -X POST "${BASE_URL}/api/members" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": '"${PATIENT_ID}"',
    "levelId": 1,
    "memberStatus": "NORMAL"
  }'
```

## 12. 短信

### 12.1 查询模板与任务

```bash
curl -s "${BASE_URL}/api/sms/templates" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/sms/tasks" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 12.2 创建短信任务

```bash
curl -s -X POST "${BASE_URL}/api/sms/tasks" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clinicId": 1,
    "bizType": "APPOINTMENT",
    "bizId": '"${APPOINTMENT_ID}"',
    "patientId": '"${PATIENT_ID}"',
    "mobile": "13912345678",
    "templateId": 1,
    "templateParams": {
      "patientName": "测试患者A",
      "clinicName": "总院门诊",
      "appointmentDate": "2026-03-13",
      "appointmentTime": "10:00",
      "doctorName": "张医生"
    }
  }'
```

## 13. 报表

```bash
curl -s "${BASE_URL}/api/reports/clinic-overview?clinicId=1&statDate=2026-03-13" \
  -H "Authorization: Bearer ${TOKEN}"

curl -s "${BASE_URL}/api/reports/doctor-performance?clinicId=1&statDate=2026-03-13" \
  -H "Authorization: Bearer ${TOKEN}"
```

## 14. 建议的联调顺序

建议按照下面顺序做一次完整冒烟：

1. 登录
2. 创建患者
3. 创建预约并签到
4. 创建病历
5. 保存牙位图
6. 创建影像记录
7. 创建收费单
8. 开通会员
9. 创建短信任务
10. 开班并关班
11. 查询经营概览和医生业绩

## 15. 备注

- 附件上传示例默认使用本机文件 `/tmp/test-image.jpg`，联调前请先准备一个测试文件
- 如果启用了异步短信派发，记得同时启动 RabbitMQ，并配置 `dental.messaging.sms-enabled=true`
- 如需查看更多接口，请直接使用 Swagger 页面
