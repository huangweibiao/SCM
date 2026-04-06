package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.entity.DictData;
import com.scm.entity.DictType;

import java.util.List;
import java.util.Map;

/**
 * 数据字典Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface DictService extends IService<DictType> {

    /**
     * 分页查询字典类型列表
     */
    PageResult<DictType> pageDictType(String dictCode, String dictName, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 创建字典类型
     */
    Long createDictType(DictType dictType);

    /**
     * 更新字典类型
     */
    Boolean updateDictType(DictType dictType);

    /**
     * 删除字典类型
     */
    Boolean deleteDictType(Long id);

    /**
     * 根据字典编码查询字典数据
     */
    List<DictData> getDictDataByCode(String dictCode);

    /**
     * 获取所有字典数据Map（用于前端选择器）
     */
    Map<String, List<Map<String, Object>>> getAllDictDataMap();

    /**
     * 创建字典数据
     */
    Long createDictData(DictData dictData);

    /**
     * 更新字典数据
     */
    Boolean updateDictData(DictData dictData);

    /**
     * 删除字典数据
     */
    Boolean deleteDictData(Long id);

    /**
     * 根据字典类型ID查询字典数据列表
     */
    List<DictData> getDictDataByTypeId(Long dictTypeId);
}
