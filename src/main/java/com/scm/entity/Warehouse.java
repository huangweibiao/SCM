package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse")
public class Warehouse extends BaseEntity {

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 仓库地址
     */
    private String address;

    /**
     * 仓库管理员
     */
    private String manager;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 1启用 0停用
     */
    private Integer status;
}
