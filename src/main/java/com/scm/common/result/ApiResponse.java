package com.scm.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应对象
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功");
    }

    /**
     * 成功返回，带数据
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    /**
     * 成功返回，自定义消息
     */
    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(200, msg, data);
    }

    /**
     * 失败返回
     */
    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(500, "操作失败");
    }

    /**
     * 失败返回，自定义消息
     */
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(500, msg);
    }

    /**
     * 失败返回，自定义状态码和消息
     */
    public static <T> ApiResponse<T> error(Integer code, String msg) {
        return new ApiResponse<>(code, msg);
    }

    /**
     * 失败返回，自定义状态码
     */
    public static <T> ApiResponse<T> error(Integer code) {
        return new ApiResponse<>(code, "操作失败");
    }
}
