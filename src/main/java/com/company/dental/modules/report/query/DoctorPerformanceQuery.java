package com.company.dental.modules.report.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DoctorPerformanceQuery {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate statDate;

    private Long clinicId;

    private Long doctorId;
}
