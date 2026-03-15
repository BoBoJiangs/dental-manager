package com.company.dental.modules.emr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.common.api.PageResult;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.common.util.BizNoGenerator;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.emr.dto.DiagnosisRecordCreateRequest;
import com.company.dental.modules.emr.dto.MedicalRecordCreateRequest;
import com.company.dental.modules.emr.entity.DiagnosisRecordEntity;
import com.company.dental.modules.emr.entity.MedicalRecordEntity;
import com.company.dental.modules.emr.mapper.DiagnosisRecordMapper;
import com.company.dental.modules.emr.mapper.MedicalRecordMapper;
import com.company.dental.modules.emr.query.MedicalRecordPageQuery;
import com.company.dental.modules.emr.service.MedicalRecordService;
import com.company.dental.modules.emr.vo.DiagnosisRecordVO;
import com.company.dental.modules.emr.vo.MedicalRecordDetailVO;
import com.company.dental.modules.emr.vo.MedicalRecordPageItemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private static final String STATUS_DRAFT = "DRAFT";

    private final MedicalRecordMapper medicalRecordMapper;
    private final DiagnosisRecordMapper diagnosisRecordMapper;
    private final BizNoGenerator bizNoGenerator;
    private final DataScopeHelper dataScopeHelper;

    public MedicalRecordServiceImpl(MedicalRecordMapper medicalRecordMapper,
                                    DiagnosisRecordMapper diagnosisRecordMapper,
                                    BizNoGenerator bizNoGenerator,
                                    DataScopeHelper dataScopeHelper) {
        this.medicalRecordMapper = medicalRecordMapper;
        this.diagnosisRecordMapper = diagnosisRecordMapper;
        this.bizNoGenerator = bizNoGenerator;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public PageResult<MedicalRecordPageItemVO> pageMedicalRecords(MedicalRecordPageQuery query) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        Page<MedicalRecordPageItemVO> page = medicalRecordMapper.selectMedicalRecordPage(
                Page.of(query.getCurrent(), query.getSize()),
                query,
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        return PageResult.<MedicalRecordPageItemVO>builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .current(page.getCurrent())
                .size(page.getSize())
                .build();
    }

    @Override
    public MedicalRecordDetailVO getMedicalRecordDetail(Long recordId) {
        return getMedicalRecordDetailOrThrow(recordId, currentLoginUser());
    }

    @Override
    @Transactional
    public MedicalRecordDetailVO createMedicalRecord(MedicalRecordCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        validateParticipants(orgId, request);

        MedicalRecordEntity record = new MedicalRecordEntity();
        record.setOrgId(orgId);
        record.setClinicId(request.getClinicId());
        record.setRecordNo(bizNoGenerator.next("MR"));
        record.setPatientId(request.getPatientId());
        record.setAppointmentId(request.getAppointmentId());
        record.setDoctorId(request.getDoctorId());
        record.setAssistantId(request.getAssistantId());
        record.setVisitType(request.getVisitType());
        record.setVisitDate(request.getVisitDate());
        record.setChiefComplaint(request.getChiefComplaint());
        record.setPresentIllness(request.getPresentIllness());
        record.setOralExamination(request.getOralExamination());
        record.setAuxiliaryExamination(request.getAuxiliaryExamination());
        record.setPreliminaryDiagnosis(request.getPreliminaryDiagnosis());
        record.setTreatmentAdvice(request.getTreatmentAdvice());
        record.setDoctorAdvice(request.getDoctorAdvice());
        record.setRevisitAdvice(request.getRevisitAdvice());
        record.setNextVisitDate(request.getNextVisitDate());
        record.setRecordStatus(isBlank(request.getRecordStatus()) ? STATUS_DRAFT : request.getRecordStatus());
        record.setSignedFlag(0);
        record.setPrintCount(0);
        record.setRemark(request.getRemark());
        medicalRecordMapper.insert(record);

        saveDiagnoses(record.getId(), request.getDiagnoses());
        return getMedicalRecordDetailOrThrow(record.getId(), loginUser);
    }

    @Override
    @Transactional
    public MedicalRecordDetailVO updateMedicalRecord(Long recordId, MedicalRecordCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        MedicalRecordEntity record = getMedicalRecordEntityOrThrow(recordId, loginUser.getOrgId());
        dataScopeHelper.assertClinicAccess(loginUser, record.getClinicId());
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        validateParticipants(loginUser.getOrgId(), request);

        record.setClinicId(request.getClinicId());
        record.setPatientId(request.getPatientId());
        record.setAppointmentId(request.getAppointmentId());
        record.setDoctorId(request.getDoctorId());
        record.setAssistantId(request.getAssistantId());
        record.setVisitType(request.getVisitType());
        record.setVisitDate(request.getVisitDate());
        record.setChiefComplaint(request.getChiefComplaint());
        record.setPresentIllness(request.getPresentIllness());
        record.setOralExamination(request.getOralExamination());
        record.setAuxiliaryExamination(request.getAuxiliaryExamination());
        record.setPreliminaryDiagnosis(request.getPreliminaryDiagnosis());
        record.setTreatmentAdvice(request.getTreatmentAdvice());
        record.setDoctorAdvice(request.getDoctorAdvice());
        record.setRevisitAdvice(request.getRevisitAdvice());
        record.setNextVisitDate(request.getNextVisitDate());
        record.setRecordStatus(isBlank(request.getRecordStatus()) ? STATUS_DRAFT : request.getRecordStatus());
        record.setRemark(request.getRemark());
        medicalRecordMapper.updateById(record);

        diagnosisRecordMapper.delete(new LambdaQueryWrapper<DiagnosisRecordEntity>()
                .eq(DiagnosisRecordEntity::getMedicalRecordId, recordId));
        saveDiagnoses(recordId, request.getDiagnoses());
        return getMedicalRecordDetailOrThrow(recordId, loginUser);
    }

    private void saveDiagnoses(Long medicalRecordId, List<DiagnosisRecordCreateRequest> diagnoses) {
        if (diagnoses == null || diagnoses.isEmpty()) {
            return;
        }
        for (int index = 0; index < diagnoses.size(); index++) {
            DiagnosisRecordCreateRequest item = diagnoses.get(index);
            DiagnosisRecordEntity diagnosis = new DiagnosisRecordEntity();
            diagnosis.setMedicalRecordId(medicalRecordId);
            diagnosis.setDiagnosisType(item.getDiagnosisType());
            diagnosis.setDiagnosisCode(item.getDiagnosisCode());
            diagnosis.setDiagnosisName(item.getDiagnosisName());
            diagnosis.setToothPosition(item.getToothPosition());
            diagnosis.setDiagnosisDesc(item.getDiagnosisDesc());
            diagnosis.setSortNo(index + 1);
            diagnosisRecordMapper.insert(diagnosis);
        }
    }

    private void validateParticipants(Long orgId, MedicalRecordCreateRequest request) {
        Long clinicCount = medicalRecordMapper.countValidClinic(orgId, request.getClinicId());
        if (clinicCount == null || clinicCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊不存在或已停用");
        }
        Long patientCount = medicalRecordMapper.countValidPatient(orgId, request.getPatientId());
        if (patientCount == null || patientCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者不存在");
        }
        Long doctorCount = medicalRecordMapper.countValidDoctor(orgId, request.getDoctorId());
        if (doctorCount == null || doctorCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "医生不存在或已停用");
        }
        if (request.getAppointmentId() != null) {
            Long appointmentCount = medicalRecordMapper.countValidAppointment(orgId, request.getAppointmentId());
            if (appointmentCount == null || appointmentCount == 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "预约不存在");
            }
        }
    }

    private MedicalRecordDetailVO getMedicalRecordDetailOrThrow(Long recordId, LoginUser loginUser) {
        MedicalRecordDetailVO detail = medicalRecordMapper.selectMedicalRecordDetailById(
                recordId,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }
        List<DiagnosisRecordVO> diagnoses = diagnosisRecordMapper.selectByMedicalRecordId(recordId);
        detail.setDiagnoses(diagnoses == null ? Collections.emptyList() : diagnoses);
        return detail;
    }

    private MedicalRecordEntity getMedicalRecordEntityOrThrow(Long recordId, Long orgId) {
        MedicalRecordEntity entity = medicalRecordMapper.selectOne(new LambdaQueryWrapper<MedicalRecordEntity>()
                .eq(MedicalRecordEntity::getId, recordId)
                .eq(MedicalRecordEntity::getOrgId, orgId)
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }
        return entity;
    }

    private LoginUser currentLoginUser() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return loginUser;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
