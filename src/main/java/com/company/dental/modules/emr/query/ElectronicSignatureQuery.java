package com.company.dental.modules.emr.query;

import lombok.Data;

@Data
public class ElectronicSignatureQuery {

    private Long clinicId;
    private Long patientId;
    private Long medicalRecordId;
    private String signerType;
}
