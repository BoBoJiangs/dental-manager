package com.company.dental.modules.billing.service.impl;

import com.company.dental.common.exception.BusinessException;
import com.company.dental.common.util.BizNoGenerator;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.billing.dto.PaymentCreateRequest;
import com.company.dental.modules.billing.dto.RefundCreateRequest;
import com.company.dental.modules.billing.entity.ChargeOrderEntity;
import com.company.dental.modules.billing.entity.PaymentRecordEntity;
import com.company.dental.modules.billing.entity.ReceivableRecordEntity;
import com.company.dental.modules.billing.entity.RefundRecordEntity;
import com.company.dental.modules.billing.mapper.ChargeItemMapper;
import com.company.dental.modules.billing.mapper.ChargeOrderMapper;
import com.company.dental.modules.billing.mapper.PaymentRecordMapper;
import com.company.dental.modules.billing.mapper.ReceivableRecordMapper;
import com.company.dental.modules.billing.mapper.RefundRecordMapper;
import com.company.dental.modules.billing.vo.ChargeOrderDetailVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChargeOrderServiceImplTest {

    private final ChargeOrderMapper chargeOrderMapper = Mockito.mock(ChargeOrderMapper.class);
    private final ChargeItemMapper chargeItemMapper = Mockito.mock(ChargeItemMapper.class);
    private final PaymentRecordMapper paymentRecordMapper = Mockito.mock(PaymentRecordMapper.class);
    private final RefundRecordMapper refundRecordMapper = Mockito.mock(RefundRecordMapper.class);
    private final ReceivableRecordMapper receivableRecordMapper = Mockito.mock(ReceivableRecordMapper.class);
    private final BizNoGenerator bizNoGenerator = Mockito.mock(BizNoGenerator.class);
    private final DataScopeHelper dataScopeHelper = new DataScopeHelper();

    private final ChargeOrderServiceImpl service = new ChargeOrderServiceImpl(
            chargeOrderMapper,
            chargeItemMapper,
            paymentRecordMapper,
            refundRecordMapper,
            receivableRecordMapper,
            dataScopeHelper,
            bizNoGenerator
    );

    @AfterEach
    void clearAuthContext() {
        AuthContext.clear();
    }

    @Test
    void shouldUpdateAmountsAndStatusWhenPayingAllArrears() {
        AuthContext.set(LoginUser.builder()
                .userId(99L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        ChargeOrderEntity order = new ChargeOrderEntity();
        order.setId(5001L);
        order.setOrgId(1L);
        order.setClinicId(1L);
        order.setPatientId(1001L);
        order.setReceivableAmount(new BigDecimal("100.00"));
        order.setPaidAmount(new BigDecimal("40.00"));
        order.setArrearsAmount(new BigDecimal("60.00"));

        ReceivableRecordEntity receivableRecord = new ReceivableRecordEntity();
        receivableRecord.setId(8001L);
        receivableRecord.setChargeOrderId(5001L);

        when(chargeOrderMapper.selectOne(any())).thenReturn(order);
        when(bizNoGenerator.next("PM")).thenReturn("PM202604100001");
        when(receivableRecordMapper.selectOne(any())).thenReturn(receivableRecord);
        when(chargeOrderMapper.selectChargeOrderDetailById(eq(5001L), eq(1L), anyString(), any(), any()))
                .thenReturn(new ChargeOrderDetailVO());
        when(chargeItemMapper.selectByChargeOrderId(5001L)).thenReturn(Collections.emptyList());
        when(paymentRecordMapper.selectByChargeOrderId(5001L)).thenReturn(Collections.emptyList());
        when(refundRecordMapper.selectByChargeOrderId(5001L)).thenReturn(Collections.emptyList());

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setPaymentMethod("WECHAT");
        request.setAmount(new BigDecimal("60.00"));
        request.setCashierId(3001L);

        service.createPayment(5001L, request);

        ArgumentCaptor<PaymentRecordEntity> paymentCaptor = ArgumentCaptor.forClass(PaymentRecordEntity.class);
        verify(paymentRecordMapper).insert(paymentCaptor.capture());
        PaymentRecordEntity payment = paymentCaptor.getValue();
        assertEquals(5001L, payment.getChargeOrderId());
        assertEquals("PM202604100001", payment.getPaymentNo());
        assertEquals(new BigDecimal("60.00"), payment.getAmount());
        assertEquals("SUCCESS", payment.getPaymentStatus());
        assertNotNull(payment.getPaidAt());

        ArgumentCaptor<ChargeOrderEntity> orderCaptor = ArgumentCaptor.forClass(ChargeOrderEntity.class);
        verify(chargeOrderMapper).updateById(orderCaptor.capture());
        ChargeOrderEntity updatedOrder = orderCaptor.getValue();
        assertEquals(new BigDecimal("100.00"), updatedOrder.getPaidAmount());
        assertEquals(new BigDecimal("0.00"), updatedOrder.getArrearsAmount());
        assertEquals("PAID", updatedOrder.getOrderStatus());
        assertNotNull(updatedOrder.getSettledAt());

        ArgumentCaptor<ReceivableRecordEntity> receivableCaptor = ArgumentCaptor.forClass(ReceivableRecordEntity.class);
        verify(receivableRecordMapper).updateById(receivableCaptor.capture());
        ReceivableRecordEntity updatedReceivable = receivableCaptor.getValue();
        assertEquals(new BigDecimal("100.00"), updatedReceivable.getReceivedAmount());
        assertEquals(new BigDecimal("0.00"), updatedReceivable.getOutstandingAmount());
        assertEquals("PAID", updatedReceivable.getReceivableStatus());
    }

    @Test
    void shouldRejectPaymentWhenAmountExceedsArrears() {
        AuthContext.set(LoginUser.builder()
                .userId(99L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        ChargeOrderEntity order = new ChargeOrderEntity();
        order.setId(5001L);
        order.setOrgId(1L);
        order.setClinicId(1L);
        order.setReceivableAmount(new BigDecimal("100.00"));
        order.setPaidAmount(new BigDecimal("50.00"));
        order.setArrearsAmount(new BigDecimal("50.00"));
        when(chargeOrderMapper.selectOne(any())).thenReturn(order);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setPaymentMethod("WECHAT");
        request.setAmount(new BigDecimal("60.00"));

        BusinessException exception = assertThrows(BusinessException.class, () -> service.createPayment(5001L, request));
        assertEquals("支付金额不能大于欠费金额", exception.getMessage());
        verify(paymentRecordMapper, never()).insert(any(PaymentRecordEntity.class));
        verify(chargeOrderMapper, never()).updateById(any(ChargeOrderEntity.class));
        verify(receivableRecordMapper, never()).updateById(any(ReceivableRecordEntity.class));
        verify(receivableRecordMapper, never()).insert(any(ReceivableRecordEntity.class));
        verify(chargeItemMapper, never()).selectByChargeOrderId(anyLong());
    }

    @Test
    void shouldSetStatusRefundedWhenRefundingAllPaidAmount() {
        AuthContext.set(LoginUser.builder()
                .userId(99L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        ChargeOrderEntity order = new ChargeOrderEntity();
        order.setId(5001L);
        order.setOrgId(1L);
        order.setClinicId(1L);
        order.setPatientId(1001L);
        order.setReceivableAmount(new BigDecimal("100.00"));
        order.setPaidAmount(new BigDecimal("100.00"));
        order.setArrearsAmount(BigDecimal.ZERO);
        when(chargeOrderMapper.selectOne(any())).thenReturn(order);

        when(bizNoGenerator.next("RF")).thenReturn("RF202604100001");

        ReceivableRecordEntity receivableRecord = new ReceivableRecordEntity();
        receivableRecord.setId(9001L);
        receivableRecord.setChargeOrderId(5001L);
        when(receivableRecordMapper.selectOne(any())).thenReturn(receivableRecord);

        when(chargeOrderMapper.selectChargeOrderDetailById(eq(5001L), eq(1L), anyString(), any(), any()))
                .thenReturn(new ChargeOrderDetailVO());
        when(chargeItemMapper.selectByChargeOrderId(5001L)).thenReturn(Collections.emptyList());
        when(paymentRecordMapper.selectByChargeOrderId(5001L)).thenReturn(Collections.emptyList());
        when(refundRecordMapper.selectByChargeOrderId(5001L)).thenReturn(Collections.emptyList());

        RefundCreateRequest request = new RefundCreateRequest();
        request.setRefundAmount(new BigDecimal("100.00"));
        request.setRefundMethod("WECHAT");
        request.setRefundReason("撤销收费");

        service.createRefund(5001L, request);

        ArgumentCaptor<RefundRecordEntity> refundCaptor = ArgumentCaptor.forClass(RefundRecordEntity.class);
        verify(refundRecordMapper).insert(refundCaptor.capture());
        RefundRecordEntity refundRecord = refundCaptor.getValue();
        assertEquals("RF202604100001", refundRecord.getRefundNo());
        assertEquals(5001L, refundRecord.getChargeOrderId());
        assertEquals(new BigDecimal("100.00"), refundRecord.getRefundAmount());
        assertEquals("SUCCESS", refundRecord.getRefundStatus());
        assertEquals(99L, refundRecord.getApprovedBy());
        assertEquals(99L, refundRecord.getOperatorId());
        assertNotNull(refundRecord.getRefundedAt());

        ArgumentCaptor<ChargeOrderEntity> orderCaptor = ArgumentCaptor.forClass(ChargeOrderEntity.class);
        verify(chargeOrderMapper).updateById(orderCaptor.capture());
        ChargeOrderEntity updatedOrder = orderCaptor.getValue();
        assertEquals(new BigDecimal("0.00"), updatedOrder.getPaidAmount());
        assertEquals(new BigDecimal("100.00"), updatedOrder.getArrearsAmount());
        assertEquals("REFUNDED", updatedOrder.getOrderStatus());
        assertNull(updatedOrder.getSettledAt());

        ArgumentCaptor<ReceivableRecordEntity> receivableCaptor = ArgumentCaptor.forClass(ReceivableRecordEntity.class);
        verify(receivableRecordMapper).updateById(receivableCaptor.capture());
        ReceivableRecordEntity updatedReceivable = receivableCaptor.getValue();
        assertEquals(new BigDecimal("0.00"), updatedReceivable.getReceivedAmount());
        assertEquals(new BigDecimal("100.00"), updatedReceivable.getOutstandingAmount());
        assertEquals("UNPAID", updatedReceivable.getReceivableStatus());
    }

    @Test
    void shouldRejectRefundWhenAmountExceedsPaid() {
        AuthContext.set(LoginUser.builder()
                .userId(99L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        ChargeOrderEntity order = new ChargeOrderEntity();
        order.setId(5001L);
        order.setOrgId(1L);
        order.setClinicId(1L);
        order.setPatientId(1001L);
        order.setReceivableAmount(new BigDecimal("100.00"));
        order.setPaidAmount(new BigDecimal("30.00"));
        order.setArrearsAmount(new BigDecimal("70.00"));
        when(chargeOrderMapper.selectOne(any())).thenReturn(order);

        RefundCreateRequest request = new RefundCreateRequest();
        request.setRefundAmount(new BigDecimal("40.00"));
        request.setRefundMethod("WECHAT");
        request.setRefundReason("金额超限");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.createRefund(5001L, request));
        assertEquals("退款金额不能大于已支付金额", exception.getMessage());
        verify(refundRecordMapper, never()).insert(any(RefundRecordEntity.class));
        verify(chargeOrderMapper, never()).updateById(any(ChargeOrderEntity.class));
    }
}
