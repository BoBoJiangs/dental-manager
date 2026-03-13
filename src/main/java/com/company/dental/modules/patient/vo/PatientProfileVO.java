package com.company.dental.modules.patient.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PatientProfileVO {

    private String bloodType;
    private String allergyHistory;
    private String pastHistory;
    private String familyHistory;
    private String pregnancyStatus;
    private String smokingStatus;
    private String drinkingStatus;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String remark;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
