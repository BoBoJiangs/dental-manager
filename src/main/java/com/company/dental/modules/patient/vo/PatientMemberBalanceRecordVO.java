package com.company.dental.modules.patient.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PatientMemberBalanceRecordVO {

    private Long id;
    private String bizType;
    private Long bizId;
    private BigDecimal changeAmount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
}
