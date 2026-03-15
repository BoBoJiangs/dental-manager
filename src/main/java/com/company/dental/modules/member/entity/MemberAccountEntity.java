package com.company.dental.modules.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_account")
public class MemberAccountEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long patientId;
    private String memberNo;
    private Long levelId;
    private BigDecimal balanceAmount;
    private Integer pointsBalance;
    private BigDecimal totalRechargeAmount;
    private BigDecimal totalConsumeAmount;
    private String memberStatus;
    private LocalDateTime activatedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
