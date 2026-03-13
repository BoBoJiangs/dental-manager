package com.company.dental.modules.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.dental.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_account")
public class AuthUserEntity extends BaseEntity {

    private Long orgId;
    private Long staffId;
    private String username;

    @TableField("password_hash")
    private String passwordHash;

    private String accountType;
    private String mobile;
    private String email;
    private Integer status;
    private LocalDateTime lastLoginAt;
    private String lastLoginIp;
    private LocalDateTime pwdUpdatedAt;
}
