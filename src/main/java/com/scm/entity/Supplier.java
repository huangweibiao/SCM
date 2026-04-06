package com.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商实体类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("supplier")
public class Supplier extends BaseEntity {

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 付款条件（账期）
     */
    private String paymentTerms;

    /**
     * 供应商评级（1-5星）
     */
    private Integer rating;

    /**
     * 1启用 0停用
     */
    private Integer status;
}
