package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;

/**
 * 物料分类实体类
 * 对应数据库表：scm_item_category
 */
@Entity
@Table(name = "scm_item_category")
public class ItemCategory extends BaseEntity {

    @Column(name = "category_code", unique = true, nullable = false, length = 50)
    private String categoryCode;

    @Column(name = "category_name", nullable = false, length = 200)
    private String categoryName;

    @Column(name = "parent_id")
    private Long parentId = 0L;

    @Column(name = "sort", nullable = false)
    private Integer sort = 0;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
