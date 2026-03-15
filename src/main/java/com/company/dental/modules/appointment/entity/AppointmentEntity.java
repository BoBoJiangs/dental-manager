package com.company.dental.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.company.dental.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("appointment")
public class AppointmentEntity extends BaseEntity {

    private Long orgId;
    private Long clinicId;
    private String appointmentNo;
    private Long patientId;
    private Long doctorId;
    private Long roomId;
    private String sourceType;
    private String appointmentType;
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String treatmentItemName;
    private String status;
    private String cancelReason;
    private LocalDateTime arrivedAt;
    private Integer noShowFlag;
    private String remark;
}
