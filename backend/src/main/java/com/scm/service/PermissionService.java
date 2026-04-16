package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 分页查询权限列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, String permissionName);

    /**
     * 查询所有权限（树形）
     */
    List<Map<String, Object>> tree();

    /**
     * 根据ID查询权限
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建权限
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新权限
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除权限
     */
    Map<String, Object> delete(Long id);
}
