package com.company.dental.modules.patient.dto;

import lombok.Data;

@Data
public class PatientProfileCreateRequest {

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
}
