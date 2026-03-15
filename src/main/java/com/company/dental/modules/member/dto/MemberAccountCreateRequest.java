package com.company.dental.modules.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberAccountCreateRequest {

    @NotNull(message = "患者不能为空")
    private Long patientId;

    private Long levelId;

    private String memberStatus;

    private LocalDateTime activatedAt;

    private LocalDateTime expiredAt;
}
