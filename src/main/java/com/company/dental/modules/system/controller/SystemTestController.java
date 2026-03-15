package com.company.dental.modules.system.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.framework.security.AuthContext;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.system.service.SystemOverviewService;
import com.company.dental.modules.system.vo.WorkbenchOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统测试")
@RestController
@RequestMapping("/api/system")
public class SystemTestController {

    private final SystemOverviewService systemOverviewService;

    public SystemTestController(SystemOverviewService systemOverviewService) {
        this.systemOverviewService = systemOverviewService;
    }

    @Operation(summary = "受保护接口测试")
    @GetMapping("/me")
    public ApiResponse<LoginUser> currentUser() {
        return ApiResponse.success(AuthContext.get());
    }

    @Operation(summary = "查询工作台概览")
    @GetMapping("/workbench")
    public ApiResponse<WorkbenchOverviewVO> workbench() {
        return ApiResponse.success(systemOverviewService.getWorkbenchOverview());
    }
}
