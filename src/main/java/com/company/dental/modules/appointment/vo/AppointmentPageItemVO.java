package com.company.dental.modules.appointment.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppointmentPageItemVO {

    private Long id;
    private Long orgId;
    private Long clinicId;
    private String clinicName;
    private String appointmentNo;
    private Long patientId;
    private String patientName;
    private String patientMobile;
    private Long doctorId;
    private String doctorName;
    private String sourceType;
    private String appointmentType;
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String treatmentItemName;
    private String status;
    private LocalDateTime arrivedAt;
    private Integer noShowFlag;
    private String remark;
}
