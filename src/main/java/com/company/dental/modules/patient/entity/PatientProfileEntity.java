package com.company.dental.modules.patient.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("patient_profile")
public class PatientProfileEntity {

    @TableId
    private Long id;
    private Long patientId;
    private String bloodType;
    private String allergyHistory;
    private String pastHistory;
    private String familyHistory;
    private String pregnancyStatus;
    private String smokingStatus;
    private String drinkingStatus;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String remark;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
