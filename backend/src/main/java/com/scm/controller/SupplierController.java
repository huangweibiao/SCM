package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.SupplierService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 供应商控制器
 */
@RestController
@RequestMapping("/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * 分页查询供应商列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(supplierService.list(pageNum, pageSize, keyword));
    }

    /**
     * 获取所有供应商（下拉框用）
     */
    @GetMapping("/all")
    public Result<List<Map<String, Object>>> all() {
        return Result.success(supplierService.all());
    }

    /**
     * 根据ID查询供应商
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(supplierService.getById(id));
    }

    /**
     * 创建供应商
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", supplierService.create(params));
    }

    /**
     * 更新供应商
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", supplierService.update(id, params));
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", supplierService.delete(id));
    }
}
