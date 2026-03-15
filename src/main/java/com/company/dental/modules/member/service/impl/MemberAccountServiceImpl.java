package com.company.dental.modules.member.service.impl;

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
import com.company.dental.modules.member.dto.MemberAccountCreateRequest;
import com.company.dental.modules.member.dto.MemberPointsAdjustRequest;
import com.company.dental.modules.member.dto.MemberRechargeRequest;
import com.company.dental.modules.member.dto.MemberStatusUpdateRequest;
import com.company.dental.modules.member.entity.MemberAccountEntity;
import com.company.dental.modules.member.entity.MemberBalanceRecordEntity;
import com.company.dental.modules.member.entity.MemberLevelEntity;
import com.company.dental.modules.member.entity.MemberPointsRecordEntity;
import com.company.dental.modules.member.mapper.MemberAccountMapper;
import com.company.dental.modules.member.mapper.MemberAccountQueryMapper;
import com.company.dental.modules.member.mapper.MemberBalanceRecordMapper;
import com.company.dental.modules.member.mapper.MemberLevelMapper;
import com.company.dental.modules.member.mapper.MemberPointsRecordMapper;
import com.company.dental.modules.member.query.MemberAccountPageQuery;
import com.company.dental.modules.member.service.MemberAccountService;
import com.company.dental.modules.member.vo.MemberAccountDetailVO;
import com.company.dental.modules.member.vo.MemberAccountPageItemVO;
import com.company.dental.modules.member.vo.MemberLevelOptionVO;
import com.company.dental.modules.patient.entity.PatientEntity;
import com.company.dental.modules.patient.mapper.PatientMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberAccountServiceImpl implements MemberAccountService {

    private static final String STATUS_NORMAL = "NORMAL";

    private final MemberAccountMapper memberAccountMapper;
    private final MemberLevelMapper memberLevelMapper;
    private final MemberAccountQueryMapper memberAccountQueryMapper;
    private final MemberBalanceRecordMapper memberBalanceRecordMapper;
    private final MemberPointsRecordMapper memberPointsRecordMapper;
    private final PatientMapper patientMapper;
    private final DataScopeHelper dataScopeHelper;
    private final BizNoGenerator bizNoGenerator;

    public MemberAccountServiceImpl(MemberAccountMapper memberAccountMapper,
                                    MemberLevelMapper memberLevelMapper,
                                    MemberAccountQueryMapper memberAccountQueryMapper,
                                    MemberBalanceRecordMapper memberBalanceRecordMapper,
                                    MemberPointsRecordMapper memberPointsRecordMapper,
                                    PatientMapper patientMapper,
                                    DataScopeHelper dataScopeHelper,
                                    BizNoGenerator bizNoGenerator) {
        this.memberAccountMapper = memberAccountMapper;
        this.memberLevelMapper = memberLevelMapper;
        this.memberAccountQueryMapper = memberAccountQueryMapper;
        this.memberBalanceRecordMapper = memberBalanceRecordMapper;
        this.memberPointsRecordMapper = memberPointsRecordMapper;
        this.patientMapper = patientMapper;
        this.dataScopeHelper = dataScopeHelper;
        this.bizNoGenerator = bizNoGenerator;
    }

    @Override
    public PageResult<MemberAccountPageItemVO> pageMemberAccounts(MemberAccountPageQuery query) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        Page<MemberAccountPageItemVO> page = memberAccountMapper.selectMemberAccountPage(
                Page.of(query.getCurrent(), query.getSize()),
                query,
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        return PageResult.<MemberAccountPageItemVO>builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .current(page.getCurrent())
                .size(page.getSize())
                .build();
    }

    @Override
    public MemberAccountDetailVO getMemberAccountDetail(Long memberAccountId) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        MemberAccountDetailVO detail = memberAccountMapper.selectMemberAccountDetailById(
                memberAccountId,
                orgId,
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会员账户不存在");
        }
        detail.setRecentBalanceRecords(memberAccountQueryMapper.selectRecentBalanceRecords(detail.getPatientId()));
        detail.setRecentPointsRecords(memberAccountQueryMapper.selectRecentPointsRecords(detail.getPatientId()));
        return detail;
    }

    @Override
    @Transactional
    public MemberAccountDetailVO createMemberAccount(MemberAccountCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        PatientEntity patient = patientMapper.selectOne(new LambdaQueryWrapper<PatientEntity>()
                .eq(PatientEntity::getId, request.getPatientId())
                .eq(PatientEntity::getOrgId, orgId)
                .eq(PatientEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (patient == null) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        assertPatientAccess(loginUser, orgId, request.getPatientId());
        MemberAccountEntity existing = memberAccountMapper.selectOne(new LambdaQueryWrapper<MemberAccountEntity>()
                .eq(MemberAccountEntity::getOrgId, orgId)
                .eq(MemberAccountEntity::getPatientId, request.getPatientId())
                .last("LIMIT 1"));
        if (existing != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该患者已开通会员账户");
        }
        if (request.getLevelId() != null) {
            Long levelCount = memberAccountMapper.countValidLevel(orgId, request.getLevelId());
            if (levelCount == null || levelCount == 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "会员等级不存在");
            }
        }

        MemberAccountEntity memberAccount = new MemberAccountEntity();
        memberAccount.setOrgId(orgId);
        memberAccount.setPatientId(request.getPatientId());
        memberAccount.setMemberNo(bizNoGenerator.next("M"));
        memberAccount.setLevelId(request.getLevelId());
        memberAccount.setBalanceAmount(BigDecimal.ZERO);
        memberAccount.setPointsBalance(0);
        memberAccount.setTotalRechargeAmount(BigDecimal.ZERO);
        memberAccount.setTotalConsumeAmount(BigDecimal.ZERO);
        memberAccount.setMemberStatus(isBlank(request.getMemberStatus()) ? STATUS_NORMAL : request.getMemberStatus());
        memberAccount.setActivatedAt(request.getActivatedAt() == null ? LocalDateTime.now() : request.getActivatedAt());
        memberAccount.setExpiredAt(request.getExpiredAt());
        memberAccountMapper.insert(memberAccount);

        patient.setMemberStatus(1);
        patientMapper.updateById(patient);

        return getMemberAccountDetail(memberAccount.getId());
    }

    @Override
    @Transactional
    public MemberAccountDetailVO rechargeMemberAccount(Long memberAccountId, MemberRechargeRequest request) {
        LoginUser loginUser = currentLoginUser();
        MemberAccountEntity memberAccount = getMemberAccountEntityOrThrow(memberAccountId, loginUser);
        BigDecimal beforeBalance = defaultZero(memberAccount.getBalanceAmount());
        BigDecimal afterBalance = beforeBalance.add(request.getAmount());
        memberAccount.setBalanceAmount(afterBalance);
        memberAccount.setTotalRechargeAmount(defaultZero(memberAccount.getTotalRechargeAmount()).add(request.getAmount()));
        memberAccountMapper.updateById(memberAccount);

        MemberBalanceRecordEntity record = new MemberBalanceRecordEntity();
        record.setMemberAccountId(memberAccount.getId());
        record.setPatientId(memberAccount.getPatientId());
        record.setBizType(isBlank(request.getBizType()) ? "RECHARGE" : request.getBizType());
        record.setBizId(request.getBizId());
        record.setChangeAmount(request.getAmount());
        record.setBeforeBalance(beforeBalance);
        record.setAfterBalance(afterBalance);
        record.setOperatorId(loginUser.getUserId());
        record.setRemark(request.getRemark());
        memberBalanceRecordMapper.insert(record);
        return getMemberAccountDetail(memberAccountId);
    }

    @Override
    @Transactional
    public MemberAccountDetailVO adjustMemberPoints(Long memberAccountId, MemberPointsAdjustRequest request) {
        LoginUser loginUser = currentLoginUser();
        MemberAccountEntity memberAccount = getMemberAccountEntityOrThrow(memberAccountId, loginUser);
        int beforePoints = defaultZero(memberAccount.getPointsBalance());
        int afterPoints = beforePoints + request.getChangePoints();
        if (afterPoints < 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "积分不足，无法扣减");
        }
        memberAccount.setPointsBalance(afterPoints);
        memberAccountMapper.updateById(memberAccount);

        MemberPointsRecordEntity record = new MemberPointsRecordEntity();
        record.setMemberAccountId(memberAccount.getId());
        record.setPatientId(memberAccount.getPatientId());
        record.setBizType(isBlank(request.getBizType()) ? (request.getChangePoints() >= 0 ? "EARN" : "ADJUST") : request.getBizType());
        record.setBizId(request.getBizId());
        record.setChangePoints(request.getChangePoints());
        record.setBeforePoints(beforePoints);
        record.setAfterPoints(afterPoints);
        record.setOperatorId(loginUser.getUserId());
        record.setRemark(request.getRemark());
        memberPointsRecordMapper.insert(record);
        return getMemberAccountDetail(memberAccountId);
    }

    @Override
    @Transactional
    public MemberAccountDetailVO updateMemberStatus(Long memberAccountId, MemberStatusUpdateRequest request) {
        LoginUser loginUser = currentLoginUser();
        MemberAccountEntity memberAccount = getMemberAccountEntityOrThrow(memberAccountId, loginUser);
        memberAccount.setMemberStatus(request.getMemberStatus());
        memberAccountMapper.updateById(memberAccount);

        PatientEntity patient = patientMapper.selectById(memberAccount.getPatientId());
        if (patient != null) {
            patient.setMemberStatus("CANCELLED".equals(request.getMemberStatus()) ? 0 : 1);
            patientMapper.updateById(patient);
        }
        return getMemberAccountDetail(memberAccountId);
    }

    @Override
    public List<MemberLevelOptionVO> listLevelOptions() {
        Long orgId = currentLoginUser().getOrgId();
        return memberLevelMapper.selectList(new LambdaQueryWrapper<MemberLevelEntity>()
                        .eq(MemberLevelEntity::getOrgId, orgId)
                        .eq(MemberLevelEntity::getStatus, 1)
                        .orderByAsc(MemberLevelEntity::getUpgradeAmount, MemberLevelEntity::getId))
                .stream()
                .map(this::toLevelOption)
                .toList();
    }

    private MemberLevelOptionVO toLevelOption(MemberLevelEntity entity) {
        MemberLevelOptionVO option = new MemberLevelOptionVO();
        option.setId(entity.getId());
        option.setLevelCode(entity.getLevelCode());
        option.setLevelName(entity.getLevelName());
        option.setUpgradeAmount(entity.getUpgradeAmount());
        option.setDiscountRate(entity.getDiscountRate());
        option.setPointsRate(entity.getPointsRate());
        return option;
    }

    private MemberAccountEntity getMemberAccountEntityOrThrow(Long memberAccountId, LoginUser loginUser) {
        getMemberAccountDetail(memberAccountId);
        MemberAccountEntity entity = memberAccountMapper.selectOne(new LambdaQueryWrapper<MemberAccountEntity>()
                .eq(MemberAccountEntity::getId, memberAccountId)
                .eq(MemberAccountEntity::getOrgId, loginUser.getOrgId())
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会员账户不存在");
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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }
}
