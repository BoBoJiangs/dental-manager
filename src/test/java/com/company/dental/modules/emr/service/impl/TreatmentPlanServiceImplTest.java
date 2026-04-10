package com.company.dental.modules.emr.service.impl;

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
import com.company.dental.modules.emr.vo.MedicalRecordDetailVO;
import com.company.dental.modules.emr.vo.TreatmentPlanDetailVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TreatmentPlanServiceImplTest {

    private final TreatmentPlanMapper treatmentPlanMapper = Mockito.mock(TreatmentPlanMapper.class);
    private final TreatmentItemMapper treatmentItemMapper = Mockito.mock(TreatmentItemMapper.class);
    private final MedicalRecordMapper medicalRecordMapper = Mockito.mock(MedicalRecordMapper.class);
    private final BizNoGenerator bizNoGenerator = Mockito.mock(BizNoGenerator.class);
    private final DataScopeHelper dataScopeHelper = new DataScopeHelper();

    private final TreatmentPlanServiceImpl service = new TreatmentPlanServiceImpl(
            treatmentPlanMapper,
            treatmentItemMapper,
            medicalRecordMapper,
            bizNoGenerator,
            dataScopeHelper
    );

    @AfterEach
    void clearAuthContext() {
        AuthContext.clear();
    }

    @Test
    void shouldCalculateTotalAndDefaultSortNoWhenCreatingPlan() {
        AuthContext.set(LoginUser.builder()
                .userId(66L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        when(medicalRecordMapper.countValidClinic(1L, 1L)).thenReturn(1L);
        when(medicalRecordMapper.countValidPatient(1L, 1001L)).thenReturn(1L);
        when(medicalRecordMapper.countValidDoctor(1L, 2001L)).thenReturn(1L);

        MedicalRecordDetailVO recordDetail = new MedicalRecordDetailVO();
        recordDetail.setClinicId(1L);
        recordDetail.setPatientId(1001L);
        when(medicalRecordMapper.selectMedicalRecordDetailById(eq(4001L), eq(1L), any(), any(), any()))
                .thenReturn(recordDetail);

        when(bizNoGenerator.next("TP")).thenReturn("TP202604100001");
        doAnswer(invocation -> {
            TreatmentPlanEntity entity = invocation.getArgument(0);
            entity.setId(9001L);
            return 1;
        }).when(treatmentPlanMapper).insert(any(TreatmentPlanEntity.class));

        TreatmentPlanDetailVO detail = new TreatmentPlanDetailVO();
        detail.setId(9001L);
        when(treatmentPlanMapper.selectTreatmentPlanDetailById(eq(9001L), eq(1L), any(), any(), any()))
                .thenReturn(detail);
        when(treatmentItemMapper.selectByTreatmentPlanId(9001L)).thenReturn(Collections.emptyList());

        TreatmentPlanCreateRequest request = new TreatmentPlanCreateRequest();
        request.setClinicId(1L);
        request.setPatientId(1001L);
        request.setMedicalRecordId(4001L);
        request.setDoctorId(2001L);
        request.setPlanName("正畸一期");

        TreatmentItemCreateRequest item1 = new TreatmentItemCreateRequest();
        item1.setItemCode("ORTHO-001");
        item1.setItemName("托槽调整");
        item1.setUnitPrice(new BigDecimal("100.00"));
        item1.setQuantity(2);
        item1.setDiscountAmount(new BigDecimal("10.00"));

        TreatmentItemCreateRequest item2 = new TreatmentItemCreateRequest();
        item2.setItemCode("ORTHO-002");
        item2.setItemName("弓丝更换");
        item2.setUnitPrice(new BigDecimal("50.00"));
        item2.setQuantity(1);
        item2.setSortNo(9);

        request.setItems(List.of(item1, item2));

        service.createTreatmentPlan(request);

        ArgumentCaptor<TreatmentPlanEntity> planCaptor = ArgumentCaptor.forClass(TreatmentPlanEntity.class);
        verify(treatmentPlanMapper).insert(planCaptor.capture());
        TreatmentPlanEntity insertedPlan = planCaptor.getValue();
        assertEquals("TP202604100001", insertedPlan.getPlanNo());
        assertEquals("DRAFT", insertedPlan.getPlanStatus());
        assertEquals(new BigDecimal("240.00"), insertedPlan.getTotalAmount());

        ArgumentCaptor<TreatmentItemEntity> itemCaptor = ArgumentCaptor.forClass(TreatmentItemEntity.class);
        verify(treatmentItemMapper, Mockito.times(2)).insert(itemCaptor.capture());
        List<TreatmentItemEntity> insertedItems = new ArrayList<>(itemCaptor.getAllValues());

        TreatmentItemEntity first = insertedItems.get(0);
        assertEquals(9001L, first.getTreatmentPlanId());
        assertEquals("ORTHO-001", first.getItemCode());
        assertEquals(new BigDecimal("190.00"), first.getReceivableAmount());
        assertEquals(1, first.getSortNo());
        assertEquals("PLANNED", first.getItemStatus());

        TreatmentItemEntity second = insertedItems.get(1);
        assertEquals(9001L, second.getTreatmentPlanId());
        assertEquals("ORTHO-002", second.getItemCode());
        assertEquals(new BigDecimal("50.00"), second.getReceivableAmount());
        assertEquals(9, second.getSortNo());
        assertEquals("PLANNED", second.getItemStatus());

        assertTrue(insertedItems.stream().allMatch(item -> item.getMedicalRecordId().equals(4001L)));
    }

    @Test
    void shouldRejectIllegalPlanStatusWhenUpdatingStatus() {
        AuthContext.set(LoginUser.builder()
                .userId(66L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        TreatmentPlanEntity plan = new TreatmentPlanEntity();
        plan.setId(9001L);
        plan.setOrgId(1L);
        plan.setClinicId(1L);
        plan.setPlanStatus("DRAFT");
        when(treatmentPlanMapper.selectOne(any())).thenReturn(plan);

        TreatmentPlanStatusUpdateRequest request = new TreatmentPlanStatusUpdateRequest();
        request.setPlanStatus("UNKNOWN_STATUS");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.updateTreatmentPlanStatus(9001L, request));
        assertEquals("治疗计划状态非法", exception.getMessage());
        verify(treatmentPlanMapper, Mockito.never()).updateById(any(TreatmentPlanEntity.class));
    }
}
