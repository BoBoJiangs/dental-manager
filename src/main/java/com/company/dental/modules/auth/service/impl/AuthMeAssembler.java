package com.company.dental.modules.auth.service.impl;

import com.company.dental.framework.security.JwtProperties;
import com.company.dental.framework.security.JwtTokenProvider;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.auth.vo.LoginResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMeAssembler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthMeAssembler(JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    public LoginResponse buildRefreshResponse(LoginUser loginUser) {
        return LoginResponse.builder()
                .accessToken(jwtTokenProvider.createAccessToken(loginUser))
                .tokenType(jwtProperties.getTokenPrefix().trim())
                .expiresInSeconds(jwtProperties.getAccessTokenExpireMinutes() * 60)
                .userId(loginUser.getUserId())
                .orgId(loginUser.getOrgId())
                .staffId(loginUser.getStaffId())
                .username(loginUser.getUsername())
                .accountType(loginUser.getAccountType())
                .clinicId(loginUser.getClinicId())
                .clinicIds(loginUser.getClinicIds())
                .roles(loginUser.getRoles())
                .dataScopes(loginUser.getDataScopes())
                .build();
    }
}
