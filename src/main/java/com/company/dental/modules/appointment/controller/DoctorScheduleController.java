package com.company.dental.modules.appointment.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.appointment.dto.DoctorScheduleCreateRequest;
import com.company.dental.modules.appointment.query.DoctorScheduleQuery;
import com.company.dental.modules.appointment.service.DoctorScheduleService;
import com.company.dental.modules.appointment.vo.DoctorScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "医生排班")
@RestController
@RequestMapping("/api/appointments/schedules")
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    public DoctorScheduleController(DoctorScheduleService doctorScheduleService) {
        this.doctorScheduleService = doctorScheduleService;
    }

    @Operation(summary = "查询医生排班")
    @GetMapping
    public ApiResponse<List<DoctorScheduleVO>> list(@ModelAttribute DoctorScheduleQuery query) {
        return ApiResponse.success(doctorScheduleService.listSchedules(query));
    }

    @Operation(summary = "创建医生排班")
    @PreAuthorize("hasAuthority('APPOINTMENT_EDIT')")
    @PostMapping
    public ApiResponse<DoctorScheduleVO> create(@Valid @RequestBody DoctorScheduleCreateRequest request) {
        return ApiResponse.success(doctorScheduleService.createSchedule(request));
    }
}
