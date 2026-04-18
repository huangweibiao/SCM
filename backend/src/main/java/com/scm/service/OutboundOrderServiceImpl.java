package com.scm.service;

import com.scm.common.AppException;
import com.scm.common.Constants;
import com.scm.entity.OutboundOrder;
import com.scm.entity.OutboundOrderDetail;
import com.scm.entity.SalesOrder;
import com.scm.entity.SalesOrderDetail;
import com.scm.repository.OutboundOrderDetailRepository;
import com.scm.repository.OutboundOrderRepository;
import com.scm.repository.SalesOrderDetailRepository;
import com.scm.repository.SalesOrderRepository;
import com.scm.util.ParamUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 出库单服务实现类
 */
@Service
public class OutboundOrderServiceImpl implements OutboundOrderService {

    private final OutboundOrderRepository outboundOrderRepository;
    private final OutboundOrderDetailRepository outboundOrderDetailRepository;
    private final InventoryService inventoryService;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderDetailRepository salesOrderDetailRepository;

    public OutboundOrderServiceImpl(OutboundOrderRepository outboundOrderRepository,
                                      OutboundOrderDetailRepository outboundOrderDetailRepository,
                                      InventoryService inventoryService,
                                      SalesOrderRepository salesOrderRepository,
                                      SalesOrderDetailRepository salesOrderDetailRepository) {
        this.outboundOrderRepository = outboundOrderRepository;
        this.outboundOrderDetailRepository = outboundOrderDetailRepository;
        this.inventoryService = inventoryService;
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderDetailRepository = salesOrderDetailRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, Long warehouseId, Integer outboundType, Integer status) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<OutboundOrder> orders = outboundOrderRepository.findAll();

        if (warehouseId != null) {
            orders = orders.stream()
                    .filter(o -> o.getWarehouseId() != null && o.getWarehouseId().equals(warehouseId))
                    .toList();
        }
        if (outboundType != null) {
            orders = orders.stream()
                    .filter(o -> o.getOutboundType() != null && o.getOutboundType().equals(outboundType))
                    .toList();
        }
        if (status != null) {
            orders = orders.stream()
                    .filter(o -> o.getStatus() != null && o.getStatus().equals(status))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, orders.size());
        List<OutboundOrder> pageList = start < orders.size() ? orders.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", orders.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        OutboundOrder order = outboundOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("出库单不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("outboundNo", order.getOutboundNo());
        result.put("soId", order.getSoId());
        result.put("moId", order.getMoId());
        result.put("warehouseId", order.getWarehouseId());
        result.put("outboundType", order.getOutboundType());
        result.put("outboundDate", order.getOutboundDate());
        result.put("totalQty", order.getTotalQty());
        result.put("totalAmount", order.getTotalAmount());
        result.put("status", order.getStatus());
        result.put("createBy", order.getCreateBy());
        result.put("remark", order.getRemark());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String outboundNo = generateOutboundNo();

        OutboundOrder order = new OutboundOrder();
        order.setOutboundNo(outboundNo);
        order.setSoId(ParamUtils.getLong(params, "soId"));
        order.setMoId(ParamUtils.getLong(params, "moId"));
        order.setWarehouseId(ParamUtils.getLong(params, "warehouseId"));
        order.setOutboundType(ParamUtils.getInteger(params, "outboundType"));
        order.setOutboundDate(LocalDateTime.now());
        order.setStatus(10);
        order.setCreateTime(LocalDateTime.now());
        order.setRemark((String) params.get("remark"));

        order = outboundOrderRepository.save(order);

        // 保存明细
        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (Map<String, Object> item : items) {
                OutboundOrderDetail detail = new OutboundOrderDetail();
                detail.setOutboundId(order.getId());
                detail.setItemId(ParamUtils.getLong(item, "itemId"));
                detail.setSoDetailId(ParamUtils.getLong(item, "soDetailId"));
                detail.setQty(ParamUtils.getBigDecimal(item, "qty"));
                detail.setPrice(ParamUtils.getBigDecimal(item, "price"));
                detail.setBatchNo((String) item.get("batchNo"));

                BigDecimal qty = detail.getQty();
                BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
                detail.setAmount(qty.multiply(price));

                outboundOrderDetailRepository.save(detail);

                totalQty = totalQty.add(qty);
                totalAmount = totalAmount.add(detail.getAmount());
            }
        }

        order.setTotalQty(totalQty);
        order.setTotalAmount(totalAmount);
        outboundOrderRepository.save(order);

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("outboundNo", order.getOutboundNo());
        result.put("status", order.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> confirm(Long id) {
        OutboundOrder order = outboundOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("出库单不存在"));

        if (order.getStatus() != 10) {
            throw new AppException("只有草稿状态的出库单才能确认");
        }

        List<OutboundOrderDetail> details = outboundOrderDetailRepository.findByOutboundId(id);

        for (OutboundOrderDetail detail : details) {
            // 扣减库存（同时减少锁定数量）
            inventoryService.deductInventory(
                    detail.getItemId(),
                    order.getWarehouseId(),
                    detail.getQty(),
                    order.getId()
            );

            // 如果有关联销售订单明细，更新已发货数量
            if (detail.getSoDetailId() != null) {
                Optional<SalesOrderDetail> detailOpt = salesOrderDetailRepository.findById(detail.getSoDetailId());
                if (detailOpt.isPresent()) {
                    SalesOrderDetail soDetail = detailOpt.get();
                    soDetail.setShippedQty(soDetail.getShippedQty().add(detail.getQty()));
                    soDetail.setRemainQty(soDetail.getQty().subtract(soDetail.getShippedQty()));
                    salesOrderDetailRepository.save(soDetail);
                }
            }
        }

        order.setStatus(20); // 已确认
        outboundOrderRepository.save(order);

        // 更新销售订单状态
        if (order.getSoId() != null) {
            updateSalesOrderStatus(order.getSoId());
        }

        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    @Override
    public List<Map<String, Object>> getDetails(Long orderId) {
        List<OutboundOrderDetail> details = outboundOrderDetailRepository.findByOutboundId(orderId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (OutboundOrderDetail detail : details) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", detail.getId());
            map.put("outboundId", detail.getOutboundId());
            map.put("itemId", detail.getItemId());
            map.put("soDetailId", detail.getSoDetailId());
            map.put("qty", detail.getQty());
            map.put("price", detail.getPrice());
            map.put("amount", detail.getAmount());
            map.put("batchNo", detail.getBatchNo());
            result.add(map);
        }

        return result;
    }

    private void updateSalesOrderStatus(Long soId) {
        SalesOrder order = salesOrderRepository.findById(soId).orElse(null);
        if (order == null) return;

        List<SalesOrderDetail> details = salesOrderDetailRepository.findBySoId(soId);
        boolean allCompleted = true;
        boolean partialCompleted = false;

        for (SalesOrderDetail detail : details) {
            if (detail.getRemainQty() != null && detail.getRemainQty().compareTo(BigDecimal.ZERO) > 0) {
                allCompleted = false;
            }
            if (detail.getShippedQty() != null && detail.getShippedQty().compareTo(BigDecimal.ZERO) > 0) {
                partialCompleted = true;
            }
        }

        if (allCompleted) {
            order.setStatus(40); // 已完成
        } else if (partialCompleted) {
            order.setStatus(30); // 部分发货
        }

        salesOrderRepository.save(order);
    }

    private String generateOutboundNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = outboundOrderRepository.count() + 1;
        return "OUT" + date + String.format("%04d", count);
    }
}
