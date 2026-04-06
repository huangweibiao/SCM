package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.exception.BusinessException;
import com.scm.dto.PermissionDTO;
import com.scm.entity.SysPermission;
import com.scm.mapper.SysPermissionMapper;
import com.scm.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements PermissionService {

    @Override
    public List<SysPermission> getPermissionTree() {
        // 查询所有权限
        List<SysPermission> allPermissions = this.lambdaQuery()
                .orderByAsc(SysPermission::getSort)
                .orderByAsc(SysPermission::getId)
                .list();

        // 构建树形结构
        return buildPermissionTree(allPermissions, 0L);
    }

    /**
     * 构建权限树
     */
    private List<SysPermission> buildPermissionTree(List<SysPermission> permissions, Long parentId) {
        List<SysPermission> tree = new ArrayList<>();

        for (SysPermission permission : permissions) {
            if (parentId.equals(permission.getParentId())) {
                permission.setChildren(buildPermissionTree(permissions, permission.getId()));
                tree.add(permission);
            }
        }

        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionDTO permissionDTO) {
        // 检查权限编码是否存在
        Long count = this.lambdaQuery()
                .eq(SysPermission::getPermissionCode, permissionDTO.getPermissionCode())
                .count();
        if (count > 0) {
            throw new BusinessException("权限编码已存在");
        }

        SysPermission permission = new SysPermission();
        permission.setParentId(permissionDTO.getParentId() != null ? permissionDTO.getParentId() : 0L);
        permission.setPermissionCode(permissionDTO.getPermissionCode());
        permission.setPermissionName(permissionDTO.getPermissionName());
        permission.setPermissionType(permissionDTO.getPermissionType());
        permission.setPath(permissionDTO.getPath());
        permission.setComponent(permissionDTO.getComponent());
        permission.setIcon(permissionDTO.getIcon());
        permission.setSort(permissionDTO.getSort());
        permission.setStatus(permissionDTO.getStatus() != null ? permissionDTO.getStatus() : 1);

        this.save(permission);
        log.info("创建权限成功，权限编码: {}", permission.getPermissionCode());

        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePermission(PermissionDTO permissionDTO) {
        SysPermission exist = this.getById(permissionDTO.getId());
        if (exist == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查权限编码是否重复
        if (!exist.getPermissionCode().equals(permissionDTO.getPermissionCode())) {
            Long count = this.lambdaQuery()
                    .eq(SysPermission::getPermissionCode, permissionDTO.getPermissionCode())
                    .ne(SysPermission::getId, permissionDTO.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("权限编码已存在");
            }
        }

        exist.setParentId(permissionDTO.getParentId() != null ? permissionDTO.getParentId() : 0L);
        exist.setPermissionCode(permissionDTO.getPermissionCode());
        exist.setPermissionName(permissionDTO.getPermissionName());
        exist.setPermissionType(permissionDTO.getPermissionType());
        exist.setPath(permissionDTO.getPath());
        exist.setComponent(permissionDTO.getComponent());
        exist.setIcon(permissionDTO.getIcon());
        exist.setSort(permissionDTO.getSort());
        exist.setStatus(permissionDTO.getStatus());

        boolean result = this.updateById(exist);
        log.info("更新权限成功，权限编码: {}", exist.getPermissionCode());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePermission(Long id) {
        SysPermission permission = this.getById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查是否有子权限
        Long childCount = this.lambdaQuery()
                .eq(SysPermission::getParentId, id)
                .count();
        if (childCount > 0) {
            throw new BusinessException("该权限下有子权限，无法删除");
        }

        boolean result = this.removeById(id);
        log.info("删除权限成功，权限编码: {}", permission.getPermissionCode());
        return result;
    }
}
