package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 物料实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("item")
public class Item extends BaseEntity {

    /**
     * 物料编码
     */
    private String itemCode;

    /**
     * 物料名称
     */
    private String itemName;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 单位（个/箱/吨）
     */
    private String unit;

    /**
     * 物料分类id
     */
    private Long categoryId;

    /**
     * 安全库存
     */
    private BigDecimal safetyStock;

    /**
     * 最高库存
     */
    private BigDecimal maxStock;

    /**
     * 最低库存
     */
    private BigDecimal minStock;

    /**
     * 1启用 0停用
     */
    private Integer status;
}
