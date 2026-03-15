package com.company.dental.modules.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_permission")
public class RolePermissionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roleId;
    private String permissionCode;
    private String permissionType;
    private LocalDateTime createdAt;
}
