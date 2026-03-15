package com.company.dental.framework.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.modules.auth.entity.AuthUserEntity;
import com.company.dental.modules.auth.mapper.AuthUserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DentalUserDetailsService implements UserDetailsService {

    private final AuthUserMapper authUserMapper;
    private final RolePermissionMatrix rolePermissionMatrix;

    public DentalUserDetailsService(AuthUserMapper authUserMapper, RolePermissionMatrix rolePermissionMatrix) {
        this.authUserMapper = authUserMapper;
        this.rolePermissionMatrix = rolePermissionMatrix;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUserEntity user = authUserMapper.selectOne(new LambdaQueryWrapper<AuthUserEntity>()
                .eq(AuthUserEntity::getUsername, username)
                .eq(AuthUserEntity::getIsDeleted, 0)
                .last("LIMIT 1"));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> roles = authUserMapper.selectRoleCodesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = rolePermissionMatrix.resolveAuthorities(roles).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(user.getStatus() == null || user.getStatus() != 1)
                .authorities(authorities)
                .build();
    }
}
