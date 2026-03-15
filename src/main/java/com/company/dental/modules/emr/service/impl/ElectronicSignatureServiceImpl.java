package com.company.dental.modules.emr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.emr.dto.ElectronicSignatureCreateRequest;
import com.company.dental.modules.emr.entity.ElectronicSignatureEntity;
import com.company.dental.modules.emr.entity.MedicalRecordEntity;
import com.company.dental.modules.emr.mapper.ElectronicSignatureMapper;
import com.company.dental.modules.emr.mapper.MedicalRecordMapper;
import com.company.dental.modules.emr.query.ElectronicSignatureQuery;
import com.company.dental.modules.emr.service.ElectronicSignatureService;
import com.company.dental.modules.emr.vo.ElectronicSignatureVO;
import com.company.dental.modules.file.mapper.FileAttachmentMapper;
import com.company.dental.modules.patient.entity.PatientEntity;
import com.company.dental.modules.patient.mapper.PatientMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ElectronicSignatureServiceImpl implements ElectronicSignatureService {

    private static final Set<String> ALLOWED_SIGNER_TYPES = Set.of("PATIENT", "GUARDIAN", "DOCTOR");

    private final ElectronicSignatureMapper electronicSignatureMapper;
    private final MedicalRecordMapper medicalRecordMapper;
    private final PatientMapper patientMapper;
    private final FileAttachmentMapper fileAttachmentMapper;
    private final DataScopeHelper dataScopeHelper;

    public ElectronicSignatureServiceImpl(ElectronicSignatureMapper electronicSignatureMapper,
                                          MedicalRecordMapper medicalRecordMapper,
                                          PatientMapper patientMapper,
                                          FileAttachmentMapper fileAttachmentMapper,
                                          DataScopeHelper dataScopeHelper) {
        this.electronicSignatureMapper = electronicSignatureMapper;
        this.medicalRecordMapper = medicalRecordMapper;
        this.patientMapper = patientMapper;
        this.fileAttachmentMapper = fileAttachmentMapper;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public List<ElectronicSignatureVO> listSignatures(ElectronicSignatureQuery query) {
        LoginUser loginUser = currentLoginUser();
        if (query.getClinicId() != null) {
            dataScopeHelper.assertClinicAccess(loginUser, query.getClinicId());
        }
        return electronicSignatureMapper.selectSignatureList(
                query,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
    }

    @Override
    @Transactional
    public ElectronicSignatureVO createSignature(ElectronicSignatureCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        String signerType = normalizeSignerType(request.getSignerType());

        if (request.getPatientId() != null) {
            PatientEntity patient = patientMapper.selectOne(new LambdaQueryWrapper<PatientEntity>()
                    .eq(PatientEntity::getId, request.getPatientId())
                    .eq(PatientEntity::getOrgId, orgId)
                    .eq(PatientEntity::getIsDeleted, 0)
                    .last("LIMIT 1"));
            if (patient == null) {
                throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
            }
        }

        MedicalRecordEntity medicalRecord = null;
        if (request.getMedicalRecordId() != null) {
            medicalRecord = medicalRecordMapper.selectOne(new LambdaQueryWrapper<MedicalRecordEntity>()
                    .eq(MedicalRecordEntity::getId, request.getMedicalRecordId())
                    .eq(MedicalRecordEntity::getOrgId, orgId)
                    .eq(MedicalRecordEntity::getIsDeleted, 0)
                    .last("LIMIT 1"));
            if (medicalRecord == null) {
                throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
            }
            if (!Objects.equals(medicalRecord.getClinicId(), request.getClinicId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "签名门诊必须与病历门诊一致");
            }
            if (request.getPatientId() != null && !Objects.equals(medicalRecord.getPatientId(), request.getPatientId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "签名患者必须与病历患者一致");
            }
        }

        if (request.getPatientId() == null && medicalRecord == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者或病历至少传一个");
        }

        Long fileCount = fileAttachmentMapper.selectCount(new LambdaQueryWrapper<com.company.dental.modules.file.entity.FileAttachmentEntity>()
                .eq(com.company.dental.modules.file.entity.FileAttachmentEntity::getId, request.getSignatureFileId())
                .eq(com.company.dental.modules.file.entity.FileAttachmentEntity::getOrgId, orgId)
                .eq(com.company.dental.modules.file.entity.FileAttachmentEntity::getFileStatus, 1)
                .eq(com.company.dental.modules.file.entity.FileAttachmentEntity::getIsDeleted, 0));
        if (fileCount == null || fileCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "签名文件不存在");
        }

        ElectronicSignatureEntity entity = new ElectronicSignatureEntity();
        entity.setOrgId(orgId);
        entity.setClinicId(request.getClinicId());
        entity.setPatientId(request.getPatientId() == null && medicalRecord != null ? medicalRecord.getPatientId() : request.getPatientId());
        entity.setMedicalRecordId(request.getMedicalRecordId());
        entity.setSignerName(request.getSignerName());
        entity.setSignerType(signerType);
        entity.setRelationToPatient(request.getRelationToPatient());
        entity.setSignatureFileId(request.getSignatureFileId());
        entity.setSignedAt(request.getSignedAt() == null ? LocalDateTime.now() : request.getSignedAt());
        entity.setIpAddress(request.getIpAddress());
        entity.setDeviceInfo(request.getDeviceInfo());
        entity.setRemark(request.getRemark());
        electronicSignatureMapper.insert(entity);

        if (medicalRecord != null && "DOCTOR".equals(signerType)) {
            medicalRecord.setSignedFlag(1);
            medicalRecord.setSignedAt(entity.getSignedAt());
            medicalRecordMapper.updateById(medicalRecord);
        }

        ElectronicSignatureVO detail = electronicSignatureMapper.selectSignatureDetailById(
                entity.getId(),
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "签名创建失败");
        }
        return detail;
    }

    private String normalizeSignerType(String signerType) {
        String normalized = signerType == null ? null : signerType.trim().toUpperCase();
        if (!ALLOWED_SIGNER_TYPES.contains(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "签署人类型非法");
        }
        return normalized;
    }

    private LoginUser currentLoginUser() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return loginUser;
    }
}
