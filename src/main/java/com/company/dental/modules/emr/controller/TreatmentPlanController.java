package com.company.dental.modules.emr.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.common.api.PageResult;
import com.company.dental.modules.emr.dto.TreatmentPlanCreateRequest;
import com.company.dental.modules.emr.dto.TreatmentPlanStatusUpdateRequest;
import com.company.dental.modules.emr.query.TreatmentPlanPageQuery;
import com.company.dental.modules.emr.service.TreatmentPlanService;
import com.company.dental.modules.emr.vo.TreatmentPlanDetailVO;
import com.company.dental.modules.emr.vo.TreatmentPlanPageItemVO;
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

@Tag(name = "治疗计划管理")
@RestController
@RequestMapping("/api/emr/treatment-plans")
public class TreatmentPlanController {

    private final TreatmentPlanService treatmentPlanService;

    public TreatmentPlanController(TreatmentPlanService treatmentPlanService) {
        this.treatmentPlanService = treatmentPlanService;
    }

    @Operation(summary = "分页查询治疗计划")
    @GetMapping
    public ApiResponse<PageResult<TreatmentPlanPageItemVO>> page(@Valid @ModelAttribute TreatmentPlanPageQuery query) {
        return ApiResponse.success(treatmentPlanService.pageTreatmentPlans(query));
    }

    @Operation(summary = "查询治疗计划详情")
    @GetMapping("/{id}")
    public ApiResponse<TreatmentPlanDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(treatmentPlanService.getTreatmentPlanDetail(id));
    }

    @Operation(summary = "新建治疗计划")
    @PreAuthorize("hasAuthority('EMR_EDIT')")
    @PostMapping
    public ApiResponse<TreatmentPlanDetailVO> create(@Valid @RequestBody TreatmentPlanCreateRequest request) {
        return ApiResponse.success(treatmentPlanService.createTreatmentPlan(request));
    }

    @Operation(summary = "更新治疗计划状态")
    @PreAuthorize("hasAuthority('EMR_EDIT')")
    @PutMapping("/{id}/status")
    public ApiResponse<TreatmentPlanDetailVO> updateStatus(@PathVariable Long id,
                                                           @Valid @RequestBody TreatmentPlanStatusUpdateRequest request) {
        return ApiResponse.success(treatmentPlanService.updateTreatmentPlanStatus(id, request));
    }
}
