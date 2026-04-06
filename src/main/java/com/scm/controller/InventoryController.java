package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.entity.Inventory;
import com.scm.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 库存Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "库存管理")
@RestController
@RequestMapping("/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "分页查询库存列表")
    @GetMapping
    public ApiResponse<PageResult<Inventory>> pageQuery(
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Inventory> result = inventoryService.pageQuery(itemId, warehouseId, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询库存详情")
    @GetMapping("/{id}")
    public ApiResponse<Inventory> get(@PathVariable Long id) {
        Inventory inventory = inventoryService.getById(id);
        return ApiResponse.success(inventory);
    }
}
