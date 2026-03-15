package com.company.dental.modules.emr.vo;

import lombok.Data;

@Data
public class PrintTemplateVO {

    private Long id;
    private Long clinicId;
    private String clinicName;
    private String templateCode;
    private String templateName;
    private String templateType;
    private String content;
    private Integer isDefault;
    private Integer status;
    private String remark;
}
