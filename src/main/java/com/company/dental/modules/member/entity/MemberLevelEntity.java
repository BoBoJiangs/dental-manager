package com.company.dental.modules.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_level")
public class MemberLevelEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private String levelCode;
    private String levelName;
    private BigDecimal upgradeAmount;
    private BigDecimal discountRate;
    private BigDecimal pointsRate;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
