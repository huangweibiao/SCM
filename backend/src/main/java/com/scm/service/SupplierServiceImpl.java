package com.scm.service;

import com.scm.common.AppException;
import com.scm.entity.Supplier;
import com.scm.repository.SupplierRepository;
import com.scm.util.ParamUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 供应商服务实现类
 */
@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, String keyword) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<Supplier> suppliers = supplierRepository.findAll();
        if (keyword != null && !keyword.isEmpty()) {
            suppliers = suppliers.stream()
                    .filter(s -> s.getSupplierCode().contains(keyword) || s.getSupplierName().contains(keyword))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, suppliers.size());
        List<Supplier> pageList = start < suppliers.size() ? suppliers.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", suppliers.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public List<Map<String, Object>> all() {
        List<Supplier> suppliers = supplierRepository.findByStatus(1);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", supplier.getId());
            map.put("supplierCode", supplier.getSupplierCode());
            map.put("supplierName", supplier.getSupplierName());
            map.put("contactPerson", supplier.getContactPerson());
            result.add(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException("供应商不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", supplier.getId());
        result.put("supplierCode", supplier.getSupplierCode());
        result.put("supplierName", supplier.getSupplierName());
        result.put("contactPerson", supplier.getContactPerson());
        result.put("phone", supplier.getPhone());
        result.put("email", supplier.getEmail());
        result.put("address", supplier.getAddress());
        result.put("paymentTerms", supplier.getPaymentTerms());
        result.put("rating", supplier.getRating());
        result.put("status", supplier.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String supplierCode = (String) params.get("supplierCode");
        if (supplierCode != null && supplierRepository.existsBySupplierCode(supplierCode)) {
            throw new AppException("供应商编码已存在");
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierCode(supplierCode);
        supplier.setSupplierName((String) params.get("supplierName"));
        supplier.setContactPerson((String) params.get("contactPerson"));
        supplier.setPhone((String) params.get("phone"));
        supplier.setEmail((String) params.get("email"));
        supplier.setAddress((String) params.get("address"));
        supplier.setPaymentTerms((String) params.get("paymentTerms"));
        supplier.setRating(ParamUtils.getInteger(params, "rating"));
        supplier.setStatus(1);
        supplier.setCreateTime(LocalDateTime.now());

        supplier = supplierRepository.save(supplier);

        Map<String, Object> result = new HashMap<>();
        result.put("id", supplier.getId());
        result.put("supplierCode", supplier.getSupplierCode());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException("供应商不存在"));

        if (params.containsKey("supplierName")) {
            supplier.setSupplierName((String) params.get("supplierName"));
        }
        if (params.containsKey("contactPerson")) {
            supplier.setContactPerson((String) params.get("contactPerson"));
        }
        if (params.containsKey("phone")) {
            supplier.setPhone((String) params.get("phone"));
        }
        if (params.containsKey("email")) {
            supplier.setEmail((String) params.get("email"));
        }
        if (params.containsKey("address")) {
            supplier.setAddress((String) params.get("address"));
        }
        if (params.containsKey("paymentTerms")) {
            supplier.setPaymentTerms((String) params.get("paymentTerms"));
        }
        if (params.containsKey("rating")) {
            supplier.setRating(ParamUtils.getInteger(params, "rating"));
        }
        if (params.containsKey("status")) {
            supplier.setStatus(ParamUtils.getInteger(params, "status"));
        }

        supplierRepository.save(supplier);

        return Map.of("id", supplier.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new AppException("供应商不存在");
        }
        supplierRepository.deleteById(id);
        return Map.of("id", id);
    }
}
