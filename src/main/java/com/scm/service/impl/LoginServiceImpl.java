package com.scm.service.impl;

import com.scm.common.exception.BusinessException;
import com.scm.common.util.JwtTokenUtil;
import com.scm.dto.LoginRequestDTO;
import com.scm.dto.LoginResponseDTO;
import com.scm.entity.SysPermission;
import com.scm.entity.SysRole;
import com.scm.entity.SysUser;
import com.scm.entity.SysUserRole;
import com.scm.mapper.SysRolePermissionMapper;
import com.scm.mapper.SysUserRoleMapper;
import com.scm.service.LoginService;
import com.scm.service.PermissionService;
import com.scm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 登录Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserService userService;
    private final PermissionService permissionService;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "scm:token:";
    private static final long TOKEN_EXPIRE_HOURS = 24;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String username = loginRequestDTO.getUsername();
        String password = loginRequestDTO.getPassword();

        // 查询用户
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成Token
        String token = jwtTokenUtil.generateToken(user.getId(), username);

        // 查询用户角色
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, user.getId())
        );

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询角色信息
        List<SysRole> roles = roleIds.isEmpty() ? List.of() :
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
                        .list();

        // 查询权限
        List<String> permissionCodes = permissionService.getPermissionTree().stream()
                .flatMap(p -> getPermissionCodes(p).stream())
                .distinct()
                .collect(Collectors.toList());

        List<String> roleCodes = roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());

        // 构建响应
        LoginResponseDTO response = LoginResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .permissions(permissionCodes)
                .roles(roleCodes)
                .build();

        // 缓存Token到Redis
        if (redisTemplate != null) {
            String redisKey = TOKEN_PREFIX + token;
            redisTemplate.opsForValue().set(redisKey, user.getId(), TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
        }

        log.info("用户登录成功，用户名: {}", username);
        return response;
    }

    /**
     * 递归获取权限编码列表
     */
    private List<String> getPermissionCodes(SysPermission permission) {
        List<String> codes = java.util.stream.Stream.of(permission.getPermissionCode())
                .collect(Collectors.toList());

        if (permission.getChildren() != null && !permission.getChildren().isEmpty()) {
            for (SysPermission child : permission.getChildren()) {
                codes.addAll(getPermissionCodes(child));
            }
        }

        return codes;
    }

    @Override
    public void logout(String token) {
        // 从Redis中删除Token
        if (redisTemplate != null) {
            String redisKey = TOKEN_PREFIX + token;
            redisTemplate.delete(redisKey);
        }
        log.info("用户登出成功");
    }
}
