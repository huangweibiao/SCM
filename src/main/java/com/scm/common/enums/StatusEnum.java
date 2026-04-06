package com.scm.common.enums;

import lombok.Getter;

/**
 * 系统状态枚举
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Getter
public enum StatusEnum {

    /**
     * 启用
     */
    ENABLED(1, "启用"),

    /**
     * 停用
     */
    DISABLED(0, "停用");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static StatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (StatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否启用
     */
    public boolean isEnabled() {
        return this == ENABLED;
    }

    /**
     * 判断是否停用
     */
    public boolean isDisabled() {
        return this == DISABLED;
    }
}
