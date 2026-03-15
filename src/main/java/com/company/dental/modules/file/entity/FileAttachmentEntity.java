package com.company.dental.modules.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_attachment")
public class FileAttachmentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private String bizType;
    private Long bizId;
    private Long patientId;
    private Long medicalRecordId;
    private String fileName;
    private String fileExt;
    private String mimeType;
    private Long fileSize;
    private String storageType;
    private String bucketName;
    private String objectKey;
    private String fileUrl;
    private String uploadSource;
    private Long uploaderId;
    private Integer fileStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer isDeleted;
}
