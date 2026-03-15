package com.company.dental.modules.emr.query;

import lombok.Data;

@Data
public class PrintTemplateQuery {

    private Long clinicId;
    private String templateType;
    private String keyword;
}
