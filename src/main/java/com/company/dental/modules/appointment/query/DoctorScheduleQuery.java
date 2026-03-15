package com.company.dental.modules.appointment.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DoctorScheduleQuery {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    private Long clinicId;

    private Long doctorId;

    private Integer status;
}
