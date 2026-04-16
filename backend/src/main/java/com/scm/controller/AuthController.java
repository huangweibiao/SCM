package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、登出等认证相关请求
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        Map<String, Object> result = userService.login(username, password);
        return Result.success("登录成功", result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/currentUser")
    public Result<Map<String, Object>> getCurrentUser() {
        Map<String, Object> result = userService.getCurrentUser();
        return Result.success(result);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.<Void>success();
    }
}
