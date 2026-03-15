package com.company.dental.modules.imaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PhotoCompareGroupCreateRequest {

    @NotNull(message = "患者不能为空")
    private Long patientId;

    private Long medicalRecordId;

    @NotBlank(message = "对比组名称不能为空")
    private String groupName;

    @NotNull(message = "术前图片不能为空")
    private Long preImageId;

    @NotNull(message = "术后图片不能为空")
    private Long postImageId;

    private String compareDesc;
}
