package com.company.dental.modules.imaging.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.imaging.dto.PatientImageCreateRequest;
import com.company.dental.modules.imaging.query.PatientImageQuery;
import com.company.dental.modules.imaging.service.PatientImageService;
import com.company.dental.modules.imaging.vo.PatientImageVO;
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

@Tag(name = "影像管理")
@RestController
@RequestMapping("/api/imaging/patient-images")
public class PatientImageController {

    private final PatientImageService patientImageService;

    public PatientImageController(PatientImageService patientImageService) {
        this.patientImageService = patientImageService;
    }

    @Operation(summary = "查询患者影像列表")
    @GetMapping
    public ApiResponse<List<PatientImageVO>> list(@Valid @ModelAttribute PatientImageQuery query) {
        return ApiResponse.success(patientImageService.listPatientImages(query));
    }

    @Operation(summary = "新建患者影像记录")
    @PreAuthorize("hasAuthority('IMAGING_EDIT')")
    @PostMapping
    public ApiResponse<PatientImageVO> create(@Valid @RequestBody PatientImageCreateRequest request) {
        return ApiResponse.success(patientImageService.createPatientImage(request));
    }
}
