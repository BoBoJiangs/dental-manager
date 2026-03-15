package com.company.dental.modules.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomSaveRequest {

    @NotNull(message = "所属门诊不能为空")
    private Long clinicId;

    @NotBlank(message = "诊室编码不能为空")
    private String roomCode;

    @NotBlank(message = "诊室名称不能为空")
    private String roomName;

    private String roomType;

    private String floorNo;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
