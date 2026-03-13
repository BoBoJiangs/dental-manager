package com.company.dental.modules.patient.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PatientDetailVO {

    private Long id;
    private Long orgId;
    private String patientCode;
    private String patientName;
    private Integer gender;
    private LocalDate birthday;
    private String mobile;
    private String idNo;
    private String sourceCode;
    private Long firstClinicId;
    private LocalDateTime firstVisitAt;
    private LocalDateTime latestVisitAt;
    private Integer memberStatus;
    private Integer patientStatus;
    private String remark;
    private PatientProfileVO profile;
}
