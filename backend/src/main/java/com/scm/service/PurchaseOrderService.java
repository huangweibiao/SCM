package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 采购订单服务接口
 */
public interface PurchaseOrderService {

    /**
     * 分页查询采购订单列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, Long supplierId, Integer status);

    /**
     * 根据ID查询采购订单
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建采购订单
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新采购订单
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除采购订单
     */
    Map<String, Object> delete(Long id);

    /**
     * 审核采购订单
     */
    Map<String, Object> audit(Long id, Map<String, Object> params);

    /**
     * 获取订单明细
     */
    List<Map<String, Object>> getDetails(Long orderId);
}
