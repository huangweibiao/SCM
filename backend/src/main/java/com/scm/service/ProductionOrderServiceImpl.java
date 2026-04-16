package com.scm.service;

import com.scm.common.AppException;
import com.scm.common.Constants;
import com.scm.entity.ProductionOrder;
import com.scm.repository.ProductionOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 生产工单服务实现类
 */
@Service
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final InboundOrderService inboundOrderService;

    public ProductionOrderServiceImpl(ProductionOrderRepository productionOrderRepository,
                                        InboundOrderService inboundOrderService) {
        this.productionOrderRepository = productionOrderRepository;
        this.inboundOrderService = inboundOrderService;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, Integer status) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<ProductionOrder> orders = productionOrderRepository.findAll();

        if (status != null) {
            orders = orders.stream()
                    .filter(o -> o.getStatus().equals(status))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, orders.size());
        List<ProductionOrder> pageList = start < orders.size() ? orders.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", orders.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("生产工单不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("moNo", order.getMoNo());
        result.put("itemId", order.getItemId());
        result.put("qty", order.getQty());
        result.put("finishedQty", order.getFinishedQty());
        result.put("startDate", order.getStartDate());
        result.put("endDate", order.getEndDate());
        result.put("actualStart", order.getActualStart());
        result.put("actualEnd", order.getActualEnd());
        result.put("status", order.getStatus());
        result.put("remark", order.getRemark());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String moNo = generateMoNo();

        ProductionOrder order = new ProductionOrder();
        order.setMoNo(moNo);
        order.setItemId((Long) params.get("itemId"));
        order.setQty((BigDecimal) params.get("qty"));
        order.setStartDate((LocalDate) params.get("startDate"));
        order.setEndDate((LocalDate) params.get("endDate"));
        order.setStatus(Constants.ProductionStatus.PLANNED);
        order.setFinishedQty(BigDecimal.ZERO);
        order.setCreateTime(LocalDateTime.now());
        order.setRemark((String) params.get("remark"));

        order = productionOrderRepository.save(order);

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("moNo", order.getMoNo());
        result.put("status", order.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("生产工单不存在"));

        if (order.getStatus() != Constants.ProductionStatus.PLANNED) {
            throw new AppException("只有计划状态的工单才能修改");
        }

        if (params.containsKey("itemId")) {
            order.setItemId((Long) params.get("itemId"));
        }
        if (params.containsKey("qty")) {
            order.setQty((BigDecimal) params.get("qty"));
        }
        if (params.containsKey("startDate")) {
            order.setStartDate((LocalDate) params.get("startDate"));
        }
        if (params.containsKey("endDate")) {
            order.setEndDate((LocalDate) params.get("endDate"));
        }
        if (params.containsKey("remark")) {
            order.setRemark((String) params.get("remark"));
        }

        productionOrderRepository.save(order);

        return Map.of("id", order.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> start(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("生产工单不存在"));

        if (order.getStatus() != Constants.ProductionStatus.PLANNED) {
            throw new AppException("只有计划状态的工单才能开始生产");
        }

        order.setStatus(Constants.ProductionStatus.IN_PROGRESS);
        order.setActualStart(LocalDateTime.now());

        productionOrderRepository.save(order);

        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    @Override
    @Transactional
    public Map<String, Object> finish(Long id, Map<String, Object> params) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("生产工单不存在"));

        if (order.getStatus() != Constants.ProductionStatus.IN_PROGRESS) {
            throw new AppException("只有生产中的工单才能完工");
        }

        BigDecimal finishedQty = (BigDecimal) params.get("finishedQty");
        order.setFinishedQty(order.getFinishedQty().add(finishedQty));
        order.setActualEnd(LocalDateTime.now());

        // 判断是否全部完工
        if (order.getFinishedQty().compareTo(order.getQty()) >= 0) {
            order.setStatus(Constants.ProductionStatus.FINISHED);
        }

        productionOrderRepository.save(order);

        // 创建入库单（生产入库）
        Map<String, Object> inboundParams = new HashMap<>();
        inboundParams.put("moId", order.getId());
        inboundParams.put("warehouseId", params.get("warehouseId"));
        inboundParams.put("inboundType", Constants.InboundType.PRODUCTION);

        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("itemId", order.getItemId());
        item.put("qty", finishedQty);
        item.put("price", params.get("price"));
        item.put("batchNo", params.get("batchNo"));
        items.add(item);
        inboundParams.put("items", items);

        inboundOrderService.create(inboundParams);

        return Map.of("id", order.getId(), "status", order.getStatus(), "finishedQty", order.getFinishedQty());
    }

    private String generateMoNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = productionOrderRepository.count() + 1;
        return "MO" + date + String.format("%04d", count);
    }
}
