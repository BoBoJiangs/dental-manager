package com.company.dental.modules.patient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("patient_doctor_rel")
public class PatientDoctorRelEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long patientId;
    private Long doctorId;
    private Long clinicId;
    private String relationType;
    private LocalDateTime firstRelatedAt;
    private LocalDateTime latestRelatedAt;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
