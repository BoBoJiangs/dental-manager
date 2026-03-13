package com.company.dental.modules.patient.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.common.api.PageResult;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.common.util.BizNoGenerator;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.patient.dto.PatientCreateRequest;
import com.company.dental.modules.patient.dto.PatientProfileCreateRequest;
import com.company.dental.modules.patient.entity.PatientEntity;
import com.company.dental.modules.patient.entity.PatientProfileEntity;
import com.company.dental.modules.patient.mapper.PatientMapper;
import com.company.dental.modules.patient.mapper.PatientProfileMapper;
import com.company.dental.modules.patient.query.PatientPageQuery;
import com.company.dental.modules.patient.service.PatientService;
import com.company.dental.modules.patient.vo.PatientDetailVO;
import com.company.dental.modules.patient.vo.PatientPageItemVO;
import com.company.dental.modules.patient.vo.PatientProfileVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientMapper patientMapper;
    private final PatientProfileMapper patientProfileMapper;
    private final BizNoGenerator bizNoGenerator;

    public PatientServiceImpl(PatientMapper patientMapper,
                              PatientProfileMapper patientProfileMapper,
                              BizNoGenerator bizNoGenerator) {
        this.patientMapper = patientMapper;
        this.patientProfileMapper = patientProfileMapper;
        this.bizNoGenerator = bizNoGenerator;
    }

    @Override
    public PageResult<PatientPageItemVO> pagePatients(PatientPageQuery query) {
        LoginUser loginUser = AuthContext.get();
        Page<PatientEntity> page = patientMapper.selectPage(
                Page.of(query.getCurrent(), query.getSize()),
                buildWrapper(query, loginUser)
        );
        List<PatientPageItemVO> records = page.getRecords().stream()
                .map(this::toPageItem)
                .toList();
        return PageResult.<PatientPageItemVO>builder()
                .records(records)
                .total(page.getTotal())
                .current(page.getCurrent())
                .size(page.getSize())
                .build();
    }

    @Override
    public PatientDetailVO getPatientDetail(Long patientId) {
        LoginUser loginUser = AuthContext.get();
        PatientEntity patient = patientMapper.selectOne(new LambdaQueryWrapper<PatientEntity>()
                .eq(PatientEntity::getId, patientId)
                .eq(loginUser != null && loginUser.getOrgId() != null, PatientEntity::getOrgId, loginUser.getOrgId())
                .last("LIMIT 1"));
        if (patient == null) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        PatientProfileEntity profile = patientProfileMapper.selectOne(new LambdaQueryWrapper<PatientProfileEntity>()
                .eq(PatientProfileEntity::getPatientId, patientId)
                .last("LIMIT 1"));
        return buildDetail(patient, profile);
    }

    @Override
    @Transactional
    public PatientDetailVO createPatient(PatientCreateRequest request) {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        validateDuplicatePatient(orgId, request);

        LocalDateTime now = LocalDateTime.now();
        PatientEntity patient = new PatientEntity();
        patient.setOrgId(orgId);
        patient.setPatientCode(bizNoGenerator.next("P"));
        patient.setPatientName(request.getPatientName());
        patient.setGender(request.getGender());
        patient.setBirthday(request.getBirthday());
        patient.setMobile(request.getMobile());
        patient.setIdNo(request.getIdNo());
        patient.setSourceCode(request.getSourceCode());
        patient.setFirstClinicId(request.getFirstClinicId());
        patient.setFirstVisitAt(now);
        patient.setLatestVisitAt(now);
        patient.setMemberStatus(request.getMemberStatus() == null ? 0 : request.getMemberStatus());
        patient.setPatientStatus(request.getPatientStatus() == null ? 1 : request.getPatientStatus());
        patient.setRemark(request.getRemark());
        patientMapper.insert(patient);

        PatientProfileEntity profile = null;
        if (request.getProfile() != null) {
            profile = buildProfileEntity(patient.getId(), request.getProfile());
            patientProfileMapper.insert(profile);
        }
        return buildDetail(patient, profile);
    }

    private void validateDuplicatePatient(Long orgId, PatientCreateRequest request) {
        Long mobileCount = patientMapper.selectCount(new LambdaQueryWrapper<PatientEntity>()
                .eq(PatientEntity::getOrgId, orgId)
                .eq(PatientEntity::getMobile, request.getMobile())
                .last("LIMIT 1"));
        if (mobileCount != null && mobileCount > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "手机号已存在患者档案");
        }
        if (StringUtils.hasText(request.getIdNo())) {
            Long idNoCount = patientMapper.selectCount(new LambdaQueryWrapper<PatientEntity>()
                    .eq(PatientEntity::getOrgId, orgId)
                    .eq(PatientEntity::getIdNo, request.getIdNo())
                    .last("LIMIT 1"));
            if (idNoCount != null && idNoCount > 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "证件号已存在患者档案");
            }
        }
    }

    private PatientProfileEntity buildProfileEntity(Long patientId, PatientProfileCreateRequest request) {
        PatientProfileEntity profile = new PatientProfileEntity();
        profile.setPatientId(patientId);
        profile.setBloodType(request.getBloodType());
        profile.setAllergyHistory(request.getAllergyHistory());
        profile.setPastHistory(request.getPastHistory());
        profile.setFamilyHistory(request.getFamilyHistory());
        profile.setPregnancyStatus(request.getPregnancyStatus());
        profile.setSmokingStatus(request.getSmokingStatus());
        profile.setDrinkingStatus(request.getDrinkingStatus());
        profile.setAddress(request.getAddress());
        profile.setEmergencyContact(request.getEmergencyContact());
        profile.setEmergencyPhone(request.getEmergencyPhone());
        profile.setRemark(request.getRemark());
        return profile;
    }

    private LambdaQueryWrapper<PatientEntity> buildWrapper(PatientPageQuery query, LoginUser loginUser) {
        LambdaQueryWrapper<PatientEntity> wrapper = new LambdaQueryWrapper<PatientEntity>()
                .eq(loginUser != null && loginUser.getOrgId() != null, PatientEntity::getOrgId, loginUser.getOrgId())
                .eq(query.getPatientStatus() != null, PatientEntity::getPatientStatus, query.getPatientStatus())
                .like(StringUtils.hasText(query.getMobile()), PatientEntity::getMobile, query.getMobile())
                .and(StringUtils.hasText(query.getKeyword()), nested -> nested
                        .like(PatientEntity::getPatientName, query.getKeyword())
                        .or()
                        .like(PatientEntity::getPatientCode, query.getKeyword())
                        .or()
                        .like(PatientEntity::getMobile, query.getKeyword()))
                .orderByDesc(PatientEntity::getUpdatedAt, PatientEntity::getId);
        return wrapper;
    }

    private PatientPageItemVO toPageItem(PatientEntity entity) {
        return PatientPageItemVO.builder()
                .id(entity.getId())
                .orgId(entity.getOrgId())
                .patientCode(entity.getPatientCode())
                .patientName(entity.getPatientName())
                .gender(entity.getGender())
                .birthday(entity.getBirthday())
                .mobile(entity.getMobile())
                .memberStatus(entity.getMemberStatus())
                .patientStatus(entity.getPatientStatus())
                .firstClinicId(entity.getFirstClinicId())
                .firstVisitAt(entity.getFirstVisitAt())
                .latestVisitAt(entity.getLatestVisitAt())
                .remark(entity.getRemark())
                .build();
    }

    private PatientDetailVO buildDetail(PatientEntity patient, PatientProfileEntity profile) {
        return PatientDetailVO.builder()
                .id(patient.getId())
                .orgId(patient.getOrgId())
                .patientCode(patient.getPatientCode())
                .patientName(patient.getPatientName())
                .gender(patient.getGender())
                .birthday(patient.getBirthday())
                .mobile(patient.getMobile())
                .idNo(patient.getIdNo())
                .sourceCode(patient.getSourceCode())
                .firstClinicId(patient.getFirstClinicId())
                .firstVisitAt(patient.getFirstVisitAt())
                .latestVisitAt(patient.getLatestVisitAt())
                .memberStatus(patient.getMemberStatus())
                .patientStatus(patient.getPatientStatus())
                .remark(patient.getRemark())
                .profile(toProfile(profile))
                .build();
    }

    private PatientProfileVO toProfile(PatientProfileEntity profile) {
        if (profile == null) {
            return null;
        }
        return PatientProfileVO.builder()
                .bloodType(profile.getBloodType())
                .allergyHistory(profile.getAllergyHistory())
                .pastHistory(profile.getPastHistory())
                .familyHistory(profile.getFamilyHistory())
                .pregnancyStatus(profile.getPregnancyStatus())
                .smokingStatus(profile.getSmokingStatus())
                .drinkingStatus(profile.getDrinkingStatus())
                .address(profile.getAddress())
                .emergencyContact(profile.getEmergencyContact())
                .emergencyPhone(profile.getEmergencyPhone())
                .remark(profile.getRemark())
                .updatedBy(profile.getUpdatedBy())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
