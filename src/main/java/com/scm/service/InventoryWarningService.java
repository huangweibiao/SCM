package com.scm.service;

import java.util.List;

/**
 * 库存预警Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface InventoryWarningService {

    /**
     * 检查库存预警
     */
    void checkInventoryWarning();

    /**
     * 获取库存预警列表（库存低于下限）
     */
    List<Object> getLowStockWarnings();

    /**
     * 获取库存预警列表（库存高于上限）
     */
    List<Object> getHighStockWarnings();
}
