package com.company.dental.modules.file.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileAttachmentUploadRequest {

    private Long clinicId;

    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    private Long bizId;

    private Long patientId;

    private Long medicalRecordId;

    @NotBlank(message = "上传来源不能为空")
    private String uploadSource;

    @NotNull(message = "文件不能为空")
    private MultipartFile file;
}
