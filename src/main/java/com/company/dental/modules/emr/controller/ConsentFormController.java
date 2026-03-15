package com.company.dental.modules.emr.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.emr.dto.ConsentFormCreateRequest;
import com.company.dental.modules.emr.query.ConsentFormQuery;
import com.company.dental.modules.emr.query.PrintTemplateQuery;
import com.company.dental.modules.emr.service.ConsentFormService;
import com.company.dental.modules.emr.vo.ConsentFormVO;
import com.company.dental.modules.emr.vo.PrintTemplateVO;
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

import java.util.List;

@Tag(name = "知情同意书与模板")
@RestController
@RequestMapping("/api/emr")
public class ConsentFormController {

    private final ConsentFormService consentFormService;

    public ConsentFormController(ConsentFormService consentFormService) {
        this.consentFormService = consentFormService;
    }

    @Operation(summary = "查询打印/知情同意书模板")
    @GetMapping("/print-templates")
    public ApiResponse<List<PrintTemplateVO>> templates(@ModelAttribute PrintTemplateQuery query) {
        return ApiResponse.success(consentFormService.listPrintTemplates(query));
    }

    @Operation(summary = "查询知情同意书列表")
    @GetMapping("/consent-forms")
    public ApiResponse<List<ConsentFormVO>> list(@ModelAttribute ConsentFormQuery query) {
        return ApiResponse.success(consentFormService.listConsentForms(query));
    }

    @Operation(summary = "查询知情同意书详情")
    @GetMapping("/consent-forms/{id}")
    public ApiResponse<ConsentFormVO> detail(@PathVariable Long id) {
        return ApiResponse.success(consentFormService.getConsentFormDetail(id));
    }

    @Operation(summary = "创建知情同意书记录")
    @PreAuthorize("hasAuthority('EMR_EDIT')")
    @PostMapping("/consent-forms")
    public ApiResponse<ConsentFormVO> create(@Valid @RequestBody ConsentFormCreateRequest request) {
        return ApiResponse.success(consentFormService.createConsentForm(request));
    }

    @Operation(summary = "更新知情同意书记录")
    @PreAuthorize("hasAuthority('EMR_EDIT')")
    @PutMapping("/consent-forms/{id}")
    public ApiResponse<ConsentFormVO> update(@PathVariable Long id, @Valid @RequestBody ConsentFormCreateRequest request) {
        return ApiResponse.success(consentFormService.updateConsentForm(id, request));
    }
}
