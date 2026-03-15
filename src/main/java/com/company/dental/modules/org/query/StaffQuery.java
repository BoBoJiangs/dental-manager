package com.company.dental.modules.org.query;

import lombok.Data;

@Data
public class StaffQuery {

    private Long clinicId;
    private String staffType;
    private Integer status;
    private String keyword;
}
