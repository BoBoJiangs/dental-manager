package com.company.dental.modules.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppointmentCreateRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    @NotNull(message = "患者不能为空")
    private Long patientId;

    @NotNull(message = "医生不能为空")
    private Long doctorId;

    private Long roomId;

    @NotBlank(message = "预约来源不能为空")
    private String sourceType;

    @NotBlank(message = "预约类型不能为空")
    private String appointmentType;

    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    private String treatmentItemName;

    private String remark;
}
