package com.company.dental.modules.patient.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDoctorVO {

    private Long doctorId;
    private String doctorName;
    private Long clinicId;
    private String clinicName;
    private String jobTitle;
    private String specialty;
    private String relationType;
    private LocalDateTime firstRelatedAt;
    private LocalDateTime latestRelatedAt;
}
