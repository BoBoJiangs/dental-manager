package com.company.dental.modules.patient.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.api.PageResult;
import com.company.dental.modules.patient.dto.PatientCreateRequest;
import com.company.dental.modules.patient.query.PatientPageQuery;
import com.company.dental.modules.patient.service.PatientService;
import com.company.dental.modules.patient.vo.PatientDetailVO;
import com.company.dental.modules.patient.vo.PatientPageItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "患者管理")
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "分页查询患者")
    @GetMapping
    public ApiResponse<PageResult<PatientPageItemVO>> page(@Valid @ModelAttribute PatientPageQuery query) {
        return ApiResponse.success(patientService.pagePatients(query));
    }

    @Operation(summary = "查询患者详情")
    @GetMapping("/{id}")
    public ApiResponse<PatientDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(patientService.getPatientDetail(id));
    }

    @Operation(summary = "新建患者")
    @PostMapping
    public ApiResponse<PatientDetailVO> create(@Valid @RequestBody PatientCreateRequest request) {
        return ApiResponse.success(patientService.createPatient(request));
    }
}
