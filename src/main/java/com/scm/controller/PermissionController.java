package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.dto.PermissionDTO;
import com.scm.dto.RoleDTO;
import com.scm.dto.UserDTO;
import com.scm.entity.SysPermission;
import com.scm.entity.SysRole;
import com.scm.entity.SysUser;
import com.scm.service.PermissionService;
import com.scm.service.RoleService;
import com.scm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/v1/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    // ========== 用户管理 ==========

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/user")
    public ApiResponse<PageResult<SysUser>> pageUser(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SysUser> result = userService.pageQuery(username, realName, status, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建用户")
    @PostMapping("/user")
    public ApiResponse<Long> createUser(@Valid @RequestBody UserDTO userDTO) {
        Long id = userService.createUser(userDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新用户")
    @PutMapping("/user/{id}")
    public ApiResponse<Boolean> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        Boolean result = userService.updateUser(userDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/user/{id}")
    public ApiResponse<Boolean> deleteUser(@PathVariable Long id) {
        Boolean result = userService.deleteUser(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "重置密码")
    @PutMapping("/user/{id}/password")
    public ApiResponse<Boolean> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        Boolean result = userService.resetPassword(id, newPassword);
        return ApiResponse.success(result);
    }

    // ========== 角色管理 ==========

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/role")
    public ApiResponse<PageResult<SysRole>> pageRole(
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SysRole> result = roleService.pageQuery(roleCode, roleName, status, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建角色")
    @PostMapping("/role")
    public ApiResponse<Long> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Long id = roleService.createRole(roleDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新角色")
    @PutMapping("/role/{id}")
    public ApiResponse<Boolean> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        roleDTO.setId(id);
        Boolean result = roleService.updateRole(roleDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/role/{id}")
    public ApiResponse<Boolean> deleteRole(@PathVariable Long id) {
        Boolean result = roleService.deleteRole(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询角色的权限ID列表")
    @GetMapping("/role/{id}/permissions")
    public ApiResponse<List<Long>> getRolePermissionIds(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getPermissionIdsByRoleId(id);
        return ApiResponse.success(permissionIds);
    }

    // ========== 权限管理 ==========

    @Operation(summary = "查询权限树")
    @GetMapping("/tree")
    public ApiResponse<List<SysPermission>> getPermissionTree() {
        List<SysPermission> tree = permissionService.getPermissionTree();
        return ApiResponse.success(tree);
    }

    @Operation(summary = "创建权限")
    @PostMapping
    public ApiResponse<Long> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        Long id = permissionService.createPermission(permissionDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionDTO permissionDTO) {
        permissionDTO.setId(id);
        Boolean result = permissionService.updatePermission(permissionDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deletePermission(@PathVariable Long id) {
        Boolean result = permissionService.deletePermission(id);
        return ApiResponse.success(result);
    }
}
