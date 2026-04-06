package com.scm.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 操作类型
     */
    OperationType type() default OperationType.OTHER;

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 是否保存请求参数
     */
    boolean saveParams() default true;

    /**
     * 是否保存响应结果
     */
    boolean saveResult() default false;

    /**
     * 操作类型枚举
     */
    enum OperationType {
        /**
         * 查询
         */
        QUERY,

        /**
         * 新增
         */
        CREATE,

        /**
         * 修改
         */
        UPDATE,

        /**
         * 删除
         */
        DELETE,

        /**
         * 导入
         */
        IMPORT,

        /**
         * 导出
         */
        EXPORT,

        /**
         * 其他
         */
        OTHER
    }
}
