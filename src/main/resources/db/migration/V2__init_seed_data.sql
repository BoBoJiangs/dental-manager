-- =========================================================
-- 1. 集团、门诊、科室、诊室
-- =========================================================

INSERT INTO org (
  id, org_code, org_name, contact_name, contact_phone, status, remark, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES
(1, 'ORG001', '牙科管理集团', '系统管理员', '13800000000', 1, '初始化集团数据', 1, NOW(), 1, NOW(), 0);

INSERT INTO clinic (
  id, org_id, clinic_code, clinic_name, clinic_type, province, city, district, address, phone, business_hours, status, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES
(1, 1, 'CL001', '总院门诊', 'HEAD', '广东省', '深圳市', '南山区', '科技园路 100 号', '0755-80000001', '09:00-18:00', 1, 1, NOW(), 1, NOW(), 0),
(2, 1, 'CL002', '福田分院', 'BRANCH', '广东省', '深圳市', '福田区', '中心路 88 号', '0755-80000002', '09:00-18:00', 1, 1, NOW(), 1, NOW(), 0);

INSERT INTO department (
  id, org_id, clinic_id, dept_code, dept_name, status, sort_no, remark, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES
(1, 1, 1, 'DEP001', '综合科', 1, 1, '总院综合科', 1, NOW(), 1, NOW(), 0),
(2, 1, 1, 'DEP002', '正畸科', 1, 2, '总院正畸科', 1, NOW(), 1, NOW(), 0),
(3, 1, 1, 'DEP003', '种植科', 1, 3, '总院种植科', 1, NOW(), 1, NOW(), 0),
(4, 1, 2, 'DEP004', '综合科', 1, 1, '分院综合科', 1, NOW(), 1, NOW(), 0);

INSERT INTO room (
  id, org_id, clinic_id, room_code, room_name, room_type, floor_no, status, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES
(1, 1, 1, 'ROOM001', '一诊室', 'GENERAL', '1F', 1, 1, NOW(), 1, NOW(), 0),
(2, 1, 1, 'ROOM002', '二诊室', 'GENERAL', '1F', 1, 1, NOW(), 1, NOW(), 0),
(3, 1, 1, 'ROOM003', '正畸诊室', 'ORTHO', '2F', 1, 1, NOW(), 1, NOW(), 0),
(4, 1, 2, 'ROOM004', '一诊室', 'GENERAL', '1F', 1, 1, NOW(), 1, NOW(), 0);

-- =========================================================
-- 2. 角色
-- =========================================================

INSERT INTO sys_role (
  id, org_id, role_code, role_name, data_scope, status, remark, created_at, updated_at, is_deleted
) VALUES
(1, 1, 'GROUP_ADMIN', '集团管理员', 'ALL', 1, '可查看全集团数据', NOW(), NOW(), 0),
(2, 1, 'CLINIC_ADMIN', '门诊管理员', 'CLINIC', 1, '可查看本门诊全部数据', NOW(), NOW(), 0),
(3, 1, 'DOCTOR', '医生', 'SELF_PATIENT', 1, '仅可查看本人历史接诊/主治患者', NOW(), NOW(), 0),
(4, 1, 'FRONT_DESK', '前台', 'CLINIC', 1, '预约、签到、收费基础操作', NOW(), NOW(), 0),
(5, 1, 'FINANCE', '财务', 'CLINIC', 1, '收费、退款、交班、财务报表', NOW(), NOW(), 0),
(6, 1, 'NURSE', '护士/助理', 'CLINIC', 1, '协助接诊、上传资料', NOW(), NOW(), 0);

-- =========================================================
-- 3. 员工与账号
-- =========================================================
-- 注意：以下密码使用 {noop} 前缀，仅用于本地开发联调
-- admin      / Admin@123456
-- clinic001  / Clinic@123456
-- front001   / Front@123456
-- doc001     / Doc@123456
-- doc002     / Doc@123456
-- finance001 / Finance@123456

INSERT INTO staff (
  id, org_id, staff_code, staff_name, gender, mobile, id_no, job_title, staff_type, main_clinic_id, dept_id, status, entry_date, leave_date, is_doctor, specialty, remark, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES
(1, 1, 'S0001', '系统管理员', 1, '13800000001', NULL, '系统管理员', 'ADMIN', 1, 1, 1, '2026-01-01', NULL, 0, NULL, '初始化管理员', 1, NOW(), 1, NOW(), 0),
(2, 1, 'S0002', '王院长', 1, '13800000002', NULL, '主任医师', 'DOCTOR', 1, 1, 1, '2026-01-01', NULL, 1, '种植,修复', '总院负责人', 1, NOW(), 1, NOW(), 0),
(3, 1, 'S0003', '前台小张', 2, '13800000003', NULL, '前台', 'FRONT_DESK', 1, 1, 1, '2026-01-01', NULL, 0, NULL, '总院前台', 1, NOW(), 1, NOW(), 0),
(4, 1, 'S0004', '张医生', 1, '13800000004', NULL, '主治医师', 'DOCTOR', 1, 2, 1, '2026-01-01', NULL, 1, '正畸', '总院医生', 1, NOW(), 1, NOW(), 0),
(5, 1, 'S0005', '李医生', 2, '13800000005', NULL, '主治医师', 'DOCTOR', 2, 4, 1, '2026-01-01', NULL, 1, '综合治疗', '分院医生', 1, NOW(), 1, NOW(), 0),
(6, 1, 'S0006', '财务小李', 2, '13800000006', NULL, '财务', 'FINANCE', 1, 1, 1, '2026-01-01', NULL, 0, NULL, '总院财务', 1, NOW(), 1, NOW(), 0),
(7, 1, 'S0007', '分院管理员', 1, '13800000007', NULL, '门诊管理员', 'ADMIN', 2, 4, 1, '2026-01-01', NULL, 0, NULL, '分院管理员', 1, NOW(), 1, NOW(), 0);

INSERT INTO user_account (
  id, org_id, staff_id, username, password_hash, account_type, mobile, email, status, last_login_at, last_login_ip, pwd_updated_at, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES
(1, 1, 1, 'admin', '{noop}Admin@123456', 'ADMIN', '13800000001', 'admin@example.com', 1, NULL, NULL, NOW(), 1, NOW(), 1, NOW(), 0),
(2, 1, 2, 'clinic001', '{noop}Clinic@123456', 'STAFF', '13800000002', 'clinic001@example.com', 1, NULL, NULL, NOW(), 1, NOW(), 1, NOW(), 0),
(3, 1, 3, 'front001', '{noop}Front@123456', 'STAFF', '13800000003', 'front001@example.com', 1, NULL, NULL, NOW(), 1, NOW(), 1, NOW(), 0),
(4, 1, 4, 'doc001', '{noop}Doc@123456', 'STAFF', '13800000004', 'doc001@example.com', 1, NULL, NULL, NOW(), 1, NOW(), 1, NOW(), 0),
(5, 1, 5, 'doc002', '{noop}Doc@123456', 'STAFF', '13800000005', 'doc002@example.com', 1, NULL, NULL, NOW(), 1, NOW(), 1, NOW(), 0),
(6, 1, 6, 'finance001', '{noop}Finance@123456', 'STAFF', '13800000006', 'finance001@example.com', 1, NULL, NULL, NOW(), 1, NOW(), 1, NOW(), 0),
(7, 1, 7, 'branch001', '{noop}Branch@123456', 'STAFF', '13800000007', 'branch001@example.com', 1, NULL, NULL, NOW(), 1, NOW(), 1, NOW(), 0);

INSERT INTO sys_user_role (
  id, user_id, role_id, clinic_id, created_at
) VALUES
(1, 1, 1, NULL, NOW()),
(2, 2, 2, 1, NOW()),
(3, 2, 3, 1, NOW()),
(4, 3, 4, 1, NOW()),
(5, 4, 3, 1, NOW()),
(6, 5, 3, 2, NOW()),
(7, 6, 5, 1, NOW()),
(8, 7, 2, 2, NOW());

-- =========================================================
-- 4. 患者标签
-- =========================================================

INSERT INTO patient_tag (
  id, org_id, tag_code, tag_name, tag_color, status, created_at, updated_at
) VALUES
(1, 1, 'VIP', 'VIP患者', '#F56C6C', 1, NOW(), NOW()),
(2, 1, 'ORTHO', '正畸患者', '#409EFF', 1, NOW(), NOW()),
(3, 1, 'IMPLANT', '种植患者', '#67C23A', 1, NOW(), NOW()),
(4, 1, 'CHILD', '儿童牙科', '#E6A23C', 1, NOW(), NOW());

-- =========================================================
-- 5. 会员等级
-- =========================================================

INSERT INTO member_level (
  id, org_id, level_code, level_name, upgrade_amount, discount_rate, points_rate, status, remark, created_at, updated_at
) VALUES
(1, 1, 'NORMAL', '普通会员', 0.00, 100.00, 1.00, 1, '默认等级', NOW(), NOW()),
(2, 1, 'SILVER', '银卡会员', 3000.00, 98.00, 1.20, 1, '累计消费满3000升级', NOW(), NOW()),
(3, 1, 'GOLD', '金卡会员', 10000.00, 95.00, 1.50, 1, '累计消费满10000升级', NOW(), NOW());

-- =========================================================
-- 6. 短信模板
-- =========================================================

INSERT INTO sms_template (
  id, org_id, clinic_id, template_code, template_name, biz_type, content, sign_name, enabled_flag, remark, created_at, updated_at
) VALUES
(1, 1, NULL, 'APPT_SUCCESS', '预约成功通知', 'APPOINTMENT',
 '【牙科门诊】尊敬的${patientName}，您已成功预约${clinicName}${appointmentDate}${appointmentTime}，接诊医生：${doctorName}，请提前10分钟到院。',
 '牙科门诊', 1, '集团默认预约成功模板', NOW(), NOW()),
(2, 1, NULL, 'APPT_REMIND', '到诊提醒', 'APPOINTMENT',
 '【牙科门诊】尊敬的${patientName}，您预约的${clinicName}${appointmentDate}${appointmentTime}即将开始，接诊医生：${doctorName}，请准时到院。',
 '牙科门诊', 1, '集团默认到诊提醒模板', NOW(), NOW()),
(3, 1, NULL, 'APPT_CHANGE', '预约变更通知', 'APPOINTMENT',
 '【牙科门诊】尊敬的${patientName}，您的预约已变更为${appointmentDate}${appointmentTime}，门诊：${clinicName}，医生：${doctorName}，如有疑问请联系客服。',
 '牙科门诊', 1, '集团默认预约变更模板', NOW(), NOW()),
(4, 1, NULL, 'REVISIT_REMIND', '复诊提醒', 'REVISIT',
 '【牙科门诊】尊敬的${patientName}，您于${revisitDate}有复诊安排，请按时到院，如需改约请提前联系门诊。',
 '牙科门诊', 1, '集团默认复诊提醒模板', NOW(), NOW()),
(5, 1, NULL, 'ARREARS_REMIND', '欠费提醒', 'ARREARS',
 '【牙科门诊】尊敬的${patientName}，您当前仍有未结清费用${arrearsAmount}元，请及时到院或联系前台处理，感谢您的配合。',
 '牙科门诊', 1, '集团默认欠费提醒模板', NOW(), NOW());

-- =========================================================
-- 7. 打印模板 / 知情同意书模板
-- =========================================================

INSERT INTO print_template (
  id, org_id, clinic_id, template_code, template_name, template_type, content, is_default, status, remark, created_by, created_at, updated_by, updated_at
) VALUES
(1, 1, 1, 'CASE_PRINT_DEFAULT', '默认病例打印模板', 'CASE_PRINT',
'<html><head><meta charset="UTF-8"><title>口腔门诊病历</title></head><body><h2 style="text-align:center;">口腔门诊病历</h2><p>门诊：${clinicName}</p><p>姓名：${patientName}　性别：${gender}　年龄：${age}　电话：${mobile}</p><p>接诊医生：${doctorName}　就诊日期：${visitDate}</p><hr/><p><strong>主诉：</strong>${chiefComplaint}</p><p><strong>现病史：</strong>${presentIllness}</p><p><strong>口腔检查：</strong>${oralExamination}</p><p><strong>辅助检查：</strong>${auxiliaryExamination}</p><p><strong>初步诊断：</strong>${preliminaryDiagnosis}</p><p><strong>治疗建议：</strong>${treatmentAdvice}</p><p><strong>医嘱：</strong>${doctorAdvice}</p><p><strong>复诊建议：</strong>${revisitAdvice}</p><br/><p>医生签名：${doctorSignature}</p></body></html>',
1, 1, '总院默认病例模板', 1, NOW(), 1, NOW()),
(2, 1, 1, 'CONSENT_IMPLANT', '种植知情同意书模板', 'CONSENT_FORM',
'<html><head><meta charset="UTF-8"><title>种植知情同意书</title></head><body><h2 style="text-align:center;">种植知情同意书</h2><p>患者姓名：${patientName}</p><p>医生姓名：${doctorName}</p><p>治疗项目：${treatmentPlanName}</p><p>本人已了解种植治疗的基本过程、可能风险、术后注意事项及替代方案，同意接受本次治疗。</p><p>患者/监护人签名：${patientSignature}</p><p>医生签名：${doctorSignature}</p><p>签署日期：${signDate}</p></body></html>',
1, 1, '总院默认种植知情同意书模板', 1, NOW(), 1, NOW()),
(3, 1, 1, 'CONSENT_ORTHO', '正畸知情同意书模板', 'CONSENT_FORM',
'<html><head><meta charset="UTF-8"><title>正畸知情同意书</title></head><body><h2 style="text-align:center;">正畸知情同意书</h2><p>患者姓名：${patientName}</p><p>医生姓名：${doctorName}</p><p>治疗项目：${treatmentPlanName}</p><p>本人已了解正畸治疗周期、配合要求、可能风险及复诊安排，同意接受本次治疗。</p><p>患者/监护人签名：${patientSignature}</p><p>医生签名：${doctorSignature}</p><p>签署日期：${signDate}</p></body></html>',
0, 1, '总院默认正畸知情同意书模板', 1, NOW(), 1, NOW());

-- =========================================================
-- 8. 演示业务数据
-- =========================================================

INSERT INTO patient (
  id, org_id, patient_code, patient_name, gender, birthday, mobile, id_no, wechat_openid, wechat_unionid, source_code, first_clinic_id, first_visit_at, latest_visit_at, member_status, patient_status, remark, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES
(1, 1, 'P202600001', '王小明', 1, '1995-05-20', '13900000001', NULL, NULL, NULL, 'MINI_PROGRAM', 1, NOW(), NOW(), 1, 1, '演示患者-正畸', 1, NOW(), 1, NOW(), 0),
(2, 1, 'P202600002', '李小美', 2, '1990-08-15', '13900000002', NULL, NULL, NULL, 'FRONT_DESK', 2, NOW(), NOW(), 0, 1, '演示患者-种植', 1, NOW(), 1, NOW(), 0);

INSERT INTO patient_profile (
  id, patient_id, blood_type, allergy_history, past_history, family_history, pregnancy_status, smoking_status, drinking_status, address, emergency_contact, emergency_phone, remark, updated_by, updated_at
) VALUES
(1, 1, 'A', '无', '无', '无', '否', '否', '否', '深圳市南山区', '王先生', '13900001001', '基础档案', 1, NOW()),
(2, 2, 'B', '青霉素过敏', '无', '无', '否', '否', '否', '深圳市福田区', '李女士', '13900001002', '基础档案', 1, NOW());

INSERT INTO patient_tag_rel (
  id, patient_id, tag_id, created_at
) VALUES
(1, 1, 2, NOW()),
(2, 1, 1, NOW()),
(3, 2, 3, NOW());

INSERT INTO patient_doctor_rel (
  id, org_id, patient_id, doctor_id, clinic_id, relation_type, first_related_at, latest_related_at, status, created_at, updated_at
) VALUES
(1, 1, 1, 4, 1, 'PRIMARY', NOW(), NOW(), 1, NOW(), NOW()),
(2, 1, 1, 4, 1, 'HISTORY', NOW(), NOW(), 1, NOW(), NOW()),
(3, 1, 2, 2, 1, 'TREATMENT', NOW(), NOW(), 1, NOW(), NOW()),
(4, 1, 2, 5, 2, 'PRIMARY', NOW(), NOW(), 1, NOW(), NOW());

INSERT INTO member_account (
  id, org_id, patient_id, member_no, level_id, balance_amount, points_balance, total_recharge_amount, total_consume_amount, member_status, activated_at, expired_at, created_at, updated_at
) VALUES
(1, 1, 1, 'M202600001', 2, 2000.00, 120, 3000.00, 1000.00, 'NORMAL', NOW(), NULL, NOW(), NOW());

INSERT INTO member_balance_record (
  id, member_account_id, patient_id, biz_type, biz_id, change_amount, before_balance, after_balance, operator_id, remark, created_at
) VALUES
(1, 1, 1, 'RECHARGE', 1, 3000.00, 0.00, 3000.00, 3, '初始化充值演示数据', NOW()),
(2, 1, 1, 'CONSUME', 1, -1000.00, 3000.00, 2000.00, 3, '初始化消费演示数据', NOW());

INSERT INTO member_points_record (
  id, member_account_id, patient_id, biz_type, biz_id, change_points, before_points, after_points, operator_id, remark, created_at
) VALUES
(1, 1, 1, 'EARN', 1, 120, 0, 120, 3, '初始化积分演示数据', NOW());

INSERT INTO doctor_schedule (
  id, org_id, clinic_id, doctor_id, room_id, schedule_date, start_time, end_time, schedule_type, max_appointment_count, status, remark, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES
(1, 1, 1, 4, 3, CURDATE(), '09:00:00', '12:00:00', 'OUTPATIENT', 10, 1, '张医生今日正畸门诊', 2, NOW(), 2, NOW(), 0),
(2, 1, 1, 2, 1, CURDATE(), '14:00:00', '18:00:00', 'OUTPATIENT', 8, 1, '院长下午出诊', 1, NOW(), 1, NOW(), 0),
(3, 1, 2, 5, 4, CURDATE(), '09:00:00', '18:00:00', 'OUTPATIENT', 12, 1, '李医生分院全天门诊', 7, NOW(), 7, NOW(), 0);

INSERT INTO appointment (
  id, org_id, clinic_id, appointment_no, patient_id, doctor_id, room_id, source_type, appointment_type, appointment_date, start_time, end_time, treatment_item_name, status, cancel_reason, arrived_at, no_show_flag, remark, created_by, created_at, updated_by, updated_at, is_deleted
) VALUES
(1, 1, 1, 'A202600001', 1, 4, 3, 'MINI_PROGRAM', 'FIRST', CURDATE(), TIMESTAMP(CURDATE(), '09:30:00'), TIMESTAMP(CURDATE(), '10:00:00'), '正畸初诊', 'CONFIRMED', NULL, NULL, 0, '小程序预约', 1, NOW(), 1, NOW(), 0),
(2, 1, 2, 'A202600002', 2, 5, 4, 'PC', 'REVISIT', CURDATE(), TIMESTAMP(CURDATE(), '10:30:00'), TIMESTAMP(CURDATE(), '11:00:00'), '种植复诊', 'CONFIRMED', NULL, NULL, 0, '前台代预约', 3, NOW(), 3, NOW(), 0);

INSERT INTO appointment_log (
  id, appointment_id, action_type, before_data, after_data, action_by, action_at, remark
) VALUES
(1, 1, 'CREATE', NULL, JSON_OBJECT('appointmentNo', 'A202600001', 'status', 'CONFIRMED', 'patientId', 1, 'doctorId', 4), 1, NOW(), '初始化预约日志'),
(2, 2, 'CREATE', NULL, JSON_OBJECT('appointmentNo', 'A202600002', 'status', 'CONFIRMED', 'patientId', 2, 'doctorId', 5), 3, NOW(), '初始化预约日志');
