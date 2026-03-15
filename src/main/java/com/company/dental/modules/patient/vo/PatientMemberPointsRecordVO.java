package com.company.dental.modules.patient.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientMemberPointsRecordVO {

    private Long id;
    private String bizType;
    private Long bizId;
    private Integer changePoints;
    private Integer beforePoints;
    private Integer afterPoints;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
}
