package com.company.dental.modules.auth.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.auth.dto.LoginRequest;
import com.company.dental.modules.auth.service.AuthService;
import com.company.dental.modules.auth.vo.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "用户名密码登录")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        return ApiResponse.success(authService.login(request, httpServletRequest.getRemoteAddr()));
    }

    @Operation(summary = "认证健康检查")
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("auth-ok");
    }
}
