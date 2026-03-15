package com.company.dental.modules.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StaffSaveRequest {

    @NotBlank(message = "员工编码不能为空")
    private String staffCode;

    @NotBlank(message = "员工姓名不能为空")
    private String staffName;

    private Integer gender;

    private String mobile;

    private String idNo;

    private String jobTitle;

    @NotBlank(message = "岗位类型不能为空")
    private String staffType;

    private Long mainClinicId;

    private Long deptId;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private LocalDate entryDate;

    private LocalDate leaveDate;

    @NotNull(message = "医生标记不能为空")
    private Integer isDoctor;

    private String specialty;

    private String remark;
}
