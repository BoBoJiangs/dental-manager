package com.company.dental.modules.auth.service;

import com.company.dental.modules.auth.dto.LoginRequest;
import com.company.dental.modules.auth.vo.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request, String loginIp);
}
