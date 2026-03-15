package com.company.dental.modules.dentalchart.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.dentalchart.dto.DentalChartSaveRequest;
import com.company.dental.modules.dentalchart.service.DentalChartService;
import com.company.dental.modules.dentalchart.vo.DentalChartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "牙位图管理")
@RestController
@RequestMapping("/api/dentalcharts")
public class DentalChartController {

    private final DentalChartService dentalChartService;

    public DentalChartController(DentalChartService dentalChartService) {
        this.dentalChartService = dentalChartService;
    }

    @Operation(summary = "按病历查询牙位图")
    @GetMapping("/medical-records/{medicalRecordId}")
    public ApiResponse<DentalChartVO> getByMedicalRecordId(@PathVariable Long medicalRecordId) {
        return ApiResponse.success(dentalChartService.getByMedicalRecordId(medicalRecordId));
    }

    @Operation(summary = "按病历保存牙位图")
    @PreAuthorize("hasAuthority('DENTALCHART_EDIT')")
    @PutMapping("/medical-records/{medicalRecordId}")
    public ApiResponse<DentalChartVO> saveByMedicalRecordId(@PathVariable Long medicalRecordId,
                                                            @Valid @RequestBody DentalChartSaveRequest request) {
        return ApiResponse.success(dentalChartService.saveByMedicalRecordId(medicalRecordId, request));
    }
}
