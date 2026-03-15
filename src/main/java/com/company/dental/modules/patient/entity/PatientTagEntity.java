package com.company.dental.modules.patient.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("patient_tag")
public class PatientTagEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private String tagCode;
    private String tagName;
    private String tagColor;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
