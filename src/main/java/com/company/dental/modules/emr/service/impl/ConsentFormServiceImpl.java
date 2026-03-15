package com.company.dental.modules.emr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.emr.dto.ConsentFormCreateRequest;
import com.company.dental.modules.emr.entity.ConsentFormRecordEntity;
import com.company.dental.modules.emr.entity.PrintTemplateEntity;
import com.company.dental.modules.emr.mapper.ConsentFormRecordMapper;
import com.company.dental.modules.emr.mapper.MedicalRecordMapper;
import com.company.dental.modules.emr.mapper.PrintTemplateMapper;
import com.company.dental.modules.emr.query.ConsentFormQuery;
import com.company.dental.modules.emr.query.PrintTemplateQuery;
import com.company.dental.modules.emr.service.ConsentFormService;
import com.company.dental.modules.emr.vo.ConsentFormVO;
import com.company.dental.modules.emr.vo.MedicalRecordDetailVO;
import com.company.dental.modules.emr.vo.PrintTemplateVO;
import com.company.dental.modules.patient.entity.PatientEntity;
import com.company.dental.modules.patient.mapper.PatientMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ConsentFormServiceImpl implements ConsentFormService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_SIGNED = "SIGNED";

    private final PrintTemplateMapper printTemplateMapper;
    private final ConsentFormRecordMapper consentFormRecordMapper;
    private final MedicalRecordMapper medicalRecordMapper;
    private final PatientMapper patientMapper;
    private final DataScopeHelper dataScopeHelper;

    public ConsentFormServiceImpl(PrintTemplateMapper printTemplateMapper,
                                  ConsentFormRecordMapper consentFormRecordMapper,
                                  MedicalRecordMapper medicalRecordMapper,
                                  PatientMapper patientMapper,
                                  DataScopeHelper dataScopeHelper) {
        this.printTemplateMapper = printTemplateMapper;
        this.consentFormRecordMapper = consentFormRecordMapper;
        this.medicalRecordMapper = medicalRecordMapper;
        this.patientMapper = patientMapper;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public List<PrintTemplateVO> listPrintTemplates(PrintTemplateQuery query) {
        LoginUser loginUser = currentLoginUser();
        if (query.getClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, query.getClinicId());
        }
        return printTemplateMapper.selectTemplateList(
                query,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
    }

    @Override
    public List<ConsentFormVO> listConsentForms(ConsentFormQuery query) {
        LoginUser loginUser = currentLoginUser();
        if (query.getClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, query.getClinicId());
        }
        return consentFormRecordMapper.selectConsentForms(
                query,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
    }

    @Override
    public ConsentFormVO getConsentFormDetail(Long consentFormId) {
        LoginUser loginUser = currentLoginUser();
        ConsentFormVO detail = consentFormRecordMapper.selectConsentFormDetailById(
                consentFormId,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.CONSENT_FORM_NOT_FOUND);
        }
        return detail;
    }

    @Override
    @Transactional
    public ConsentFormVO createConsentForm(ConsentFormCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        PrintTemplateEntity template = validateRequest(loginUser, orgId, request);

        ConsentFormRecordEntity entity = new ConsentFormRecordEntity();
        entity.setOrgId(orgId);
        entity.setClinicId(request.getClinicId());
        entity.setPatientId(request.getPatientId());
        entity.setMedicalRecordId(request.getMedicalRecordId());
        entity.setFormCode(template.getTemplateCode());
        entity.setFormName(template.getTemplateName());
        entity.setFormContent(isBlank(request.getFormContent()) ? template.getContent() : request.getFormContent());
        entity.setSignerSignatureId(request.getSignerSignatureId());
        entity.setDoctorSignatureId(request.getDoctorSignatureId());
        entity.setFormStatus(normalizeStatus(request.getFormStatus()));
        if (STATUS_SIGNED.equals(entity.getFormStatus())) {
            entity.setSignedAt(LocalDateTime.now());
        }
        consentFormRecordMapper.insert(entity);
        return getConsentFormDetail(entity.getId());
    }

    @Override
    @Transactional
    public ConsentFormVO updateConsentForm(Long consentFormId, ConsentFormCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        ConsentFormRecordEntity entity = consentFormRecordMapper.selectOne(new LambdaQueryWrapper<ConsentFormRecordEntity>()
                .eq(ConsentFormRecordEntity::getId, consentFormId)
                .eq(ConsentFormRecordEntity::getOrgId, orgId)
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.CONSENT_FORM_NOT_FOUND);
        }
        dataScopeHelper.assertClinicAccess(loginUser, entity.getClinicId());

        PrintTemplateEntity template = validateRequest(loginUser, orgId, request);
        entity.setClinicId(request.getClinicId());
        entity.setPatientId(request.getPatientId());
        entity.setMedicalRecordId(request.getMedicalRecordId());
        entity.setFormCode(template.getTemplateCode());
        entity.setFormName(template.getTemplateName());
        entity.setFormContent(isBlank(request.getFormContent()) ? template.getContent() : request.getFormContent());
        entity.setSignerSignatureId(request.getSignerSignatureId());
        entity.setDoctorSignatureId(request.getDoctorSignatureId());
        entity.setFormStatus(normalizeStatus(request.getFormStatus()));
        entity.setSignedAt(STATUS_SIGNED.equals(entity.getFormStatus()) ? LocalDateTime.now() : null);
        consentFormRecordMapper.updateById(entity);
        return getConsentFormDetail(consentFormId);
    }

    private PrintTemplateEntity validateRequest(LoginUser loginUser, Long orgId, ConsentFormCreateRequest request) {
        PatientEntity patient = patientMapper.selectOne(new LambdaQueryWrapper<PatientEntity>()
                .eq(PatientEntity::getId, request.getPatientId())
                .eq(PatientEntity::getOrgId, orgId)
                .eq(PatientEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (patient == null) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }

        PrintTemplateEntity template = printTemplateMapper.selectOne(new LambdaQueryWrapper<PrintTemplateEntity>()
                .eq(PrintTemplateEntity::getId, request.getTemplateId())
                .eq(PrintTemplateEntity::getOrgId, orgId)
                .eq(PrintTemplateEntity::getStatus, 1)
                .last("LIMIT 1"));
        if (template == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "模板不存在");
        }
        if (template.getClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, template.getClinicId());
        }

        if (request.getMedicalRecordId() != null) {
            MedicalRecordDetailVO recordDetail = medicalRecordMapper.selectMedicalRecordDetailById(
                    request.getMedicalRecordId(),
                    orgId,
                    dataScopeHelper.resolve(loginUser).name(),
                    dataScopeHelper.resolveClinicIds(loginUser),
                    loginUser.getStaffId()
            );
            if (recordDetail == null) {
                throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
            }
            if (!Objects.equals(recordDetail.getClinicId(), request.getClinicId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "同意书门诊必须与病历门诊一致");
            }
            if (!Objects.equals(recordDetail.getPatientId(), request.getPatientId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "同意书患者必须与病历患者一致");
            }
        }
        if (STATUS_SIGNED.equals(normalizeStatus(request.getFormStatus()))
                && (request.getSignerSignatureId() == null || request.getDoctorSignatureId() == null)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已签署同意书必须绑定患者/监护人签名和医生签名");
        }
        return template;
    }

    private LoginUser currentLoginUser() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return loginUser;
    }

    private String normalizeStatus(String status) {
        if (isBlank(status)) {
            return STATUS_DRAFT;
        }
        String normalized = status.trim().toUpperCase();
        if (!STATUS_DRAFT.equals(normalized) && !STATUS_SIGNED.equals(normalized) && !"VOID".equals(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "知情同意书状态非法");
        }
        return normalized;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
