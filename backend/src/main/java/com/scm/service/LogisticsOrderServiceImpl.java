package com.scm.service;

import com.scm.common.AppException;
import com.scm.entity.LogisticsOrder;
import com.scm.repository.LogisticsOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 物流订单服务实现类
 */
@Service
public class LogisticsOrderServiceImpl implements LogisticsOrderService {

    private final LogisticsOrderRepository logisticsOrderRepository;

    public LogisticsOrderServiceImpl(LogisticsOrderRepository logisticsOrderRepository) {
        this.logisticsOrderRepository = logisticsOrderRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, Integer businessType, Integer status) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<LogisticsOrder> orders = logisticsOrderRepository.findAll();

        if (businessType != null) {
            orders = orders.stream()
                    .filter(o -> o.getBusinessType() != null && o.getBusinessType().equals(businessType))
                    .toList();
        }
        if (status != null) {
            orders = orders.stream()
                    .filter(o -> o.getStatus() != null && o.getStatus().equals(status))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, orders.size());
        List<LogisticsOrder> pageList = start < orders.size() ? orders.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", orders.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        LogisticsOrder order = logisticsOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("物流订单不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("logisticsNo", order.getLogisticsNo());
        result.put("businessType", order.getBusinessType());
        result.put("businessId", order.getBusinessId());
        result.put("courierName", order.getCourierName());
        result.put("courierNo", order.getCourierNo());
        result.put("sendAddress", order.getSendAddress());
        result.put("receiveAddress", order.getReceiveAddress());
        result.put("receivePerson", order.getReceivePerson());
        result.put("receivePhone", order.getReceivePhone());
        result.put("status", order.getStatus());
        result.put("logisticsFee", order.getLogisticsFee());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String logisticsNo = generateLogisticsNo();

        LogisticsOrder order = new LogisticsOrder();
        order.setLogisticsNo(logisticsNo);
        order.setBusinessType((Integer) params.get("businessType"));
        order.setBusinessId((Long) params.get("businessId"));
        order.setCourierName((String) params.get("courierName"));
        order.setCourierNo((String) params.get("courierNo"));
        order.setSendAddress((String) params.get("sendAddress"));
        order.setReceiveAddress((String) params.get("receiveAddress"));
        order.setReceivePerson((String) params.get("receivePerson"));
        order.setReceivePhone((String) params.get("receivePhone"));
        order.setStatus(10);
        order.setLogisticsFee((BigDecimal) params.get("logisticsFee"));
        order.setCreateTime(LocalDateTime.now());

        order = logisticsOrderRepository.save(order);

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("logisticsNo", order.getLogisticsNo());
        result.put("status", order.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        LogisticsOrder order = logisticsOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("物流订单不存在"));

        if (params.containsKey("courierName")) {
            order.setCourierName((String) params.get("courierName"));
        }
        if (params.containsKey("courierNo")) {
            order.setCourierNo((String) params.get("courierNo"));
        }
        if (params.containsKey("sendAddress")) {
            order.setSendAddress((String) params.get("sendAddress"));
        }
        if (params.containsKey("receiveAddress")) {
            order.setReceiveAddress((String) params.get("receiveAddress"));
        }
        if (params.containsKey("receivePerson")) {
            order.setReceivePerson((String) params.get("receivePerson"));
        }
        if (params.containsKey("receivePhone")) {
            order.setReceivePhone((String) params.get("receivePhone"));
        }
        if (params.containsKey("logisticsFee")) {
            order.setLogisticsFee((BigDecimal) params.get("logisticsFee"));
        }

        logisticsOrderRepository.save(order);

        return Map.of("id", order.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        if (!logisticsOrderRepository.existsById(id)) {
            throw new AppException("物流订单不存在");
        }
        logisticsOrderRepository.deleteById(id);
        return Map.of("id", id);
    }

    @Override
    @Transactional
    public Map<String, Object> updateStatus(Long id, Integer status) {
        LogisticsOrder order = logisticsOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("物流订单不存在"));

        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());

        logisticsOrderRepository.save(order);

        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    private String generateLogisticsNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = logisticsOrderRepository.count() + 1;
        return "LOG" + date + String.format("%04d", count);
    }
}
