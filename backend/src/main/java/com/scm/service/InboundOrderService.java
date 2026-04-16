package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 入库单服务接口
 */
public interface InboundOrderService {

    /**
     * 分页查询入库单列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, Long warehouseId, Integer inboundType, Integer status);

    /**
     * 根据ID查询入库单
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建入库单
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 确认入库
     */
    Map<String, Object> confirm(Long id);

    /**
     * 获取入库单明细
     */
    List<Map<String, Object>> getDetails(Long orderId);
}
