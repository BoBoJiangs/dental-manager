package com.company.dental.modules.emr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("diagnosis_record")
public class DiagnosisRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long medicalRecordId;
    private String diagnosisType;
    private String diagnosisCode;
    private String diagnosisName;
    private String toothPosition;
    private String diagnosisDesc;
    private Integer sortNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
