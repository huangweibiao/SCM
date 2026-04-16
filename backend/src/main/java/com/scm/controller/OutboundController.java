package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.OutboundOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 出库单控制器
 */
@RestController
@RequestMapping("/outbound")
public class OutboundController {

    private final OutboundOrderService outboundOrderService;

    public OutboundController(OutboundOrderService outboundOrderService) {
        this.outboundOrderService = outboundOrderService;
    }

    /**
     * 分页查询出库单列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer outboundType,
            @RequestParam(required = false) Integer status) {
        return Result.success(outboundOrderService.list(pageNum, pageSize, warehouseId, outboundType, status));
    }

    /**
     * 根据ID查询出库单
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(outboundOrderService.getById(id));
    }

    /**
     * 创建出库单
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", outboundOrderService.create(params));
    }

    /**
     * 确认出库
     */
    @PostMapping("/{id}/confirm")
    public Result<Map<String, Object>> confirm(@PathVariable Long id) {
        return Result.success("确认成功", outboundOrderService.confirm(id));
    }

    /**
     * 获取出库单明细
     */
    @GetMapping("/{id}/details")
    public Result<List<Map<String, Object>>> getDetails(@PathVariable Long id) {
        return Result.success(outboundOrderService.getDetails(id));
    }
}
