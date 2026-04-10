package com.company.dental.modules.workflow;

import com.company.dental.modules.sms.controller.SmsTaskController;
import com.company.dental.modules.sms.dto.SmsTaskCreateRequest;
import com.company.dental.modules.sms.service.SmsTaskService;
import com.company.dental.modules.sms.vo.SmsTaskDetailVO;
import com.company.dental.modules.sms.vo.SmsTemplateVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SmsWorkflowControllerTest {

    private final SmsTaskService smsTaskService = Mockito.mock(SmsTaskService.class);

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
            new SmsTaskController(smsTaskService)
    ).build();

    @Test
    void shouldCoverSmsTaskWorkflowApis() throws Exception {
        when(smsTaskService.listTemplates()).thenReturn(List.of(new SmsTemplateVO()));
        when(smsTaskService.createTask(any(SmsTaskCreateRequest.class))).thenReturn(new SmsTaskDetailVO());
        when(smsTaskService.getTaskDetail(eq(7001L))).thenReturn(new SmsTaskDetailVO());

        mockMvc.perform(get("/api/sms/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(smsTaskService).listTemplates();

        mockMvc.perform(post("/api/sms/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clinicId": 1,
                                  "bizType": "APPOINTMENT_REMINDER",
                                  "bizId": 3001,
                                  "patientId": 1001,
                                  "mobile": "13800000001",
                                  "templateId": 1,
                                  "templateParams": {
                                    "patientName": "张三"
                                  },
                                  "scheduledAt": "2026-04-09T15:30:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(smsTaskService).createTask(any(SmsTaskCreateRequest.class));

        mockMvc.perform(get("/api/sms/tasks/{id}", 7001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
        verify(smsTaskService).getTaskDetail(7001L);
    }
}
