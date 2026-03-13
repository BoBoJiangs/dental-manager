package com.company.dental.modules.patient.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PatientPageItemVO {

    private Long id;
    private Long orgId;
    private String patientCode;
    private String patientName;
    private Integer gender;
    private LocalDate birthday;
    private String mobile;
    private Integer memberStatus;
    private Integer patientStatus;
    private Long firstClinicId;
    private LocalDateTime firstVisitAt;
    private LocalDateTime latestVisitAt;
    private String remark;
}
