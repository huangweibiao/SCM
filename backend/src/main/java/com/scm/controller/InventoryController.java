package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 库存控制器
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * 分页查询库存列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long warehouseId) {
        return Result.success(inventoryService.list(pageNum, pageSize, itemId, warehouseId));
    }

    /**
     * 根据ID查询库存
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(inventoryService.getById(id));
    }

    /**
     * 查询物料在指定仓库的库存
     */
    @GetMapping("/item/{itemId}/warehouse/{warehouseId}")
    public Result<Map<String, Object>> getByItemAndWarehouse(
            @PathVariable Long itemId,
            @PathVariable Long warehouseId) {
        return Result.success(inventoryService.getByItemAndWarehouse(itemId, warehouseId));
    }

    /**
     * 库存预警查询
     */
    @GetMapping("/warnings")
    public Result<List<Map<String, Object>>> getWarnings() {
        return Result.success(inventoryService.getWarnings());
    }
}
