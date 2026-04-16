package com.scm.service;

import java.util.List;
import java.util.Map;

/**
 * 数据字典服务接口
 */
public interface DictService {

    /**
     * 查询字典类型列表
     */
    Map<String, Object> listTypes(Integer pageNum, Integer pageSize);

    /**
     * 创建字典类型
     */
    Map<String, Object> createType(Map<String, Object> params);

    /**
     * 更新字典类型
     */
    Map<String, Object> updateType(Long id, Map<String, Object> params);

    /**
     * 删除字典类型
     */
    Map<String, Object> deleteType(Long id);

    /**
     * 查询字典数据列表
     */
    Map<String, Object> listData(Long dictTypeId);

    /**
     * 创建字典数据
     */
    Map<String, Object> createData(Map<String, Object> params);

    /**
     * 更新字典数据
     */
    Map<String, Object> updateData(Long id, Map<String, Object> params);

    /**
     * 删除字典数据
     */
    Map<String, Object> deleteData(Long id);

    /**
     * 根据类型编码获取字典数据
     */
    List<Map<String, Object>> getDataByTypeCode(String typeCode);
}
