package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限控制器
 */
@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 分页查询权限列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String permissionName) {
        return Result.success(permissionService.list(pageNum, pageSize, permissionName));
    }

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree() {
        return Result.success(permissionService.tree());
    }

    /**
     * 根据ID查询权限
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    /**
     * 创建权限
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", permissionService.create(params));
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", permissionService.update(id, params));
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", permissionService.delete(id));
    }
}
