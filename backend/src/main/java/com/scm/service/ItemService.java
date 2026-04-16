package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 物料服务接口
 */
public interface ItemService {

    /**
     * 分页查询物料列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 查询所有物料（下拉框用）
     */
    List<Map<String, Object>> all();

    /**
     * 根据ID查询物料
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建物料
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新物料
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除物料
     */
    Map<String, Object> delete(Long id);
}
