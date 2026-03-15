package com.company.dental.modules.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberPointsAdjustRequest {

    @NotNull(message = "积分变动不能为空")
    private Integer changePoints;

    private String bizType;

    private Long bizId;

    private String remark;
}
