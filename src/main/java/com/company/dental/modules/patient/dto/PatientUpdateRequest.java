package com.company.dental.modules.patient.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PatientUpdateRequest {

    @NotBlank(message = "患者姓名不能为空")
    private String patientName;

    private Integer gender;

    private LocalDate birthday;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String mobile;

    private String idNo;

    @NotBlank(message = "来源不能为空")
    private String sourceCode;

    @NotNull(message = "首诊门诊不能为空")
    private Long firstClinicId;

    private Integer memberStatus;

    private Integer patientStatus;

    private String remark;

    private List<Long> tagIds;

    @Valid
    private PatientProfileCreateRequest profile;
}
