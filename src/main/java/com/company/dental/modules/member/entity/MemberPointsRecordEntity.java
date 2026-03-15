package com.company.dental.modules.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("member_points_record")
public class MemberPointsRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberAccountId;
    private Long patientId;
    private String bizType;
    private Long bizId;
    private Integer changePoints;
    private Integer beforePoints;
    private Integer afterPoints;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
}
