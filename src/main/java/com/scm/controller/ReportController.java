package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 报表Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "报表分析")
@RestController
@RequestMapping("/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ========== 采购报表 ==========

    @Operation(summary = "采购订单量统计")
    @GetMapping("/purchase/order-count")
    public ApiResponse<Map<String, Object>> purchaseOrderCountReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> result = reportService.purchaseOrderCountReport(startDate, endDate);
        return ApiResponse.success(result);
    }

    @Operation(summary = "采购金额统计")
    @GetMapping("/purchase/amount")
    public ApiResponse<List<Map<String, Object>>> purchaseAmountReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> result = reportService.purchaseAmountReport(startDate, endDate);
        return ApiResponse.success(result);
    }

    @Operation(summary = "采购完成率统计")
    @GetMapping("/purchase/completion")
    public ApiResponse<Map<String, Object>> purchaseCompletionReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> result = reportService.purchaseCompletionReport(startDate, endDate);
        return ApiResponse.success(result);
    }

    // ========== 销售报表 ==========

    @Operation(summary = "销售订单量统计")
    @GetMapping("/sales/order-count")
    public ApiResponse<Map<String, Object>> salesOrderCountReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> result = reportService.salesOrderCountReport(startDate, endDate);
        return ApiResponse.success(result);
    }

    @Operation(summary = "销售金额统计")
    @GetMapping("/sales/amount")
    public ApiResponse<List<Map<String, Object>>> salesAmountReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> result = reportService.salesAmountReport(startDate, endDate);
        return ApiResponse.success(result);
    }

    @Operation(summary = "销售发货率统计")
    @GetMapping("/sales/shipment")
    public ApiResponse<Map<String, Object>> salesShipmentReport(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> result = reportService.salesShipmentReport(startDate, endDate);
        return ApiResponse.success(result);
    }

    // ========== 库存报表 ==========

    @Operation(summary = "库存数量统计")
    @GetMapping("/inventory/count")
    public ApiResponse<Map<String, Object>> inventoryCountReport(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId) {
        Map<String, Object> result = reportService.inventoryCountReport(warehouseId, categoryId);
        return ApiResponse.success(result);
    }

    @Operation(summary = "库存预警统计")
    @GetMapping("/inventory/warn")
    public ApiResponse<List<Map<String, Object>>> inventoryWarnReport() {
        List<Map<String, Object>> result = reportService.inventoryWarnReport();
        return ApiResponse.success(result);
    }
}
