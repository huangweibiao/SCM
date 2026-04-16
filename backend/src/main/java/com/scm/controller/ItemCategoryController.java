package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.ItemCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 物料分类控制器
 */
@RestController
@RequestMapping("/basic/itemCategory")
public class ItemCategoryController {

    private final ItemCategoryService itemCategoryService;

    public ItemCategoryController(ItemCategoryService itemCategoryService) {
        this.itemCategoryService = itemCategoryService;
    }

    /**
     * 分页查询物料分类列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String categoryName) {
        return Result.success(itemCategoryService.list(pageNum, pageSize, categoryName));
    }

    /**
     * 获取物料分类树
     */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree() {
        return Result.success(itemCategoryService.tree());
    }

    /**
     * 根据ID查询物料分类
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(itemCategoryService.getById(id));
    }

    /**
     * 创建物料分类
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", itemCategoryService.create(params));
    }

    /**
     * 更新物料分类
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", itemCategoryService.update(id, params));
    }

    /**
     * 删除物料分类
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", itemCategoryService.delete(id));
    }
}
