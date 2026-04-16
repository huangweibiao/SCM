package com.scm.service;

import com.scm.common.AppException;
import com.scm.common.Constants;
import com.scm.entity.PurchaseOrder;
import com.scm.entity.PurchaseOrderDetail;
import com.scm.repository.PurchaseOrderDetailRepository;
import com.scm.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 采购订单服务实现类
 */
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private final InventoryService inventoryService;
    private final InboundOrderService inboundOrderService;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                    PurchaseOrderDetailRepository purchaseOrderDetailRepository,
                                    InventoryService inventoryService,
                                    InboundOrderService inboundOrderService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderDetailRepository = purchaseOrderDetailRepository;
        this.inventoryService = inventoryService;
        this.inboundOrderService = inboundOrderService;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, Long supplierId, Integer status) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        if (supplierId != null) {
            orders = orders.stream().filter(o -> o.getSupplierId() != null && o.getSupplierId().equals(supplierId)).toList();
        }
        if (status != null) {
            orders = orders.stream().filter(o -> o.getStatus().equals(status)).toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, orders.size());
        List<PurchaseOrder> pageList = start < orders.size() ? orders.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", orders.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("采购订单不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("poNo", order.getPoNo());
        result.put("supplierId", order.getSupplierId());
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
        String poNo = generatePoNo();

        PurchaseOrder order = new PurchaseOrder();
        order.setPoNo(poNo);
        order.setSupplierId((Long) params.get("supplierId"));
        order.setWarehouseId((Long) params.get("warehouseId"));
        order.setOrderDate(LocalDate.now());
        order.setStatus(10);
        order.setCreateTime(LocalDateTime.now());
        order.setRemark((String) params.get("remark"));

        order = purchaseOrderRepository.save(order);

        // 保存明细
        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (Map<String, Object> item : items) {
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setPoId(order.getId());
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
                detail.setReceivedQty(BigDecimal.ZERO);
                detail.setRemainQty(qty);

                purchaseOrderDetailRepository.save(detail);

                totalAmount = totalAmount.add(amount);
            }
        }

        order.setTotalAmount(totalAmount);
        purchaseOrderRepository.save(order);

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("poNo", order.getPoNo());
        result.put("status", order.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(Long id, Map<String, Object> params) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("采购订单不存在"));

        if (order.getStatus() != 10) {
            throw new AppException("只有草稿状态的订单才能修改");
        }

        if (params.containsKey("supplierId")) {
            order.setSupplierId((Long) params.get("supplierId"));
        }
        if (params.containsKey("warehouseId")) {
            order.setWarehouseId((Long) params.get("warehouseId"));
        }
        if (params.containsKey("remark")) {
            order.setRemark((String) params.get("remark"));
        }

        purchaseOrderRepository.save(order);

        return Map.of("id", order.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> delete(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("采购订单不存在"));

        if (order.getStatus() != 10) {
            throw new AppException("只有草稿状态的订单才能删除");
        }

        purchaseOrderDetailRepository.deleteByPoId(id);
        purchaseOrderRepository.deleteById(id);

        return Map.of("id", id);
    }

    @Override
    @Transactional
    public Map<String, Object> audit(Long id, Map<String, Object> params) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("采购订单不存在"));

        if (order.getStatus() != 10) {
            throw new AppException("只有待审核状态的订单才能审核");
        }

        Integer auditStatus = (Integer) params.get("status");
        order.setAuditBy((Long) params.get("auditBy"));
        order.setAuditTime(LocalDateTime.now());

        if (auditStatus != null && auditStatus == 20) {
            order.setStatus(20); // 已审核
        } else {
            order.setStatus(50); // 驳回/关闭
        }

        purchaseOrderRepository.save(order);

        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    @Override
    public List<Map<String, Object>> getDetails(Long orderId) {
        List<PurchaseOrderDetail> details = purchaseOrderDetailRepository.findByPoId(orderId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (PurchaseOrderDetail detail : details) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", detail.getId());
            map.put("poId", detail.getPoId());
            map.put("itemId", detail.getItemId());
            map.put("qty", detail.getQty());
            map.put("price", detail.getPrice());
            map.put("taxRate", detail.getTaxRate());
            map.put("taxAmount", detail.getTaxAmount());
            map.put("amount", detail.getAmount());
            map.put("receivedQty", detail.getReceivedQty());
            map.put("remainQty", detail.getRemainQty());
            result.add(map);
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> receive(Long id, Map<String, Object> params) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("采购订单不存在"));

        if (order.getStatus() != 20) {
            throw new AppException("只有已审核的订单才能收货");
        }

        // 获取收货明细
        List<Map<String, Object>> details = (List<Map<String, Object>>) params.get("details");
        if (details == null || details.isEmpty()) {
            throw new AppException("请选择收货物料");
        }

        Long warehouseId = (Long) params.get("warehouseId");
        if (warehouseId == null) {
            throw new AppException("请选择仓库");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map<String, Object> detail : details) {
            Long itemId = (Long) detail.get("itemId");
            BigDecimal qty = new BigDecimal(detail.get("qty").toString());

            // 获取订单明细
            List<PurchaseOrderDetail> orderDetails = purchaseOrderDetailRepository.findByPoId(id);
            PurchaseOrderDetail orderDetail = orderDetails.stream()
                    .filter(d -> d.getItemId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new AppException("订单中不存在该物料"));

            BigDecimal remainQty = orderDetail.getRemainQty();
            if (qty.compareTo(remainQty) > 0) {
                throw new AppException("收货数量不能超过剩余数量: " + remainQty);
            }

            // 更新已收货数量
            BigDecimal newReceivedQty = orderDetail.getReceivedQty().add(qty);
            orderDetail.setReceivedQty(newReceivedQty);
            orderDetail.setRemainQty(remainQty.subtract(qty));
            purchaseOrderDetailRepository.save(orderDetail);

            // 增加库存
            inventoryService.addInventory(itemId, warehouseId, qty, null);

            totalAmount = totalAmount.add(orderDetail.getPrice().multiply(qty));
        }

        // 检查是否全部收货
        List<PurchaseOrderDetail> allDetails = purchaseOrderDetailRepository.findByPoId(id);
        boolean allReceived = allDetails.stream().allMatch(d -> d.getRemainQty().compareTo(BigDecimal.ZERO) == 0);
        if (allReceived) {
            order.setStatus(40); // 已完成
        } else {
            order.setStatus(30); // 部分收货
        }
        purchaseOrderRepository.save(order);

        return Map.of("id", order.getId(), "status", order.getStatus(), "amount", totalAmount);
    }

    @Override
    @Transactional
    public Map<String, Object> close(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("采购订单不存在"));

        if (order.getStatus() == 40) {
            throw new AppException("已完成订单不能关闭");
        }
        if (order.getStatus() == 50) {
            throw new AppException("订单已关闭");
        }

        order.setStatus(50); // 已关闭
        purchaseOrderRepository.save(order);

        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    private String generatePoNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = purchaseOrderRepository.count() + 1;
        return "PO" + date + String.format("%04d", count);
    }
}
