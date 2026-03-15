package com.company.dental.modules.org.vo;

import lombok.Data;

@Data
public class DepartmentVO {

    private Long id;
    private Long clinicId;
    private String deptCode;
    private String deptName;
    private Integer status;
    private Integer sortNo;
    private String remark;
}
