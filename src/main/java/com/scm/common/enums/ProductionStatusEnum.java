package com.scm.common.enums;

import lombok.Getter;

/**
 * 生产状态枚举
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Getter
public enum ProductionStatusEnum {

    /**
     * 计划
     */
    PLANNED(10, "计划"),

    /**
     * 生产中
     */
    IN_PROGRESS(20, "生产中"),

    /**
     * 完工
     */
    COMPLETED(30, "完工");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    ProductionStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static ProductionStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ProductionStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否可以开始生产
     */
    public boolean canStart() {
        return this == PLANNED;
    }

    /**
     * 判断是否可以完工
     */
    public boolean canComplete() {
        return this == IN_PROGRESS;
    }
}
