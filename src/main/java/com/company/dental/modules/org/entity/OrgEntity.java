package com.company.dental.modules.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("org")
public class OrgEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orgCode;
    private String orgName;
    private String contactName;
    private String contactPhone;
    private Integer status;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
