package com.scm.service;

import com.scm.common.AppException;
import com.scm.entity.Warehouse;
import com.scm.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 仓库服务实现类
 */
@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, String keyword) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<Warehouse> warehouses = warehouseRepository.findAll();
        if (keyword != null && !keyword.isEmpty()) {
            warehouses = warehouses.stream()
                    .filter(w -> w.getWarehouseCode().contains(keyword) || w.getWarehouseName().contains(keyword))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, warehouses.size());
        List<Warehouse> pageList = start < warehouses.size() ? warehouses.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", warehouses.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public List<Map<String, Object>> all() {
        List<Warehouse> warehouses = warehouseRepository.findByStatus(1);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Warehouse warehouse : warehouses) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", warehouse.getId());
            map.put("warehouseCode", warehouse.getWarehouseCode());
            map.put("warehouseName", warehouse.getWarehouseName());
            map.put("address", warehouse.getAddress());
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new AppException("仓库不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", warehouse.getId());
        result.put("warehouseCode", warehouse.getWarehouseCode());
        result.put("warehouseName", warehouse.getWarehouseName());
        result.put("address", warehouse.getAddress());
        result.put("manager", warehouse.getManager());
        result.put("phone", warehouse.getPhone());
        result.put("status", warehouse.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String warehouseCode = (String) params.get("warehouseCode");
        if (warehouseRepository.existsByWarehouseCode(warehouseCode)) {
            throw new AppException("仓库编码已存在");
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseCode(warehouseCode);
        warehouse.setWarehouseName((String) params.get("warehouseName"));
        warehouse.setAddress((String) params.get("address"));
        warehouse.setManager((String) params.get("manager"));
        warehouse.setPhone((String) params.get("phone"));
        warehouse.setStatus(1);
        warehouse.setCreateTime(LocalDateTime.now());

        warehouse = warehouseRepository.save(warehouse);

        Map<String, Object> result = new HashMap<>();
        result.put("id", warehouse.getId());
        result.put("warehouseCode", warehouse.getWarehouseCode());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new AppException("仓库不存在"));

        if (params.containsKey("warehouseName")) {
            warehouse.setWarehouseName((String) params.get("warehouseName"));
        }
        if (params.containsKey("address")) {
            warehouse.setAddress((String) params.get("address"));
        }
        if (params.containsKey("manager")) {
            warehouse.setManager((String) params.get("manager"));
        }
        if (params.containsKey("phone")) {
            warehouse.setPhone((String) params.get("phone"));
        }
        if (params.containsKey("status")) {
            warehouse.setStatus((Integer) params.get("status"));
        }

        warehouseRepository.save(warehouse);

        return Map.of("id", warehouse.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new AppException("仓库不存在");
        }
        warehouseRepository.deleteById(id);
        return Map.of("id", id);
    }
}
