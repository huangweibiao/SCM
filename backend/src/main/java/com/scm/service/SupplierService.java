package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 供应商服务接口
 */
public interface SupplierService {

    /**
     * 分页查询供应商列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 查询所有供应商（下拉框用）
     */
    List<Map<String, Object>> all();

    /**
     * 根据ID查询供应商
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建供应商
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新供应商
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除供应商
     */
    Map<String, Object> delete(Long id);
}
