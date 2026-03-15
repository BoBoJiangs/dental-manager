package com.company.dental.modules.emr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("electronic_signature")
public class ElectronicSignatureEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private Long patientId;
    private Long medicalRecordId;
    private String signerName;
    private String signerType;
    private String relationToPatient;
    private Long signatureFileId;
    private LocalDateTime signedAt;
    private String ipAddress;
    private String deviceInfo;
    private String remark;
    private LocalDateTime createdAt;
}
