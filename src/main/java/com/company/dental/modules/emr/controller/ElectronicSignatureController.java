package com.company.dental.modules.emr.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.emr.dto.ElectronicSignatureCreateRequest;
import com.company.dental.modules.emr.query.ElectronicSignatureQuery;
import com.company.dental.modules.emr.service.ElectronicSignatureService;
import com.company.dental.modules.emr.vo.ElectronicSignatureVO;
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

@Tag(name = "电子签名")
@RestController
@RequestMapping("/api/emr/signatures")
public class ElectronicSignatureController {

    private final ElectronicSignatureService electronicSignatureService;

    public ElectronicSignatureController(ElectronicSignatureService electronicSignatureService) {
        this.electronicSignatureService = electronicSignatureService;
    }

    @Operation(summary = "查询电子签名列表")
    @GetMapping
    public ApiResponse<List<ElectronicSignatureVO>> list(@ModelAttribute ElectronicSignatureQuery query) {
        return ApiResponse.success(electronicSignatureService.listSignatures(query));
    }

    @Operation(summary = "创建电子签名记录")
    @PreAuthorize("hasAuthority('EMR_EDIT')")
    @PostMapping
    public ApiResponse<ElectronicSignatureVO> create(@Valid @RequestBody ElectronicSignatureCreateRequest request) {
        return ApiResponse.success(electronicSignatureService.createSignature(request));
    }
}
