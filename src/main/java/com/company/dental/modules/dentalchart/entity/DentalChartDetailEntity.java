package com.company.dental.modules.dentalchart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dental_chart_detail")
public class DentalChartDetailEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dentalChartId;
    private String toothNo;
    private String toothSurface;
    private String toothStatus;
    private Integer diagnosisFlag;
    private Integer treatmentFlag;
    private Long treatmentItemId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
