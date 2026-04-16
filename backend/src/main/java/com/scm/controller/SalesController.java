package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.SalesOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 销售订单控制器
 */
@RestController
@RequestMapping("/sales")
public class SalesController {

    private final SalesOrderService salesOrderService;

    public SalesController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    /**
     * 分页查询销售订单列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer status) {
        return Result.success(salesOrderService.list(pageNum, pageSize, customerName, status));
    }

    /**
     * 根据ID查询销售订单
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(salesOrderService.getById(id));
    }

    /**
     * 创建销售订单
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", salesOrderService.create(params));
    }

    /**
     * 更新销售订单
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", salesOrderService.update(id, params));
    }

    /**
     * 删除销售订单
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", salesOrderService.delete(id));
    }

    /**
     * 审核销售订单（锁定库存）
     */
    @PostMapping("/{id}/audit")
    public Result<Map<String, Object>> audit(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("审核成功", salesOrderService.audit(id, params));
    }

    /**
     * 获取订单明细
     */
    @GetMapping("/{id}/details")
    public Result<List<Map<String, Object>>> getDetails(@PathVariable Long id) {
        return Result.success(salesOrderService.getDetails(id));
    }

    /**
     * 销售发货（部分/全部）
     */
    @PostMapping("/{id}/deliver")
    public Result<Map<String, Object>> deliver(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("发货成功", salesOrderService.deliver(id, params));
    }
}
