package com.company.dental.framework.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataScopeHelperTest {

    private final DataScopeHelper dataScopeHelper = new DataScopeHelper();

    @Test
    void shouldResolveAllByRole() {
        LoginUser loginUser = LoginUser.builder()
                .roles(List.of("GROUP_ADMIN"))
                .build();

        assertEquals(DataScopeType.ALL, dataScopeHelper.resolve(loginUser));
    }

    @Test
    void shouldResolveClinicByDataScope() {
        LoginUser loginUser = LoginUser.builder()
                .dataScopes(List.of("CLINIC"))
                .clinicIds(List.of(1L, 2L))
                .build();

        assertEquals(DataScopeType.CLINIC, dataScopeHelper.resolve(loginUser));
        assertEquals(List.of(1L, 2L), dataScopeHelper.resolveClinicIds(loginUser));
    }

    @Test
    void shouldResolveSelfPatientByDoctorRole() {
        LoginUser loginUser = LoginUser.builder()
                .roles(List.of("DOCTOR"))
                .staffId(4L)
                .build();

        assertEquals(DataScopeType.SELF_PATIENT, dataScopeHelper.resolve(loginUser));
    }
}
