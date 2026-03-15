package com.company.dental.modules.appointment.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DoctorScheduleVO {

    private Long id;
    private Long clinicId;
    private String clinicName;
    private Long doctorId;
    private String doctorName;
    private Long roomId;
    private String roomName;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String scheduleType;
    private Integer maxAppointmentCount;
    private Integer status;
    private String remark;
}
