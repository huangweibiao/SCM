package com.scm.service;

import com.scm.common.AppException;
import com.scm.entity.Customer;
import com.scm.repository.CustomerRepository;
import com.scm.util.ParamUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 客户服务实现类
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<Customer> customers = customerRepository.findAll();

        if (keyword != null && !keyword.isEmpty()) {
            customers = customers.stream()
                    .filter(c -> c.getCustomerCode().contains(keyword) || c.getCustomerName().contains(keyword))
                    .toList();
        }
        if (status != null) {
            customers = customers.stream().filter(c -> c.getStatus().equals(status)).toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, customers.size());
        List<Customer> pageList = start < customers.size() ? customers.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageList.stream().map(this::toMap).toList());
        result.put("total", customers.size());

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException("客户不存在"));
        return toMap(customer);
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String customerCode = "C" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", customerRepository.count() + 1);

        Customer customer = new Customer();
        customer.setCustomerCode(customerCode);
        customer.setCustomerName((String) params.get("customerName"));
        customer.setContactPerson((String) params.get("contactPerson"));
        customer.setPhone((String) params.get("phone"));
        customer.setEmail((String) params.get("email"));
        customer.setAddress((String) params.get("address"));
        customer.setRating(ParamUtils.getInteger(params, "rating"));
        customer.setStatus(ParamUtils.getInteger(params, "status"));
        customer.setCreateTime(LocalDateTime.now());

        customerRepository.save(customer);

        return Map.of("id", customer.getId(), "customerCode", customer.getCustomerCode());
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException("客户不存在"));

        if (params.containsKey("customerName")) {
            customer.setCustomerName((String) params.get("customerName"));
        }
        if (params.containsKey("contactPerson")) {
            customer.setContactPerson((String) params.get("contactPerson"));
        }
        if (params.containsKey("phone")) {
            customer.setPhone((String) params.get("phone"));
        }
        if (params.containsKey("email")) {
            customer.setEmail((String) params.get("email"));
        }
        if (params.containsKey("address")) {
            customer.setAddress((String) params.get("address"));
        }
        if (params.containsKey("rating")) {
            customer.setRating(ParamUtils.getInteger(params, "rating"));
        }
        if (params.containsKey("status")) {
            customer.setStatus(ParamUtils.getInteger(params, "status"));
        }
        customer.setUpdateTime(LocalDateTime.now());

        customerRepository.save(customer);

        return Map.of("id", customer.getId(), "status", customer.getStatus());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException("客户不存在"));

        customerRepository.delete(customer);

        return Map.of("id", id);
    }

    private Map<String, Object> toMap(Customer customer) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", customer.getId());
        map.put("customerCode", customer.getCustomerCode());
        map.put("customerName", customer.getCustomerName());
        map.put("contactPerson", customer.getContactPerson());
        map.put("phone", customer.getPhone());
        map.put("email", customer.getEmail());
        map.put("address", customer.getAddress());
        map.put("rating", customer.getRating());
        map.put("status", customer.getStatus());
        return map;
    }
}
