package com.company.dental.modules.sms.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.api.PageResult;
import com.company.dental.modules.sms.dto.SmsTaskCreateRequest;
import com.company.dental.modules.sms.query.SmsTaskPageQuery;
import com.company.dental.modules.sms.service.SmsTaskService;
import com.company.dental.modules.sms.vo.SmsTaskDetailVO;
import com.company.dental.modules.sms.vo.SmsTaskPageItemVO;
import com.company.dental.modules.sms.vo.SmsTemplateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "短信中心")
@RestController
@RequestMapping("/api/sms")
public class SmsTaskController {

    private final SmsTaskService smsTaskService;

    public SmsTaskController(SmsTaskService smsTaskService) {
        this.smsTaskService = smsTaskService;
    }

    @Operation(summary = "查询短信模板")
    @GetMapping("/templates")
    public ApiResponse<List<SmsTemplateVO>> templates() {
        return ApiResponse.success(smsTaskService.listTemplates());
    }

    @Operation(summary = "分页查询短信任务")
    @GetMapping("/tasks")
    public ApiResponse<PageResult<SmsTaskPageItemVO>> pageTasks(@Valid @ModelAttribute SmsTaskPageQuery query) {
        return ApiResponse.success(smsTaskService.pageTasks(query));
    }

    @Operation(summary = "查询短信任务详情")
    @GetMapping("/tasks/{id}")
    public ApiResponse<SmsTaskDetailVO> taskDetail(@PathVariable Long id) {
        return ApiResponse.success(smsTaskService.getTaskDetail(id));
    }

    @Operation(summary = "创建短信任务")
    @PreAuthorize("hasAuthority('SMS_CREATE')")
    @PostMapping("/tasks")
    public ApiResponse<SmsTaskDetailVO> createTask(@Valid @RequestBody SmsTaskCreateRequest request) {
        return ApiResponse.success(smsTaskService.createTask(request));
    }
}
