package com.scm.common.exception;

/**
 * 权限异常
 *
 * @author SCM System
 * @since 2026-04-06
 */
public class PermissionException extends RuntimeException {

    private Integer code;

    public PermissionException(String message) {
        super(message);
        this.code = 403;
    }

    public PermissionException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
