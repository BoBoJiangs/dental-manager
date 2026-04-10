package com.company.dental.modules.member.service.impl;

import com.company.dental.common.exception.BusinessException;
import com.company.dental.common.util.BizNoGenerator;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.member.dto.MemberPointsAdjustRequest;
import com.company.dental.modules.member.dto.MemberRechargeRequest;
import com.company.dental.modules.member.entity.MemberAccountEntity;
import com.company.dental.modules.member.entity.MemberBalanceRecordEntity;
import com.company.dental.modules.member.entity.MemberPointsRecordEntity;
import com.company.dental.modules.member.mapper.MemberAccountMapper;
import com.company.dental.modules.member.mapper.MemberAccountQueryMapper;
import com.company.dental.modules.member.mapper.MemberBalanceRecordMapper;
import com.company.dental.modules.member.mapper.MemberLevelMapper;
import com.company.dental.modules.member.mapper.MemberPointsRecordMapper;
import com.company.dental.modules.member.vo.MemberAccountDetailVO;
import com.company.dental.modules.patient.mapper.PatientMapper;
import com.company.dental.modules.patient.vo.PatientMemberBalanceRecordVO;
import com.company.dental.modules.patient.vo.PatientMemberPointsRecordVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberAccountServiceImplTest {

    private final MemberAccountMapper memberAccountMapper = Mockito.mock(MemberAccountMapper.class);
    private final MemberLevelMapper memberLevelMapper = Mockito.mock(MemberLevelMapper.class);
    private final MemberAccountQueryMapper memberAccountQueryMapper = Mockito.mock(MemberAccountQueryMapper.class);
    private final MemberBalanceRecordMapper memberBalanceRecordMapper = Mockito.mock(MemberBalanceRecordMapper.class);
    private final MemberPointsRecordMapper memberPointsRecordMapper = Mockito.mock(MemberPointsRecordMapper.class);
    private final PatientMapper patientMapper = Mockito.mock(PatientMapper.class);
    private final DataScopeHelper dataScopeHelper = new DataScopeHelper();
    private final BizNoGenerator bizNoGenerator = Mockito.mock(BizNoGenerator.class);

    private final MemberAccountServiceImpl service = new MemberAccountServiceImpl(
            memberAccountMapper,
            memberLevelMapper,
            memberAccountQueryMapper,
            memberBalanceRecordMapper,
            memberPointsRecordMapper,
            patientMapper,
            dataScopeHelper,
            bizNoGenerator
    );

    @AfterEach
    void clearAuthContext() {
        AuthContext.clear();
    }

    @Test
    void shouldIncreaseBalanceAndCreateRecordWhenRecharging() {
        AuthContext.set(LoginUser.builder()
                .userId(88L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        MemberAccountDetailVO detail = new MemberAccountDetailVO();
        detail.setId(6001L);
        detail.setPatientId(1001L);
        detail.setRecentBalanceRecords(Collections.emptyList());
        detail.setRecentPointsRecords(Collections.emptyList());

        MemberAccountEntity accountEntity = new MemberAccountEntity();
        accountEntity.setId(6001L);
        accountEntity.setOrgId(1L);
        accountEntity.setPatientId(1001L);
        accountEntity.setBalanceAmount(new BigDecimal("100.00"));
        accountEntity.setTotalRechargeAmount(new BigDecimal("200.00"));
        accountEntity.setPointsBalance(10);

        when(memberAccountMapper.selectMemberAccountDetailById(eq(6001L), eq(1L), any(), any(), any()))
                .thenReturn(detail);
        when(memberAccountQueryMapper.selectRecentBalanceRecords(1001L)).thenReturn(List.of(new PatientMemberBalanceRecordVO()));
        when(memberAccountQueryMapper.selectRecentPointsRecords(1001L)).thenReturn(List.of(new PatientMemberPointsRecordVO()));
        when(memberAccountMapper.selectOne(any())).thenReturn(accountEntity);

        MemberRechargeRequest request = new MemberRechargeRequest();
        request.setAmount(new BigDecimal("50.00"));

        service.rechargeMemberAccount(6001L, request);

        ArgumentCaptor<MemberAccountEntity> accountCaptor = ArgumentCaptor.forClass(MemberAccountEntity.class);
        verify(memberAccountMapper).updateById(accountCaptor.capture());
        MemberAccountEntity updatedAccount = accountCaptor.getValue();
        assertEquals(new BigDecimal("150.00"), updatedAccount.getBalanceAmount());
        assertEquals(new BigDecimal("250.00"), updatedAccount.getTotalRechargeAmount());

        ArgumentCaptor<MemberBalanceRecordEntity> recordCaptor = ArgumentCaptor.forClass(MemberBalanceRecordEntity.class);
        verify(memberBalanceRecordMapper).insert(recordCaptor.capture());
        MemberBalanceRecordEntity balanceRecord = recordCaptor.getValue();
        assertEquals(6001L, balanceRecord.getMemberAccountId());
        assertEquals(1001L, balanceRecord.getPatientId());
        assertEquals("RECHARGE", balanceRecord.getBizType());
        assertEquals(new BigDecimal("50.00"), balanceRecord.getChangeAmount());
        assertEquals(new BigDecimal("100.00"), balanceRecord.getBeforeBalance());
        assertEquals(new BigDecimal("150.00"), balanceRecord.getAfterBalance());
        assertEquals(88L, balanceRecord.getOperatorId());
    }

    @Test
    void shouldRejectNegativePointsBeyondCurrentBalance() {
        AuthContext.set(LoginUser.builder()
                .userId(88L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        MemberAccountDetailVO detail = new MemberAccountDetailVO();
        detail.setId(6001L);
        detail.setPatientId(1001L);
        detail.setRecentBalanceRecords(Collections.emptyList());
        detail.setRecentPointsRecords(Collections.emptyList());

        MemberAccountEntity accountEntity = new MemberAccountEntity();
        accountEntity.setId(6001L);
        accountEntity.setOrgId(1L);
        accountEntity.setPatientId(1001L);
        accountEntity.setPointsBalance(5);

        when(memberAccountMapper.selectMemberAccountDetailById(eq(6001L), eq(1L), any(), any(), any()))
                .thenReturn(detail);
        when(memberAccountQueryMapper.selectRecentBalanceRecords(anyLong())).thenReturn(Collections.emptyList());
        when(memberAccountQueryMapper.selectRecentPointsRecords(anyLong())).thenReturn(Collections.emptyList());
        when(memberAccountMapper.selectOne(any())).thenReturn(accountEntity);

        MemberPointsAdjustRequest request = new MemberPointsAdjustRequest();
        request.setChangePoints(-10);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.adjustMemberPoints(6001L, request));
        assertEquals("积分不足，无法扣减", exception.getMessage());
        verify(memberAccountMapper, never()).updateById(any(MemberAccountEntity.class));
        verify(memberPointsRecordMapper, never()).insert(any(MemberPointsRecordEntity.class));
    }
}
