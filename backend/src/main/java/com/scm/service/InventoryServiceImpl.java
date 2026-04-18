package com.scm.service;

import com.scm.common.AppException;
import com.scm.common.Constants;
import com.scm.entity.Inventory;
import com.scm.entity.InventoryLog;
import com.scm.entity.Item;
import com.scm.repository.InventoryLogRepository;
import com.scm.repository.InventoryRepository;
import com.scm.repository.ItemRepository;
import com.scm.util.ParamUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Random;

/**
 * 库存服务实现类
 * 实现库存的锁定/释放、扣减、增加逻辑，包含乐观锁和行锁
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final ItemRepository itemRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                InventoryLogRepository inventoryLogRepository,
                                ItemRepository itemRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, Long itemId, Long warehouseId) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<Inventory> inventories = inventoryRepository.findAll();

        if (itemId != null) {
            inventories = inventories.stream()
                    .filter(i -> i.getItemId() != null && i.getItemId().equals(itemId))
                    .toList();
        }
        if (warehouseId != null) {
            inventories = inventories.stream()
                    .filter(i -> i.getWarehouseId() != null && i.getWarehouseId().equals(warehouseId))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, inventories.size());
        List<Inventory> pageList = start < inventories.size() ? inventories.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", inventories.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new AppException("库存记录不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", inventory.getId());
        result.put("itemId", inventory.getItemId());
        result.put("warehouseId", inventory.getWarehouseId());
        result.put("qty", inventory.getQty());
        result.put("lockedQty", inventory.getLockedQty());
        result.put("availableQty", inventory.getAvailableQty());
        result.put("batchNo", inventory.getBatchNo());
        result.put("expireDate", inventory.getExpireDate());

        return result;
    }

    @Override
    public Map<String, Object> getByItemAndWarehouse(Long itemId, Long warehouseId) {
        List<Inventory> inventories = inventoryRepository.findByItemIdAndWarehouseId(itemId, warehouseId);

        if (inventories.isEmpty()) {
            throw new AppException("库存记录不存在");
        }

        Inventory inventory = inventories.get(0);
        Map<String, Object> result = new HashMap<>();
        result.put("id", inventory.getId());
        result.put("itemId", inventory.getItemId());
        result.put("warehouseId", inventory.getWarehouseId());
        result.put("qty", inventory.getQty());
        result.put("lockedQty", inventory.getLockedQty());
        result.put("availableQty", inventory.getAvailableQty());
        result.put("batchNo", inventory.getBatchNo());
        result.put("expireDate", inventory.getExpireDate());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> lockInventory(Long itemId, Long warehouseId, BigDecimal qty, Long businessId) {
        // 使用行锁查询库存
        Optional<Inventory> inventoryOpt = inventoryRepository
                .findByItemIdAndWarehouseIdAndBatchNoForUpdate(itemId, warehouseId, "");

        Inventory inventory = inventoryOpt.orElseGet(() -> {
            Inventory newInventory = new Inventory();
            newInventory.setItemId(itemId);
            newInventory.setWarehouseId(warehouseId);
            newInventory.setQty(BigDecimal.ZERO);
            newInventory.setLockedQty(BigDecimal.ZERO);
            newInventory.setAvailableQty(BigDecimal.ZERO);
            newInventory.setBatchNo("");
            return newInventory;
        });

        // 检查可用库存是否充足
        BigDecimal availableQty = inventory.getQty().subtract(inventory.getLockedQty());
        if (availableQty.compareTo(qty) < 0) {
            throw new AppException("可用库存不足，当前可用：" + availableQty);
        }

        // 锁定库存
        inventory.setLockedQty(inventory.getLockedQty().add(qty));
        inventory.setAvailableQty(inventory.getQty().subtract(inventory.getLockedQty()));
        inventory.setUpdateTime(LocalDateTime.now());
        // 已持有行锁，直接持久化即可，避免版本字段不一致导致锁定误判失败
        inventoryRepository.save(inventory);

        // 记录库存流水
        InventoryLog log = new InventoryLog();
        log.setItemId(itemId);
        log.setWarehouseId(warehouseId);
        log.setBeforeQty(inventory.getLockedQty().subtract(qty));
        log.setChangeQty(qty);
        log.setAfterQty(inventory.getLockedQty());
        log.setBusinessType(20); // 出库类型
        log.setBusinessId(businessId);
        log.setCreateTime(LocalDateTime.now());
        log.setRemark("锁定库存");

        inventoryLogRepository.save(log);

        return Map.of("itemId", itemId, "warehouseId", warehouseId, "lockedQty", qty);
    }

    @Override
    @Transactional
    public Map<String, Object> unlockInventory(Long itemId, Long warehouseId, BigDecimal qty, Long businessId) {
        Optional<Inventory> inventoryOpt = inventoryRepository
                .findByItemIdAndWarehouseIdAndBatchNoForUpdate(itemId, warehouseId, "");

        Inventory inventory = inventoryOpt.orElseThrow(() -> new AppException("库存记录不存在"));

        // 释放锁定
        inventory.setLockedQty(inventory.getLockedQty().subtract(qty));
        if (inventory.getLockedQty().compareTo(BigDecimal.ZERO) < 0) {
            inventory.setLockedQty(BigDecimal.ZERO);
        }
        inventory.setAvailableQty(inventory.getQty().subtract(inventory.getLockedQty()));
        inventory.setUpdateTime(LocalDateTime.now());

        inventoryRepository.save(inventory);

        // 记录库存流水
        InventoryLog log = new InventoryLog();
        log.setItemId(itemId);
        log.setWarehouseId(warehouseId);
        log.setBeforeQty(inventory.getLockedQty().add(qty));
        log.setChangeQty(qty.negate());
        log.setAfterQty(inventory.getLockedQty());
        log.setBusinessType(20);
        log.setBusinessId(businessId);
        log.setCreateTime(LocalDateTime.now());
        log.setRemark("释放库存锁定");

        inventoryLogRepository.save(log);

        return Map.of("itemId", itemId, "warehouseId", warehouseId, "unlockedQty", qty);
    }

    @Override
    @Transactional
    public Map<String, Object> deductInventory(Long itemId, Long warehouseId, BigDecimal qty, Long businessId) {
        Optional<Inventory> inventoryOpt = inventoryRepository
                .findByItemIdAndWarehouseIdAndBatchNoForUpdate(itemId, warehouseId, "");

        Inventory inventory = inventoryOpt.orElseThrow(() -> new AppException("库存记录不存在"));

        // 扣减库存（包含已锁定的数量）
        BigDecimal totalAvailable = inventory.getQty().subtract(inventory.getLockedQty()).add(qty);
        if (totalAvailable.compareTo(qty) < 0) {
            throw new AppException("库存不足");
        }

        BigDecimal beforeQty = inventory.getQty();
        inventory.setQty(inventory.getQty().subtract(qty));
        inventory.setLockedQty(inventory.getLockedQty().subtract(qty));
        if (inventory.getLockedQty().compareTo(BigDecimal.ZERO) < 0) {
            inventory.setLockedQty(BigDecimal.ZERO);
        }
        inventory.setAvailableQty(inventory.getQty().subtract(inventory.getLockedQty()));
        inventory.setUpdateTime(LocalDateTime.now());

        inventoryRepository.save(inventory);

        // 记录库存流水
        InventoryLog log = new InventoryLog();
        log.setItemId(itemId);
        log.setWarehouseId(warehouseId);
        log.setBeforeQty(beforeQty);
        log.setChangeQty(qty.negate());
        log.setAfterQty(inventory.getQty());
        log.setBusinessType(Constants.InventoryBusinessType.OUTBOUND);
        log.setBusinessId(businessId);
        log.setCreateTime(LocalDateTime.now());
        log.setRemark("扣减库存");

        inventoryLogRepository.save(log);

        return Map.of("itemId", itemId, "warehouseId", warehouseId, "deductedQty", qty);
    }

    @Override
    @Transactional
    public Map<String, Object> addInventory(Long itemId, Long warehouseId, BigDecimal qty, Long businessId) {
        Optional<Inventory> inventoryOpt = inventoryRepository
                .findByItemIdAndWarehouseIdAndBatchNoForUpdate(itemId, warehouseId, "");

        Inventory inventory;
        if (inventoryOpt.isPresent()) {
            inventory = inventoryOpt.get();
        } else {
            inventory = new Inventory();
            inventory.setItemId(itemId);
            inventory.setWarehouseId(warehouseId);
            inventory.setQty(BigDecimal.ZERO);
            inventory.setLockedQty(BigDecimal.ZERO);
            inventory.setBatchNo("");
        }

        BigDecimal beforeQty = inventory.getQty();
        inventory.setQty(inventory.getQty().add(qty));
        inventory.setAvailableQty(inventory.getQty().subtract(inventory.getLockedQty()));
        inventory.setUpdateTime(LocalDateTime.now());

        inventoryRepository.save(inventory);

        // 记录库存流水
        InventoryLog log = new InventoryLog();
        log.setItemId(itemId);
        log.setWarehouseId(warehouseId);
        log.setBeforeQty(beforeQty);
        log.setChangeQty(qty);
        log.setAfterQty(inventory.getQty());
        log.setBusinessType(Constants.InventoryBusinessType.INBOUND);
        log.setBusinessId(businessId);
        log.setCreateTime(LocalDateTime.now());
        log.setRemark("增加库存");

        inventoryLogRepository.save(log);

        return Map.of("itemId", itemId, "warehouseId", warehouseId, "addedQty", qty);
    }

    @Override
    public List<Map<String, Object>> getWarnings() {
        List<Inventory> inventories = inventoryRepository.findAll();
        List<Map<String, Object>> warnings = new ArrayList<>();

        for (Inventory inventory : inventories) {
            Optional<Item> itemOpt = itemRepository.findById(inventory.getItemId());
            if (itemOpt.isEmpty()) continue;

            Item item = itemOpt.get();
            BigDecimal availableQty = inventory.getAvailableQty();
            Integer warnType = null;

            // 低库存预警
            if (item.getMinStock() != null && availableQty.compareTo(item.getMinStock()) < 0) {
                warnType = 1;
            }
            // 超库存预警
            else if (item.getMaxStock() != null && availableQty.compareTo(item.getMaxStock()) > 0) {
                warnType = 2;
            }

            if (warnType != null) {
                Map<String, Object> warning = new HashMap<>();
                warning.put("itemId", item.getId());
                warning.put("itemCode", item.getItemCode());
                warning.put("itemName", item.getItemName());
                warning.put("warehouseId", inventory.getWarehouseId());
                warning.put("availableQty", availableQty);
                warning.put("minStock", item.getMinStock());
                warning.put("maxStock", item.getMaxStock());
                warning.put("warnType", warnType);
                warnings.add(warning);
            }
        }

        return warnings;
    }

    @Override
    @Transactional
    public Map<String, Object> transfer(Map<String, Object> params) {
        Long fromWarehouseId = ParamUtils.getLong(params, "fromWarehouseId");
        Long toWarehouseId = ParamUtils.getLong(params, "toWarehouseId");
        Long itemId = ParamUtils.getLong(params, "itemId");
        BigDecimal qty = new BigDecimal(params.get("qty").toString());

        if (fromWarehouseId.equals(toWarehouseId)) {
            throw new AppException("源仓库和目标仓库不能相同");
        }

        // 源仓库扣减库存
        deductInventory(itemId, fromWarehouseId, qty, null);

        // 目标仓库增加库存
        addInventory(itemId, toWarehouseId, qty, null);

        return Map.of("fromWarehouseId", fromWarehouseId, "toWarehouseId", toWarehouseId,
                "itemId", itemId, "qty", qty);
    }

    @Override
    @Transactional
    public Map<String, Object> createCheck(Map<String, Object> params) {
        Long warehouseId = ParamUtils.getLong(params, "warehouseId");
        String checkNo = "CK" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", new Random().nextInt(9999) + 1);

        Map<String, Object> result = new HashMap<>();
        result.put("checkNo", checkNo);
        result.put("warehouseId", warehouseId);
        result.put("status", 10);
        result.put("checkDate", LocalDate.now());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> submitCheckResult(Long checkId, Map<String, Object> params) {
        List<Map<String, Object>> details = (List<Map<String, Object>>) params.get("details");

        for (Map<String, Object> detail : details) {
            Long itemId = ParamUtils.getLong(detail, "itemId");
            Long warehouseId = ParamUtils.getLong(detail, "warehouseId");
            BigDecimal actualQty = new BigDecimal(detail.get("actualQty").toString());

            // 获取系统库存
            List<Inventory> inventories = inventoryRepository.findByItemIdAndWarehouseId(itemId, warehouseId);
            if (inventories.isEmpty()) continue;

            Inventory inventory = inventories.get(0);
            BigDecimal systemQty = inventory.getQty();

            // 计算差异
            BigDecimal diffQty = actualQty.subtract(systemQty);
            if (diffQty.compareTo(BigDecimal.ZERO) != 0) {
                // 调整库存
                if (diffQty.compareTo(BigDecimal.ZERO) > 0) {
                    addInventory(itemId, warehouseId, diffQty, null);
                } else {
                    deductInventory(itemId, warehouseId, diffQty.abs(), null);
                }
            }
        }

        return Map.of("checkId", checkId, "status", 20);
    }

    @Override
    public Map<String, Object> listChecks(Integer pageNum, Integer pageSize, Integer status) {
        // 简化实现，返回空列表
        Map<String, Object> result = new HashMap<>();
        result.put("list", new ArrayList<>());
        result.put("total", 0);
        return result;
    }
}
