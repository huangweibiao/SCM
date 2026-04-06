package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.dto.LoginRequestDTO;
import com.scm.dto.LoginResponseDTO;
import com.scm.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = loginService.login(loginRequestDTO);
        return ApiResponse.success(response);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            loginService.logout(token);
        }
        return ApiResponse.success();
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/userinfo")
    public ApiResponse<LoginResponseDTO> getUserInfo(HttpServletRequest request) {
        String token = extractToken(request);
        // 这里可以扩展获取当前用户信息的功能
        return ApiResponse.success();
    }

    /**
     * 从请求头中提取Token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
