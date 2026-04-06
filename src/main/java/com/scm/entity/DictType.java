package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典类型实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dict_type")
public class DictType extends BaseEntity {

    /**
     * 字典类型编码
     */
    private String dictCode;

    /**
     * 字典类型名称
     */
    private String dictName;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 1启用 0停用
     */
    private Integer status;
}
