package com.scm.service;

import com.scm.dto.LoginRequestDTO;
import com.scm.dto.LoginResponseDTO;

/**
 * 登录Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface LoginService {

    /**
     * 用户登录
     */
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    /**
     * 用户登出
     */
    void logout(String token);
}
