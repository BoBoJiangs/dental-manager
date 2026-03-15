package com.company.dental.modules.auth.service;

import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.auth.dto.LoginRequest;
import com.company.dental.modules.auth.vo.LoginResponse;
import com.company.dental.modules.auth.vo.PermissionMetaVO;

public interface AuthService {

    LoginResponse login(LoginRequest request, String loginIp);

    LoginUser currentUser();

    LoginResponse refreshToken();

    void logout();

    PermissionMetaVO currentPermissions();
}
