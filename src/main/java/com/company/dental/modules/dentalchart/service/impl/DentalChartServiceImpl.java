package com.company.dental.modules.dentalchart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.dentalchart.dto.DentalChartDetailSaveRequest;
import com.company.dental.modules.dentalchart.dto.DentalChartSaveRequest;
import com.company.dental.modules.dentalchart.entity.DentalChartDetailEntity;
import com.company.dental.modules.dentalchart.entity.DentalChartEntity;
import com.company.dental.modules.dentalchart.mapper.DentalChartDetailMapper;
import com.company.dental.modules.dentalchart.mapper.DentalChartMapper;
import com.company.dental.modules.dentalchart.service.DentalChartService;
import com.company.dental.modules.dentalchart.vo.DentalChartDetailVO;
import com.company.dental.modules.dentalchart.vo.DentalChartVO;
import com.company.dental.modules.emr.mapper.MedicalRecordMapper;
import com.company.dental.modules.emr.vo.MedicalRecordDetailVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class DentalChartServiceImpl implements DentalChartService {

    private final DentalChartMapper dentalChartMapper;
    private final DentalChartDetailMapper dentalChartDetailMapper;
    private final MedicalRecordMapper medicalRecordMapper;
    private final DataScopeHelper dataScopeHelper;

    public DentalChartServiceImpl(DentalChartMapper dentalChartMapper,
                                  DentalChartDetailMapper dentalChartDetailMapper,
                                  MedicalRecordMapper medicalRecordMapper,
                                  DataScopeHelper dataScopeHelper) {
        this.dentalChartMapper = dentalChartMapper;
        this.dentalChartDetailMapper = dentalChartDetailMapper;
        this.medicalRecordMapper = medicalRecordMapper;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public DentalChartVO getByMedicalRecordId(Long medicalRecordId) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        validateMedicalRecordAccess(loginUser, medicalRecordId);
        DentalChartVO chart = dentalChartMapper.selectByMedicalRecordId(medicalRecordId, orgId);
        if (chart == null) {
            return null;
        }
        chart.setDetails(getDetails(chart.getId()));
        return chart;
    }

    @Override
    @Transactional
    public DentalChartVO saveByMedicalRecordId(Long medicalRecordId, DentalChartSaveRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        MedicalRecordDetailVO recordDetail = validateMedicalRecordAccess(loginUser, medicalRecordId);
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        validateParticipants(orgId, request);
        if (!Objects.equals(recordDetail.getClinicId(), request.getClinicId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "牙位图门诊必须与病历门诊一致");
        }
        if (!Objects.equals(recordDetail.getPatientId(), request.getPatientId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "牙位图患者必须与病历患者一致");
        }

        DentalChartEntity chart = dentalChartMapper.selectOne(new LambdaQueryWrapper<DentalChartEntity>()
                .eq(DentalChartEntity::getMedicalRecordId, medicalRecordId)
                .eq(DentalChartEntity::getOrgId, orgId)
                .last("LIMIT 1"));
        Long currentUserId = currentUserId();
        if (chart == null) {
            chart = new DentalChartEntity();
            chart.setOrgId(orgId);
            chart.setMedicalRecordId(medicalRecordId);
            chart.setClinicId(request.getClinicId());
            chart.setPatientId(request.getPatientId());
            chart.setChartType(request.getChartType());
            chart.setChartVersion(1);
            chart.setChartStatus(request.getChartStatus());
            chart.setCreatedBy(currentUserId);
            chart.setUpdatedBy(currentUserId);
            dentalChartMapper.insert(chart);
        } else {
            chart.setClinicId(request.getClinicId());
            chart.setPatientId(request.getPatientId());
            chart.setChartType(request.getChartType());
            chart.setChartStatus(request.getChartStatus());
            chart.setChartVersion(chart.getChartVersion() == null ? 1 : chart.getChartVersion() + 1);
            chart.setUpdatedBy(currentUserId);
            dentalChartMapper.updateById(chart);
            dentalChartDetailMapper.delete(new LambdaQueryWrapper<DentalChartDetailEntity>()
                    .eq(DentalChartDetailEntity::getDentalChartId, chart.getId()));
        }

        saveDetails(chart.getId(), request.getDetails());
        DentalChartVO result = dentalChartMapper.selectByMedicalRecordId(medicalRecordId, orgId);
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "牙位图保存失败");
        }
        result.setDetails(getDetails(result.getId()));
        return result;
    }

    private void saveDetails(Long dentalChartId, List<DentalChartDetailSaveRequest> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        for (DentalChartDetailSaveRequest item : details) {
            DentalChartDetailEntity detail = new DentalChartDetailEntity();
            detail.setDentalChartId(dentalChartId);
            detail.setToothNo(item.getToothNo());
            detail.setToothSurface(item.getToothSurface());
            detail.setToothStatus(item.getToothStatus());
            detail.setDiagnosisFlag(item.getDiagnosisFlag() == null ? 0 : item.getDiagnosisFlag());
            detail.setTreatmentFlag(item.getTreatmentFlag() == null ? 0 : item.getTreatmentFlag());
            detail.setTreatmentItemId(item.getTreatmentItemId());
            detail.setNotes(item.getNotes());
            dentalChartDetailMapper.insert(detail);
        }
    }

    private List<DentalChartDetailVO> getDetails(Long dentalChartId) {
        List<DentalChartDetailVO> details = dentalChartDetailMapper.selectByDentalChartId(dentalChartId);
        return details == null ? Collections.emptyList() : details;
    }

    private MedicalRecordDetailVO validateMedicalRecordAccess(LoginUser loginUser, Long medicalRecordId) {
        MedicalRecordDetailVO recordDetail = medicalRecordMapper.selectMedicalRecordDetailById(
                medicalRecordId,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (recordDetail == null) {
            throw new BusinessException(ErrorCode.MEDICAL_RECORD_NOT_FOUND);
        }
        return recordDetail;
    }

    private void validateParticipants(Long orgId, DentalChartSaveRequest request) {
        Long clinicCount = medicalRecordMapper.countValidClinic(orgId, request.getClinicId());
        if (clinicCount == null || clinicCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊不存在或已停用");
        }
        Long patientCount = medicalRecordMapper.countValidPatient(orgId, request.getPatientId());
        if (patientCount == null || patientCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者不存在");
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

    private Long currentUserId() {
        LoginUser loginUser = AuthContext.get();
        return loginUser == null ? null : loginUser.getUserId();
    }
}
