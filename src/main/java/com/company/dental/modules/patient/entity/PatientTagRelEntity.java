package com.company.dental.modules.patient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("patient_tag_rel")
public class PatientTagRelEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private Long tagId;
    private LocalDateTime createdAt;
}
