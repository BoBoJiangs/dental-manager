package com.company.dental.modules.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PermissionMetaVO {

    private List<String> roles;
    private List<String> dataScopes;
    private List<String> menuPermissions;
    private List<String> buttonPermissions;
}
