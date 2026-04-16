package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 仓库服务接口
 */
public interface WarehouseService {

    /**
     * 分页查询仓库列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 查询所有仓库（下拉框用）
     */
    List<Map<String, Object>> all();

    /**
     * 根据ID查询仓库
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建仓库
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新仓库
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除仓库
     */
    Map<String, Object> delete(Long id);
}
