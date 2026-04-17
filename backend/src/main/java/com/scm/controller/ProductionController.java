package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.ProductionOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 生产工单控制器
 */
@RestController
@RequestMapping("/api/production")
public class ProductionController {

    private final ProductionOrderService productionOrderService;

    public ProductionController(ProductionOrderService productionOrderService) {
        this.productionOrderService = productionOrderService;
    }

    /**
     * 分页查询生产工单列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(productionOrderService.list(pageNum, pageSize, status));
    }

    /**
     * 根据ID查询生产工单
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(productionOrderService.getById(id));
    }

    /**
     * 创建生产工单
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", productionOrderService.create(params));
    }

    /**
     * 更新生产工单
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", productionOrderService.update(id, params));
    }

    /**
     * 开始生产
     */
    @PostMapping("/{id}/start")
    public Result<Map<String, Object>> start(@PathVariable Long id) {
        return Result.success("开始成功", productionOrderService.start(id));
    }

    /**
     * 完工入库
     */
    @PostMapping("/{id}/finish")
    public Result<Map<String, Object>> finish(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("完工成功", productionOrderService.finish(id, params));
    }

    /**
     * 生产领料
     */
    @PostMapping("/{id}/pick")
    public Result<Map<String, Object>> pick(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("领料成功", productionOrderService.pickMaterials(id, params));
    }
}
