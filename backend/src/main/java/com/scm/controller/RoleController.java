package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.PermissionService;
import com.scm.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/system/role")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    public RoleController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    /**
     * 分页查询角色列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roleName) {
        return Result.success(roleService.list(pageNum, pageSize, roleName));
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", roleService.create(params));
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", roleService.update(id, params));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", roleService.delete(id));
    }

    /**
     * 获取角色权限
     */
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissionIds(@PathVariable Long id) {
        return Result.success(roleService.getPermissionIds(id));
    }

    /**
     * 分配权限
     */
    @PostMapping("/{id}/permissions")
    public Result<Map<String, Object>> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        return Result.success("分配成功", roleService.assignPermissions(id, permissionIds));
    }
}
