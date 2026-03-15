package com.company.dental.framework.security;

import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DataScopeHelper {

    public DataScopeType resolve(LoginUser loginUser) {
        if (loginUser == null) {
            return DataScopeType.ALL;
        }
        List<String> dataScopes = loginUser.getDataScopes();
        if (dataScopes != null) {
            if (dataScopes.contains("ALL")) {
                return DataScopeType.ALL;
            }
            if (dataScopes.contains("CLINIC")) {
                return DataScopeType.CLINIC;
            }
            if (dataScopes.contains("SELF_PATIENT")) {
                return DataScopeType.SELF_PATIENT;
            }
        }
        List<String> roles = loginUser.getRoles();
        if (roles == null || roles.isEmpty()) {
            return loginUser.getClinicId() == null ? DataScopeType.ALL : DataScopeType.CLINIC;
        }
        if (roles.contains("GROUP_ADMIN")) {
            return DataScopeType.ALL;
        }
        if (roles.contains("CLINIC_ADMIN") || roles.contains("FRONT_DESK") || roles.contains("FINANCE") || roles.contains("NURSE")) {
            return DataScopeType.CLINIC;
        }
        if (roles.contains("DOCTOR")) {
            return DataScopeType.SELF_PATIENT;
        }
        return loginUser.getClinicId() == null ? DataScopeType.ALL : DataScopeType.CLINIC;
    }

    public List<Long> resolveClinicIds(LoginUser loginUser) {
        if (loginUser == null) {
            return Collections.emptyList();
        }
        if (loginUser.getClinicIds() != null && !loginUser.getClinicIds().isEmpty()) {
            return loginUser.getClinicIds();
        }
        if (loginUser.getClinicId() != null) {
            return List.of(loginUser.getClinicId());
        }
        return Collections.emptyList();
    }

    public void assertClinicAccess(LoginUser loginUser, Long clinicId) {
        if (clinicId == null) {
            return;
        }
        DataScopeType scopeType = resolve(loginUser);
        if (scopeType == DataScopeType.ALL) {
            return;
        }
        List<Long> clinicIds = resolveClinicIds(loginUser);
        if (!clinicIds.contains(clinicId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问该门诊数据");
        }
    }
}
