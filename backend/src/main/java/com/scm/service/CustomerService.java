package com.scm.service;

import java.util.Map;

/**
 * 客户服务接口
 */
public interface CustomerService {

    /**
     * 分页查询客户列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, String keyword, Integer status);

    /**
     * 根据ID查询客户
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建客户
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新客户
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除客户
     */
    Map<String, Object> delete(Long id);
}
