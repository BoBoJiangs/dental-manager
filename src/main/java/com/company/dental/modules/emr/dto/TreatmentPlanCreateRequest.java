package com.company.dental.modules.emr.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class TreatmentPlanCreateRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    @NotNull(message = "患者不能为空")
    private Long patientId;

    @NotNull(message = "病历不能为空")
    private Long medicalRecordId;

    @NotNull(message = "医生不能为空")
    private Long doctorId;

    @NotBlank(message = "计划名称不能为空")
    private String planName;

    private String planStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String remark;

    @Valid
    @NotEmpty(message = "治疗项目不能为空")
    private List<TreatmentItemCreateRequest> items;
}
