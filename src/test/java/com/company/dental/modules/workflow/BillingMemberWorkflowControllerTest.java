package com.company.dental.modules.workflow;

import com.company.dental.modules.billing.controller.ChargeOrderController;
import com.company.dental.modules.billing.dto.ChargeOrderCreateRequest;
import com.company.dental.modules.billing.dto.PaymentCreateRequest;
import com.company.dental.modules.billing.dto.RefundCreateRequest;
import com.company.dental.modules.billing.service.ChargeOrderService;
import com.company.dental.modules.billing.vo.ChargeOrderDetailVO;
import com.company.dental.modules.member.controller.MemberAccountController;
import com.company.dental.modules.member.dto.MemberPointsAdjustRequest;
import com.company.dental.modules.member.dto.MemberRechargeRequest;
import com.company.dental.modules.member.service.MemberAccountService;
import com.company.dental.modules.member.vo.MemberAccountDetailVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BillingMemberWorkflowControllerTest {

    private final ChargeOrderService chargeOrderService = Mockito.mock(ChargeOrderService.class);
    private final MemberAccountService memberAccountService = Mockito.mock(MemberAccountService.class);

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
            new ChargeOrderController(chargeOrderService),
            new MemberAccountController(memberAccountService)
    ).build();

    @Test
    void shouldCoverBillingAndMemberWorkflowApis() throws Exception {
        when(chargeOrderService.createChargeOrder(any(ChargeOrderCreateRequest.class))).thenReturn(new ChargeOrderDetailVO());
        when(chargeOrderService.createPayment(eq(5001L), any(PaymentCreateRequest.class))).thenReturn(new ChargeOrderDetailVO());
        when(chargeOrderService.createRefund(eq(5001L), any(RefundCreateRequest.class))).thenReturn(new ChargeOrderDetailVO());
        when(memberAccountService.rechargeMemberAccount(eq(6001L), any(MemberRechargeRequest.class))).thenReturn(new MemberAccountDetailVO());
        when(memberAccountService.adjustMemberPoints(eq(6001L), any(MemberPointsAdjustRequest.class))).thenReturn(new MemberAccountDetailVO());

        mockMvc.perform(post("/api/billing/charge-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clinicId": 1,
                                  "patientId": 1001,
                                  "medicalRecordId": 4001,
                                  "treatmentPlanId": 5001,
                                  "items": [
                                    {
                                      "itemCode": "FEE-001",
                                      "itemName": "综合检查",
                                      "unitPrice": 300,
                                      "quantity": 1
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(chargeOrderService).createChargeOrder(any(ChargeOrderCreateRequest.class));

        mockMvc.perform(put("/api/billing/charge-orders/{id}/payments", 5001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentMethod": "WECHAT",
                                  "amount": 300
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(chargeOrderService).createPayment(eq(5001L), any(PaymentCreateRequest.class));

        mockMvc.perform(put("/api/billing/charge-orders/{id}/refunds", 5001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refundAmount": 100,
                                  "refundMethod": "WECHAT",
                                  "refundReason": "重复收费"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(chargeOrderService).createRefund(eq(5001L), any(RefundCreateRequest.class));

        mockMvc.perform(put("/api/members/{id}/recharge", 6001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 1000,
                                  "bizType": "RECHARGE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(memberAccountService).rechargeMemberAccount(eq(6001L), any(MemberRechargeRequest.class));

        mockMvc.perform(put("/api/members/{id}/points", 6001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "changePoints": 20,
                                  "bizType": "MANUAL_ADJUST"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(memberAccountService).adjustMemberPoints(eq(6001L), any(MemberPointsAdjustRequest.class));
    }
}
