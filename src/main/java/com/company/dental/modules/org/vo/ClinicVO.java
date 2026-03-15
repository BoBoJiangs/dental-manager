package com.company.dental.modules.org.vo;

import lombok.Data;

@Data
public class ClinicVO {

    private Long id;
    private String clinicCode;
    private String clinicName;
    private String clinicType;
    private String province;
    private String city;
    private String district;
    private String address;
    private String phone;
    private String businessHours;
    private Integer status;
}
