package com.company.dental.modules.imaging.query;

import lombok.Data;

@Data
public class PatientImageQuery {

    private Long patientId;

    private Long medicalRecordId;

    private String imageType;

    private String imageGroupType;
}
