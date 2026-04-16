package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 库存服务接口
 */
public interface InventoryService {

    /**
     * 分页查询库存列表
     */
    Map<String, Object> list(Integer pageNum, Integer pageSize, Long itemId, Long warehouseId);

    /**
     * 查询库存详情
     */
    Map<String, Object> getById(Long id);

    /**
     * 查询物料在指定仓库的库存
     */
    Map<String, Object> getByItemAndWarehouse(Long itemId, Long warehouseId);

    /**
     * 锁定库存（销售订单审核时调用）
     */
    Map<String, Object> lockInventory(Long itemId, Long warehouseId, java.math.BigDecimal qty, Long businessId);

    /**
     * 释放库存（销售订单取消时调用）
     */
    Map<String, Object> unlockInventory(Long itemId, Long warehouseId, java.math.BigDecimal qty, Long businessId);

    /**
     * 扣减库存（出库确认时调用）
     */
    Map<String, Object> deductInventory(Long itemId, Long warehouseId, java.math.BigDecimal qty, Long businessId);

    /**
     * 增加库存（入库确认时调用）
     */
    Map<String, Object> addInventory(Long itemId, Long warehouseId, java.math.BigDecimal qty, Long businessId);

    /**
     * 库存预警查询
     */
    List<Map<String, Object>> getWarnings();

    /**
     * 库存调拨
     */
    Map<String, Object> transfer(Map<String, Object> params);

    /**
     * 创建盘点单
     */
    Map<String, Object> createCheck(Map<String, Object> params);

    /**
     * 提交盘点结果
     */
    Map<String, Object> submitCheckResult(Long checkId, Map<String, Object> params);

    /**
     * 查询盘点单列表
     */
    Map<String, Object> listChecks(Integer pageNum, Integer pageSize, Integer status);
}
