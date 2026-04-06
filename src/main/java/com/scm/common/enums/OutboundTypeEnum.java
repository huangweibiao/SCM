package com.scm.common.enums;

import lombok.Getter;

/**
 * 出库类型枚举
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Getter
public enum OutboundTypeEnum {

    /**
     * 销售出库
     */
    SALES(10, "销售出库"),

    /**
     * 生产领料
     */
    PRODUCTION(20, "生产领料"),

    /**
     * 退货出库
     */
    RETURN(30, "退货出库"),

    /**
     * 调拨出库
     */
    TRANSFER(40, "调拨出库");

    /**
     * 类型码
     */
    private final Integer code;

    /**
     * 类型描述
     */
    private final String desc;

    OutboundTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据类型码获取枚举
     */
    public static OutboundTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OutboundTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
