package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.PurchaseOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 采购订单控制器
 */
@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    /**
     * 分页查询采购订单列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status) {
        return Result.success(purchaseOrderService.list(pageNum, pageSize, supplierId, status));
    }

    /**
     * 根据ID查询采购订单
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(purchaseOrderService.getById(id));
    }

    /**
     * 创建采购订单
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", purchaseOrderService.create(params));
    }

    /**
     * 更新采购订单
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", purchaseOrderService.update(id, params));
    }

    /**
     * 删除采购订单
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", purchaseOrderService.delete(id));
    }

    /**
     * 审核采购订单
     */
    @PostMapping("/{id}/audit")
    public Result<Map<String, Object>> audit(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("审核成功", purchaseOrderService.audit(id, params));
    }

    /**
     * 获取订单明细
     */
    @GetMapping("/{id}/details")
    public Result<List<Map<String, Object>>> getDetails(@PathVariable Long id) {
        return Result.success(purchaseOrderService.getDetails(id));
    }

    /**
     * 采购收货（部分/全部）
     */
    @PostMapping("/{id}/receive")
    public Result<Map<String, Object>> receive(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("收货成功", purchaseOrderService.receive(id, params));
    }

    /**
     * 关闭采购订单
     */
    @PostMapping("/{id}/close")
    public Result<Map<String, Object>> close(@PathVariable Long id) {
        return Result.success("关闭成功", purchaseOrderService.close(id));
    }
}
