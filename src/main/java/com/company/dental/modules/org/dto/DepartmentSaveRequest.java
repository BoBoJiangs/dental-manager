package com.company.dental.modules.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepartmentSaveRequest {

    @NotNull(message = "所属门诊不能为空")
    private Long clinicId;

    @NotBlank(message = "科室编码不能为空")
    private String deptCode;

    @NotBlank(message = "科室名称不能为空")
    private String deptName;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private Integer sortNo;

    private String remark;
}
