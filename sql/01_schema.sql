SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS dental_mgmt
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE dental_mgmt;

-- =========================================================
-- 1. 组织与权限
-- =========================================================

CREATE TABLE IF NOT EXISTS org (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  org_code VARCHAR(64) NOT NULL COMMENT '集团编码',
  org_name VARCHAR(128) NOT NULL COMMENT '集团名称',
  contact_name VARCHAR(64) DEFAULT NULL COMMENT '联系人',
  contact_phone VARCHAR(32) DEFAULT NULL COMMENT '联系电话',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_org_code (org_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团表';

CREATE TABLE IF NOT EXISTS clinic (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL COMMENT '集团ID',
  clinic_code VARCHAR(64) NOT NULL COMMENT '门诊编码',
  clinic_name VARCHAR(128) NOT NULL COMMENT '门诊名称',
  clinic_type VARCHAR(32) DEFAULT NULL COMMENT '门诊类型：总院/分院',
  province VARCHAR(32) DEFAULT NULL,
  city VARCHAR(32) DEFAULT NULL,
  district VARCHAR(32) DEFAULT NULL,
  address VARCHAR(255) DEFAULT NULL,
  phone VARCHAR(32) DEFAULT NULL,
  business_hours VARCHAR(128) DEFAULT NULL COMMENT '营业时间',
  status TINYINT NOT NULL DEFAULT 1,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_org_clinic_code (org_id, clinic_code),
  KEY idx_clinic_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门诊表';

CREATE TABLE IF NOT EXISTS department (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  dept_code VARCHAR(64) NOT NULL,
  dept_name VARCHAR(64) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  sort_no INT NOT NULL DEFAULT 0,
  remark VARCHAR(255) DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_clinic_dept_code (clinic_id, dept_code),
  KEY idx_department_org_id (org_id),
  KEY idx_department_clinic_id (clinic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

CREATE TABLE IF NOT EXISTS room (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  room_code VARCHAR(64) NOT NULL,
  room_name VARCHAR(64) NOT NULL,
  room_type VARCHAR(32) DEFAULT NULL,
  floor_no VARCHAR(16) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_clinic_room_code (clinic_id, room_code),
  KEY idx_room_org_id (org_id),
  KEY idx_room_clinic_id (clinic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊室表';

CREATE TABLE IF NOT EXISTS staff (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  staff_code VARCHAR(64) NOT NULL,
  staff_name VARCHAR(64) NOT NULL,
  gender TINYINT DEFAULT NULL COMMENT '1男 2女',
  mobile VARCHAR(32) DEFAULT NULL,
  id_no VARCHAR(32) DEFAULT NULL,
  job_title VARCHAR(64) DEFAULT NULL,
  staff_type VARCHAR(32) NOT NULL COMMENT 'DOCTOR/FRONT_DESK/NURSE/FINANCE/ADMIN',
  main_clinic_id BIGINT DEFAULT NULL,
  dept_id BIGINT DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1在职 0停用',
  entry_date DATE DEFAULT NULL,
  leave_date DATE DEFAULT NULL,
  is_doctor TINYINT NOT NULL DEFAULT 0,
  specialty VARCHAR(255) DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_org_staff_code (org_id, staff_code),
  KEY idx_staff_mobile (mobile),
  KEY idx_staff_main_clinic_id (main_clinic_id),
  KEY idx_staff_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

CREATE TABLE IF NOT EXISTS user_account (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  staff_id BIGINT DEFAULT NULL COMMENT '员工ID，患者端可为空',
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  account_type VARCHAR(32) NOT NULL COMMENT 'STAFF/PATIENT/ADMIN',
  mobile VARCHAR(32) DEFAULT NULL,
  email VARCHAR(128) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  last_login_at DATETIME DEFAULT NULL,
  last_login_ip VARCHAR(64) DEFAULT NULL,
  pwd_updated_at DATETIME DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_username (username),
  KEY idx_user_staff_id (staff_id),
  KEY idx_user_mobile (mobile),
  KEY idx_user_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号表';

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  role_code VARCHAR(64) NOT NULL,
  role_name VARCHAR(64) NOT NULL,
  data_scope VARCHAR(32) NOT NULL DEFAULT 'CLINIC' COMMENT 'ALL/CLINIC/SELF_PATIENT',
  status TINYINT NOT NULL DEFAULT 1,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_org_role_code (org_id, role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  clinic_id BIGINT DEFAULT NULL COMMENT '门诊范围，可为空',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_role_clinic (user_id, role_id, clinic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- =========================================================
-- 2. 患者中心
-- =========================================================

CREATE TABLE IF NOT EXISTS patient (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  patient_code VARCHAR(64) NOT NULL,
  patient_name VARCHAR(64) NOT NULL,
  gender TINYINT DEFAULT NULL COMMENT '1男 2女',
  birthday DATE DEFAULT NULL,
  mobile VARCHAR(32) DEFAULT NULL,
  id_no VARCHAR(32) DEFAULT NULL,
  wechat_openid VARCHAR(128) DEFAULT NULL,
  wechat_unionid VARCHAR(128) DEFAULT NULL,
  source_code VARCHAR(32) DEFAULT NULL,
  first_clinic_id BIGINT DEFAULT NULL,
  first_visit_at DATETIME DEFAULT NULL,
  latest_visit_at DATETIME DEFAULT NULL,
  member_status TINYINT NOT NULL DEFAULT 0,
  patient_status TINYINT NOT NULL DEFAULT 1,
  remark VARCHAR(255) DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_org_patient_code (org_id, patient_code),
  KEY idx_patient_mobile (mobile),
  KEY idx_patient_id_no (id_no),
  KEY idx_patient_name_birthday (patient_name, birthday),
  KEY idx_patient_unionid (wechat_unionid),
  KEY idx_patient_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者主表（集团统一患者池）';

CREATE TABLE IF NOT EXISTS patient_profile (
  id BIGINT NOT NULL AUTO_INCREMENT,
  patient_id BIGINT NOT NULL,
  blood_type VARCHAR(8) DEFAULT NULL,
  allergy_history TEXT DEFAULT NULL,
  past_history TEXT DEFAULT NULL,
  family_history TEXT DEFAULT NULL,
  pregnancy_status VARCHAR(32) DEFAULT NULL,
  smoking_status VARCHAR(32) DEFAULT NULL,
  drinking_status VARCHAR(32) DEFAULT NULL,
  address VARCHAR(255) DEFAULT NULL,
  emergency_contact VARCHAR(64) DEFAULT NULL,
  emergency_phone VARCHAR(32) DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_patient_profile_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者扩展信息表';

CREATE TABLE IF NOT EXISTS patient_tag (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  tag_code VARCHAR(64) NOT NULL,
  tag_name VARCHAR(64) NOT NULL,
  tag_color VARCHAR(16) DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_org_tag_code (org_id, tag_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者标签表';

CREATE TABLE IF NOT EXISTS patient_tag_rel (
  id BIGINT NOT NULL AUTO_INCREMENT,
  patient_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_patient_tag (patient_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者标签关联表';

CREATE TABLE IF NOT EXISTS patient_doctor_rel (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL COMMENT 'staff.id',
  clinic_id BIGINT DEFAULT NULL,
  relation_type VARCHAR(32) NOT NULL COMMENT 'PRIMARY/HISTORY/APPOINTMENT/TREATMENT',
  first_related_at DATETIME DEFAULT NULL,
  latest_related_at DATETIME DEFAULT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_patient_doctor (patient_id, doctor_id),
  KEY idx_doctor_clinic (doctor_id, clinic_id),
  KEY idx_relation_type (relation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者-医生关系表';

-- =========================================================
-- 3. 预约与排班
-- =========================================================

CREATE TABLE IF NOT EXISTS doctor_schedule (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  room_id BIGINT DEFAULT NULL,
  schedule_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  schedule_type VARCHAR(32) NOT NULL COMMENT 'OUTPATIENT/STOP/LEAVE',
  max_appointment_count INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  remark VARCHAR(255) DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_schedule_doctor_date (doctor_id, schedule_date),
  KEY idx_schedule_clinic_date (clinic_id, schedule_date),
  KEY idx_schedule_room_date (room_id, schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生排班表';

CREATE TABLE IF NOT EXISTS appointment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  appointment_no VARCHAR(64) NOT NULL,
  patient_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  room_id BIGINT DEFAULT NULL,
  source_type VARCHAR(32) NOT NULL COMMENT 'PC/APP/H5/MINI_PROGRAM',
  appointment_type VARCHAR(32) NOT NULL COMMENT 'FIRST/REVISIT',
  appointment_date DATE NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  treatment_item_name VARCHAR(128) DEFAULT NULL,
  status VARCHAR(32) NOT NULL COMMENT 'CREATED/CONFIRMED/CANCELLED/ARRIVED/FINISHED/NO_SHOW',
  cancel_reason VARCHAR(255) DEFAULT NULL,
  arrived_at DATETIME DEFAULT NULL,
  no_show_flag TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(255) DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_appointment_no (appointment_no),
  KEY idx_appt_patient_date (patient_id, appointment_date),
  KEY idx_appt_doctor_date (doctor_id, appointment_date),
  KEY idx_appt_clinic_date (clinic_id, appointment_date),
  KEY idx_appt_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

CREATE TABLE IF NOT EXISTS appointment_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  appointment_id BIGINT NOT NULL,
  action_type VARCHAR(32) NOT NULL COMMENT 'CREATE/UPDATE/CANCEL/CHECKIN/RESCHEDULE',
  before_data JSON DEFAULT NULL,
  after_data JSON DEFAULT NULL,
  action_by BIGINT DEFAULT NULL,
  action_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remark VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_appt_log_appointment_id (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约日志表';

CREATE TABLE IF NOT EXISTS queue_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  appointment_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  queue_no VARCHAR(32) DEFAULT NULL,
  queue_status VARCHAR(32) NOT NULL COMMENT 'WAITING/CALLING/IN_TREATMENT/COMPLETED/SKIPPED',
  checkin_at DATETIME DEFAULT NULL,
  call_at DATETIME DEFAULT NULL,
  start_treatment_at DATETIME DEFAULT NULL,
  end_treatment_at DATETIME DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_queue_clinic_status (clinic_id, queue_status),
  KEY idx_queue_doctor_status (doctor_id, queue_status),
  KEY idx_queue_appointment_id (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='候诊记录表';

-- =========================================================
-- 4. 病历与接诊
-- =========================================================

CREATE TABLE IF NOT EXISTS print_template (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT DEFAULT NULL COMMENT '为空表示集团模板',
  template_code VARCHAR(64) NOT NULL,
  template_name VARCHAR(128) NOT NULL,
  template_type VARCHAR(32) NOT NULL COMMENT 'CASE_PRINT/CONSENT_FORM',
  content LONGTEXT NOT NULL COMMENT '模板内容',
  is_default TINYINT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  remark VARCHAR(255) DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_clinic_template_code (clinic_id, template_code),
  KEY idx_template_org_id (org_id),
  KEY idx_template_type (template_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印/知情同意书模板表';

CREATE TABLE IF NOT EXISTS medical_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  record_no VARCHAR(64) NOT NULL,
  patient_id BIGINT NOT NULL,
  appointment_id BIGINT DEFAULT NULL,
  doctor_id BIGINT NOT NULL,
  assistant_id BIGINT DEFAULT NULL,
  visit_type VARCHAR(32) NOT NULL COMMENT 'FIRST/REVISIT',
  visit_date DATETIME NOT NULL,
  chief_complaint TEXT DEFAULT NULL,
  present_illness TEXT DEFAULT NULL,
  oral_examination TEXT DEFAULT NULL,
  auxiliary_examination TEXT DEFAULT NULL,
  preliminary_diagnosis TEXT DEFAULT NULL,
  treatment_advice TEXT DEFAULT NULL,
  doctor_advice TEXT DEFAULT NULL,
  revisit_advice TEXT DEFAULT NULL,
  next_visit_date DATE DEFAULT NULL,
  record_status VARCHAR(32) NOT NULL COMMENT 'DRAFT/SUBMITTED/SIGNED/ARCHIVED/VOID',
  signed_flag TINYINT NOT NULL DEFAULT 0,
  signed_at DATETIME DEFAULT NULL,
  print_count INT NOT NULL DEFAULT 0,
  remark VARCHAR(255) DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_record_no (record_no),
  KEY idx_record_patient_date (patient_id, visit_date),
  KEY idx_record_doctor_date (doctor_id, visit_date),
  KEY idx_record_clinic_date (clinic_id, visit_date),
  KEY idx_record_status (record_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病历主表';

CREATE TABLE IF NOT EXISTS diagnosis_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  medical_record_id BIGINT NOT NULL,
  diagnosis_type VARCHAR(32) NOT NULL COMMENT 'INITIAL/REVISIT/FINAL',
  diagnosis_code VARCHAR(64) DEFAULT NULL,
  diagnosis_name VARCHAR(255) NOT NULL,
  tooth_position VARCHAR(64) DEFAULT NULL,
  diagnosis_desc TEXT DEFAULT NULL,
  sort_no INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_diag_record_id (medical_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊断记录表';

CREATE TABLE IF NOT EXISTS treatment_plan (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  plan_no VARCHAR(64) NOT NULL,
  patient_id BIGINT NOT NULL,
  medical_record_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  plan_name VARCHAR(128) NOT NULL,
  total_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  plan_status VARCHAR(32) NOT NULL COMMENT 'DRAFT/CONFIRMED/IN_PROGRESS/COMPLETED/CANCELLED',
  start_date DATE DEFAULT NULL,
  end_date DATE DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_plan_no (plan_no),
  KEY idx_plan_patient_id (patient_id),
  KEY idx_plan_doctor_id (doctor_id),
  KEY idx_plan_status (plan_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治疗计划表';

CREATE TABLE IF NOT EXISTS treatment_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  treatment_plan_id BIGINT NOT NULL,
  medical_record_id BIGINT NOT NULL,
  item_code VARCHAR(64) NOT NULL,
  item_name VARCHAR(128) NOT NULL,
  item_category VARCHAR(64) DEFAULT NULL,
  tooth_position VARCHAR(64) DEFAULT NULL,
  unit_price DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  quantity INT NOT NULL DEFAULT 1,
  discount_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  receivable_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  executed_flag TINYINT NOT NULL DEFAULT 0,
  executed_at DATETIME DEFAULT NULL,
  item_status VARCHAR(32) NOT NULL COMMENT 'PLANNED/EXECUTED/CANCELLED',
  sort_no INT NOT NULL DEFAULT 0,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_treat_item_plan_id (treatment_plan_id),
  KEY idx_treat_item_record_id (medical_record_id),
  KEY idx_treat_item_code (item_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治疗项目表';

CREATE TABLE IF NOT EXISTS medical_record_ext_ortho (
  id BIGINT NOT NULL AUTO_INCREMENT,
  medical_record_id BIGINT NOT NULL,
  malocclusion_type VARCHAR(128) DEFAULT NULL,
  overjet VARCHAR(32) DEFAULT NULL,
  overbite VARCHAR(32) DEFAULT NULL,
  crowding_desc TEXT DEFAULT NULL,
  midline_desc TEXT DEFAULT NULL,
  tmj_desc TEXT DEFAULT NULL,
  facial_profile VARCHAR(64) DEFAULT NULL,
  ortho_plan_desc TEXT DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_ortho_record_id (medical_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='正畸病历扩展表';

CREATE TABLE IF NOT EXISTS medical_record_ext_implant (
  id BIGINT NOT NULL AUTO_INCREMENT,
  medical_record_id BIGINT NOT NULL,
  missing_tooth_desc TEXT DEFAULT NULL,
  bone_condition_desc TEXT DEFAULT NULL,
  implant_site VARCHAR(128) DEFAULT NULL,
  implant_brand VARCHAR(128) DEFAULT NULL,
  implant_plan_desc TEXT DEFAULT NULL,
  surgery_risk_desc TEXT DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_implant_record_id (medical_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='种植病历扩展表';

CREATE TABLE IF NOT EXISTS dental_chart (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  medical_record_id BIGINT NOT NULL,
  chart_type VARCHAR(32) NOT NULL COMMENT 'PERMANENT/DECIDUOUS/MIXED',
  chart_version INT NOT NULL DEFAULT 1,
  chart_status VARCHAR(32) NOT NULL COMMENT 'DRAFT/CONFIRMED',
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_chart_patient_id (patient_id),
  KEY idx_chart_record_id (medical_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牙位图主表';

CREATE TABLE IF NOT EXISTS dental_chart_detail (
  id BIGINT NOT NULL AUTO_INCREMENT,
  dental_chart_id BIGINT NOT NULL,
  tooth_no VARCHAR(16) NOT NULL,
  tooth_surface VARCHAR(32) DEFAULT NULL,
  tooth_status VARCHAR(32) NOT NULL COMMENT 'NORMAL/CARIES/MISSING/RESTORED/IMPLANT/ORTHO',
  diagnosis_flag TINYINT NOT NULL DEFAULT 0,
  treatment_flag TINYINT NOT NULL DEFAULT 0,
  treatment_item_id BIGINT DEFAULT NULL,
  notes VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_chart_detail_tooth (dental_chart_id, tooth_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牙位图明细表';

-- =========================================================
-- 5. 文件、影像、签名
-- =========================================================

CREATE TABLE IF NOT EXISTS file_attachment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT DEFAULT NULL,
  biz_type VARCHAR(32) NOT NULL COMMENT 'PATIENT/MEDICAL_RECORD/CONSENT/SIGNATURE',
  biz_id BIGINT DEFAULT NULL,
  patient_id BIGINT DEFAULT NULL,
  medical_record_id BIGINT DEFAULT NULL,
  file_name VARCHAR(255) NOT NULL,
  file_ext VARCHAR(32) DEFAULT NULL,
  mime_type VARCHAR(128) DEFAULT NULL,
  file_size BIGINT NOT NULL DEFAULT 0,
  storage_type VARCHAR(32) NOT NULL DEFAULT 'MINIO',
  bucket_name VARCHAR(128) DEFAULT NULL,
  object_key VARCHAR(255) DEFAULT NULL,
  file_url VARCHAR(500) DEFAULT NULL,
  upload_source VARCHAR(32) NOT NULL COMMENT 'PC/H5/APP/MINI_PROGRAM',
  uploader_id BIGINT DEFAULT NULL,
  file_status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_file_biz (biz_type, biz_id),
  KEY idx_file_patient_id (patient_id),
  KEY idx_file_record_id (medical_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件附件表';

CREATE TABLE IF NOT EXISTS patient_image (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  medical_record_id BIGINT DEFAULT NULL,
  file_id BIGINT NOT NULL,
  image_type VARCHAR(32) NOT NULL COMMENT 'INTRAORAL/FACE/XRAY/PDF',
  image_group_type VARCHAR(32) DEFAULT 'NORMAL' COMMENT 'PRE_OP/POST_OP/NORMAL',
  shot_time DATETIME DEFAULT NULL,
  tooth_position VARCHAR(64) DEFAULT NULL,
  sort_no INT NOT NULL DEFAULT 0,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_image_patient_time (patient_id, shot_time),
  KEY idx_image_record_id (medical_record_id),
  KEY idx_image_group_type (image_group_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者影像表';

CREATE TABLE IF NOT EXISTS photo_compare_group (
  id BIGINT NOT NULL AUTO_INCREMENT,
  patient_id BIGINT NOT NULL,
  medical_record_id BIGINT DEFAULT NULL,
  group_name VARCHAR(128) NOT NULL,
  pre_image_id BIGINT NOT NULL,
  post_image_id BIGINT NOT NULL,
  compare_desc VARCHAR(255) DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_compare_patient_id (patient_id),
  KEY idx_compare_record_id (medical_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='术前术后对比组表';

CREATE TABLE IF NOT EXISTS electronic_signature (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  patient_id BIGINT DEFAULT NULL,
  medical_record_id BIGINT DEFAULT NULL,
  signer_name VARCHAR(64) NOT NULL,
  signer_type VARCHAR(32) NOT NULL COMMENT 'PATIENT/GUARDIAN/DOCTOR',
  relation_to_patient VARCHAR(32) DEFAULT NULL,
  signature_file_id BIGINT NOT NULL,
  signed_at DATETIME NOT NULL,
  ip_address VARCHAR(64) DEFAULT NULL,
  device_info VARCHAR(255) DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_sign_patient_id (patient_id),
  KEY idx_sign_record_id (medical_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电子签名表';

CREATE TABLE IF NOT EXISTS consent_form_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  medical_record_id BIGINT DEFAULT NULL,
  form_code VARCHAR(64) NOT NULL,
  form_name VARCHAR(128) NOT NULL,
  form_content LONGTEXT NOT NULL COMMENT '签署时内容快照',
  signer_signature_id BIGINT DEFAULT NULL,
  doctor_signature_id BIGINT DEFAULT NULL,
  form_status VARCHAR(32) NOT NULL COMMENT 'DRAFT/SIGNED/VOID',
  signed_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_consent_patient_id (patient_id),
  KEY idx_consent_record_id (medical_record_id),
  KEY idx_consent_form_code (form_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知情同意书记录表';

-- =========================================================
-- 6. 收费与财务
-- =========================================================

CREATE TABLE IF NOT EXISTS charge_order (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  charge_no VARCHAR(64) NOT NULL,
  patient_id BIGINT NOT NULL,
  medical_record_id BIGINT DEFAULT NULL,
  treatment_plan_id BIGINT DEFAULT NULL,
  cashier_id BIGINT DEFAULT NULL,
  total_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  discount_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  receivable_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  paid_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  arrears_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  order_status VARCHAR(32) NOT NULL COMMENT 'DRAFT/PART_PAID/PAID/CANCELLED/REFUNDED',
  charge_time DATETIME NOT NULL,
  settled_at DATETIME DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_by BIGINT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_charge_no (charge_no),
  KEY idx_charge_patient_time (patient_id, charge_time),
  KEY idx_charge_clinic_time (clinic_id, charge_time),
  KEY idx_charge_status (order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收费单表';

CREATE TABLE IF NOT EXISTS charge_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  charge_order_id BIGINT NOT NULL,
  treatment_item_id BIGINT DEFAULT NULL,
  item_code VARCHAR(64) NOT NULL,
  item_name VARCHAR(128) NOT NULL,
  item_category VARCHAR(64) DEFAULT NULL,
  tooth_position VARCHAR(64) DEFAULT NULL,
  unit_price DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  quantity INT NOT NULL DEFAULT 1,
  total_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  discount_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  receivable_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  doctor_id BIGINT DEFAULT NULL,
  item_status VARCHAR(32) NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL/REFUNDED/CANCELLED',
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_charge_item_order_id (charge_order_id),
  KEY idx_charge_item_doctor_id (doctor_id),
  KEY idx_charge_item_code (item_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收费明细表';

CREATE TABLE IF NOT EXISTS payment_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  charge_order_id BIGINT NOT NULL,
  payment_no VARCHAR(64) NOT NULL,
  payment_method VARCHAR(32) NOT NULL COMMENT 'CASH/WECHAT/ALIPAY/BANK_CARD/BALANCE',
  amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  transaction_no VARCHAR(128) DEFAULT NULL,
  payer_name VARCHAR(64) DEFAULT NULL,
  payment_status VARCHAR(32) NOT NULL COMMENT 'INIT/SUCCESS/FAIL/CANCELLED',
  paid_at DATETIME DEFAULT NULL,
  cashier_id BIGINT DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_payment_no (payment_no),
  KEY idx_payment_order_id (charge_order_id),
  KEY idx_payment_method (payment_method),
  KEY idx_payment_paid_at (paid_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

CREATE TABLE IF NOT EXISTS receivable_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  charge_order_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  receivable_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  received_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  outstanding_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  due_date DATE DEFAULT NULL,
  receivable_status VARCHAR(32) NOT NULL COMMENT 'UNPAID/PARTIAL/PAID/WRITEOFF',
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_receivable_patient_id (patient_id),
  KEY idx_receivable_status (receivable_status),
  KEY idx_receivable_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收欠费表';

CREATE TABLE IF NOT EXISTS refund_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  refund_no VARCHAR(64) NOT NULL,
  charge_order_id BIGINT NOT NULL,
  payment_record_id BIGINT DEFAULT NULL,
  patient_id BIGINT NOT NULL,
  refund_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  refund_method VARCHAR(32) NOT NULL COMMENT 'ORIGINAL/CASH/BALANCE',
  refund_reason VARCHAR(255) DEFAULT NULL,
  refund_status VARCHAR(32) NOT NULL COMMENT 'INIT/SUCCESS/FAIL/CANCELLED',
  approved_by BIGINT DEFAULT NULL,
  refunded_at DATETIME DEFAULT NULL,
  operator_id BIGINT DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_refund_no (refund_no),
  KEY idx_refund_charge_id (charge_order_id),
  KEY idx_refund_patient_id (patient_id),
  KEY idx_refund_status (refund_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

CREATE TABLE IF NOT EXISTS cashier_shift_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  shift_no VARCHAR(64) NOT NULL,
  cashier_id BIGINT NOT NULL,
  shift_date DATE NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME DEFAULT NULL,
  cash_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  wechat_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  alipay_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  card_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  balance_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  refund_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  net_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  shift_status VARCHAR(32) NOT NULL COMMENT 'OPEN/CLOSED',
  closed_by BIGINT DEFAULT NULL,
  closed_at DATETIME DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_shift_no (shift_no),
  KEY idx_shift_clinic_date (clinic_id, shift_date),
  KEY idx_shift_cashier_date (cashier_id, shift_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收银交班表';

-- =========================================================
-- 7. 会员
-- =========================================================

CREATE TABLE IF NOT EXISTS member_level (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  level_code VARCHAR(64) NOT NULL,
  level_name VARCHAR(64) NOT NULL,
  upgrade_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  discount_rate DECIMAL(6,2) NOT NULL DEFAULT 100.00 COMMENT '95.00表示95折',
  points_rate DECIMAL(6,2) NOT NULL DEFAULT 1.00,
  status TINYINT NOT NULL DEFAULT 1,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_level_code (org_id, level_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

CREATE TABLE IF NOT EXISTS member_account (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  member_no VARCHAR(64) NOT NULL,
  level_id BIGINT DEFAULT NULL,
  balance_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  points_balance INT NOT NULL DEFAULT 0,
  total_recharge_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  total_consume_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  member_status VARCHAR(32) NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL/FROZEN/CANCELLED',
  activated_at DATETIME DEFAULT NULL,
  expired_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_member_patient_id (patient_id),
  UNIQUE KEY uk_member_no (member_no),
  KEY idx_member_level_id (level_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员账户表';

CREATE TABLE IF NOT EXISTS member_balance_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_account_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  biz_type VARCHAR(32) NOT NULL COMMENT 'RECHARGE/CONSUME/REFUND/ADJUST',
  biz_id BIGINT DEFAULT NULL,
  change_amount DECIMAL(18,2) NOT NULL,
  before_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  after_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  operator_id BIGINT DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_balance_member_time (member_account_id, created_at),
  KEY idx_balance_patient_time (patient_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员余额流水表';

CREATE TABLE IF NOT EXISTS member_points_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  member_account_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  biz_type VARCHAR(32) NOT NULL COMMENT 'EARN/USE/EXPIRE/ADJUST',
  biz_id BIGINT DEFAULT NULL,
  change_points INT NOT NULL,
  before_points INT NOT NULL DEFAULT 0,
  after_points INT NOT NULL DEFAULT 0,
  operator_id BIGINT DEFAULT NULL,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_points_member_time (member_account_id, created_at),
  KEY idx_points_patient_time (patient_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分流水表';

-- =========================================================
-- 8. 短信
-- =========================================================

CREATE TABLE IF NOT EXISTS sms_template (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT DEFAULT NULL COMMENT '为空表示集团级',
  template_code VARCHAR(64) NOT NULL,
  template_name VARCHAR(64) NOT NULL,
  biz_type VARCHAR(32) NOT NULL COMMENT 'APPOINTMENT/REVISIT/ARREARS/MARKETING',
  content VARCHAR(500) NOT NULL,
  sign_name VARCHAR(64) DEFAULT NULL,
  enabled_flag TINYINT NOT NULL DEFAULT 1,
  remark VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sms_template (clinic_id, template_code),
  KEY idx_sms_template_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信模板表';

CREATE TABLE IF NOT EXISTS sms_task (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT DEFAULT NULL,
  biz_type VARCHAR(32) NOT NULL,
  biz_id BIGINT DEFAULT NULL,
  patient_id BIGINT DEFAULT NULL,
  mobile VARCHAR(32) NOT NULL,
  template_id BIGINT DEFAULT NULL,
  template_params JSON DEFAULT NULL,
  task_status VARCHAR(32) NOT NULL COMMENT 'PENDING/SENDING/SUCCESS/FAIL/CANCELLED',
  scheduled_at DATETIME DEFAULT NULL,
  sent_at DATETIME DEFAULT NULL,
  retry_count INT NOT NULL DEFAULT 0,
  fail_reason VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_sms_task_status_time (task_status, scheduled_at),
  KEY idx_sms_task_patient_id (patient_id),
  KEY idx_sms_task_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信任务表';

CREATE TABLE IF NOT EXISTS sms_send_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  sms_task_id BIGINT NOT NULL,
  provider_name VARCHAR(64) DEFAULT NULL,
  provider_msg_id VARCHAR(128) DEFAULT NULL,
  send_status VARCHAR(32) NOT NULL COMMENT 'SUCCESS/FAIL',
  response_code VARCHAR(64) DEFAULT NULL,
  response_msg VARCHAR(255) DEFAULT NULL,
  sent_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_sms_send_task_id (sms_task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信发送记录表';

-- =========================================================
-- 9. 统计
-- =========================================================

CREATE TABLE IF NOT EXISTS clinic_stat_daily (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  stat_date DATE NOT NULL,
  appointment_count INT NOT NULL DEFAULT 0,
  arrived_count INT NOT NULL DEFAULT 0,
  first_visit_count INT NOT NULL DEFAULT 0,
  revisit_count INT NOT NULL DEFAULT 0,
  charge_order_count INT NOT NULL DEFAULT 0,
  receivable_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  paid_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  refund_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  arrears_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_clinic_stat_date (clinic_id, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门诊日报表';

CREATE TABLE IF NOT EXISTS doctor_stat_daily (
  id BIGINT NOT NULL AUTO_INCREMENT,
  org_id BIGINT NOT NULL,
  clinic_id BIGINT NOT NULL,
  doctor_id BIGINT NOT NULL,
  stat_date DATE NOT NULL,
  appointment_count INT NOT NULL DEFAULT 0,
  arrived_count INT NOT NULL DEFAULT 0,
  first_visit_count INT NOT NULL DEFAULT 0,
  revisit_count INT NOT NULL DEFAULT 0,
  treatment_count INT NOT NULL DEFAULT 0,
  performance_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_doctor_stat_date (clinic_id, doctor_id, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生日报表';

-- =========================================================
-- 10. 二期/三期预留集成表
-- =========================================================

CREATE TABLE IF NOT EXISTS invoice_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  charge_order_id BIGINT NOT NULL,
  invoice_no VARCHAR(64) DEFAULT NULL,
  invoice_type VARCHAR(32) DEFAULT NULL COMMENT 'NORMAL/SPECIAL/ELECTRONIC',
  invoice_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  invoice_status VARCHAR(32) NOT NULL COMMENT 'INIT/SUCCESS/FAIL/VOID',
  provider_name VARCHAR(64) DEFAULT NULL,
  provider_resp TEXT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_invoice_charge_id (charge_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票记录表（预留）';

CREATE TABLE IF NOT EXISTS insurance_settlement_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  charge_order_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  settlement_no VARCHAR(64) DEFAULT NULL,
  insurance_type VARCHAR(32) DEFAULT NULL,
  total_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  insurance_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  self_pay_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  settlement_status VARCHAR(32) NOT NULL COMMENT 'INIT/SUCCESS/FAIL',
  provider_resp TEXT DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_insurance_charge_id (charge_order_id),
  KEY idx_insurance_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医保结算记录表（预留）';

CREATE TABLE IF NOT EXISTS integration_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  biz_type VARCHAR(32) NOT NULL,
  biz_id BIGINT DEFAULT NULL,
  provider_name VARCHAR(64) DEFAULT NULL,
  request_data LONGTEXT DEFAULT NULL,
  response_data LONGTEXT DEFAULT NULL,
  result_status VARCHAR(32) NOT NULL COMMENT 'SUCCESS/FAIL',
  error_msg VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_integration_biz (biz_type, biz_id),
  KEY idx_integration_provider (provider_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方集成日志表（预留）';

SET FOREIGN_KEY_CHECKS = 1;
