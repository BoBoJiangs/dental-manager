package com.company.dental.modules.member.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberAccountPageItemVO {

    private Long id;
    private Long patientId;
    private String patientName;
    private String patientMobile;
    private String memberNo;
    private Long levelId;
    private String levelName;
    private BigDecimal balanceAmount;
    private Integer pointsBalance;
    private BigDecimal totalRechargeAmount;
    private BigDecimal totalConsumeAmount;
    private String memberStatus;
    private LocalDateTime activatedAt;
    private LocalDateTime expiredAt;
}
