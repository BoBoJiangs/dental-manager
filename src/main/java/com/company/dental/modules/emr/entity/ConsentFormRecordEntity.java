package com.company.dental.modules.emr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consent_form_record")
public class ConsentFormRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private Long patientId;
    private Long medicalRecordId;
    private String formCode;
    private String formName;
    private String formContent;
    private Long signerSignatureId;
    private Long doctorSignatureId;
    private String formStatus;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
