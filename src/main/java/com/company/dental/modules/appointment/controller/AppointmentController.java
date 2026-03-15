package com.company.dental.modules.appointment.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.api.PageResult;
import com.company.dental.modules.appointment.dto.AppointmentCancelRequest;
import com.company.dental.modules.appointment.dto.AppointmentCheckInRequest;
import com.company.dental.modules.appointment.dto.AppointmentCreateRequest;
import com.company.dental.modules.appointment.dto.AppointmentRescheduleRequest;
import com.company.dental.modules.appointment.query.AppointmentPageQuery;
import com.company.dental.modules.appointment.service.AppointmentService;
import com.company.dental.modules.appointment.vo.AppointmentDetailVO;
import com.company.dental.modules.appointment.vo.AppointmentPageItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "预约管理")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "分页查询预约")
    @GetMapping
    public ApiResponse<PageResult<AppointmentPageItemVO>> page(@Valid @ModelAttribute AppointmentPageQuery query) {
        return ApiResponse.success(appointmentService.pageAppointments(query));
    }

    @Operation(summary = "查询预约详情")
    @GetMapping("/{id}")
    public ApiResponse<AppointmentDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(appointmentService.getAppointmentDetail(id));
    }

    @Operation(summary = "创建预约")
    @PreAuthorize("hasAuthority('APPOINTMENT_EDIT')")
    @PostMapping
    public ApiResponse<AppointmentDetailVO> create(@Valid @RequestBody AppointmentCreateRequest request) {
        return ApiResponse.success(appointmentService.createAppointment(request));
    }

    @Operation(summary = "改约")
    @PreAuthorize("hasAuthority('APPOINTMENT_EDIT')")
    @PutMapping("/{id}/reschedule")
    public ApiResponse<AppointmentDetailVO> reschedule(@PathVariable Long id,
                                                       @Valid @RequestBody AppointmentRescheduleRequest request) {
        return ApiResponse.success(appointmentService.rescheduleAppointment(id, request));
    }

    @Operation(summary = "取消预约")
    @PreAuthorize("hasAuthority('APPOINTMENT_EDIT')")
    @PutMapping("/{id}/cancel")
    public ApiResponse<AppointmentDetailVO> cancel(@PathVariable Long id,
                                                   @Valid @RequestBody AppointmentCancelRequest request) {
        return ApiResponse.success(appointmentService.cancelAppointment(id, request));
    }

    @Operation(summary = "到诊签到")
    @PreAuthorize("hasAuthority('APPOINTMENT_CHECKIN')")
    @PutMapping("/{id}/check-in")
    public ApiResponse<AppointmentDetailVO> checkIn(@PathVariable Long id,
                                                    @Valid @RequestBody AppointmentCheckInRequest request) {
        return ApiResponse.success(appointmentService.checkInAppointment(id, request));
    }
}
