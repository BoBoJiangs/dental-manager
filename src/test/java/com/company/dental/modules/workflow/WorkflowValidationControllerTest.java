package com.company.dental.modules.workflow;

import com.company.dental.modules.appointment.controller.AppointmentController;
import com.company.dental.modules.appointment.dto.AppointmentCreateRequest;
import com.company.dental.modules.appointment.service.AppointmentService;
import com.company.dental.modules.billing.controller.ChargeOrderController;
import com.company.dental.modules.billing.dto.PaymentCreateRequest;
import com.company.dental.modules.billing.service.ChargeOrderService;
import com.company.dental.modules.emr.controller.TreatmentPlanController;
import com.company.dental.modules.emr.dto.TreatmentPlanCreateRequest;
import com.company.dental.modules.emr.service.TreatmentPlanService;
import com.company.dental.modules.patient.controller.PatientController;
import com.company.dental.modules.patient.dto.PatientCreateRequest;
import com.company.dental.modules.patient.service.PatientService;
import com.company.dental.modules.sms.controller.SmsTaskController;
import com.company.dental.modules.sms.dto.SmsTaskCreateRequest;
import com.company.dental.modules.sms.service.SmsTaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkflowValidationControllerTest {

    private final PatientService patientService = Mockito.mock(PatientService.class);
    private final AppointmentService appointmentService = Mockito.mock(AppointmentService.class);
    private final TreatmentPlanService treatmentPlanService = Mockito.mock(TreatmentPlanService.class);
    private final ChargeOrderService chargeOrderService = Mockito.mock(ChargeOrderService.class);
    private final SmsTaskService smsTaskService = Mockito.mock(SmsTaskService.class);

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
            new PatientController(patientService),
            new AppointmentController(appointmentService),
            new TreatmentPlanController(treatmentPlanService),
            new ChargeOrderController(chargeOrderService),
            new SmsTaskController(smsTaskService)
    ).build();

    @Test
    void shouldRejectPatientCreateWhenMobileInvalid() throws Exception {
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientName": "张三",
                                  "mobile": "123",
                                  "sourceCode": "ONLINE",
                                  "firstClinicId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).createPatient(any(PatientCreateRequest.class));
    }

    @Test
    void shouldRejectAppointmentCreateWhenRequiredFieldMissing() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clinicId": 1,
                                  "doctorId": 2001,
                                  "sourceType": "MANUAL",
                                  "appointmentType": "INITIAL",
                                  "appointmentDate": "2026-04-10",
                                  "startTime": "2026-04-10T09:00:00",
                                  "endTime": "2026-04-10T09:30:00"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(appointmentService, never()).createAppointment(any(AppointmentCreateRequest.class));
    }

    @Test
    void shouldRejectTreatmentPlanCreateWhenItemsEmpty() throws Exception {
        mockMvc.perform(post("/api/emr/treatment-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clinicId": 1,
                                  "patientId": 1001,
                                  "medicalRecordId": 4001,
                                  "doctorId": 2001,
                                  "planName": "空项目测试",
                                  "items": []
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(treatmentPlanService, never()).createTreatmentPlan(any(TreatmentPlanCreateRequest.class));
    }

    @Test
    void shouldRejectPaymentCreateWhenAmountNonPositive() throws Exception {
        mockMvc.perform(put("/api/billing/charge-orders/{id}/payments", 5001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentMethod": "WECHAT",
                                  "amount": 0
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(chargeOrderService, never()).createPayment(anyLong(), any(PaymentCreateRequest.class));
    }

    @Test
    void shouldRejectSmsTaskCreateWhenTemplateMissing() throws Exception {
        mockMvc.perform(post("/api/sms/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bizType": "APPOINTMENT_REMINDER",
                                  "mobile": "13800000001"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(smsTaskService, never()).createTask(any(SmsTaskCreateRequest.class));
    }
}
