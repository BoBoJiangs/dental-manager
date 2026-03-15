package com.company.dental.modules.imaging.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("photo_compare_group")
public class PhotoCompareGroupEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private Long medicalRecordId;
    private String groupName;
    private Long preImageId;
    private Long postImageId;
    private String compareDesc;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
