package com.company.dental.modules.emr.vo;

import lombok.Data;

@Data
public class DiagnosisRecordVO {

    private Long id;
    private String diagnosisType;
    private String diagnosisCode;
    private String diagnosisName;
    private String toothPosition;
    private String diagnosisDesc;
    private Integer sortNo;
}
