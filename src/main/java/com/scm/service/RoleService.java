package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.dto.RoleDTO;
import com.scm.entity.SysRole;

/**
 * 角色Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface RoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     */
    PageResult<SysRole> pageQuery(String roleCode, String roleName, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 创建角色
     */
    Long createRole(RoleDTO roleDTO);

    /**
     * 更新角色
     */
    Boolean updateRole(RoleDTO roleDTO);

    /**
     * 删除角色
     */
    Boolean deleteRole(Long id);

    /**
     * 分配权限
     */
    Boolean assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 查询角色的权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);
}
