package com.scm.common.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Getter
public enum OrderStatusEnum {

    /**
     * 待审核
     */
    PENDING(10, "待审核"),

    /**
     * 已审核
     */
    AUDITED(20, "已审核"),

    /**
     * 部分收货
     */
    PARTIAL_RECEIVED(30, "部分收货"),

    /**
     * 已完成
     */
    COMPLETED(40, "已完成"),

    /**
     * 已关闭
     */
    CLOSED(50, "已关闭"),

    /**
     * 部分发货
     */
    PARTIAL_SHIPPED(30, "部分发货");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static OrderStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否可以审核
     */
    public boolean canAudit() {
        return this == PENDING;
    }

    /**
     * 判断是否可以关闭
     */
    public boolean canClose() {
        return this != COMPLETED && this != CLOSED;
    }

    /**
     * 判断是否已完成
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * 判断是否已关闭
     */
    public boolean isClosed() {
        return this == CLOSED;
    }
}
