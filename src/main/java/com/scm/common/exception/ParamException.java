package com.scm.common.exception;

/**
 * 参数异常
 *
 * @author SCM System
 * @since 2026-04-06
 */
public class ParamException extends RuntimeException {

    private Integer code;

    public ParamException(String message) {
        super(message);
        this.code = 400;
    }

    public ParamException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ParamException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }

    public Integer getCode() {
        return code;
    }
}
