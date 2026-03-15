package com.company.dental.modules.imaging.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.DataScopeType;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.imaging.dto.PatientImageCreateRequest;
import com.company.dental.modules.imaging.entity.PatientImageEntity;
import com.company.dental.modules.imaging.mapper.PatientImageMapper;
import com.company.dental.modules.imaging.query.PatientImageQuery;
import com.company.dental.modules.imaging.service.PatientImageService;
import com.company.dental.modules.imaging.vo.PatientImageVO;
import com.company.dental.modules.patient.entity.PatientEntity;
import com.company.dental.modules.patient.mapper.PatientMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientImageServiceImpl implements PatientImageService {

    private final PatientImageMapper patientImageMapper;
    private final PatientMapper patientMapper;
    private final DataScopeHelper dataScopeHelper;

    public PatientImageServiceImpl(PatientImageMapper patientImageMapper,
                                   PatientMapper patientMapper,
                                   DataScopeHelper dataScopeHelper) {
        this.patientImageMapper = patientImageMapper;
        this.patientMapper = patientMapper;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public List<PatientImageVO> listPatientImages(PatientImageQuery query) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        if (query.getPatientId() != null) {
            validatePatient(loginUser, orgId, query.getPatientId());
        }
        if (query.getMedicalRecordId() != null) {
            validateMedicalRecord(orgId, query.getMedicalRecordId());
        }
        return patientImageMapper.selectPatientImages(
                query,
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
    }

    @Override
    @Transactional
    public PatientImageVO createPatientImage(PatientImageCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        validateClinic(orgId, request.getClinicId());
        validatePatient(loginUser, orgId, request.getPatientId());
        if (request.getMedicalRecordId() != null) {
            validateMedicalRecord(orgId, request.getMedicalRecordId());
        }
        validateFile(orgId, request.getFileId());

        PatientImageEntity image = new PatientImageEntity();
        image.setOrgId(orgId);
        image.setClinicId(request.getClinicId());
        image.setPatientId(request.getPatientId());
        image.setMedicalRecordId(request.getMedicalRecordId());
        image.setFileId(request.getFileId());
        image.setImageType(request.getImageType());
        image.setImageGroupType(request.getImageGroupType() == null ? "NORMAL" : request.getImageGroupType());
        image.setShotTime(request.getShotTime());
        image.setToothPosition(request.getToothPosition());
        image.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        image.setRemark(request.getRemark());
        patientImageMapper.insert(image);

        PatientImageQuery query = new PatientImageQuery();
        query.setPatientId(request.getPatientId());
        query.setMedicalRecordId(request.getMedicalRecordId());
        List<PatientImageVO> images = patientImageMapper.selectPatientImages(
                query,
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        return images.stream()
                .filter(item -> image.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM_ERROR, "影像记录创建失败"));
    }

    private void validatePatient(LoginUser loginUser, Long orgId, Long patientId) {
        LambdaQueryWrapper<PatientEntity> wrapper = new LambdaQueryWrapper<PatientEntity>()
                .eq(PatientEntity::getId, patientId)
                .eq(PatientEntity::getOrgId, orgId)
                .eq(PatientEntity::getIsDeleted, 0);
        applyPatientDataScope(wrapper, loginUser);
        Long count = patientMapper.selectCount(wrapper);
        if (count == null || count == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者不存在");
        }
    }

    private void validateClinic(Long orgId, Long clinicId) {
        Long count = patientImageMapper.countValidClinic(orgId, clinicId);
        if (count == null || count == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊不存在或已停用");
        }
    }

    private void validateMedicalRecord(Long orgId, Long medicalRecordId) {
        Long count = patientImageMapper.countValidMedicalRecord(orgId, medicalRecordId);
        if (count == null || count == 0) {
            throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }
    }

    private void validateFile(Long orgId, Long fileId) {
        Long count = patientImageMapper.countValidFile(orgId, fileId);
        if (count == null || count == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件不存在");
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
