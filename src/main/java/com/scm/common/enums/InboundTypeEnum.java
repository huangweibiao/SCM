package com.scm.common.enums;

import lombok.Getter;

/**
 * 入库类型枚举
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Getter
public enum InboundTypeEnum {

    /**
     * 采购入库
     */
    PURCHASE(10, "采购入库"),

    /**
     * 生产入库
     */
    PRODUCTION(20, "生产入库"),

    /**
     * 退货入库
     */
    RETURN(30, "退货入库"),

    /**
     * 调拨入库
     */
    TRANSFER(40, "调拨入库");

    /**
     * 类型码
     */
    private final Integer code;

    /**
     * 类型描述
     */
    private final String desc;

    InboundTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据类型码获取枚举
     */
    public static InboundTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (InboundTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
