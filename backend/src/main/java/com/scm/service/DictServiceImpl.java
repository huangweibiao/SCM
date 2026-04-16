package com.scm.service;

import com.scm.common.AppException;
import com.scm.entity.DictData;
import com.scm.entity.DictType;
import com.scm.repository.DictDataRepository;
import com.scm.repository.DictTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 数据字典服务实现类
 */
@Service
public class DictServiceImpl implements DictService {

    private final DictTypeRepository dictTypeRepository;
    private final DictDataRepository dictDataRepository;

    public DictServiceImpl(DictTypeRepository dictTypeRepository, DictDataRepository dictDataRepository) {
        this.dictTypeRepository = dictTypeRepository;
        this.dictDataRepository = dictDataRepository;
    }

    @Override
    public Map<String, Object> listTypes(Integer pageNum, Integer pageSize) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<DictType> types = dictTypeRepository.findAll();

        int start = (page - 1) * size;
        int end = Math.min(start + size, types.size());
        List<DictType> pageList = start < types.size() ? types.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", types.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> createType(Map<String, Object> params) {
        String dictCode = (String) params.get("dictCode");
        if (dictTypeRepository.existsByDictCode(dictCode)) {
            throw new AppException("字典类型编码已存在");
        }

        DictType type = new DictType();
        type.setDictCode(dictCode);
        type.setDictName((String) params.get("dictName"));
        type.setSort(params.get("sort") != null ? (Integer) params.get("sort") : 0);
        type.setStatus(1);
        type.setCreateTime(LocalDateTime.now());

        type = dictTypeRepository.save(type);

        Map<String, Object> result = new HashMap<>();
        result.put("id", type.getId());
        result.put("dictCode", type.getDictCode());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> updateType(Long id, Map<String, Object> params) {
        DictType type = dictTypeRepository.findById(id)
                .orElseThrow(() -> new AppException("字典类型不存在"));

        if (params.containsKey("dictName")) {
            type.setDictName((String) params.get("dictName"));
        }
        if (params.containsKey("sort")) {
            type.setSort((Integer) params.get("sort"));
        }
        if (params.containsKey("status")) {
            type.setStatus((Integer) params.get("status"));
        }

        dictTypeRepository.save(type);

        return Map.of("id", type.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> deleteType(Long id) {
        if (!dictTypeRepository.existsById(id)) {
            throw new AppException("字典类型不存在");
        }
        // 删除关联的字典数据
        List<DictData> dataList = dictDataRepository.findByDictTypeId(id);
        dictDataRepository.deleteAll(dataList);

        dictTypeRepository.deleteById(id);
        return Map.of("id", id);
    }

    @Override
    public Map<String, Object> listData(Long dictTypeId) {
        List<DictData> dataList = dictDataRepository.findByDictTypeId(dictTypeId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", dataList);

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> createData(Map<String, Object> params) {
        DictData data = new DictData();
        data.setDictTypeId((Long) params.get("dictTypeId"));
        data.setDictLabel((String) params.get("dictLabel"));
        data.setDictValue((String) params.get("dictValue"));
        data.setSort(params.get("sort") != null ? (Integer) params.get("sort") : 0);
        data.setStatus(1);
        data.setCreateTime(LocalDateTime.now());

        data = dictDataRepository.save(data);

        Map<String, Object> result = new HashMap<>();
        result.put("id", data.getId());
        result.put("dictLabel", data.getDictLabel());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> updateData(Long id, Map<String, Object> params) {
        DictData data = dictDataRepository.findById(id)
                .orElseThrow(() -> new AppException("字典数据不存在"));

        if (params.containsKey("dictLabel")) {
            data.setDictLabel((String) params.get("dictLabel"));
        }
        if (params.containsKey("dictValue")) {
            data.setDictValue((String) params.get("dictValue"));
        }
        if (params.containsKey("sort")) {
            data.setSort((Integer) params.get("sort"));
        }
        if (params.containsKey("status")) {
            data.setStatus((Integer) params.get("status"));
        }

        dictDataRepository.save(data);

        return Map.of("id", data.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> deleteData(Long id) {
        if (!dictDataRepository.existsById(id)) {
            throw new AppException("字典数据不存在");
        }
        dictDataRepository.deleteById(id);
        return Map.of("id", id);
    }

    @Override
    public List<Map<String, Object>> getDataByTypeCode(String typeCode) {
        DictType type = dictTypeRepository.findByDictCode(typeCode)
                .orElseThrow(() -> new AppException("字典类型不存在"));

        List<DictData> dataList = dictDataRepository.findByDictTypeId(type.getId());
        List<Map<String, Object>> result = new ArrayList<>();

        for (DictData data : dataList) {
            Map<String, Object> map = new HashMap<>();
            map.put("dictLabel", data.getDictLabel());
            map.put("dictValue", data.getDictValue());
            result.add(map);
        }

        return result;
    }
}
