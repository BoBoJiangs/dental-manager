package com.company.dental.modules.appointment.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class QueueRecordVO {

    private Long id;
    private Long clinicId;
    private String clinicName;
    private Long appointmentId;
    private String appointmentNo;
    private LocalDate appointmentDate;
    private Long patientId;
    private String patientName;
    private String patientMobile;
    private Long doctorId;
    private String doctorName;
    private String queueNo;
    private String queueStatus;
    private LocalDateTime checkinAt;
    private LocalDateTime callAt;
    private LocalDateTime startTreatmentAt;
    private LocalDateTime endTreatmentAt;
    private String remark;
}
