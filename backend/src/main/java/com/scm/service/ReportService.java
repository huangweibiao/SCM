package com.scm.service;

import java.util.Map;

/**
 * 报表服务接口
 */
public interface ReportService {

    /**
     * 获取仪表盘数据
     */
    Map<String, Object> getDashboardData();

    /**
     * 采购报表
     */
    Map<String, Object> getPurchaseReport(String startDate, String endDate);

    /**
     * 销售报表
     */
    Map<String, Object> getSalesReport(String startDate, String endDate);

    /**
     * 库存报表
     */
    Map<String, Object> getInventoryReport();

    /**
     * 供应商统计
     */
    Map<String, Object> getSupplierReport();
}
