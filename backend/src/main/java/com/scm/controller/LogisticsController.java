package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.LogisticsOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 物流订单控制器
 */
@RestController
@RequestMapping("/logistics")
public class LogisticsController {

    private final LogisticsOrderService logisticsOrderService;

    public LogisticsController(LogisticsOrderService logisticsOrderService) {
        this.logisticsOrderService = logisticsOrderService;
    }

    /**
     * 分页查询物流订单列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer businessType,
            @RequestParam(required = false) Integer status) {
        return Result.success(logisticsOrderService.list(pageNum, pageSize, businessType, status));
    }

    /**
     * 根据ID查询物流订单
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(logisticsOrderService.getById(id));
    }

    /**
     * 创建物流订单
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", logisticsOrderService.create(params));
    }

    /**
     * 更新物流订单
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", logisticsOrderService.update(id, params));
    }

    /**
     * 删除物流订单
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", logisticsOrderService.delete(id));
    }

    /**
     * 更新物流状态
     */
    @PostMapping("/{id}/status")
    public Result<Map<String, Object>> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = (Integer) params.get("status");
        return Result.success("更新成功", logisticsOrderService.updateStatus(id, status));
    }
}
