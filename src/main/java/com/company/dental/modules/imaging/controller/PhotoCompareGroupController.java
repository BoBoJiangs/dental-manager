package com.company.dental.modules.imaging.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.imaging.dto.PhotoCompareGroupCreateRequest;
import com.company.dental.modules.imaging.query.PhotoCompareGroupQuery;
import com.company.dental.modules.imaging.service.PhotoCompareGroupService;
import com.company.dental.modules.imaging.vo.PhotoCompareGroupVO;
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

@Tag(name = "术前术后对比")
@RestController
@RequestMapping("/api/imaging/photo-compare-groups")
public class PhotoCompareGroupController {

    private final PhotoCompareGroupService photoCompareGroupService;

    public PhotoCompareGroupController(PhotoCompareGroupService photoCompareGroupService) {
        this.photoCompareGroupService = photoCompareGroupService;
    }

    @Operation(summary = "查询术前术后对比组")
    @GetMapping
    public ApiResponse<List<PhotoCompareGroupVO>> list(@ModelAttribute PhotoCompareGroupQuery query) {
        return ApiResponse.success(photoCompareGroupService.listCompareGroups(query));
    }

    @Operation(summary = "创建术前术后对比组")
    @PreAuthorize("hasAuthority('IMAGING_EDIT')")
    @PostMapping
    public ApiResponse<PhotoCompareGroupVO> create(@Valid @RequestBody PhotoCompareGroupCreateRequest request) {
        return ApiResponse.success(photoCompareGroupService.createCompareGroup(request));
    }
}
