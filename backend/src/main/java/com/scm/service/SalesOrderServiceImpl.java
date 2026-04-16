package com.scm.service;

import com.scm.common.AppException;
import com.scm.common.Constants;
import com.scm.entity.SalesOrder;
import com.scm.entity.SalesOrderDetail;
import com.scm.repository.SalesOrderDetailRepository;
import com.scm.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 销售订单服务实现类
 */
@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderDetailRepository salesOrderDetailRepository;
    private final InventoryService inventoryService;

    public SalesOrderServiceImpl(SalesOrderRepository salesOrderRepository,
                                   SalesOrderDetailRepository salesOrderDetailRepository,
                                   InventoryService inventoryService) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderDetailRepository = salesOrderDetailRepository;
        this.inventoryService = inventoryService;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, String customerName, Integer status) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<SalesOrder> orders = salesOrderRepository.findAll();

        if (customerName != null && !customerName.isEmpty()) {
            orders = orders.stream()
                    .filter(o -> o.getCustomerName().contains(customerName))
                    .toList();
        }
        if (status != null) {
            orders = orders.stream()
                    .filter(o -> o.getStatus().equals(status))
                    .toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, orders.size());
        List<SalesOrder> pageList = start < orders.size() ? orders.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", orders.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("销售订单不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("soNo", order.getSoNo());
        result.put("customerName", order.getCustomerName());
        result.put("customerPhone", order.getCustomerPhone());
        result.put("warehouseId", order.getWarehouseId());
        result.put("orderDate", order.getOrderDate());
        result.put("totalAmount", order.getTotalAmount());
        result.put("status", order.getStatus());
        result.put("createBy", order.getCreateBy());
        result.put("auditBy", order.getAuditBy());
        result.put("auditTime", order.getAuditTime());
        result.put("remark", order.getRemark());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Map<String, Object> params) {
        String soNo = generateSoNo();

        SalesOrder order = new SalesOrder();
        order.setSoNo(soNo);
        order.setCustomerName((String) params.get("customerName"));
        order.setCustomerPhone((String) params.get("customerPhone"));
        order.setWarehouseId((Long) params.get("warehouseId"));
        order.setOrderDate(LocalDate.now());
        order.setStatus(10);
        order.setCreateTime(LocalDateTime.now());
        order.setRemark((String) params.get("remark"));

        order = salesOrderRepository.save(order);

        // 保存明细
        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (Map<String, Object> item : items) {
                SalesOrderDetail detail = new SalesOrderDetail();
                detail.setSoId(order.getId());
                detail.setItemId((Long) item.get("itemId"));
                detail.setQty((BigDecimal) item.get("qty"));
                detail.setPrice((BigDecimal) item.get("price"));
                detail.setTaxRate((BigDecimal) item.get("taxRate"));

                BigDecimal taxRate = detail.getTaxRate() != null ? detail.getTaxRate() : BigDecimal.ZERO;
                BigDecimal qty = detail.getQty();
                BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;

                BigDecimal taxAmount = qty.multiply(price).multiply(taxRate).divide(BigDecimal.valueOf(100));
                BigDecimal amount = qty.multiply(price).add(taxAmount);

                detail.setTaxAmount(taxAmount);
                detail.setAmount(amount);
                detail.setShippedQty(BigDecimal.ZERO);
                detail.setRemainQty(qty);

                salesOrderDetailRepository.save(detail);

                totalAmount = totalAmount.add(amount);
            }
        }

        order.setTotalAmount(totalAmount);
        salesOrderRepository.save(order);

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("soNo", order.getSoNo());
        result.put("status", order.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("销售订单不存在"));

        if (order.getStatus() != 10) {
            throw new AppException("只有草稿状态的订单才能修改");
        }

        if (params.containsKey("customerName")) {
            order.setCustomerName((String) params.get("customerName"));
        }
        if (params.containsKey("customerPhone")) {
            order.setCustomerPhone((String) params.get("customerPhone"));
        }
        if (params.containsKey("warehouseId")) {
            order.setWarehouseId((Long) params.get("warehouseId"));
        }
        if (params.containsKey("remark")) {
            order.setRemark((String) params.get("remark"));
        }

        salesOrderRepository.save(order);

        return Map.of("id", order.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("销售订单不存在"));

        if (order.getStatus() != 10) {
            throw new AppException("只有草稿状态的订单才能删除");
        }

        salesOrderDetailRepository.deleteBySoId(id);
        salesOrderRepository.deleteById(id);

        return Map.of("id", id);
    }

    @Override
    @Transactional
    public Map<String, Object> audit(Long id, Map<String, Object> params) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("销售订单不存在"));

        if (order.getStatus() != 10) {
            throw new AppException("只有待审核状态的订单才能审核");
        }

        Long auditBy = (Long) params.get("auditBy");
        Integer auditStatus = (Integer) params.get("status");

        order.setAuditBy(auditBy);
        order.setAuditTime(LocalDateTime.now());

        if (auditStatus != null && auditStatus == 20) {
            // 审核通过，锁定库存
            List<SalesOrderDetail> details = salesOrderDetailRepository.findBySoId(id);
            for (SalesOrderDetail detail : details) {
                try {
                    inventoryService.lockInventory(
                            detail.getItemId(),
                            order.getWarehouseId(),
                            detail.getQty(),
                            order.getId()
                    );
                } catch (AppException e) {
                    // 库存不足，审核失败
                    order.setStatus(50);
                    order.setRemark("库存不足，审核失败");
                    salesOrderRepository.save(order);
                    throw new AppException("库存不足：" + e.getMessage());
                }
            }
            order.setStatus(20); // 已审核
        } else {
            order.setStatus(50); // 驳回
        }

        salesOrderRepository.save(order);

        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    @Override
    public List<Map<String, Object>> getDetails(Long orderId) {
        List<SalesOrderDetail> details = salesOrderDetailRepository.findBySoId(orderId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (SalesOrderDetail detail : details) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", detail.getId());
            map.put("soId", detail.getSoId());
            map.put("itemId", detail.getItemId());
            map.put("qty", detail.getQty());
            map.put("price", detail.getPrice());
            map.put("taxRate", detail.getTaxRate());
            map.put("taxAmount", detail.getTaxAmount());
            map.put("amount", detail.getAmount());
            map.put("shippedQty", detail.getShippedQty());
            map.put("remainQty", detail.getRemainQty());
            result.add(map);
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> deliver(Long id, Map<String, Object> params) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("销售订单不存在"));

        if (order.getStatus() != 20) {
            throw new AppException("只有已审核的订单才能发货");
        }

        // 获取发货明细
        List<Map<String, Object>> details = (List<Map<String, Object>>) params.get("details");
        if (details == null || details.isEmpty()) {
            throw new AppException("请选择发货物料");
        }

        Long warehouseId = order.getWarehouseId();
        if (warehouseId == null) {
            throw new AppException("订单未指定仓库");
        }

        for (Map<String, Object> detail : details) {
            Long itemId = (Long) detail.get("itemId");
            BigDecimal qty = new BigDecimal(detail.get("qty").toString());

            // 获取订单明细
            List<SalesOrderDetail> orderDetails = salesOrderDetailRepository.findBySoId(id);
            SalesOrderDetail orderDetail = orderDetails.stream()
                    .filter(d -> d.getItemId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new AppException("订单中不存在该物料"));

            BigDecimal remainQty = orderDetail.getRemainQty();
            if (qty.compareTo(remainQty) > 0) {
                throw new AppException("发货数量不能超过剩余数量: " + remainQty);
            }

            // 更新已发货数量
            BigDecimal newShippedQty = orderDetail.getShippedQty().add(qty);
            orderDetail.setShippedQty(newShippedQty);
            orderDetail.setRemainQty(remainQty.subtract(qty));
            salesOrderDetailRepository.save(orderDetail);

            // 扣减库存（已锁定库存转为实际扣减）
            inventoryService.deductInventory(itemId, warehouseId, qty, id);
        }

        // 检查是否全部发货
        List<SalesOrderDetail> allDetails = salesOrderDetailRepository.findBySoId(id);
        boolean allShipped = allDetails.stream().allMatch(d -> d.getRemainQty().compareTo(BigDecimal.ZERO) == 0);
        if (allShipped) {
            order.setStatus(40); // 已完成
        } else {
            order.setStatus(30); // 部分发货
        }
        salesOrderRepository.save(order);

        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    private String generateSoNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = salesOrderRepository.count() + 1;
        return "SO" + date + String.format("%04d", count);
    }
}
