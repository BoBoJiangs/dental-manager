package com.company.dental.modules.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.auth.entity.AuthUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuthUserMapper extends BaseMapper<AuthUserEntity> {

    @Select("""
            SELECT r.role_code
            FROM sys_user_role ur
            JOIN sys_role r ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
              AND r.status = 1
              AND r.is_deleted = 0
            ORDER BY r.id
            """)
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT clinic_id
            FROM sys_user_role
            WHERE user_id = #{userId}
              AND clinic_id IS NOT NULL
            ORDER BY id
            LIMIT 1
            """)
    Long selectPrimaryClinicId(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT clinic_id
            FROM sys_user_role
            WHERE user_id = #{userId}
              AND clinic_id IS NOT NULL
            ORDER BY clinic_id
            """)
    List<Long> selectAuthorizedClinicIdsByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT r.data_scope
            FROM sys_user_role ur
            JOIN sys_role r ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
              AND r.status = 1
              AND r.is_deleted = 0
            ORDER BY r.data_scope
            """)
    List<String> selectRoleDataScopesByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT rp.permission_code
            FROM sys_user_role ur
            JOIN sys_role_permission rp ON ur.role_id = rp.role_id
            WHERE ur.user_id = #{userId}
              AND rp.permission_type = #{permissionType}
            ORDER BY rp.permission_code
            """)
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId, @Param("permissionType") String permissionType);

    @Select("""
            SELECT DISTINCT r.role_code
            FROM sys_user_role ur
            JOIN sys_role r ON ur.role_id = r.id
            JOIN sys_role_permission rp ON ur.role_id = rp.role_id
            WHERE ur.user_id = #{userId}
              AND rp.permission_type = #{permissionType}
              AND r.status = 1
              AND r.is_deleted = 0
            ORDER BY r.role_code
            """)
    List<String> selectRoleCodesWithCustomPermissions(@Param("userId") Long userId, @Param("permissionType") String permissionType);
}
