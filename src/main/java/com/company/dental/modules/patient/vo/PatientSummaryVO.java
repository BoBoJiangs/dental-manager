package com.company.dental.modules.patient.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientSummaryVO {

    private long total;
    private long activeCount;
}
