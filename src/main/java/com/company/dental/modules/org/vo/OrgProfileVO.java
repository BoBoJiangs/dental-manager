package com.company.dental.modules.org.vo;

import lombok.Data;

@Data
public class OrgProfileVO {

    private Long id;
    private String orgCode;
    private String orgName;
    private String contactName;
    private String contactPhone;
    private Integer status;
    private String remark;
}
