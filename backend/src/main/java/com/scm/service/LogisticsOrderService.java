package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 物流订单服务接口
 */
public interface LogisticsOrderService {

    /**
     * 分页查询物流订单列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, Integer businessType, Integer status);

    /**
     * 根据ID查询物流订单
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建物流订单
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新物流订单
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除物流订单
     */
    Map<String, Object> delete(Long id);

    /**
     * 更新物流状态
     */
    Map<String, Object> updateStatus(Long id, Integer status);
}
