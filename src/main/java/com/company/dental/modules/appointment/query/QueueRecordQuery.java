package com.company.dental.modules.appointment.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class QueueRecordQuery {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate queueDate;

    private Long clinicId;

    private Long doctorId;

    private String queueStatus;
}
