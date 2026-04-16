package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 出库单服务接口
 */
public interface OutboundOrderService {

    /**
     * 分页查询出库单列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, Long warehouseId, Integer outboundType, Integer status);

    /**
     * 根据ID查询出库单
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建出库单
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 确认出库
     */
    Map<String, Object> confirm(Long id);

    /**
     * 获取出库单明细
     */
    List<Map<String, Object>> getDetails(Long orderId);
}
