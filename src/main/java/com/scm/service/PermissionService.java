package com.scm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.dto.PermissionDTO;
import com.scm.entity.SysPermission;

import java.util.List;

/**
 * 权限Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface PermissionService extends IService<SysPermission> {

    /**
     * 查询权限树
     */
    List<SysPermission> getPermissionTree();

    /**
     * 创建权限
     */
    Long createPermission(PermissionDTO permissionDTO);

    /**
     * 更新权限
     */
    Boolean updatePermission(PermissionDTO permissionDTO);

    /**
     * 删除权限
     */
    Boolean deletePermission(Long id);
}
