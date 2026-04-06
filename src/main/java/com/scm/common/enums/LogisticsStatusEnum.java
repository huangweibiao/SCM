package com.scm.common.enums;

import lombok.Getter;

/**
 * 物流状态枚举
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Getter
public enum LogisticsStatusEnum {

    /**
     * 待发货
     */
    PENDING(10, "待发货"),

    /**
     * 运输中
     */
    SHIPPING(20, "运输中"),

    /**
     * 已签收
     */
    RECEIVED(30, "已签收"),

    /**
     * 拒收
     */
    REJECTED(40, "拒收");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    LogisticsStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static LogisticsStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LogisticsStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否可以更新状态
     */
    public boolean canUpdate() {
        return this != RECEIVED && this != REJECTED;
    }
}
