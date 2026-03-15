package com.company.dental.modules.imaging.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("patient_image")
public class PatientImageEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private Long patientId;
    private Long medicalRecordId;
    private Long fileId;
    private String imageType;
    private String imageGroupType;
    private LocalDateTime shotTime;
    private String toothPosition;
    private Integer sortNo;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
