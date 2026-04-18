package com.scm.service;

import com.scm.common.AppException;
import com.scm.entity.ItemCategory;
import com.scm.repository.ItemCategoryRepository;
import com.scm.util.ParamUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 物料分类服务实现类
 */
@Service
public class ItemCategoryServiceImpl implements ItemCategoryService {

    private final ItemCategoryRepository itemCategoryRepository;

    public ItemCategoryServiceImpl(ItemCategoryRepository itemCategoryRepository) {
        this.itemCategoryRepository = itemCategoryRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, String categoryName) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<ItemCategory> categories = itemCategoryRepository.findAll();
        if (categoryName != null && !categoryName.isEmpty()) {
            categories = categories.stream()
                    .filter(c -> c.getCategoryName().contains(categoryName))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, categories.size());
        List<ItemCategory> pageList = start < categories.size() ? categories.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", categories.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public List<Map<String, Object>> tree() {
        List<ItemCategory> allCategories = itemCategoryRepository.findByStatus(1);
        return buildTree(allCategories, 0L);
    }

    private List<Map<String, Object>> buildTree(List<ItemCategory> categories, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (ItemCategory category : categories) {
            if (Objects.equals(category.getParentId(), parentId)) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", category.getId());
                node.put("categoryCode", category.getCategoryCode());
                node.put("categoryName", category.getCategoryName());
                node.put("parentId", category.getParentId());
                node.put("sort", category.getSort());

                List<Map<String, Object>> children = buildTree(categories, category.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }
                tree.add(node);
            }
        }
        return tree;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        ItemCategory category = itemCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException("物料分类不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", category.getId());
        result.put("categoryCode", category.getCategoryCode());
        result.put("categoryName", category.getCategoryName());
        result.put("parentId", category.getParentId());
        result.put("sort", category.getSort());
        result.put("status", category.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String categoryCode = (String) params.get("categoryCode");
        if (itemCategoryRepository.existsByCategoryCode(categoryCode)) {
            throw new AppException("物料分类编码已存在");
        }

        ItemCategory category = new ItemCategory();
        category.setCategoryCode(categoryCode);
        category.setCategoryName((String) params.get("categoryName"));
        category.setParentId(params.get("parentId") != null ? ParamUtils.getLong(params, "parentId") : 0L);
        category.setSort(params.get("sort") != null ? ParamUtils.getInteger(params, "sort") : 0);
        category.setStatus(1);
        category.setCreateTime(LocalDateTime.now());

        category = itemCategoryRepository.save(category);

        Map<String, Object> result = new HashMap<>();
        result.put("id", category.getId());
        result.put("categoryCode", category.getCategoryCode());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        ItemCategory category = itemCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException("物料分类不存在"));

        if (params.containsKey("categoryName")) {
            category.setCategoryName((String) params.get("categoryName"));
        }
        if (params.containsKey("parentId")) {
            category.setParentId(ParamUtils.getLong(params, "parentId"));
        }
        if (params.containsKey("sort")) {
            category.setSort(ParamUtils.getInteger(params, "sort"));
        }
        if (params.containsKey("status")) {
            category.setStatus(ParamUtils.getInteger(params, "status"));
        }

        itemCategoryRepository.save(category);

        return Map.of("id", category.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        if (!itemCategoryRepository.existsById(id)) {
            throw new AppException("物料分类不存在");
        }
        itemCategoryRepository.deleteById(id);
        return Map.of("id", id);
    }
}
