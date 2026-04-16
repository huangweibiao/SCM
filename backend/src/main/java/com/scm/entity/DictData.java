package com.scm.entity;

import com.scm.common.BaseEntity;
import jakarta.persistence.*;

/**
 * 数据字典数据实体类
 * 对应数据库表：scm_dict_data
 */
@Entity
@Table(name = "scm_dict_data")
public class DictData extends BaseEntity {

    @Column(name = "dict_type_id", nullable = false)
    private Long dictTypeId;

    @Column(name = "dict_label", nullable = false, length = 100)
    private String dictLabel;

    @Column(name = "dict_value", nullable = false, length = 100)
    private String dictValue;

    @Column(name = "sort", nullable = false)
    private Integer sort = 0;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    public Long getDictTypeId() {
        return dictTypeId;
    }

    public void setDictTypeId(Long dictTypeId) {
        this.dictTypeId = dictTypeId;
    }

    public String getDictLabel() {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
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
