package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 销售订单服务接口
 */
public interface SalesOrderService {

    /**
     * 分页查询销售订单列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, String customerName, Integer status);

    /**
     * 根据ID查询销售订单
     */
    Map<String, Object> getById(Long id);

    /**
     * 创建销售订单
     */
    Map<String, Object> create(Map<String, Object> params);

    /**
     * 更新销售订单
     */
    Map<String, Object> update(Long id, Map<String, Object> params);

    /**
     * 删除销售订单
     */
    Map<String, Object> delete(Long id);

    /**
     * 审核销售订单（锁定库存）
     */
    Map<String, Object> audit(Long id, Map<String, Object> params);

    /**
     * 获取订单明细
     */
    List<Map<String, Object>> getDetails(Long orderId);

    /**
     * 销售发货（部分/全部）
     */
    Map<String, Object> deliver(Long id, Map<String, Object> params);
}
