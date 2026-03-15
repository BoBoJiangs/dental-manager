package com.company.dental.modules.emr.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class MedicalRecordPageQuery {

    private Long patientId;
    private Long doctorId;
    private Long clinicId;
    private String recordStatus;
    private String keyword;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDateTo;

    @Min(value = 1, message = "页码不能小于 1")
    private long current = 1;

    @Min(value = 1, message = "分页大小不能小于 1")
    @Max(value = 100, message = "分页大小不能超过 100")
    private long size = 10;
}
