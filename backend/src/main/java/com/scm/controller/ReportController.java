package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 报表控制器
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(reportService.getDashboardData());
    }

    /**
     * 采购报表
     */
    @GetMapping("/purchase")
    public Result<Map<String, Object>> purchaseReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(reportService.getPurchaseReport(startDate, endDate));
    }

    /**
     * 销售报表
     */
    @GetMapping("/sales")
    public Result<Map<String, Object>> salesReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(reportService.getSalesReport(startDate, endDate));
    }

    /**
     * 库存报表
     */
    @GetMapping("/inventory")
    public Result<Map<String, Object>> inventoryReport() {
        return Result.success(reportService.getInventoryReport());
    }

    /**
     * 供应商统计
     */
    @GetMapping("/supplier")
    public Result<Map<String, Object>> supplierReport() {
        return Result.success(reportService.getSupplierReport());
    }
}
