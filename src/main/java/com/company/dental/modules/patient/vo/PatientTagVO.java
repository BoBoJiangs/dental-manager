package com.company.dental.modules.patient.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientTagVO {

    private Long id;
    private String tagCode;
    private String tagName;
    private String tagColor;
}
