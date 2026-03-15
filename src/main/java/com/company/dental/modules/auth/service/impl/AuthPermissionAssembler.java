package com.company.dental.modules.auth.service.impl;

import com.company.dental.framework.security.LoginUser;
import com.company.dental.framework.security.RolePermissionMatrix;
import com.company.dental.modules.auth.mapper.AuthUserMapper;
import com.company.dental.modules.auth.vo.PermissionMetaVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthPermissionAssembler {

    private final RolePermissionMatrix rolePermissionMatrix;
    private final AuthUserMapper authUserMapper;

    public AuthPermissionAssembler(RolePermissionMatrix rolePermissionMatrix,
                                   AuthUserMapper authUserMapper) {
        this.rolePermissionMatrix = rolePermissionMatrix;
        this.authUserMapper = authUserMapper;
    }

    public PermissionMetaVO build(LoginUser loginUser) {
        List<String> roles = loginUser.getRoles() == null ? List.of() : loginUser.getRoles();
        List<String> dataScopes = loginUser.getDataScopes() == null ? List.of() : loginUser.getDataScopes();
        List<String> customMenuPermissions = authUserMapper.selectPermissionCodesByUserId(loginUser.getUserId(), "MENU");
        List<String> customButtonPermissions = authUserMapper.selectPermissionCodesByUserId(loginUser.getUserId(), "BUTTON");
        Set<String> menuRolesWithCustomPermissions = Set.copyOf(authUserMapper.selectRoleCodesWithCustomPermissions(loginUser.getUserId(), "MENU"));
        Set<String> buttonRolesWithCustomPermissions = Set.copyOf(authUserMapper.selectRoleCodesWithCustomPermissions(loginUser.getUserId(), "BUTTON"));
        return PermissionMetaVO.builder()
                .roles(roles)
                .dataScopes(dataScopes)
                .menuPermissions(resolvePermissions(roles, customMenuPermissions, menuRolesWithCustomPermissions, true))
                .buttonPermissions(resolvePermissions(roles, customButtonPermissions, buttonRolesWithCustomPermissions, false))
                .build();
    }

    private List<String> resolvePermissions(List<String> roles,
                                            List<String> customPermissions,
                                            Set<String> rolesWithCustomPermissions,
                                            boolean menu) {
        Set<String> merged = new java.util.LinkedHashSet<>(customPermissions == null ? List.of() : customPermissions);
        List<String> fallbackRoles = roles.stream()
                .filter(role -> !rolesWithCustomPermissions.contains(role))
                .toList();
        merged.addAll(menu
                ? rolePermissionMatrix.resolveMenuPermissions(fallbackRoles)
                : rolePermissionMatrix.resolveButtonPermissions(fallbackRoles));
        return merged.stream().sorted().collect(Collectors.toList());
    }
}
