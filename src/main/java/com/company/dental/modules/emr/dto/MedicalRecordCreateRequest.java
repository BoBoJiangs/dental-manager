package com.company.dental.modules.emr.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MedicalRecordCreateRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    @NotNull(message = "患者不能为空")
    private Long patientId;

    private Long appointmentId;

    @NotNull(message = "医生不能为空")
    private Long doctorId;

    private Long assistantId;

    @NotBlank(message = "就诊类型不能为空")
    private String visitType;

    @NotNull(message = "就诊时间不能为空")
    private LocalDateTime visitDate;

    private String chiefComplaint;
    private String presentIllness;
    private String oralExamination;
    private String auxiliaryExamination;
    private String preliminaryDiagnosis;
    private String treatmentAdvice;
    private String doctorAdvice;
    private String revisitAdvice;
    private LocalDate nextVisitDate;
    private String recordStatus;
    private String remark;

    @Valid
    private List<DiagnosisRecordCreateRequest> diagnoses;
}
