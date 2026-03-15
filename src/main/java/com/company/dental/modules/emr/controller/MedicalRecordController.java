package com.company.dental.modules.emr.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.api.PageResult;
import com.company.dental.modules.emr.dto.MedicalRecordCreateRequest;
import com.company.dental.modules.emr.query.MedicalRecordPageQuery;
import com.company.dental.modules.emr.service.MedicalRecordService;
import com.company.dental.modules.emr.vo.MedicalRecordDetailVO;
import com.company.dental.modules.emr.vo.MedicalRecordPageItemVO;
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

@Tag(name = "病历管理")
@RestController
@RequestMapping("/api/emr/records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @Operation(summary = "分页查询病历")
    @GetMapping
    public ApiResponse<PageResult<MedicalRecordPageItemVO>> page(@Valid @ModelAttribute MedicalRecordPageQuery query) {
        return ApiResponse.success(medicalRecordService.pageMedicalRecords(query));
    }

    @Operation(summary = "查询病历详情")
    @GetMapping("/{id}")
    public ApiResponse<MedicalRecordDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(medicalRecordService.getMedicalRecordDetail(id));
    }

    @Operation(summary = "新建病历")
    @PreAuthorize("hasAuthority('EMR_EDIT')")
    @PostMapping
    public ApiResponse<MedicalRecordDetailVO> create(@Valid @RequestBody MedicalRecordCreateRequest request) {
        return ApiResponse.success(medicalRecordService.createMedicalRecord(request));
    }

    @Operation(summary = "更新病历")
    @PreAuthorize("hasAuthority('EMR_EDIT')")
    @PutMapping("/{id}")
    public ApiResponse<MedicalRecordDetailVO> update(@PathVariable Long id, @Valid @RequestBody MedicalRecordCreateRequest request) {
        return ApiResponse.success(medicalRecordService.updateMedicalRecord(id, request));
    }
}
