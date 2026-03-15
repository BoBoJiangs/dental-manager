package com.company.dental.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.company.dental.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("doctor_schedule")
public class DoctorScheduleEntity extends BaseEntity {

    private Long orgId;
    private Long clinicId;
    private Long doctorId;
    private Long roomId;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String scheduleType;
    private Integer maxAppointmentCount;
    private Integer status;
    private String remark;
}
