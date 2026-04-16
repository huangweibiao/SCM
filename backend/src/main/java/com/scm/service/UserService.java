package com.scm.service;

import com.scm.common.Result;
import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password);

    /**
     * 获取当前用户信息
     */
    Map<String, Object> getCurrentUser();

    /**
     * 分页查询用户列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, String username);

    /**
     * 根据ID查询用户
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建用户
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新用户
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除用户
     */
    Map<String, Object> delete(Long id);

    /**
     * 分配角色
     */
    Map<String, Object> assignRoles(Long userId, List<Long> roleIds);
}
