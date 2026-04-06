package com.scm.common.exception;

/**
 * 认证异常
 *
 * @author SCM System
 * @since 2026-04-06
 */
public class AuthException extends RuntimeException {

    private Integer code;

    public AuthException(String message) {
        super(message);
        this.code = 401;
    }

    public AuthException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
