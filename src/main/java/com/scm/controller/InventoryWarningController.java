package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.dto.InventoryWarningDTO;
import com.scm.service.InventoryWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存预警Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "库存预警")
@RestController
@RequestMapping("/v1/inventory-warning")
@RequiredArgsConstructor
public class InventoryWarningController {

    private final InventoryWarningService inventoryWarningService;

    @Operation(summary = "手动触发库存预警检查")
    @PostMapping("/check")
    public ApiResponse<Void> manualCheck() {
        inventoryWarningService.checkInventoryWarning();
        return ApiResponse.success();
    }

    @Operation(summary = "获取低库存预警列表")
    @GetMapping("/low")
    public ApiResponse<List<InventoryWarningDTO>> getLowStockWarnings() {
        List<InventoryWarningDTO> warnings = (List<InventoryWarningDTO>) inventoryWarningService.getLowStockWarnings();
        return ApiResponse.success(warnings);
    }

    @Operation(summary = "获取高库存预警列表")
    @GetMapping("/high")
    public ApiResponse<List<InventoryWarningDTO>> getHighStockWarnings() {
        List<InventoryWarningDTO> warnings = (List<InventoryWarningDTO>) inventoryWarningService.getHighStockWarnings();
        return ApiResponse.success(warnings);
    }
}
