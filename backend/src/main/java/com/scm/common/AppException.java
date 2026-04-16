package com.scm.common;

/**
 * 业务异常类
 * 用于抛出业务相关的异常
 */
public class AppException extends RuntimeException {

    private Integer code;

    public AppException(String message) {
        super(message);
        this.code = 500;
    }

    public AppException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
