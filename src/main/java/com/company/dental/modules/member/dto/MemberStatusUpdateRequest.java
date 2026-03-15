package com.company.dental.modules.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberStatusUpdateRequest {

    @NotBlank(message = "会员状态不能为空")
    private String memberStatus;
}
