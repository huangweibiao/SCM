package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 仓库控制器
 */
@RestController
@RequestMapping("/basic/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * 分页查询仓库列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(warehouseService.list(pageNum, pageSize, keyword));
    }

    /**
     * 获取所有仓库（下拉框用）
     */
    @GetMapping("/all")
    public Result<List<Map<String, Object>>> all() {
        return Result.success(warehouseService.all());
    }

    /**
     * 根据ID查询仓库
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(warehouseService.getById(id));
    }

    /**
     * 创建仓库
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", warehouseService.create(params));
    }

    /**
     * 更新仓库
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", warehouseService.update(id, params));
    }

    /**
     * 删除仓库
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", warehouseService.delete(id));
    }
}
