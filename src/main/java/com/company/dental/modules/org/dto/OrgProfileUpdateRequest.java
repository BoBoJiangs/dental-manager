package com.company.dental.modules.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrgProfileUpdateRequest {

    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    private String contactName;

    private String contactPhone;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;
}
