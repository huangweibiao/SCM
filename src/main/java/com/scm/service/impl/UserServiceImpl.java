package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.exception.BusinessException;
import com.scm.common.result.PageResult;
import com.scm.common.util.StringUtils;
import com.scm.dto.UserDTO;
import com.scm.entity.SysRole;
import com.scm.entity.SysUser;
import com.scm.entity.SysUserRole;
import com.scm.mapper.SysRoleMapper;
import com.scm.mapper.SysUserRoleMapper;
import com.scm.mapper.SysUserMapper;
import com.scm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public PageResult<SysUser> pageQuery(String username, String realName, Integer status, Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(username), SysUser::getUsername, username)
                .like(StringUtils.isNotEmpty(realName), SysUser::getRealName, realName)
                .eq(status != null, SysUser::getStatus, status)
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserDTO userDTO) {
        // 检查用户名是否存在
        Long count = this.lambdaQuery()
                .eq(SysUser::getUsername, userDTO.getUsername())
                .count();
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRealName(userDTO.getRealName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : 1);

        this.save(user);
        log.info("创建用户成功，用户名: {}", user.getUsername());

        // 分配角色
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUser(UserDTO userDTO) {
        SysUser exist = this.getById(userDTO.getId());
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户名是否重复
        if (!exist.getUsername().equals(userDTO.getUsername())) {
            Long count = this.lambdaQuery()
                    .eq(SysUser::getUsername, userDTO.getUsername())
                    .ne(SysUser::getId, userDTO.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("用户名已存在");
            }
        }

        exist.setUsername(userDTO.getUsername());
        exist.setRealName(userDTO.getRealName());
        exist.setPhone(userDTO.getPhone());
        exist.setEmail(userDTO.getEmail());
        exist.setStatus(userDTO.getStatus());

        if (StringUtils.isNotEmpty(userDTO.getPassword())) {
            exist.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        boolean result = this.updateById(exist);
        log.info("更新用户成功，用户名: {}", exist.getUsername());

        // 重新分配角色
        assignRoles(exist.getId(), userDTO.getRoleIds());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUser(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 不允许删除admin用户
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除admin用户");
        }

        // 删除用户角色关联
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, id)
        );

        boolean result = this.removeById(id);
        log.info("删除用户成功，用户名: {}", user.getUsername());
        return result;
    }

    @Override
    public Boolean resetPassword(Long id, String newPassword) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        boolean result = this.updateById(user);
        log.info("重置用户密码成功，用户名: {}", user.getUsername());
        return result;
    }

    @Override
    public SysUser getByUsername(String username) {
        return this.lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean assignRoles(Long userId, List<Long> roleIds) {
        // 删除旧的角色关联
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );

        // 添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                // 验证角色是否存在
                SysRole role = sysRoleMapper.selectById(roleId);
                if (role == null) {
                    throw new BusinessException("角色不存在，roleId: " + roleId);
                }

                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }

        return true;
    }
}
