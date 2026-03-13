package com.company.dental.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.framework.security.JwtProperties;
import com.company.dental.framework.security.JwtTokenProvider;
import com.company.dental.framework.security.LoginUser;
import com.company.dental.modules.auth.dto.LoginRequest;
import com.company.dental.modules.auth.entity.AuthUserEntity;
import com.company.dental.modules.auth.mapper.AuthUserMapper;
import com.company.dental.modules.auth.service.AuthService;
import com.company.dental.modules.auth.vo.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthUserMapper authUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthServiceImpl(AuthUserMapper authUserMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           JwtProperties jwtProperties) {
        this.authUserMapper = authUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String loginIp) {
        AuthUserEntity user = authUserMapper.selectOne(new LambdaQueryWrapper<AuthUserEntity>()
                .eq(AuthUserEntity::getUsername, request.getUsername())
                .eq(AuthUserEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        List<String> roles = authUserMapper.selectRoleCodesByUserId(user.getId());
        Long clinicId = authUserMapper.selectPrimaryClinicId(user.getId());
        LoginUser loginUser = LoginUser.builder()
                .userId(user.getId())
                .orgId(user.getOrgId())
                .staffId(user.getStaffId())
                .username(user.getUsername())
                .accountType(user.getAccountType())
                .clinicId(clinicId)
                .roles(roles)
                .build();
        String accessToken = jwtTokenProvider.createAccessToken(loginUser);

        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(StringUtils.hasText(loginIp) ? loginIp : "unknown");
        authUserMapper.updateById(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType(jwtProperties.getTokenPrefix().trim())
                .expiresInSeconds(jwtProperties.getAccessTokenExpireMinutes() * 60)
                .userId(user.getId())
                .orgId(user.getOrgId())
                .staffId(user.getStaffId())
                .username(user.getUsername())
                .accountType(user.getAccountType())
                .clinicId(clinicId)
                .roles(roles)
                .build();
    }
}
