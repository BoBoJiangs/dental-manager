package com.company.dental.modules.appointment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DoctorScheduleCreateRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    @NotNull(message = "医生不能为空")
    private Long doctorId;

    private Long roomId;

    @NotNull(message = "排班日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @NotBlank(message = "排班类型不能为空")
    private String scheduleType;

    @Min(value = 0, message = "最大预约数不能小于 0")
    private Integer maxAppointmentCount;

    private Integer status;

    private String remark;
}
