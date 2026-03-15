package com.company.dental.modules.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClinicSaveRequest {

    @NotBlank(message = "门诊编码不能为空")
    private String clinicCode;

    @NotBlank(message = "门诊名称不能为空")
    private String clinicName;

    private String clinicType;

    private String province;

    private String city;

    private String district;

    private String address;

    private String phone;

    private String businessHours;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
