package com.company.dental.modules.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("staff")
public class StaffEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private String staffCode;
    private String staffName;
    private Integer gender;
    private String mobile;
    private String idNo;
    private String jobTitle;
    private String staffType;
    private Long mainClinicId;
    private Long deptId;
    private Integer status;
    private LocalDate entryDate;
    private LocalDate leaveDate;
    private Integer isDoctor;
    private String specialty;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
