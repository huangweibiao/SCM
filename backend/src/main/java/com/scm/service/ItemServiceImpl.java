package com.scm.service;

import com.scm.common.AppException;
import com.scm.entity.Item;
import com.scm.repository.ItemRepository;
import com.scm.util.ParamUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 物料服务实现类
 */
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, String keyword) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<Item> items = itemRepository.findAll();
        if (keyword != null && !keyword.isEmpty()) {
            items = items.stream()
                    .filter(i -> i.getItemCode().contains(keyword) || i.getItemName().contains(keyword))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, items.size());
        List<Item> pageList = start < items.size() ? items.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", items.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public List<Map<String, Object>> all() {
        List<Item> items = itemRepository.findAllActive();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Item item : items) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("itemCode", item.getItemCode());
            map.put("itemName", item.getItemName());
            map.put("spec", item.getSpec());
            map.put("unit", item.getUnit());
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new AppException("物料不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", item.getId());
        result.put("itemCode", item.getItemCode());
        result.put("itemName", item.getItemName());
        result.put("spec", item.getSpec());
        result.put("unit", item.getUnit());
        result.put("categoryId", item.getCategoryId());
        result.put("safetyStock", item.getSafetyStock());
        result.put("maxStock", item.getMaxStock());
        result.put("minStock", item.getMinStock());
        result.put("status", item.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String itemCode = (String) params.get("itemCode");
        if (itemRepository.existsByItemCode(itemCode)) {
            throw new AppException("物料编码已存在");
        }

        Item item = new Item();
        item.setItemCode(itemCode);
        item.setItemName(ParamUtils.getString(params, "itemName"));
        item.setSpec(ParamUtils.getString(params, "spec"));
        item.setUnit(ParamUtils.getString(params, "unit"));
        item.setCategoryId(ParamUtils.getLong(params, "categoryId"));
        item.setSafetyStock(ParamUtils.getBigDecimal(params, "safetyStock"));
        item.setMaxStock(ParamUtils.getBigDecimal(params, "maxStock"));
        item.setMinStock(ParamUtils.getBigDecimal(params, "minStock"));
        item.setStatus(1);
        item.setCreateTime(LocalDateTime.now());

        item = itemRepository.save(item);

        Map<String, Object> result = new HashMap<>();
        result.put("id", item.getId());
        result.put("itemCode", item.getItemCode());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new AppException("物料不存在"));

        if (params.containsKey("itemName")) {
            item.setItemName(ParamUtils.getString(params, "itemName"));
        }
        if (params.containsKey("spec")) {
            item.setSpec(ParamUtils.getString(params, "spec"));
        }
        if (params.containsKey("unit")) {
            item.setUnit(ParamUtils.getString(params, "unit"));
        }
        if (params.containsKey("categoryId")) {
            item.setCategoryId(ParamUtils.getLong(params, "categoryId"));
        }
        if (params.containsKey("safetyStock")) {
            item.setSafetyStock(ParamUtils.getBigDecimal(params, "safetyStock"));
        }
        if (params.containsKey("maxStock")) {
            item.setMaxStock(ParamUtils.getBigDecimal(params, "maxStock"));
        }
        if (params.containsKey("minStock")) {
            item.setMinStock(ParamUtils.getBigDecimal(params, "minStock"));
        }
        if (params.containsKey("status")) {
            item.setStatus(ParamUtils.getInteger(params, "status"));
        }

        itemRepository.save(item);

        return Map.of("id", item.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new AppException("物料不存在");
        }
        itemRepository.deleteById(id);
        return Map.of("id", id);
    }
}
