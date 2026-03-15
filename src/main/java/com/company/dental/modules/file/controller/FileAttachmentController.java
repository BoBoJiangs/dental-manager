package com.company.dental.modules.file.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.file.dto.FileAttachmentUploadRequest;
import com.company.dental.modules.file.query.FileAttachmentQuery;
import com.company.dental.modules.file.service.FileAttachmentService;
import com.company.dental.modules.file.vo.FileAttachmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "文件附件")
@RestController
@RequestMapping("/api/files/attachments")
public class FileAttachmentController {

    private final FileAttachmentService fileAttachmentService;

    public FileAttachmentController(FileAttachmentService fileAttachmentService) {
        this.fileAttachmentService = fileAttachmentService;
    }

    @Operation(summary = "查询附件列表")
    @GetMapping
    public ApiResponse<List<FileAttachmentVO>> list(@ModelAttribute FileAttachmentQuery query) {
        return ApiResponse.success(fileAttachmentService.listAttachments(query));
    }

    @Operation(summary = "上传并登记附件")
    @PreAuthorize("hasAuthority('IMAGING_EDIT') or hasAuthority('EMR_EDIT')")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ApiResponse<FileAttachmentVO> upload(@Valid @ModelAttribute FileAttachmentUploadRequest request) {
        return ApiResponse.success(fileAttachmentService.uploadAttachment(request));
    }
}
