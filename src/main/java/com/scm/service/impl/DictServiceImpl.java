package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.exception.BusinessException;
import com.scm.common.result.PageResult;
import com.scm.common.util.BeanUtils;
import com.scm.entity.DictData;
import com.scm.entity.DictType;
import com.scm.mapper.DictDataMapper;
import com.scm.mapper.DictTypeMapper;
import com.scm.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据字典Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictService {

    private final DictDataMapper dictDataMapper;

    @Override
    public PageResult<DictType> pageDictType(String dictCode, String dictName, Integer status, Integer pageNum, Integer pageSize) {
        Page<DictType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dictCode != null, DictType::getDictCode, dictCode)
                .like(dictName != null, DictType::getDictName, dictName)
                .eq(status != null, DictType::getStatus, status)
                .orderByDesc(DictType::getSort)
                .orderByDesc(DictType::getCreateTime);
        Page<DictType> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public Long createDictType(DictType dictType) {
        // 检查字典编码是否存在
        Long count = this.lambdaQuery()
                .eq(DictType::getDictCode, dictType.getDictCode())
                .count();
        if (count > 0) {
            throw new BusinessException("字典类型编码已存在");
        }

        this.save(dictType);
        log.info("创建字典类型成功，id: {}, 编码: {}", dictType.getId(), dictType.getDictCode());
        return dictType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public Boolean updateDictType(DictType dictType) {
        DictType exist = this.getById(dictType.getId());
        if (exist == null) {
            throw new BusinessException("字典类型不存在");
        }

        // 检查编码是否重复
        if (!exist.getDictCode().equals(dictType.getDictCode())) {
            Long count = this.lambdaQuery()
                    .eq(DictType::getDictCode, dictType.getDictCode())
                    .ne(DictType::getId, dictType.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("字典类型编码已存在");
            }
        }

        boolean result = this.updateById(dictType);
        log.info("更新字典类型成功，id: {}", dictType.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public Boolean deleteDictType(Long id) {
        DictType dictType = this.getById(id);
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }

        // 检查是否有字典数据
        Long dataCount = dictDataMapper.selectCount(
                new LambdaQueryWrapper<DictData>()
                        .eq(DictData::getDictTypeId, id)
        );
        if (dataCount > 0) {
            throw new BusinessException("该字典类型下存在字典数据，无法删除");
        }

        boolean result = this.removeById(id);
        log.info("删除字典类型成功，id: {}", id);
        return result;
    }

    @Override
    @Cacheable(value = "dict", key = "'data:' + #dictCode")
    public List<DictData> getDictDataByCode(String dictCode) {
        // 先根据编码查询字典类型
        DictType dictType = this.lambdaQuery()
                .eq(DictType::getDictCode, dictCode)
                .eq(DictType::getStatus, 1)
                .one();

        if (dictType == null) {
            return null;
        }

        // 再根据字典类型ID查询字典数据
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictTypeId, dictType.getId())
                .eq(DictData::getStatus, 1)
                .orderByAsc(DictData::getSort)
                .orderByAsc(DictData::getId);

        return dictDataMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(value = "dict", key = "'all'")
    public Map<String, List<Map<String, Object>>> getAllDictDataMap() {
        Map<String, List<Map<String, Object>>> resultMap = new HashMap<>();

        // 查询所有启用的字典类型
        List<DictType> dictTypes = this.lambdaQuery()
                .eq(DictType::getStatus, 1)
                .list();

        // 查询每个字典类型下的数据
        for (DictType dictType : dictTypes) {
            List<DictData> dataList = getDictDataByCode(dictType.getDictCode());

            List<Map<String, Object>> maps = dataList.stream()
                    .map(data -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("label", data.getDictLabel());
                        map.put("value", data.getDictValue());
                        return map;
                    })
                    .collect(Collectors.toList());

            resultMap.put(dictType.getDictCode(), maps);
        }

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public Long createDictData(DictData dictData) {
        // 校验字典类型是否存在
        DictType dictType = this.getById(dictData.getDictTypeId());
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }

        dictDataMapper.insert(dictData);
        log.info("创建字典数据成功，id: {}", dictData.getId());
        return dictData.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public Boolean updateDictData(DictData dictData) {
        DictData exist = dictDataMapper.selectById(dictData.getId());
        if (exist == null) {
            throw new BusinessException("字典数据不存在");
        }

        boolean result = dictDataMapper.updateById(dictData) > 0;
        log.info("更新字典数据成功，id: {}", dictData.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public Boolean deleteDictData(Long id) {
        DictData dictData = dictDataMapper.selectById(id);
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }

        boolean result = dictDataMapper.deleteById(id) > 0;
        log.info("删除字典数据成功，id: {}", id);
        return result;
    }

    @Override
    public List<DictData> getDictDataByTypeId(Long dictTypeId) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictTypeId, dictTypeId)
                .eq(DictData::getStatus, 1)
                .orderByAsc(DictData::getSort)
                .orderByAsc(DictData::getId);
        return dictDataMapper.selectList(wrapper);
    }
}
