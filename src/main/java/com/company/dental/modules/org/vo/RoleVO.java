package com.company.dental.modules.org.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoleVO {

    private Long id;
    private String roleCode;
    private String roleName;
    private String dataScope;
    private Integer status;
    private String remark;
    private List<String> menuPermissions;
    private List<String> buttonPermissions;
}
