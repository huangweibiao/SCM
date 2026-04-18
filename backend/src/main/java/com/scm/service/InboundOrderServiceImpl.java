package com.scm.service;

import com.scm.common.AppException;
import com.scm.common.Constants;
import com.scm.entity.InboundOrder;
import com.scm.entity.InboundOrderDetail;
import com.scm.util.ParamUtils;
import com.scm.entity.Inventory;
import com.scm.entity.InventoryLog;
import com.scm.entity.PurchaseOrder;
import com.scm.entity.PurchaseOrderDetail;
import com.scm.repository.InboundOrderDetailRepository;
import com.scm.repository.InboundOrderRepository;
import com.scm.repository.InventoryLogRepository;
import com.scm.repository.InventoryRepository;
import com.scm.repository.PurchaseOrderDetailRepository;
import com.scm.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 入库单服务实现类
 */
@Service
public class InboundOrderServiceImpl implements InboundOrderService {

    private final InboundOrderRepository inboundOrderRepository;
    private final InboundOrderDetailRepository inboundOrderDetailRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    public InboundOrderServiceImpl(InboundOrderRepository inboundOrderRepository,
                                   InboundOrderDetailRepository inboundOrderDetailRepository,
                                   InventoryRepository inventoryRepository,
                                   InventoryLogRepository inventoryLogRepository,
                                   PurchaseOrderRepository purchaseOrderRepository,
                                   PurchaseOrderDetailRepository purchaseOrderDetailRepository) {
        this.inboundOrderRepository = inboundOrderRepository;
        this.inboundOrderDetailRepository = inboundOrderDetailRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderDetailRepository = purchaseOrderDetailRepository;
    }

    @Override
    public Map<String, Object> list(Integer pageNum, Integer pageSize, Long warehouseId, Integer inboundType, Integer status) {
        int page = pageNum != null ? pageNum : 1;
        int size = pageSize != null ? pageSize : 10;

        List<InboundOrder> orders = inboundOrderRepository.findAll();

        if (warehouseId != null) {
            orders = orders.stream().filter(o -> o.getWarehouseId() != null && o.getWarehouseId().equals(warehouseId)).toList();
        }
        if (inboundType != null) {
            orders = orders.stream().filter(o -> o.getInboundType() != null && o.getInboundType().equals(inboundType)).toList();
        }
        if (status != null) {
            orders = orders.stream().filter(o -> o.getStatus() != null && o.getStatus().equals(status)).toList();
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, orders.size());
        List<InboundOrder> pageList = start < orders.size() ? orders.subList(start, end) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("total", orders.size());
        result.put("list", pageList);

        return result;
    }

    @Override
    public Map<String, Object> getById(Long id) {
        InboundOrder order = inboundOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("入库单不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("inboundNo", order.getInboundNo());
        result.put("poId", order.getPoId());
        result.put("moId", order.getMoId());
        result.put("warehouseId", order.getWarehouseId());
        result.put("inboundType", order.getInboundType());
        result.put("inboundDate", order.getInboundDate());
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
        String inboundNo = generateInboundNo();

        InboundOrder order = new InboundOrder();
        order.setInboundNo(inboundNo);
        order.setPoId(ParamUtils.getLong(params, "poId"));
        order.setMoId(ParamUtils.getLong(params, "moId"));
        order.setWarehouseId(ParamUtils.getLong(params, "warehouseId"));
        order.setInboundType(ParamUtils.getInteger(params, "inboundType"));
        order.setInboundDate(LocalDateTime.now());
        order.setStatus(10);
        order.setCreateTime(LocalDateTime.now());
        order.setRemark((String) params.get("remark"));

        order = inboundOrderRepository.save(order);

        // 保存明细
        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (Map<String, Object> item : items) {
                InboundOrderDetail detail = new InboundOrderDetail();
                detail.setInboundId(order.getId());
                detail.setItemId(ParamUtils.getLong(item, "itemId"));
                detail.setPoDetailId(ParamUtils.getLong(item, "poDetailId"));
                detail.setQty(ParamUtils.getBigDecimal(item, "qty"));
                detail.setPrice(ParamUtils.getBigDecimal(item, "price"));
                detail.setBatchNo((String) item.get("batchNo"));
                detail.setExpireDate((java.time.LocalDate) item.get("expireDate"));

                BigDecimal qty = detail.getQty();
                BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
                detail.setAmount(qty.multiply(price));

                inboundOrderDetailRepository.save(detail);

                totalQty = totalQty.add(qty);
                totalAmount = totalAmount.add(detail.getAmount());
            }
        }

        order.setTotalQty(totalQty);
        order.setTotalAmount(totalAmount);
        inboundOrderRepository.save(order);

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("inboundNo", order.getInboundNo());
        result.put("status", order.getStatus());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> confirm(Long id) {
        InboundOrder order = inboundOrderRepository.findById(id)
                .orElseThrow(() -> new AppException("入库单不存在"));

        if (order.getStatus() != 10) {
            throw new AppException("只有草稿状态的入库单才能确认");
        }

        List<InboundOrderDetail> details = inboundOrderDetailRepository.findByInboundId(id);

        for (InboundOrderDetail detail : details) {
            // 获取或创建库存记录
            String batchNo = detail.getBatchNo() != null ? detail.getBatchNo() : "";
            Optional<Inventory> inventoryOpt = inventoryRepository
                    .findByItemIdAndWarehouseIdAndBatchNo(detail.getItemId(), order.getWarehouseId(), batchNo);

            Inventory inventory;
            if (inventoryOpt.isPresent()) {
                inventory = inventoryOpt.get();
                inventory.setQty(inventory.getQty().add(detail.getQty()));
            } else {
                inventory = new Inventory();
                inventory.setItemId(detail.getItemId());
                inventory.setWarehouseId(order.getWarehouseId());
                inventory.setQty(detail.getQty());
                inventory.setLockedQty(BigDecimal.ZERO);
                inventory.setBatchNo(batchNo);
            }

            // 计算可用库存
            BigDecimal availableQty = inventory.getQty().subtract(inventory.getLockedQty());
            inventory.setAvailableQty(availableQty);
            inventory.setUpdateTime(LocalDateTime.now());

            inventoryRepository.save(inventory);

            // 记录库存流水
            InventoryLog log = new InventoryLog();
            log.setItemId(detail.getItemId());
            log.setWarehouseId(order.getWarehouseId());
            log.setBeforeQty(inventory.getQty().subtract(detail.getQty()));
            log.setChangeQty(detail.getQty());
            log.setAfterQty(inventory.getQty());
            log.setBusinessType(Constants.InventoryBusinessType.INBOUND);
            log.setBusinessId(order.getId());
            log.setOperateBy(order.getCreateBy());
            log.setCreateTime(LocalDateTime.now());
            log.setRemark("入库单入库");

            inventoryLogRepository.save(log);

            // 如果有关联采购订单，更新采购订单明细的已收货数量
            if (detail.getPoDetailId() != null) {
                Optional<PurchaseOrderDetail> detailOpt = purchaseOrderDetailRepository.findById(detail.getPoDetailId());
                if (detailOpt.isPresent()) {
                    PurchaseOrderDetail poDetail = detailOpt.get();
                    poDetail.setReceivedQty(poDetail.getReceivedQty().add(detail.getQty()));
                    poDetail.setRemainQty(poDetail.getQty().subtract(poDetail.getReceivedQty()));
                    purchaseOrderDetailRepository.save(poDetail);
                }
            }
        }

        order.setStatus(20); // 已确认
        inboundOrderRepository.save(order);

        // 更新采购订单状态
        if (order.getPoId() != null) {
            updatePurchaseOrderStatus(order.getPoId());
        }

        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    @Override
    public List<Map<String, Object>> getDetails(Long orderId) {
        List<InboundOrderDetail> details = inboundOrderDetailRepository.findByInboundId(orderId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (InboundOrderDetail detail : details) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", detail.getId());
            map.put("inboundId", detail.getInboundId());
            map.put("itemId", detail.getItemId());
            map.put("poDetailId", detail.getPoDetailId());
            map.put("qty", detail.getQty());
            map.put("price", detail.getPrice());
            map.put("amount", detail.getAmount());
            map.put("batchNo", detail.getBatchNo());
            map.put("expireDate", detail.getExpireDate());
            result.add(map);
        }

        return result;
    }

    private void updatePurchaseOrderStatus(Long poId) {
        PurchaseOrder order = purchaseOrderRepository.findById(poId).orElse(null);
        if (order == null) return;

        List<PurchaseOrderDetail> details = purchaseOrderDetailRepository.findByPoId(poId);
        boolean allCompleted = true;
        boolean partialCompleted = false;

        for (PurchaseOrderDetail detail : details) {
            if (detail.getRemainQty() != null && detail.getRemainQty().compareTo(BigDecimal.ZERO) > 0) {
                allCompleted = false;
            }
            if (detail.getReceivedQty() != null && detail.getReceivedQty().compareTo(BigDecimal.ZERO) > 0) {
                partialCompleted = true;
            }
        }

        if (allCompleted) {
            order.setStatus(40); // 已完成
        } else if (partialCompleted) {
            order.setStatus(30); // 部分收货
        }

        purchaseOrderRepository.save(order);
    }

    private String generateInboundNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = inboundOrderRepository.count() + 1;
        return "IN" + date + String.format("%04d", count);
    }
}
