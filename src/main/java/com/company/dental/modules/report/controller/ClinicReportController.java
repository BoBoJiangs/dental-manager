package com.company.dental.modules.report.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.report.query.ClinicOverviewQuery;
import com.company.dental.modules.report.query.DoctorPerformanceQuery;
import com.company.dental.modules.report.service.ClinicReportService;
import com.company.dental.modules.report.vo.ClinicOverviewVO;
import com.company.dental.modules.report.vo.DoctorPerformanceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "报表中心")
@RestController
@RequestMapping("/api/reports")
public class ClinicReportController {

    private final ClinicReportService clinicReportService;

    public ClinicReportController(ClinicReportService clinicReportService) {
        this.clinicReportService = clinicReportService;
    }

    @Operation(summary = "查询门诊经营概览")
    @GetMapping("/clinic-overview")
    public ApiResponse<ClinicOverviewVO> clinicOverview(@ModelAttribute ClinicOverviewQuery query) {
        return ApiResponse.success(clinicReportService.getClinicOverview(query));
    }

    @Operation(summary = "查询医生业绩概览")
    @GetMapping("/doctor-performance")
    public ApiResponse<List<DoctorPerformanceVO>> doctorPerformance(@ModelAttribute DoctorPerformanceQuery query) {
        return ApiResponse.success(clinicReportService.listDoctorPerformance(query));
    }
}
