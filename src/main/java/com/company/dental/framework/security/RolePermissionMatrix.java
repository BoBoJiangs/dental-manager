package com.company.dental.framework.security;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RolePermissionMatrix {

    private static final List<String> ADMIN_MENUS = List.of(
            PermissionCodes.WORKBENCH_VIEW,
            PermissionCodes.PATIENT_VIEW,
            PermissionCodes.APPOINTMENT_VIEW,
            PermissionCodes.EMR_VIEW,
            PermissionCodes.IMAGING_VIEW,
            PermissionCodes.BILLING_VIEW,
            PermissionCodes.MEMBER_VIEW,
            PermissionCodes.SMS_VIEW,
            PermissionCodes.REPORT_VIEW,
            PermissionCodes.ORG_VIEW,
            PermissionCodes.ORG_ROLE_VIEW
    );

    private static final List<String> ADMIN_BUTTONS = List.of(
            PermissionCodes.PATIENT_EDIT,
            PermissionCodes.PATIENT_DELETE,
            PermissionCodes.APPOINTMENT_EDIT,
            PermissionCodes.APPOINTMENT_CHECKIN,
            PermissionCodes.EMR_EDIT,
            PermissionCodes.DENTALCHART_EDIT,
            PermissionCodes.IMAGING_EDIT,
            PermissionCodes.BILLING_CREATE,
            PermissionCodes.BILLING_PAYMENT,
            PermissionCodes.BILLING_REFUND,
            PermissionCodes.MEMBER_CREATE,
            PermissionCodes.MEMBER_OPERATE,
            PermissionCodes.SMS_CREATE,
            PermissionCodes.ORG_EDIT
    );

    private static final Map<String, List<String>> MENU_PERMISSIONS = Map.of(
            "GROUP_ADMIN", ADMIN_MENUS,
            "CLINIC_ADMIN", ADMIN_MENUS,
            "FRONT_DESK", List.of(
                    PermissionCodes.WORKBENCH_VIEW,
                    PermissionCodes.PATIENT_VIEW,
                    PermissionCodes.APPOINTMENT_VIEW,
                    PermissionCodes.BILLING_VIEW,
                    PermissionCodes.MEMBER_VIEW,
                    PermissionCodes.SMS_VIEW
            ),
            "DOCTOR", List.of(
                    PermissionCodes.WORKBENCH_VIEW,
                    PermissionCodes.PATIENT_VIEW,
                    PermissionCodes.APPOINTMENT_VIEW,
                    PermissionCodes.EMR_VIEW,
                    PermissionCodes.IMAGING_VIEW
            ),
            "FINANCE", List.of(
                    PermissionCodes.WORKBENCH_VIEW,
                    PermissionCodes.BILLING_VIEW,
                    PermissionCodes.MEMBER_VIEW,
                    PermissionCodes.REPORT_VIEW
            ),
            "NURSE", List.of(
                    PermissionCodes.WORKBENCH_VIEW,
                    PermissionCodes.PATIENT_VIEW,
                    PermissionCodes.APPOINTMENT_VIEW,
                    PermissionCodes.EMR_VIEW,
                    PermissionCodes.IMAGING_VIEW
            )
    );

    private static final Map<String, List<String>> BUTTON_PERMISSIONS = Map.of(
            "GROUP_ADMIN", ADMIN_BUTTONS,
            "CLINIC_ADMIN", ADMIN_BUTTONS,
            "FRONT_DESK", List.of(
                    PermissionCodes.PATIENT_EDIT,
                    PermissionCodes.APPOINTMENT_EDIT,
                    PermissionCodes.APPOINTMENT_CHECKIN,
                    PermissionCodes.BILLING_CREATE,
                    PermissionCodes.BILLING_PAYMENT,
                    PermissionCodes.MEMBER_CREATE,
                    PermissionCodes.MEMBER_OPERATE,
                    PermissionCodes.SMS_CREATE
            ),
            "DOCTOR", List.of(
                    PermissionCodes.PATIENT_EDIT,
                    PermissionCodes.EMR_EDIT,
                    PermissionCodes.DENTALCHART_EDIT,
                    PermissionCodes.IMAGING_EDIT
            ),
            "FINANCE", List.of(
                    PermissionCodes.BILLING_CREATE,
                    PermissionCodes.BILLING_PAYMENT,
                    PermissionCodes.BILLING_REFUND,
                    PermissionCodes.MEMBER_CREATE,
                    PermissionCodes.MEMBER_OPERATE
            ),
            "NURSE", List.of(
                    PermissionCodes.PATIENT_EDIT,
                    PermissionCodes.APPOINTMENT_EDIT,
                    PermissionCodes.APPOINTMENT_CHECKIN,
                    PermissionCodes.EMR_EDIT,
                    PermissionCodes.DENTALCHART_EDIT,
                    PermissionCodes.IMAGING_EDIT
            )
    );

    public Set<String> resolveMenuPermissions(Collection<String> roles) {
        return resolve(roles, MENU_PERMISSIONS);
    }

    public Set<String> allMenuPermissions() {
        return new LinkedHashSet<>(ADMIN_MENUS);
    }

    public Set<String> resolveButtonPermissions(Collection<String> roles) {
        return resolve(roles, BUTTON_PERMISSIONS);
    }

    public Set<String> allButtonPermissions() {
        return new LinkedHashSet<>(ADMIN_BUTTONS);
    }

    public Set<String> resolveAuthorities(Collection<String> roles) {
        Set<String> authorities = new LinkedHashSet<>();
        if (roles != null) {
            authorities.addAll(roles);
        }
        authorities.addAll(resolveMenuPermissions(roles));
        authorities.addAll(resolveButtonPermissions(roles));
        return authorities;
    }

    private Set<String> resolve(Collection<String> roles, Map<String, List<String>> mapping) {
        Set<String> permissions = new LinkedHashSet<>();
        if (roles == null || roles.isEmpty()) {
            return permissions;
        }
        for (String role : roles) {
            List<String> rolePermissions = mapping.get(role);
            if (rolePermissions != null) {
                permissions.addAll(rolePermissions);
            }
        }
        return permissions;
    }
}
