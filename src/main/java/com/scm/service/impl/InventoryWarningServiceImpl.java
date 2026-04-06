package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scm.dto.InventoryWarningDTO;
import com.scm.entity.Inventory;
import com.scm.entity.Item;
import com.scm.entity.Warehouse;
import com.scm.mapper.InventoryMapper;
import com.scm.mapper.ItemMapper;
import com.scm.mapper.WarehouseMapper;
import com.scm.service.InventoryWarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存预警Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryWarningServiceImpl implements InventoryWarningService {

    private final InventoryMapper inventoryMapper;
    private final ItemMapper itemMapper;
    private final WarehouseMapper warehouseMapper;

    /**
     * 每天上午9点执行库存预警检查
     */
    @Override
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkInventoryWarning() {
        log.info("开始执行库存预警检查任务");

        List<InventoryWarningDTO> lowStockWarnings = getLowStockWarnings();
        List<InventoryWarningDTO> highStockWarnings = getHighStockWarnings();

        // 记录预警信息
        if (!lowStockWarnings.isEmpty()) {
            log.warn("发现{}条低库存预警", lowStockWarnings.size());
            for (InventoryWarningDTO warning : lowStockWarnings) {
                log.warn("低库存预警: 商品[{}-{}]在仓库[{}]当前库存{}，低于安全库存{}",
                        warning.getItemCode(), warning.getItemName(),
                        warning.getWarehouseName(), warning.getQty(), warning.getMinQty());
                // TODO: 发送预警通知（邮件/短信/系统通知）
            }
        }

        if (!highStockWarnings.isEmpty()) {
            log.warn("发现{}条高库存预警", highStockWarnings.size());
            for (InventoryWarningDTO warning : highStockWarnings) {
                log.warn("高库存预警: 商品[{}-{}]在仓库[{}]当前库存{}，高于库存上限{}",
                        warning.getItemCode(), warning.getItemName(),
                        warning.getWarehouseName(), warning.getQty(), warning.getMaxQty());
                // TODO: 发送预警通知（邮件/短信/系统通知）
            }
        }

        log.info("库存预警检查任务执行完成，低库存预警{}条，高库存预警{}条",
                lowStockWarnings.size(), highStockWarnings.size());
    }

    @Override
    public List<Object> getLowStockWarnings() {
        // 查询所有库存
        List<Inventory> inventories = inventoryMapper.selectList(
                new LambdaQueryWrapper<Inventory>()
                        .eq(Inventory::getDeleted, 0)
        );

        List<InventoryWarningDTO> warnings = new ArrayList<>();

        for (Inventory inventory : inventories) {
            Item item = itemMapper.selectById(inventory.getItemId());
            Warehouse warehouse = warehouseMapper.selectById(inventory.getWarehouseId());

            if (item != null && warehouse != null) {
                // 检查是否低于安全库存下限
                if (item.getMinQty() != null && inventory.getQty().compareTo(item.getMinQty()) < 0) {
                    InventoryWarningDTO warning = InventoryWarningDTO.builder()
                            .id(inventory.getId())
                            .itemId(inventory.getItemId())
                            .itemCode(item.getItemCode())
                            .itemName(item.getItemName())
                            .warehouseId(inventory.getWarehouseId())
                            .warehouseName(warehouse.getName())
                            .qty(inventory.getQty())
                            .minQty(item.getMinQty())
                            .maxQty(item.getMaxQty())
                            .warningType("LOW")
                            .warningMessage(String.format("库存不足，当前库存%s，安全库存%s",
                                    inventory.getQty(), item.getMinQty()))
                            .build();
                    warnings.add(warning);
                }
            }
        }

        return new ArrayList<>(warnings);
    }

    @Override
    public List<Object> getHighStockWarnings() {
        // 查询所有库存
        List<Inventory> inventories = inventoryMapper.selectList(
                new LambdaQueryWrapper<Inventory>()
                        .eq(Inventory::getDeleted, 0)
        );

        List<InventoryWarningDTO> warnings = new ArrayList<>();

        for (Inventory inventory : inventories) {
            Item item = itemMapper.selectById(inventory.getItemId());
            Warehouse warehouse = warehouseMapper.selectById(inventory.getWarehouseId());

            if (item != null && warehouse != null) {
                // 检查是否高于库存上限
                if (item.getMaxQty() != null && inventory.getQty().compareTo(item.getMaxQty()) > 0) {
                    InventoryWarningDTO warning = InventoryWarningDTO.builder()
                            .id(inventory.getId())
                            .itemId(inventory.getItemId())
                            .itemCode(item.getItemCode())
                            .itemName(item.getItemName())
                            .warehouseId(inventory.getWarehouseId())
                            .warehouseName(warehouse.getName())
                            .qty(inventory.getQty())
                            .minQty(item.getMinQty())
                            .maxQty(item.getMaxQty())
                            .warningType("HIGH")
                            .warningMessage(String.format("库存过高，当前库存%s，库存上限%s",
                                    inventory.getQty(), item.getMaxQty()))
                            .build();
                    warnings.add(warning);
                }
            }
        }

        return new ArrayList<>(warnings);
    }
}
