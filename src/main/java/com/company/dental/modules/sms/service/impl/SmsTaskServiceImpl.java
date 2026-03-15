package com.company.dental.modules.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.common.api.PageResult;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.DataScopeType;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.patient.entity.PatientEntity;
import com.company.dental.modules.patient.mapper.PatientMapper;
import com.company.dental.modules.sms.dto.SmsTaskCreateRequest;
import com.company.dental.modules.sms.entity.SmsTaskEntity;
import com.company.dental.modules.sms.entity.SmsTemplateEntity;
import com.company.dental.modules.sms.mapper.SmsSendRecordMapper;
import com.company.dental.modules.sms.mapper.SmsTaskMapper;
import com.company.dental.modules.sms.mapper.SmsTemplateMapper;
import com.company.dental.modules.sms.service.SmsTaskDispatchService;
import com.company.dental.modules.sms.query.SmsTaskPageQuery;
import com.company.dental.modules.sms.service.SmsTaskService;
import com.company.dental.modules.sms.vo.SmsTaskDetailVO;
import com.company.dental.modules.sms.vo.SmsTaskPageItemVO;
import com.company.dental.modules.sms.vo.SmsTemplateVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SmsTaskServiceImpl implements SmsTaskService {

    private static final String STATUS_PENDING = "PENDING";
    private final SmsTemplateMapper smsTemplateMapper;
    private final SmsTaskMapper smsTaskMapper;
    private final SmsSendRecordMapper smsSendRecordMapper;
    private final PatientMapper patientMapper;
    private final DataScopeHelper dataScopeHelper;
    private final SmsTaskDispatchService smsTaskDispatchService;
    private final ObjectMapper objectMapper;

    public SmsTaskServiceImpl(SmsTemplateMapper smsTemplateMapper,
                              SmsTaskMapper smsTaskMapper,
                              SmsSendRecordMapper smsSendRecordMapper,
                              PatientMapper patientMapper,
                              DataScopeHelper dataScopeHelper,
                              SmsTaskDispatchService smsTaskDispatchService,
                              ObjectMapper objectMapper) {
        this.smsTemplateMapper = smsTemplateMapper;
        this.smsTaskMapper = smsTaskMapper;
        this.smsSendRecordMapper = smsSendRecordMapper;
        this.patientMapper = patientMapper;
        this.dataScopeHelper = dataScopeHelper;
        this.smsTaskDispatchService = smsTaskDispatchService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<SmsTemplateVO> listTemplates() {
        return smsTemplateMapper.selectEnabledTemplates(currentLoginUser().getOrgId());
    }

    @Override
    public PageResult<SmsTaskPageItemVO> pageTasks(SmsTaskPageQuery query) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        Page<SmsTaskPageItemVO> page = smsTaskMapper.selectSmsTaskPage(
                Page.of(query.getCurrent(), query.getSize()),
                query,
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        return PageResult.<SmsTaskPageItemVO>builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .current(page.getCurrent())
                .size(page.getSize())
                .build();
    }

    @Override
    public SmsTaskDetailVO getTaskDetail(Long taskId) {
        LoginUser loginUser = currentLoginUser();
        SmsTaskDetailVO detail = smsTaskMapper.selectSmsTaskDetailById(
                taskId,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "短信任务不存在");
        }
        detail.setTemplateParams(parseParams(detail.getTemplateParamsJson()));
        detail.setSendRecords(smsSendRecordMapper.selectBySmsTaskId(taskId));
        return detail;
    }

    @Override
    @Transactional
    public SmsTaskDetailVO createTask(SmsTaskCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        SmsTemplateEntity template = smsTemplateMapper.selectOne(new LambdaQueryWrapper<SmsTemplateEntity>()
                .eq(SmsTemplateEntity::getId, request.getTemplateId())
                .eq(SmsTemplateEntity::getOrgId, orgId)
                .eq(SmsTemplateEntity::getEnabledFlag, 1)
                .last("LIMIT 1"));
        if (template == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "短信模板不存在");
        }
        if (request.getPatientId() != null) {
            PatientEntity patient = patientMapper.selectOne(new LambdaQueryWrapper<PatientEntity>()
                    .eq(PatientEntity::getId, request.getPatientId())
                    .eq(PatientEntity::getOrgId, orgId)
                    .eq(PatientEntity::getIsDeleted, 0)
                    .last("LIMIT 1"));
            if (patient == null) {
                throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
            }
            assertPatientAccess(loginUser, orgId, request.getPatientId());
        }

        SmsTaskEntity task = new SmsTaskEntity();
        task.setOrgId(orgId);
        task.setClinicId(request.getClinicId());
        task.setBizType(request.getBizType());
        task.setBizId(request.getBizId());
        task.setPatientId(request.getPatientId());
        task.setMobile(request.getMobile());
        task.setTemplateId(request.getTemplateId());
        task.setTemplateParams(toJson(request.getTemplateParams()));
        task.setTaskStatus(STATUS_PENDING);
        task.setScheduledAt(request.getScheduledAt());
        task.setRetryCount(0);
        smsTaskMapper.insert(task);

        if (request.getScheduledAt() == null || !request.getScheduledAt().isAfter(LocalDateTime.now())) {
            smsTaskDispatchService.enqueue(task.getId());
        }
        return getTaskDetail(task.getId());
    }

    private String toJson(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "短信模板参数格式错误");
        }
    }

    private Map<String, String> parseParams(String paramsJson) {
        if (paramsJson == null || paramsJson.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(paramsJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "短信任务参数解析失败");
        }
    }

    private LoginUser currentLoginUser() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return loginUser;
    }

    private void assertPatientAccess(LoginUser loginUser, Long orgId, Long patientId) {
        LambdaQueryWrapper<PatientEntity> wrapper = new LambdaQueryWrapper<PatientEntity>()
                .eq(PatientEntity::getId, patientId)
                .eq(PatientEntity::getOrgId, orgId)
                .eq(PatientEntity::getIsDeleted, 0);
        applyPatientDataScope(wrapper, loginUser);
        Long count = patientMapper.selectCount(wrapper);
        if (count == null || count == 0) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
    }

    private void applyPatientDataScope(LambdaQueryWrapper<PatientEntity> wrapper, LoginUser loginUser) {
        DataScopeType scopeType = dataScopeHelper.resolve(loginUser);
        if (scopeType == DataScopeType.ALL) {
            return;
        }
        if (scopeType == DataScopeType.CLINIC) {
            List<Long> clinicIds = dataScopeHelper.resolveClinicIds(loginUser);
            if (clinicIds.isEmpty()) {
                wrapper.apply("1 = 0");
                return;
            }
            String clinicIdSql = clinicIds.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
            wrapper.and(nested -> nested
                    .in(PatientEntity::getFirstClinicId, clinicIds)
                    .or()
                    .inSql(PatientEntity::getId, "SELECT patient_id FROM appointment WHERE clinic_id IN (" + clinicIdSql + ") AND is_deleted = 0")
                    .or()
                    .inSql(PatientEntity::getId, "SELECT patient_id FROM medical_record WHERE clinic_id IN (" + clinicIdSql + ") AND is_deleted = 0")
                    .or()
                    .inSql(PatientEntity::getId, "SELECT patient_id FROM treatment_plan WHERE clinic_id IN (" + clinicIdSql + ") AND is_deleted = 0")
                    .or()
                    .inSql(PatientEntity::getId, "SELECT patient_id FROM patient_doctor_rel WHERE clinic_id IN (" + clinicIdSql + ") AND status = 1"));
            return;
        }
        Long staffId = loginUser == null ? null : loginUser.getStaffId();
        if (staffId == null) {
            wrapper.apply("1 = 0");
            return;
        }
        wrapper.inSql(PatientEntity::getId,
                "SELECT patient_id FROM patient_doctor_rel WHERE doctor_id = " + staffId + " AND status = 1 " +
                        "UNION SELECT patient_id FROM appointment WHERE doctor_id = " + staffId + " AND is_deleted = 0 " +
                        "UNION SELECT patient_id FROM medical_record WHERE doctor_id = " + staffId + " AND is_deleted = 0 " +
                        "UNION SELECT patient_id FROM treatment_plan WHERE doctor_id = " + staffId + " AND is_deleted = 0");
    }
}
