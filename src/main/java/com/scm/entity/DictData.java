package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 数据字典数据实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@TableName("dict_data")
public class DictData {

    /**
     * 主键
     */
    private Long id;

    /**
     * 字典类型id
     */
    private Long dictTypeId;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 1启用 0停用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;
}
