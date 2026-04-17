package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.InboundOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 入库单控制器
 */
@RestController
@RequestMapping("/api/inbound")
public class InboundController {

    private final InboundOrderService inboundOrderService;

    public InboundController(InboundOrderService inboundOrderService) {
        this.inboundOrderService = inboundOrderService;
    }

    /**
     * 分页查询入库单列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer inboundType,
            @RequestParam(required = false) Integer status) {
        return Result.success(inboundOrderService.list(pageNum, pageSize, warehouseId, inboundType, status));
    }

    /**
     * 根据ID查询入库单
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(inboundOrderService.getById(id));
    }

    /**
     * 创建入库单
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", inboundOrderService.create(params));
    }

    /**
     * 确认入库
     */
    @PostMapping("/{id}/confirm")
    public Result<Map<String, Object>> confirm(@PathVariable Long id) {
        return Result.success("确认成功", inboundOrderService.confirm(id));
    }

    /**
     * 获取入库单明细
     */
    @GetMapping("/{id}/details")
    public Result<List<Map<String, Object>>> getDetails(@PathVariable Long id) {
        return Result.success(inboundOrderService.getDetails(id));
    }
}
