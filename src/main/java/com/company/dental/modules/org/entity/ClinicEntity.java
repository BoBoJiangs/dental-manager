package com.company.dental.modules.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("clinic")
public class ClinicEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
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
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
