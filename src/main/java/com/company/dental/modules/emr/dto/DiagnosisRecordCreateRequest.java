package com.company.dental.modules.emr.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiagnosisRecordCreateRequest {

    @NotBlank(message = "诊断类型不能为空")
    private String diagnosisType;

    private String diagnosisCode;

    @NotBlank(message = "诊断名称不能为空")
    private String diagnosisName;

    private String toothPosition;

    private String diagnosisDesc;
}
