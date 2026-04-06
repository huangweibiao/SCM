package com.scm.common.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "分页查询基类")
public class BasePageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码（从1开始）
     */
    @Schema(description = "页码（从1开始）", example = "1")
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    /**
     * 计算偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
