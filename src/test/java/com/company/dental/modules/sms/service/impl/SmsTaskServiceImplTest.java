package com.company.dental.modules.sms.service.impl;

import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.DataScopeHelper;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.patient.mapper.PatientMapper;
import com.company.dental.modules.sms.dto.SmsTaskCreateRequest;
import com.company.dental.modules.sms.entity.SmsTaskEntity;
import com.company.dental.modules.sms.entity.SmsTemplateEntity;
import com.company.dental.modules.sms.mapper.SmsSendRecordMapper;
import com.company.dental.modules.sms.mapper.SmsTaskMapper;
import com.company.dental.modules.sms.mapper.SmsTemplateMapper;
import com.company.dental.modules.sms.service.SmsTaskDispatchService;
import com.company.dental.modules.sms.vo.SmsTaskDetailVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsTaskServiceImplTest {

    private final SmsTemplateMapper smsTemplateMapper = Mockito.mock(SmsTemplateMapper.class);
    private final SmsTaskMapper smsTaskMapper = Mockito.mock(SmsTaskMapper.class);
    private final SmsSendRecordMapper smsSendRecordMapper = Mockito.mock(SmsSendRecordMapper.class);
    private final PatientMapper patientMapper = Mockito.mock(PatientMapper.class);
    private final DataScopeHelper dataScopeHelper = new DataScopeHelper();
    private final SmsTaskDispatchService smsTaskDispatchService = Mockito.mock(SmsTaskDispatchService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SmsTaskServiceImpl service = new SmsTaskServiceImpl(
            smsTemplateMapper,
            smsTaskMapper,
            smsSendRecordMapper,
            patientMapper,
            dataScopeHelper,
            smsTaskDispatchService,
            objectMapper
    );

    @AfterEach
    void clearAuthContext() {
        AuthContext.clear();
    }

    @Test
    void shouldEnqueueImmediatelyWhenScheduledAtIsNull() {
        AuthContext.set(LoginUser.builder()
                .userId(77L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        SmsTemplateEntity template = new SmsTemplateEntity();
        template.setId(1L);
        template.setOrgId(1L);
        template.setEnabledFlag(1);
        when(smsTemplateMapper.selectOne(any())).thenReturn(template);

        doAnswer(invocation -> {
            SmsTaskEntity entity = invocation.getArgument(0);
            entity.setId(7001L);
            return 1;
        }).when(smsTaskMapper).insert(any(SmsTaskEntity.class));

        SmsTaskDetailVO detail = new SmsTaskDetailVO();
        detail.setId(7001L);
        detail.setTemplateParamsJson("{\"patientName\":\"张三\"}");
        when(smsTaskMapper.selectSmsTaskDetailById(eq(7001L), eq(1L), any(), any(), any())).thenReturn(detail);
        when(smsSendRecordMapper.selectBySmsTaskId(7001L)).thenReturn(Collections.emptyList());

        SmsTaskCreateRequest request = new SmsTaskCreateRequest();
        request.setClinicId(1L);
        request.setBizType("APPOINTMENT_REMINDER");
        request.setBizId(3001L);
        request.setMobile("13800000001");
        request.setTemplateId(1L);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("patientName", "张三");
        request.setTemplateParams(params);

        service.createTask(request);

        ArgumentCaptor<SmsTaskEntity> taskCaptor = ArgumentCaptor.forClass(SmsTaskEntity.class);
        verify(smsTaskMapper).insert(taskCaptor.capture());
        SmsTaskEntity insertedTask = taskCaptor.getValue();
        assertEquals("PENDING", insertedTask.getTaskStatus());
        assertEquals(0, insertedTask.getRetryCount());
        assertTrue(insertedTask.getTemplateParams().contains("patientName"));

        verify(smsTaskDispatchService).enqueue(7001L);
    }

    @Test
    void shouldNotEnqueueWhenScheduledAtInFuture() {
        AuthContext.set(LoginUser.builder()
                .userId(77L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        SmsTemplateEntity template = new SmsTemplateEntity();
        template.setId(1L);
        template.setOrgId(1L);
        template.setEnabledFlag(1);
        when(smsTemplateMapper.selectOne(any())).thenReturn(template);

        doAnswer(invocation -> {
            SmsTaskEntity entity = invocation.getArgument(0);
            entity.setId(7002L);
            return 1;
        }).when(smsTaskMapper).insert(any(SmsTaskEntity.class));

        SmsTaskDetailVO detail = new SmsTaskDetailVO();
        detail.setId(7002L);
        detail.setTemplateParamsJson("{}");
        when(smsTaskMapper.selectSmsTaskDetailById(eq(7002L), eq(1L), any(), any(), any())).thenReturn(detail);
        when(smsSendRecordMapper.selectBySmsTaskId(7002L)).thenReturn(Collections.emptyList());

        SmsTaskCreateRequest request = new SmsTaskCreateRequest();
        request.setClinicId(1L);
        request.setBizType("APPOINTMENT_REMINDER");
        request.setMobile("13800000001");
        request.setTemplateId(1L);
        request.setScheduledAt(LocalDateTime.now().plusDays(1));

        service.createTask(request);

        verify(smsTaskDispatchService, never()).enqueue(any());
    }

    @Test
    void shouldRejectTaskCreationWhenTemplateNotFound() {
        AuthContext.set(LoginUser.builder()
                .userId(77L)
                .orgId(1L)
                .dataScopes(List.of("ALL"))
                .build());

        when(smsTemplateMapper.selectOne(any())).thenReturn(null);

        SmsTaskCreateRequest request = new SmsTaskCreateRequest();
        request.setClinicId(1L);
        request.setBizType("APPOINTMENT_REMINDER");
        request.setMobile("13800000001");
        request.setTemplateId(999L);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.createTask(request));
        assertEquals("短信模板不存在", exception.getMessage());
        verify(smsTaskMapper, never()).insert(any(SmsTaskEntity.class));
        verify(smsTaskDispatchService, never()).enqueue(any());
    }
}
