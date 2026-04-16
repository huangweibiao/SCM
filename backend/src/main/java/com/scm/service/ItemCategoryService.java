package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 物料分类服务接口
 */
public interface ItemCategoryService {

    /**
     * 分页查询物料分类列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, String categoryName);

    /**
     * 查询所有物料分类（树形）
     */
    List<Map<String, Object>> tree();

    /**
     * 根据ID查询物料分类
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建物料分类
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新物料分类
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除物料分类
     */
    Map<String, Object> delete(Long id);
}
