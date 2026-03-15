package com.company.dental.modules.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresInSeconds;
    private Long userId;
    private Long orgId;
    private Long staffId;
    private String username;
    private String accountType;
    private Long clinicId;
    private List<Long> clinicIds;
    private List<String> roles;
    private List<String> dataScopes;
}
