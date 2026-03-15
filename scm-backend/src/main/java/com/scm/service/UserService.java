package com.scm.service;

import com.scm.config.CustomOidcUser;
import com.scm.dto.UserInfoDTO;
import com.scm.entity.SysUser;
import com.scm.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserRepository sysUserRepository;

    /**
     * Get current authenticated user
     */
    public Optional<SysUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomOidcUser oidcUser) {
            return Optional.of(oidcUser.getLocalUser());
        }
        return Optional.empty();
    }

    /**
     * Get current user info DTO
     */
    public Optional<UserInfoDTO> getCurrentUserInfo() {
        return getCurrentUser().map(this::toUserInfoDTO);
    }

    /**
     * Find user by ID
     */
    public Optional<SysUser> findById(Long id) {
        return sysUserRepository.findById(id);
    }

    /**
     * Find user by username
     */
    public Optional<SysUser> findByUsername(String username) {
        return sysUserRepository.findByUsername(username);
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserInfoDTO updateProfile(UserInfoDTO userInfoDTO) {
        SysUser user = getCurrentUser()
            .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        // Update allowed fields
        if (userInfoDTO.getNickname() != null) {
            user.setNickname(userInfoDTO.getNickname());
        }
        if (userInfoDTO.getPhone() != null) {
            user.setPhone(userInfoDTO.getPhone());
        }
        if (userInfoDTO.getAvatar() != null) {
            user.setAvatar(userInfoDTO.getAvatar());
        }

        SysUser savedUser = sysUserRepository.save(user);
        log.info("Updated profile for user: {}", savedUser.getUsername());

        return toUserInfoDTO(savedUser);
    }

    /**
     * Convert entity to DTO
     */
    public UserInfoDTO toUserInfoDTO(SysUser user) {
        return UserInfoDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .phone(user.getPhone())
            .avatar(user.getAvatar())
            .status(user.getStatus())
            .build();
    }
}
