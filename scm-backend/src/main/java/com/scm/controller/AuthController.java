package com.scm.controller;

import com.scm.dto.ApiResponse;
import com.scm.dto.UserInfoDTO;
import com.scm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Authentication Controller
 * Handles user authentication and profile endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Get current authenticated user info
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoDTO>> getCurrentUser() {
        Optional<UserInfoDTO> userInfo = userService.getCurrentUserInfo();
        
        if (userInfo.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(userInfo.get()));
        } else {
            return ResponseEntity.status(401)
                .body(ApiResponse.error("Not authenticated"));
        }
    }

    /**
     * Check authentication status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkAuthStatus() {
        boolean isAuthenticated = userService.getCurrentUser().isPresent();
        return ResponseEntity.ok(ApiResponse.success(isAuthenticated));
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserInfoDTO>> updateProfile(
            @RequestBody UserInfoDTO userInfoDTO) {
        try {
            UserInfoDTO updated = userService.updateProfile(userInfoDTO);
            return ResponseEntity.ok(ApiResponse.success("Profile updated", updated));
        } catch (Exception e) {
            log.error("Failed to update profile", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update profile", e.getMessage()));
        }
    }
}
