package com.company.dental.modules.report.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ClinicOverviewQuery {

    private Long clinicId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate statDate;
}
