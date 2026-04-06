package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物料分类实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("item_category")
public class ItemCategory extends BaseEntity {

    /**
     * 物料分类编码
     */
    private String categoryCode;

    /**
     * 物料分类名称
     */
    private String categoryName;

    /**
     * 上级分类id，0表示顶级
     */
    private Long parentId;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 1启用 0停用
     */
    private Integer status;
}
