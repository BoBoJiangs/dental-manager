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
import com.company.dental.modules.emr.dto.TreatmentItemCreateRequest;
import com.company.dental.modules.emr.dto.TreatmentPlanCreateRequest;
import com.company.dental.modules.emr.dto.TreatmentPlanStatusUpdateRequest;
import com.company.dental.modules.emr.entity.TreatmentItemEntity;
import com.company.dental.modules.emr.entity.TreatmentPlanEntity;
import com.company.dental.modules.emr.mapper.MedicalRecordMapper;
import com.company.dental.modules.emr.mapper.TreatmentItemMapper;
import com.company.dental.modules.emr.mapper.TreatmentPlanMapper;
import com.company.dental.modules.emr.query.TreatmentPlanPageQuery;
import com.company.dental.modules.emr.service.TreatmentPlanService;
import com.company.dental.modules.emr.vo.MedicalRecordDetailVO;
import com.company.dental.modules.emr.vo.TreatmentItemVO;
import com.company.dental.modules.emr.vo.TreatmentPlanDetailVO;
import com.company.dental.modules.emr.vo.TreatmentPlanPageItemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class TreatmentPlanServiceImpl implements TreatmentPlanService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final Set<String> ALLOWED_STATUSES = Set.of("DRAFT", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED");

    private final TreatmentPlanMapper treatmentPlanMapper;
    private final TreatmentItemMapper treatmentItemMapper;
    private final MedicalRecordMapper medicalRecordMapper;
    private final BizNoGenerator bizNoGenerator;
    private final DataScopeHelper dataScopeHelper;

    public TreatmentPlanServiceImpl(TreatmentPlanMapper treatmentPlanMapper,
                                    TreatmentItemMapper treatmentItemMapper,
                                    MedicalRecordMapper medicalRecordMapper,
                                    BizNoGenerator bizNoGenerator,
                                    DataScopeHelper dataScopeHelper) {
        this.treatmentPlanMapper = treatmentPlanMapper;
        this.treatmentItemMapper = treatmentItemMapper;
        this.medicalRecordMapper = medicalRecordMapper;
        this.bizNoGenerator = bizNoGenerator;
        this.dataScopeHelper = dataScopeHelper;
    }

    @Override
    public PageResult<TreatmentPlanPageItemVO> pageTreatmentPlans(TreatmentPlanPageQuery query) {
        LoginUser loginUser = currentLoginUser();
        Page<TreatmentPlanPageItemVO> page = treatmentPlanMapper.selectTreatmentPlanPage(
                Page.of(query.getCurrent(), query.getSize()),
                query,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        return PageResult.<TreatmentPlanPageItemVO>builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .current(page.getCurrent())
                .size(page.getSize())
                .build();
    }

    @Override
    public TreatmentPlanDetailVO getTreatmentPlanDetail(Long planId) {
        return getTreatmentPlanDetailOrThrow(planId, currentLoginUser());
    }

    @Override
    @Transactional
    public TreatmentPlanDetailVO createTreatmentPlan(TreatmentPlanCreateRequest request) {
        LoginUser loginUser = currentLoginUser();
        Long orgId = loginUser.getOrgId();
        dataScopeHelper.assertClinicAccess(loginUser, request.getClinicId());
        validateParticipants(orgId, request, loginUser);

        AmountSummary summary = calculateAmounts(request.getItems());
        TreatmentPlanEntity plan = new TreatmentPlanEntity();
        plan.setOrgId(orgId);
        plan.setClinicId(request.getClinicId());
        plan.setPlanNo(bizNoGenerator.next("TP"));
        plan.setPatientId(request.getPatientId());
        plan.setMedicalRecordId(request.getMedicalRecordId());
        plan.setDoctorId(request.getDoctorId());
        plan.setPlanName(request.getPlanName());
        plan.setTotalAmount(summary.totalAmount());
        plan.setPlanStatus(isBlank(request.getPlanStatus()) ? STATUS_DRAFT : normalizeStatus(request.getPlanStatus()));
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setRemark(request.getRemark());
        treatmentPlanMapper.insert(plan);

        saveTreatmentItems(plan.getId(), request.getMedicalRecordId(), request.getItems());
        return getTreatmentPlanDetailOrThrow(plan.getId(), loginUser);
    }

    @Override
    @Transactional
    public TreatmentPlanDetailVO updateTreatmentPlanStatus(Long planId, TreatmentPlanStatusUpdateRequest request) {
        LoginUser loginUser = currentLoginUser();
        TreatmentPlanEntity plan = getTreatmentPlanEntityOrThrow(planId, loginUser.getOrgId());
        dataScopeHelper.assertClinicAccess(loginUser, plan.getClinicId());
        plan.setPlanStatus(normalizeStatus(request.getPlanStatus()));
        if (request.getRemark() != null && !request.getRemark().isBlank()) {
            plan.setRemark(request.getRemark());
        }
        treatmentPlanMapper.updateById(plan);
        return getTreatmentPlanDetailOrThrow(planId, loginUser);
    }

    private void saveTreatmentItems(Long planId, Long medicalRecordId, List<TreatmentItemCreateRequest> items) {
        for (int index = 0; index < items.size(); index++) {
            TreatmentItemCreateRequest item = items.get(index);
            BigDecimal totalAmount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal discountAmount = defaultZero(item.getDiscountAmount());
            BigDecimal receivableAmount = totalAmount.subtract(discountAmount);
            if (receivableAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "治疗项目优惠金额不能大于总金额");
            }
            TreatmentItemEntity entity = new TreatmentItemEntity();
            entity.setTreatmentPlanId(planId);
            entity.setMedicalRecordId(medicalRecordId);
            entity.setItemCode(item.getItemCode());
            entity.setItemName(item.getItemName());
            entity.setItemCategory(item.getItemCategory());
            entity.setToothPosition(item.getToothPosition());
            entity.setUnitPrice(item.getUnitPrice());
            entity.setQuantity(item.getQuantity());
            entity.setDiscountAmount(discountAmount);
            entity.setReceivableAmount(receivableAmount);
            entity.setExecutedFlag(0);
            entity.setItemStatus("PLANNED");
            entity.setSortNo(item.getSortNo() == null ? index + 1 : item.getSortNo());
            entity.setRemark(item.getRemark());
            treatmentItemMapper.insert(entity);
        }
    }

    private void validateParticipants(Long orgId, TreatmentPlanCreateRequest request, LoginUser loginUser) {
        Long clinicCount = medicalRecordMapper.countValidClinic(orgId, request.getClinicId());
        if (clinicCount == null || clinicCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "门诊不存在或已停用");
        }
        Long patientCount = medicalRecordMapper.countValidPatient(orgId, request.getPatientId());
        if (patientCount == null || patientCount == 0) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        Long doctorCount = medicalRecordMapper.countValidDoctor(orgId, request.getDoctorId());
        if (doctorCount == null || doctorCount == 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "医生不存在或已停用");
        }
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
            throw new BusinessException(ErrorCode.BAD_REQUEST, "治疗计划门诊必须与病历门诊一致");
        }
        if (!Objects.equals(recordDetail.getPatientId(), request.getPatientId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "治疗计划患者必须与病历患者一致");
        }
    }

    private AmountSummary calculateAmounts(List<TreatmentItemCreateRequest> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (TreatmentItemCreateRequest item : items) {
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal discountAmount = defaultZero(item.getDiscountAmount());
            if (discountAmount.compareTo(itemTotal) > 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "治疗项目优惠金额不能大于总金额");
            }
            totalAmount = totalAmount.add(itemTotal.subtract(discountAmount));
        }
        return new AmountSummary(totalAmount);
    }

    private TreatmentPlanDetailVO getTreatmentPlanDetailOrThrow(Long planId, LoginUser loginUser) {
        TreatmentPlanDetailVO detail = treatmentPlanMapper.selectTreatmentPlanDetailById(
                planId,
                loginUser.getOrgId(),
                dataScopeHelper.resolve(loginUser).name(),
                dataScopeHelper.resolveClinicIds(loginUser),
                loginUser.getStaffId()
        );
        if (detail == null) {
            throw new BusinessException(ErrorCode.TREATMENT_PLAN_NOT_FOUND);
        }
        List<TreatmentItemVO> items = treatmentItemMapper.selectByTreatmentPlanId(planId);
        detail.setItems(items == null ? Collections.emptyList() : items);
        return detail;
    }

    private TreatmentPlanEntity getTreatmentPlanEntityOrThrow(Long planId, Long orgId) {
        TreatmentPlanEntity entity = treatmentPlanMapper.selectOne(new LambdaQueryWrapper<TreatmentPlanEntity>()
                .eq(TreatmentPlanEntity::getId, planId)
                .eq(TreatmentPlanEntity::getOrgId, orgId)
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BusinessException(ErrorCode.TREATMENT_PLAN_NOT_FOUND);
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

    private String normalizeStatus(String status) {
        String normalized = status == null ? null : status.trim().toUpperCase();
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "治疗计划状态非法");
        }
        return normalized;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private record AmountSummary(BigDecimal totalAmount) {
    }
}
