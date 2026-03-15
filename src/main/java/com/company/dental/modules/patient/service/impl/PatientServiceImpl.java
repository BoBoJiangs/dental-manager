package com.company.dental.modules.patient.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.common.api.PageResult;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.common.util.BizNoGenerator;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.DataScopeType;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.appointment.entity.AppointmentEntity;
import com.company.dental.modules.appointment.mapper.AppointmentMapper;
import com.company.dental.modules.billing.entity.ChargeOrderEntity;
import com.company.dental.modules.billing.mapper.ChargeOrderMapper;
import com.company.dental.modules.emr.entity.ConsentFormRecordEntity;
import com.company.dental.modules.emr.entity.ElectronicSignatureEntity;
import com.company.dental.modules.emr.entity.MedicalRecordEntity;
import com.company.dental.modules.emr.mapper.ConsentFormRecordMapper;
import com.company.dental.modules.emr.mapper.ElectronicSignatureMapper;
import com.company.dental.modules.emr.mapper.MedicalRecordMapper;
import com.company.dental.modules.emr.mapper.TreatmentPlanMapper;
import com.company.dental.modules.imaging.entity.PatientImageEntity;
import com.company.dental.modules.imaging.entity.PhotoCompareGroupEntity;
import com.company.dental.modules.imaging.query.PatientImageQuery;
import com.company.dental.modules.imaging.mapper.PatientImageMapper;
import com.company.dental.modules.imaging.mapper.PhotoCompareGroupMapper;
import com.company.dental.modules.imaging.vo.PatientImageVO;
import com.company.dental.modules.member.entity.MemberAccountEntity;
import com.company.dental.modules.member.mapper.MemberAccountMapper;
import com.company.dental.modules.member.mapper.MemberAccountQueryMapper;
import com.company.dental.modules.patient.dto.PatientCreateRequest;
import com.company.dental.modules.patient.dto.PatientPrimaryDoctorUpdateRequest;
import com.company.dental.modules.patient.dto.PatientProfileCreateRequest;
import com.company.dental.modules.patient.dto.PatientStatusUpdateRequest;
import com.company.dental.modules.patient.dto.PatientTagUpdateRequest;
import com.company.dental.modules.patient.dto.PatientUpdateRequest;
import com.company.dental.modules.patient.entity.PatientDoctorRelEntity;
import com.company.dental.modules.patient.entity.PatientEntity;
import com.company.dental.modules.patient.entity.PatientProfileEntity;
import com.company.dental.modules.patient.entity.PatientTagEntity;
import com.company.dental.modules.patient.entity.PatientTagRelEntity;
import com.company.dental.modules.patient.mapper.PatientDoctorRelMapper;
import com.company.dental.modules.patient.mapper.PatientMapper;
import com.company.dental.modules.patient.mapper.PatientProfileMapper;
import com.company.dental.modules.patient.mapper.PatientTagMapper;
import com.company.dental.modules.patient.mapper.PatientTagRelMapper;
import com.company.dental.modules.patient.query.PatientPageQuery;
import com.company.dental.modules.patient.service.PatientService;
import com.company.dental.modules.patient.vo.PatientDetailVO;
import com.company.dental.modules.patient.vo.PatientDoctorVO;
import com.company.dental.modules.patient.vo.PatientMemberInfoVO;
import com.company.dental.modules.patient.vo.PatientPageItemVO;
import com.company.dental.modules.patient.vo.PatientProfileVO;
import com.company.dental.modules.patient.vo.PatientTagVO;
import com.company.dental.modules.patient.vo.PatientVisitRecordVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class PatientServiceImpl implements PatientService {

    private static final String PRIMARY_RELATION_TYPE = "PRIMARY";

    private final PatientMapper patientMapper;
    private final PatientProfileMapper patientProfileMapper;
    private final PatientDoctorRelMapper patientDoctorRelMapper;
    private final PatientTagMapper patientTagMapper;
    private final PatientTagRelMapper patientTagRelMapper;
    private final AppointmentMapper appointmentMapper;
    private final MedicalRecordMapper medicalRecordMapper;
    private final TreatmentPlanMapper treatmentPlanMapper;
    private final PatientImageMapper patientImageMapper;
    private final PhotoCompareGroupMapper photoCompareGroupMapper;
    private final MemberAccountMapper memberAccountMapper;
    private final MemberAccountQueryMapper memberAccountQueryMapper;
    private final ChargeOrderMapper chargeOrderMapper;
    private final ElectronicSignatureMapper electronicSignatureMapper;
    private final ConsentFormRecordMapper consentFormRecordMapper;
    private final DataScopeHelper dataScopeHelper;
    private final BizNoGenerator bizNoGenerator;

    public PatientServiceImpl(PatientMapper patientMapper,
                              PatientProfileMapper patientProfileMapper,
                              PatientDoctorRelMapper patientDoctorRelMapper,
                              PatientTagMapper patientTagMapper,
                              PatientTagRelMapper patientTagRelMapper,
                              AppointmentMapper appointmentMapper,
                              MedicalRecordMapper medicalRecordMapper,
                              TreatmentPlanMapper treatmentPlanMapper,
                              PatientImageMapper patientImageMapper,
                              PhotoCompareGroupMapper photoCompareGroupMapper,
                              MemberAccountMapper memberAccountMapper,
                              MemberAccountQueryMapper memberAccountQueryMapper,
                              ChargeOrderMapper chargeOrderMapper,
                              ElectronicSignatureMapper electronicSignatureMapper,
                              ConsentFormRecordMapper consentFormRecordMapper,
                              DataScopeHelper dataScopeHelper,
                              BizNoGenerator bizNoGenerator) {
        this.patientMapper = patientMapper;
        this.patientProfileMapper = patientProfileMapper;
        this.patientDoctorRelMapper = patientDoctorRelMapper;
        this.patientTagMapper = patientTagMapper;
        this.patientTagRelMapper = patientTagRelMapper;
        this.appointmentMapper = appointmentMapper;
        this.medicalRecordMapper = medicalRecordMapper;
        this.treatmentPlanMapper = treatmentPlanMapper;
        this.patientImageMapper = patientImageMapper;
        this.photoCompareGroupMapper = photoCompareGroupMapper;
        this.memberAccountMapper = memberAccountMapper;
        this.memberAccountQueryMapper = memberAccountQueryMapper;
        this.chargeOrderMapper = chargeOrderMapper;
        this.electronicSignatureMapper = electronicSignatureMapper;
        this.consentFormRecordMapper = consentFormRecordMapper;
        this.dataScopeHelper = dataScopeHelper;
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
        PatientEntity patient = getPatientOrThrow(patientId, loginUser == null ? null : loginUser.getOrgId());
        PatientProfileEntity profile = getPatientProfile(patientId);
        return buildDetail(patient, profile, getPatientTags(patientId), getPrimaryDoctors(patientId));
    }

    @Override
    @Transactional
    public PatientDetailVO createPatient(PatientCreateRequest request) {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        dataScopeHelper.assertClinicAccess(loginUser, request.getFirstClinicId());
        Long operatorId = loginUser.getUserId();
        validateDuplicatePatient(orgId, request.getMobile(), request.getIdNo(), null);

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

        if (request.getProfile() != null) {
            PatientProfileEntity profile = buildProfileEntity(patient.getId(), request.getProfile(), operatorId);
            patientProfileMapper.insert(profile);
        }

        replacePatientTags(patient.getId(), orgId, request.getTagIds());
        return buildDetail(
                patient,
                getPatientProfile(patient.getId()),
                getPatientTags(patient.getId()),
                getPrimaryDoctors(patient.getId())
        );
    }

    @Override
    @Transactional
    public PatientDetailVO updatePatient(Long patientId, PatientUpdateRequest request) {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        dataScopeHelper.assertClinicAccess(loginUser, request.getFirstClinicId());

        PatientEntity patient = getPatientOrThrow(patientId, orgId);
        validateDuplicatePatient(orgId, request.getMobile(), request.getIdNo(), patientId);

        patient.setPatientName(request.getPatientName());
        patient.setGender(request.getGender());
        patient.setBirthday(request.getBirthday());
        patient.setMobile(request.getMobile());
        patient.setIdNo(request.getIdNo());
        patient.setSourceCode(request.getSourceCode());
        patient.setFirstClinicId(request.getFirstClinicId());
        patient.setMemberStatus(request.getMemberStatus() == null ? patient.getMemberStatus() : request.getMemberStatus());
        patient.setPatientStatus(request.getPatientStatus() == null ? patient.getPatientStatus() : request.getPatientStatus());
        patient.setRemark(request.getRemark());
        patientMapper.updateById(patient);

        if (request.getProfile() != null) {
            saveOrUpdateProfile(patientId, request.getProfile(), loginUser.getUserId());
        }

        if (request.getTagIds() != null) {
            replacePatientTags(patientId, orgId, request.getTagIds());
        }

        return buildDetail(
                patient,
                getPatientProfile(patientId),
                getPatientTags(patientId),
                getPrimaryDoctors(patientId)
        );
    }

    @Override
    public List<PatientTagVO> listTagOptions() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return patientTagMapper.selectList(new LambdaQueryWrapper<PatientTagEntity>()
                        .eq(PatientTagEntity::getOrgId, orgId)
                        .eq(PatientTagEntity::getStatus, 1)
                        .orderByAsc(PatientTagEntity::getId))
                .stream()
                .map(this::toTagVO)
                .toList();
    }

    @Override
    @Transactional
    public PatientDetailVO updatePatientTags(Long patientId, PatientTagUpdateRequest request) {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        PatientEntity patient = getPatientOrThrow(patientId, orgId);
        replacePatientTags(patientId, orgId, request.getTagIds());
        return buildDetail(
                patient,
                getPatientProfile(patientId),
                getPatientTags(patientId),
                getPrimaryDoctors(patientId)
        );
    }

    @Override
    public List<PatientDoctorVO> listDoctorOptions() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        List<PatientDoctorVO> doctors = patientDoctorRelMapper.selectDoctorOptionsByOrgId(orgId);
        DataScopeType scopeType = dataScopeHelper.resolve(loginUser);
        if (scopeType == DataScopeType.ALL) {
            return doctors;
        }
        if (scopeType == DataScopeType.CLINIC) {
            List<Long> clinicIds = dataScopeHelper.resolveClinicIds(loginUser);
            if (clinicIds.isEmpty()) {
                return List.of();
            }
            return doctors.stream()
                    .filter(item -> clinicIds.contains(item.getClinicId()))
                    .toList();
        }
        Long staffId = loginUser.getStaffId();
        if (staffId == null) {
            return List.of();
        }
        return doctors.stream()
                .filter(item -> staffId.equals(item.getDoctorId()))
                .toList();
    }

    @Override
    @Transactional
    public PatientDetailVO updatePrimaryDoctors(Long patientId, PatientPrimaryDoctorUpdateRequest request) {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        PatientEntity patient = getPatientOrThrow(patientId, orgId);
        replacePrimaryDoctors(patientId, orgId, request.getDoctorIds(), loginUser);
        return buildDetail(
                patient,
                getPatientProfile(patientId),
                getPatientTags(patientId),
                getPrimaryDoctors(patientId)
        );
    }

    @Override
    public List<PatientVisitRecordVO> listVisitRecords(Long patientId) {
        Long orgId = currentOrgId();
        getPatientOrThrow(patientId, orgId);
        List<PatientVisitRecordVO> records = medicalRecordMapper.selectPatientVisitRecords(patientId, orgId);
        return records == null ? Collections.emptyList() : records;
    }

    @Override
    public List<PatientImageVO> listPatientImages(Long patientId) {
        LoginUser loginUser = AuthContext.get();
        Long orgId = currentOrgId();
        getPatientOrThrow(patientId, orgId);
        PatientImageQuery query = new PatientImageQuery();
        query.setPatientId(patientId);
        List<PatientImageVO> images = patientImageMapper.selectPatientImages(
                query,
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser == null ? null : loginUser.getStaffId()
        );
        return images == null ? Collections.emptyList() : images;
    }

    @Override
    public PatientMemberInfoVO getPatientMemberInfo(Long patientId) {
        Long orgId = currentOrgId();
        getPatientOrThrow(patientId, orgId);
        PatientMemberInfoVO memberInfo = memberAccountQueryMapper.selectPatientMemberInfo(orgId, patientId);
        if (memberInfo == null) {
            return null;
        }
        memberInfo.setRecentBalanceRecords(memberAccountQueryMapper.selectRecentBalanceRecords(patientId));
        memberInfo.setRecentPointsRecords(memberAccountQueryMapper.selectRecentPointsRecords(patientId));
        return memberInfo;
    }

    @Override
    @Transactional
    public PatientDetailVO updatePatientStatus(Long patientId, PatientStatusUpdateRequest request) {
        Long orgId = currentOrgId();
        PatientEntity patient = getPatientOrThrow(patientId, orgId);
        patient.setPatientStatus(request.getPatientStatus());
        if (request.getRemark() != null) {
            patient.setRemark(request.getRemark());
        }
        patientMapper.updateById(patient);
        return buildDetail(
                patient,
                getPatientProfile(patientId),
                getPatientTags(patientId),
                getPrimaryDoctors(patientId)
        );
    }

    @Override
    @Transactional
    public void deletePatient(Long patientId) {
        Long orgId = currentOrgId();
        PatientEntity patient = getPatientOrThrow(patientId, orgId);
        ensureDeleteAllowed(patientId, orgId);
        patientMapper.deleteById(patient.getId());
    }

    private void ensureDeleteAllowed(Long patientId, Long orgId) {
        if (memberAccountMapper.selectCount(new LambdaQueryWrapper<MemberAccountEntity>()
                .eq(MemberAccountEntity::getOrgId, orgId)
                .eq(MemberAccountEntity::getPatientId, patientId)) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者已关联会员账户，不能删除");
        }
        if (appointmentMapper.selectCount(new LambdaQueryWrapper<AppointmentEntity>()
                .eq(AppointmentEntity::getOrgId, orgId)
                .eq(AppointmentEntity::getPatientId, patientId)
                .eq(AppointmentEntity::getIsDeleted, 0)) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者已关联预约记录，不能删除");
        }
        if (medicalRecordMapper.selectCount(new LambdaQueryWrapper<MedicalRecordEntity>()
                .eq(MedicalRecordEntity::getOrgId, orgId)
                .eq(MedicalRecordEntity::getPatientId, patientId)
                .eq(MedicalRecordEntity::getIsDeleted, 0)) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者已关联病历记录，不能删除");
        }
        if (treatmentPlanMapper.selectCount(new LambdaQueryWrapper<com.company.dental.modules.emr.entity.TreatmentPlanEntity>()
                .eq(com.company.dental.modules.emr.entity.TreatmentPlanEntity::getOrgId, orgId)
                .eq(com.company.dental.modules.emr.entity.TreatmentPlanEntity::getPatientId, patientId)
                .eq(com.company.dental.modules.emr.entity.TreatmentPlanEntity::getIsDeleted, 0)) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者已关联治疗计划，不能删除");
        }
        if (chargeOrderMapper.selectCount(new LambdaQueryWrapper<ChargeOrderEntity>()
                .eq(ChargeOrderEntity::getOrgId, orgId)
                .eq(ChargeOrderEntity::getPatientId, patientId)
                .eq(ChargeOrderEntity::getIsDeleted, 0)) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者已关联收费记录，不能删除");
        }
        if (patientImageMapper.selectCount(new LambdaQueryWrapper<PatientImageEntity>()
                .eq(PatientImageEntity::getOrgId, orgId)
                .eq(PatientImageEntity::getPatientId, patientId)) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者已关联影像资料，不能删除");
        }
        if (photoCompareGroupMapper.selectCount(new LambdaQueryWrapper<PhotoCompareGroupEntity>()
                .eq(PhotoCompareGroupEntity::getPatientId, patientId)) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者已关联术前术后对比资料，不能删除");
        }
        if (electronicSignatureMapper.selectCount(new LambdaQueryWrapper<ElectronicSignatureEntity>()
                .eq(ElectronicSignatureEntity::getOrgId, orgId)
                .eq(ElectronicSignatureEntity::getPatientId, patientId)) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者已关联电子签名记录，不能删除");
        }
        if (consentFormRecordMapper.selectCount(new LambdaQueryWrapper<ConsentFormRecordEntity>()
                .eq(ConsentFormRecordEntity::getOrgId, orgId)
                .eq(ConsentFormRecordEntity::getPatientId, patientId)) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "患者已关联知情同意书，不能删除");
        }
    }

    private void validateDuplicatePatient(Long orgId, String mobile, String idNo, Long excludePatientId) {
        Long mobileCount = patientMapper.selectCount(new LambdaQueryWrapper<PatientEntity>()
                .eq(PatientEntity::getOrgId, orgId)
                .eq(PatientEntity::getMobile, mobile)
                .ne(excludePatientId != null, PatientEntity::getId, excludePatientId)
                .last("LIMIT 1"));
        if (mobileCount != null && mobileCount > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "手机号已存在患者档案");
        }
        if (StringUtils.hasText(idNo)) {
            Long idNoCount = patientMapper.selectCount(new LambdaQueryWrapper<PatientEntity>()
                    .eq(PatientEntity::getOrgId, orgId)
                    .eq(PatientEntity::getIdNo, idNo)
                    .ne(excludePatientId != null, PatientEntity::getId, excludePatientId)
                    .last("LIMIT 1"));
            if (idNoCount != null && idNoCount > 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "证件号已存在患者档案");
            }
        }
    }

    private PatientProfileEntity buildProfileEntity(Long patientId, PatientProfileCreateRequest request, Long operatorId) {
        PatientProfileEntity profile = new PatientProfileEntity();
        profile.setPatientId(patientId);
        fillProfile(profile, request, operatorId);
        return profile;
    }

    private void fillProfile(PatientProfileEntity profile, PatientProfileCreateRequest request, Long operatorId) {
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
        profile.setUpdatedBy(operatorId);
    }

    private void saveOrUpdateProfile(Long patientId, PatientProfileCreateRequest request, Long operatorId) {
        PatientProfileEntity existingProfile = getPatientProfile(patientId);
        if (existingProfile == null) {
            patientProfileMapper.insert(buildProfileEntity(patientId, request, operatorId));
            return;
        }
        fillProfile(existingProfile, request, operatorId);
        patientProfileMapper.updateById(existingProfile);
    }

    private PatientEntity getPatientOrThrow(Long patientId, Long orgId) {
        LoginUser loginUser = AuthContext.get();
        LambdaQueryWrapper<PatientEntity> wrapper = new LambdaQueryWrapper<PatientEntity>()
                .eq(PatientEntity::getId, patientId)
                .eq(orgId != null, PatientEntity::getOrgId, orgId);
        applyPatientDataScope(wrapper, loginUser);
        PatientEntity patient = patientMapper.selectOne(wrapper.last("LIMIT 1"));
        if (patient == null) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        return patient;
    }

    private PatientProfileEntity getPatientProfile(Long patientId) {
        return patientProfileMapper.selectOne(new LambdaQueryWrapper<PatientProfileEntity>()
                .eq(PatientProfileEntity::getPatientId, patientId)
                .last("LIMIT 1"));
    }

    private Long currentOrgId() {
        LoginUser loginUser = AuthContext.get();
        Long orgId = loginUser == null ? null : loginUser.getOrgId();
        if (orgId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return orgId;
    }

    private void replacePatientTags(Long patientId, Long orgId, List<Long> tagIds) {
        if (tagIds == null) {
            return;
        }

        List<Long> normalizedTagIds = tagIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        validateTagIds(orgId, normalizedTagIds);

        patientTagRelMapper.delete(new LambdaQueryWrapper<PatientTagRelEntity>()
                .eq(PatientTagRelEntity::getPatientId, patientId));
        if (normalizedTagIds.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (Long tagId : normalizedTagIds) {
            PatientTagRelEntity relation = new PatientTagRelEntity();
            relation.setPatientId(patientId);
            relation.setTagId(tagId);
            relation.setCreatedAt(now);
            patientTagRelMapper.insert(relation);
        }
    }

    private void validateTagIds(Long orgId, List<Long> tagIds) {
        if (tagIds.isEmpty()) {
            return;
        }
        List<PatientTagEntity> tags = patientTagMapper.selectList(new LambdaQueryWrapper<PatientTagEntity>()
                .eq(PatientTagEntity::getOrgId, orgId)
                .eq(PatientTagEntity::getStatus, 1)
                .in(PatientTagEntity::getId, tagIds));
        if (tags.size() != tagIds.size()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "存在无效的患者标签");
        }
    }

    private List<PatientTagVO> getPatientTags(Long patientId) {
        List<PatientTagEntity> tags = patientTagMapper.selectByPatientId(patientId);
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }
        return tags.stream()
                .map(this::toTagVO)
                .toList();
    }

    private void replacePrimaryDoctors(Long patientId, Long orgId, List<Long> doctorIds, LoginUser loginUser) {
        if (doctorIds == null) {
            return;
        }
        List<Long> normalizedDoctorIds = doctorIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<PatientDoctorVO> validDoctors = validateAndGetDoctors(orgId, normalizedDoctorIds, loginUser);

        patientDoctorRelMapper.delete(new LambdaQueryWrapper<PatientDoctorRelEntity>()
                .eq(PatientDoctorRelEntity::getPatientId, patientId)
                .eq(PatientDoctorRelEntity::getRelationType, PRIMARY_RELATION_TYPE));
        if (validDoctors.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (PatientDoctorVO doctor : validDoctors) {
            PatientDoctorRelEntity relation = new PatientDoctorRelEntity();
            relation.setOrgId(orgId);
            relation.setPatientId(patientId);
            relation.setDoctorId(doctor.getDoctorId());
            relation.setClinicId(doctor.getClinicId());
            relation.setRelationType(PRIMARY_RELATION_TYPE);
            relation.setFirstRelatedAt(now);
            relation.setLatestRelatedAt(now);
            relation.setStatus(1);
            relation.setCreatedAt(now);
            relation.setUpdatedAt(now);
            patientDoctorRelMapper.insert(relation);
        }
    }

    private List<PatientDoctorVO> validateAndGetDoctors(Long orgId, List<Long> doctorIds, LoginUser loginUser) {
        if (doctorIds == null || doctorIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<PatientDoctorVO> validDoctors = listDoctorOptions().stream()
                .filter(item -> doctorIds.contains(item.getDoctorId()))
                .toList();
        if (validDoctors.size() != doctorIds.size()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "存在无效的主治医生");
        }
        return validDoctors;
    }

    private List<PatientDoctorVO> getPrimaryDoctors(Long patientId) {
        List<PatientDoctorVO> doctors = patientDoctorRelMapper.selectPrimaryDoctorsByPatientId(patientId);
        if (doctors == null || doctors.isEmpty()) {
            return Collections.emptyList();
        }
        return doctors;
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
        applyPatientDataScope(wrapper, loginUser);
        return wrapper;
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

    private PatientDetailVO buildDetail(PatientEntity patient,
                                        PatientProfileEntity profile,
                                        List<PatientTagVO> tags,
                                        List<PatientDoctorVO> primaryDoctors) {
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
                .tags(tags == null ? Collections.emptyList() : tags)
                .primaryDoctors(primaryDoctors == null ? Collections.emptyList() : primaryDoctors)
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

    private PatientTagVO toTagVO(PatientTagEntity entity) {
        return PatientTagVO.builder()
                .id(entity.getId())
                .tagCode(entity.getTagCode())
                .tagName(entity.getTagName())
                .tagColor(entity.getTagColor())
                .build();
    }
}
