package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.exception.BusinessException;
import com.scm.common.result.PageResult;
import com.scm.common.util.StringUtils;
import com.scm.dto.RoleDTO;
import com.scm.entity.SysPermission;
import com.scm.entity.SysRole;
import com.scm.entity.SysRolePermission;
import com.scm.mapper.SysPermissionMapper;
import com.scm.mapper.SysRoleMapper;
import com.scm.mapper.SysRolePermissionMapper;
import com.scm.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements RoleService {

    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public PageResult<SysRole> pageQuery(String roleCode, String roleName, Integer status, Integer pageNum, Integer pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(roleCode), SysRole::getRoleCode, roleCode)
                .like(StringUtils.isNotEmpty(roleName), SysRole::getRoleName, roleName)
                .eq(status != null, SysRole::getStatus, status)
                .orderByDesc(SysRole::getCreateTime);
        Page<SysRole> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleDTO roleDTO) {
        // 检查角色编码是否存在
        Long count = this.lambdaQuery()
                .eq(SysRole::getRoleCode, roleDTO.getRoleCode())
                .count();
        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setRoleCode(roleDTO.getRoleCode());
        role.setRoleName(roleDTO.getRoleName());
        role.setDescription(roleDTO.getDescription());
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);

        this.save(role);
        log.info("创建角色成功，角色编码: {}", role.getRoleCode());

        // 分配权限
        if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), roleDTO.getPermissionIds());
        }

        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRole(RoleDTO roleDTO) {
        SysRole exist = this.getById(roleDTO.getId());
        if (exist == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色编码是否重复
        if (!exist.getRoleCode().equals(roleDTO.getRoleCode())) {
            Long count = this.lambdaQuery()
                    .eq(SysRole::getRoleCode, roleDTO.getRoleCode())
                    .ne(SysRole::getId, roleDTO.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("角色编码已存在");
            }
        }

        exist.setRoleCode(roleDTO.getRoleCode());
        exist.setRoleName(roleDTO.getRoleName());
        exist.setDescription(roleDTO.getDescription());
        exist.setStatus(roleDTO.getStatus());

        boolean result = this.updateById(exist);
        log.info("更新角色成功，角色编码: {}", exist.getRoleCode());

        // 重新分配权限
        assignPermissions(exist.getId(), roleDTO.getPermissionIds());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRole(Long id) {
        SysRole role = this.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否有用户使用该角色
        Long userCount = sysRolePermissionMapper.selectCount(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, id)
        );
        if (userCount > 0) {
            throw new BusinessException("该角色下有用户，无法删除");
        }

        // 删除角色权限关联
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, id)
        );

        boolean result = this.removeById(id);
        log.info("删除角色成功，角色编码: {}", role.getRoleCode());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 删除旧的权限关联
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId)
        );

        // 添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                // 验证权限是否存在
                SysPermission permission = sysPermissionMapper.selectById(permissionId);
                if (permission == null) {
                    throw new BusinessException("权限不存在，permissionId: " + permissionId);
                }

                SysRolePermission rolePermission = new SysRolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                sysRolePermissionMapper.insert(rolePermission);
            }
        }

        return true;
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId)
        );

        return rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .collect(java.util.stream.Collectors.toList());
    }
}
