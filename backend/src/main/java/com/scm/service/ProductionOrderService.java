package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 生产工单服务接口
 */
public interface ProductionOrderService {

    /**
     * 分页查询生产工单列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, Integer status);

    /**
     * 根据ID查询生产工单
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建生产工单
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新生产工单
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 开始生产
     */
    Map<String, Object> start(Long id);

    /**
     * 完工入库
     */
    Map<String, Object> finish(Long id, Map<String, Object> params);
}
