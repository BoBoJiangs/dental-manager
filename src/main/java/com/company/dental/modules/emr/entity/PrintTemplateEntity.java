package com.company.dental.modules.emr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("print_template")
public class PrintTemplateEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private String templateCode;
    private String templateName;
    private String templateType;
    private String content;
    private Integer isDefault;
    private Integer status;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
