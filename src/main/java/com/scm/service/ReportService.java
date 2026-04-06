package com.scm.service;

import com.scm.common.result.ApiResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 报表Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface ReportService {

    /**
     * 采购报表 - 采购订单量统计
     */
    Map<String, Object> purchaseOrderCountReport(LocalDate startDate, LocalDate endDate);

    /**
     * 采购报表 - 采购金额统计
     */
    List<Map<String, Object>> purchaseAmountReport(LocalDate startDate, LocalDate endDate);

    /**
     * 采购报表 - 采购完成率统计
     */
    Map<String, Object> purchaseCompletionReport(LocalDate startDate, LocalDate endDate);

    /**
     * 销售报表 - 销售订单量统计
     */
    Map<String, Object> salesOrderCountReport(LocalDate startDate, LocalDate endDate);

    /**
     * 销售报表 - 销售金额统计
     */
    List<Map<String, Object>> salesAmountReport(LocalDate startDate, LocalDate endDate);

    /**
     * 销售报表 - 销售发货率统计
     */
    Map<String, Object> salesShipmentReport(LocalDate startDate, LocalDate endDate);

    /**
     * 库存报表 - 库存数量统计
     */
    Map<String, Object> inventoryCountReport(Long warehouseId, Long categoryId);

    /**
     * 库存报表 - 库存预警统计
     */
    List<Map<String, Object>> inventoryWarnReport();
}
