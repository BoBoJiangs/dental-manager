package com.company.dental.modules.workflow;

import com.company.dental.modules.appointment.controller.AppointmentController;
import com.company.dental.modules.appointment.dto.AppointmentCheckInRequest;
import com.company.dental.modules.appointment.dto.AppointmentCreateRequest;
import com.company.dental.modules.appointment.service.AppointmentService;
import com.company.dental.modules.appointment.vo.AppointmentDetailVO;
import com.company.dental.modules.emr.controller.MedicalRecordController;
import com.company.dental.modules.emr.controller.TreatmentPlanController;
import com.company.dental.modules.emr.dto.MedicalRecordCreateRequest;
import com.company.dental.modules.emr.dto.TreatmentPlanCreateRequest;
import com.company.dental.modules.emr.service.MedicalRecordService;
import com.company.dental.modules.emr.service.TreatmentPlanService;
import com.company.dental.modules.emr.vo.MedicalRecordDetailVO;
import com.company.dental.modules.emr.vo.TreatmentPlanDetailVO;
import com.company.dental.modules.patient.controller.PatientController;
import com.company.dental.modules.patient.dto.PatientCreateRequest;
import com.company.dental.modules.patient.service.PatientService;
import com.company.dental.modules.patient.vo.PatientDetailVO;
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

class PatientAppointmentEmrWorkflowControllerTest {

    private final PatientService patientService = Mockito.mock(PatientService.class);
    private final AppointmentService appointmentService = Mockito.mock(AppointmentService.class);
    private final MedicalRecordService medicalRecordService = Mockito.mock(MedicalRecordService.class);
    private final TreatmentPlanService treatmentPlanService = Mockito.mock(TreatmentPlanService.class);

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
            new PatientController(patientService),
            new AppointmentController(appointmentService),
            new MedicalRecordController(medicalRecordService),
            new TreatmentPlanController(treatmentPlanService)
    ).build();

    @Test
    void shouldCoverPatientToTreatmentPlanWorkflowApis() throws Exception {
        when(patientService.createPatient(any(PatientCreateRequest.class))).thenReturn(PatientDetailVO.builder().build());
        when(appointmentService.createAppointment(any(AppointmentCreateRequest.class))).thenReturn(new AppointmentDetailVO());
        when(appointmentService.checkInAppointment(eq(3001L), any(AppointmentCheckInRequest.class))).thenReturn(new AppointmentDetailVO());
        when(medicalRecordService.createMedicalRecord(any(MedicalRecordCreateRequest.class))).thenReturn(new MedicalRecordDetailVO());
        when(treatmentPlanService.createTreatmentPlan(any(TreatmentPlanCreateRequest.class))).thenReturn(new TreatmentPlanDetailVO());

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientName": "张三",
                                  "mobile": "13800000001",
                                  "sourceCode": "ONLINE",
                                  "firstClinicId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(patientService).createPatient(any(PatientCreateRequest.class));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clinicId": 1,
                                  "patientId": 1001,
                                  "doctorId": 2001,
                                  "sourceType": "MANUAL",
                                  "appointmentType": "INITIAL",
                                  "appointmentDate": "2026-04-09",
                                  "startTime": "2026-04-09T09:00:00",
                                  "endTime": "2026-04-09T09:30:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(appointmentService).createAppointment(any(AppointmentCreateRequest.class));

        mockMvc.perform(put("/api/appointments/{id}/check-in", 3001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "remark": "按时到诊"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(appointmentService).checkInAppointment(eq(3001L), any(AppointmentCheckInRequest.class));

        mockMvc.perform(post("/api/emr/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clinicId": 1,
                                  "patientId": 1001,
                                  "appointmentId": 3001,
                                  "doctorId": 2001,
                                  "visitType": "FOLLOW_UP",
                                  "visitDate": "2026-04-09T10:00:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(medicalRecordService).createMedicalRecord(any(MedicalRecordCreateRequest.class));

        mockMvc.perform(post("/api/emr/treatment-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clinicId": 1,
                                  "patientId": 1001,
                                  "medicalRecordId": 4001,
                                  "doctorId": 2001,
                                  "planName": "正畸一期计划",
                                  "items": [
                                    {
                                      "itemCode": "ORTHO-001",
                                      "itemName": "托槽调整",
                                      "unitPrice": 500,
                                      "quantity": 1
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(treatmentPlanService).createTreatmentPlan(any(TreatmentPlanCreateRequest.class));
    }
}
