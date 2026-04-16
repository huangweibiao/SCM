package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 物料控制器
 */
@RestController
@RequestMapping("/basic/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * 分页查询物料列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(itemService.list(pageNum, pageSize, keyword));
    }

    /**
     * 获取所有物料（下拉框用）
     */
    @GetMapping("/all")
    public Result<List<Map<String, Object>>> all() {
        return Result.success(itemService.all());
    }

    /**
     * 根据ID查询物料
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(itemService.getById(id));
    }

    /**
     * 创建物料
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", itemService.create(params));
    }

    /**
     * 更新物料
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", itemService.update(id, params));
    }

    /**
     * 删除物料
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", itemService.delete(id));
    }
}
