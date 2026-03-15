package com.company.dental.framework.security;

import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Builder
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long userId;
    private final Long orgId;
    private final Long staffId;
    private final String username;
    private final String accountType;
    private final Long clinicId;
    private final List<Long> clinicIds;
    private final List<String> roles;
    private final List<String> dataScopes;
}
