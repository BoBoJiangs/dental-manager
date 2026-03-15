package com.company.dental.modules.org.vo;

import lombok.Data;

@Data
public class StaffVO {

    private Long id;
    private String staffCode;
    private String staffName;
    private Integer gender;
    private String mobile;
    private String jobTitle;
    private String staffType;
    private Long mainClinicId;
    private Long deptId;
    private Integer status;
    private Integer isDoctor;
    private String specialty;
    private String remark;
}
