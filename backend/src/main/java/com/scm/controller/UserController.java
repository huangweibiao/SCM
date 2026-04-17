package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/system/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {
        return Result.success(userService.list(pageNum, pageSize, username));
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", userService.create(params));
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", userService.update(id, params));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", userService.delete(id));
    }

    /**
     * 分配角色
     */
    @PostMapping("/{id}/roles")
    public Result<Map<String, Object>> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        return Result.success("分配成功", userService.assignRoles(id, roleIds));
    }
}
