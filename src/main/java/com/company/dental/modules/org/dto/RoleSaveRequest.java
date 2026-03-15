package com.company.dental.modules.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleSaveRequest {

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "数据范围不能为空")
    private String dataScope;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;

    private List<String> menuPermissions;

    private List<String> buttonPermissions;
}
