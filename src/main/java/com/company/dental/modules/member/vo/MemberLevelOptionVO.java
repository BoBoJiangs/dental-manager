package com.company.dental.modules.member.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberLevelOptionVO {

    private Long id;
    private String levelCode;
    private String levelName;
    private BigDecimal upgradeAmount;
    private BigDecimal discountRate;
    private BigDecimal pointsRate;
}
