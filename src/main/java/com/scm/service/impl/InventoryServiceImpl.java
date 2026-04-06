package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.constant.BusinessTypeConstant;
import com.scm.common.exception.BusinessException;
import com.scm.common.util.StringUtils;
import com.scm.entity.Inventory;
import com.scm.entity.InventoryLog;
import com.scm.mapper.InventoryLogMapper;
import com.scm.mapper.InventoryMapper;
import com.scm.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 库存Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    private final InventoryLogMapper inventoryLogMapper;

    @Override
    public PageResult<Inventory> pageQuery(Long itemId, Long warehouseId, Integer pageNum, Integer pageSize) {
        Page<Inventory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(itemId != null, Inventory::getItemId, itemId)
                .eq(warehouseId != null, Inventory::getWarehouseId, warehouseId)
                .orderByDesc(Inventory::getLastUpdateTime);
        Page<Inventory> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inbound(Long itemId, Long warehouseId, BigDecimal qty, String batchNo, Long businessId, Integer businessType, Long operateBy, String remark) {
        // 使用行锁查询库存
        Inventory inventory = baseMapper.selectForUpdate(itemId, warehouseId, StringUtils.defaultIfEmpty(batchNo, ""));

        if (inventory == null) {
            // 不存在库存记录，创建新记录
            inventory = new Inventory();
            inventory.setItemId(itemId);
            inventory.setWarehouseId(warehouseId);
            inventory.setQty(qty);
            inventory.setLockedQty(BigDecimal.ZERO);
            inventory.setBatchNo(batchNo);
            inventory.setVersion(0);
            baseMapper.insert(inventory);
            log.info("创建库存记录，物料id: {}, 仓库id: {}, 批次号: {}, 数量: {}", itemId, warehouseId, batchNo, qty);
        } else {
            // 使用乐观锁更新库存
            int rows = baseMapper.update(inventory,
                    new LambdaQueryWrapper<Inventory>()
                            .eq(Inventory::getId, inventory.getId())
                            .eq(Inventory::getVersion, inventory.getVersion())
            );
            if (rows == 0) {
                throw new BusinessException("库存更新失败，请重试");
            }
            inventory.setQty(inventory.getQty().add(qty));
            inventory.setVersion(inventory.getVersion() + 1);
            baseMapper.updateById(inventory);
            log.info("更新库存成功，物料id: {}, 仓库id: {}, 批次号: {}, 增加数量: {}, 当前库存: {}",
                    itemId, warehouseId, batchNo, qty, inventory.getQty());
        }

        // 记录库存流水
        recordInventoryLog(itemId, warehouseId,
                inventory.getQty().subtract(qty),
                qty,
                inventory.getQty(),
                businessType,
                businessId,
                operateBy,
                remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outbound(Long itemId, Long warehouseId, BigDecimal qty, String batchNo, Long businessId, Integer businessType, Long operateBy, String remark) {
        // 使用行锁查询库存
        Inventory inventory = baseMapper.selectForUpdate(itemId, warehouseId, StringUtils.defaultIfEmpty(batchNo, ""));

        if (inventory == null) {
            throw new BusinessException("库存不存在");
        }

        // 校验可用库存
        BigDecimal availableQty = inventory.getQty().subtract(inventory.getLockedQty());
        if (availableQty.compareTo(qty) < 0) {
            throw new BusinessException("可用库存不足，可用库存: " + availableQty + "，需出库: " + qty);
        }

        // 使用乐观锁更新库存
        int rows = baseMapper.update(inventory,
                new LambdaQueryWrapper<Inventory>()
                        .eq(Inventory::getId, inventory.getId())
                        .eq(Inventory::getVersion, inventory.getVersion())
        );
        if (rows == 0) {
            throw new BusinessException("库存更新失败，请重试");
        }
        inventory.setQty(inventory.getQty().subtract(qty));
        inventory.setVersion(inventory.getVersion() + 1);
        baseMapper.updateById(inventory);
        log.info("更新库存成功，物料id: {}, 仓库id: {}, 批次号: {}, 减少数量: {}, 当前库存: {}",
                itemId, warehouseId, batchNo, qty, inventory.getQty());

        // 记录库存流水
        recordInventoryLog(itemId, warehouseId,
                inventory.getQty().add(qty),
                qty.negate(),
                inventory.getQty(),
                businessType,
                businessId,
                operateBy,
                remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockInventory(Long itemId, Long warehouseId, BigDecimal qty) {
        // 使用行锁查询库存
        Inventory inventory = baseMapper.selectForUpdate(itemId, warehouseId, "");

        if (inventory == null) {
            throw new BusinessException("库存不存在");
        }

        // 校验可用库存
        BigDecimal availableQty = inventory.getQty().subtract(inventory.getLockedQty());
        if (availableQty.compareTo(qty) < 0) {
            throw new BusinessException("可用库存不足，无法锁定");
        }

        // 更新锁定库存
        inventory.setLockedQty(inventory.getLockedQty().add(qty));
        baseMapper.updateById(inventory);
        log.info("锁定库存成功，物料id: {}, 仓库id: {}, 锁定数量: {}", itemId, warehouseId, qty);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockInventory(Long itemId, Long warehouseId, BigDecimal qty) {
        // 使用行锁查询库存
        Inventory inventory = baseMapper.selectForUpdate(itemId, warehouseId, "");

        if (inventory == null) {
            throw new BusinessException("库存不存在");
        }

        // 校验锁定库存
        if (inventory.getLockedQty().compareTo(qty) < 0) {
            throw new BusinessException("锁定库存不足，无法释放");
        }

        // 更新锁定库存
        inventory.setLockedQty(inventory.getLockedQty().subtract(qty));
        baseMapper.updateById(inventory);
        log.info("释放锁定库存成功，物料id: {}, 仓库id: {}, 释放数量: {}", itemId, warehouseId, qty);
    }

    /**
     * 记录库存流水
     */
    private void recordInventoryLog(Long itemId, Long warehouseId,
                                     BigDecimal beforeQty, BigDecimal changeQty, BigDecimal afterQty,
                                     Integer businessType, Long businessId, Long operateBy, String remark) {
        InventoryLog log = new InventoryLog();
        log.setItemId(itemId);
        log.setWarehouseId(warehouseId);
        log.setBeforeQty(beforeQty);
        log.setChangeQty(changeQty);
        log.setAfterQty(afterQty);
        log.setBusinessType(businessType);
        log.setBusinessId(businessId);
        log.setOperateBy(operateBy);
        log.setRemark(remark);
        inventoryLogMapper.insert(log);
    }
}
