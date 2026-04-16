package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;

/**
 * 数据字典类型实体类
 * 对应数据库表：scm_dict_type
 */
@Entity
@Table(name = "scm_dict_type")
public class DictType extends BaseEntity {

    @Column(name = "dict_code", unique = true, nullable = false, length = 50)
    private String dictCode;

    @Column(name = "dict_name", nullable = false, length = 200)
    private String dictName;

    @Column(name = "sort", nullable = false)
    private Integer sort = 0;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @Column(name = "description", length = 500)
    private String description;

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
