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
}
