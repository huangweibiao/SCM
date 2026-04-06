package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.entity.Inventory;
import com.scm.entity.InventoryLog;

import java.math.BigDecimal;

/**
 * 库存Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface InventoryService extends IService<Inventory> {

    /**
     * 分页查询库存列表
     */
    PageResult<Inventory> pageQuery(Long itemId, Long warehouseId, Integer pageNum, Integer pageSize);

    /**
     * 库存入库
     *
     * @param itemId      物料id
     * @param warehouseId 仓库id
     * @param qty         入库数量
     * @param batchNo     批次号
     * @param businessId  关联业务单id
     * @param businessType 业务类型
     * @param operateBy   操作人
     * @param remark      备注
     */
    void inbound(Long itemId, Long warehouseId, BigDecimal qty, String batchNo, Long businessId, Integer businessType, Long operateBy, String remark);

    /**
     * 库存出库
     *
     * @param itemId      物料id
     * @param warehouseId 仓库id
     * @param qty         出库数量
     * @param batchNo     批次号
     * @param businessId  关联业务单id
     * @param businessType 业务类型
     * @param operateBy   操作人
     * @param remark      备注
     */
    void outbound(Long itemId, Long warehouseId, BigDecimal qty, String batchNo, Long businessId, Integer businessType, Long operateBy, String remark);

    /**
     * 锁定库存
     *
     * @param itemId      物料id
     * @param warehouseId 仓库id
     * @param qty         锁定数量
     */
    void lockInventory(Long itemId, Long warehouseId, BigDecimal qty);

    /**
     * 释放锁定库存
     *
     * @param itemId      物料id
     * @param warehouseId 仓库id
     * @param qty         释放数量
     */
    void unlockInventory(Long itemId, Long warehouseId, BigDecimal qty);
}
