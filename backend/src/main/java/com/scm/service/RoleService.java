package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, String roleName);

    /**
     * 根据ID查询角色
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建角色
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新角色
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除角色
     */
    Map<String, Object> delete(Long id);

    /**
     * 获取角色关联的权限ID列表
     */
    List<Long> getPermissionIds(Long roleId);

    /**
     * 分配权限
     */
    Map<String, Object> assignPermissions(Long roleId, List<Long> permissionIds);
}
