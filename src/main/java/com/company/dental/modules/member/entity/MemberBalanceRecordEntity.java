package com.company.dental.modules.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_balance_record")
public class MemberBalanceRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberAccountId;
    private Long patientId;
    private String bizType;
    private Long bizId;
    private BigDecimal changeAmount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
}
