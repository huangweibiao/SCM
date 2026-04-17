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
@RequestMapping("/api/inventory")
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

    /**
     * 库存调拨
     */
    @PostMapping("/transfer")
    public Result<Map<String, Object>> transfer(@RequestBody Map<String, Object> params) {
        return Result.success("调拨成功", inventoryService.transfer(params));
    }

    /**
     * 创建盘点单
     */
    @PostMapping("/check")
    public Result<Map<String, Object>> createCheck(@RequestBody Map<String, Object> params) {
        return Result.success("盘点单创建成功", inventoryService.createCheck(params));
    }

    /**
     * 提交盘点结果
     */
    @PostMapping("/check/{id}/result")
    public Result<Map<String, Object>> submitCheckResult(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("盘点结果提交成功", inventoryService.submitCheckResult(id, params));
    }

    /**
     * 查询盘点单列表
     */
    @GetMapping("/check/list")
    public Result<Map<String, Object>> listChecks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(inventoryService.listChecks(pageNum, pageSize, status));
    }
}
