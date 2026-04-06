package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scm.common.enums.OrderStatusEnum;
import com.scm.common.result.ApiResponse;
import com.scm.common.util.StringUtils;
import com.scm.entity.*;
import com.scm.mapper.*;
import com.scm.service.ReportService;
import com.scm.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final SalesOrderMapper salesOrderMapper;
    private final InventoryMapper inventoryMapper;
    private final SupplierMapper supplierMapper;
    private final ItemMapper itemMapper;

    @Override
    public Map<String, Object> purchaseOrderCountReport(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, PurchaseOrder::getOrderDate, startDate)
                .le(endDate != null, PurchaseOrder::getOrderDate, endDate);

        List<PurchaseOrder> orders = purchaseOrderMapper.selectList(wrapper);

        long total = orders.size();
        long pending = orders.stream().filter(o -> OrderStatusEnum.PENDING.getCode().equals(o.getStatus())).count();
        long audited = orders.stream().filter(o -> OrderStatusEnum.AUDITED.getCode().equals(o.getStatus())).count();
        long completed = orders.stream().filter(o -> OrderStatusEnum.COMPLETED.getCode().equals(o.getStatus())).count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pending", pending);
        result.put("audited", audited);
        result.put("completed", completed);

        return result;
    }

    @Override
    public List<Map<String, Object>> purchaseAmountReport(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, PurchaseOrder::getOrderDate, startDate)
                .le(endDate != null, PurchaseOrder::getOrderDate, endDate)
                .in(PurchaseOrder::getStatus, Arrays.asList(
                        OrderStatusEnum.AUDITED.getCode(),
                        OrderStatusEnum.PARTIAL_RECEIVED.getCode(),
                        OrderStatusEnum.COMPLETED.getCode()
                ))
                .groupBy(PurchaseOrder::getSupplierId);

        List<PurchaseOrder> orders = purchaseOrderMapper.selectList(wrapper);

        // 按供应商分组统计
        Map<Long, List<PurchaseOrder>> supplierMap = orders.stream()
                .collect(Collectors.groupingBy(PurchaseOrder::getSupplierId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, List<PurchaseOrder>> entry : supplierMap.entrySet()) {
            Long supplierId = entry.getKey();
            List<PurchaseOrder> supplierOrders = entry.getValue();

            BigDecimal totalAmount = supplierOrders.stream()
                    .map(PurchaseOrder::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Supplier supplier = supplierMapper.selectById(supplierId);

            Map<String, Object> item = new HashMap<>();
            item.put("supplierId", supplierId);
            item.put("supplierName", supplier != null ? supplier.getSupplierName() : "");
            item.put("supplierCode", supplier != null ? supplier.getSupplierCode() : "");
            item.put("orderCount", supplierOrders.size());
            item.put("totalAmount", totalAmount);

            result.add(item);
        }

        // 按金额降序排序
        result.sort((a, b) -> ((BigDecimal) b.get("totalAmount")).compareTo((BigDecimal) a.get("totalAmount")));

        return result;
    }

    @Override
    public Map<String, Object> purchaseCompletionReport(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, PurchaseOrder::getOrderDate, startDate)
                .le(endDate != null, PurchaseOrder::getOrderDate, endDate);

        List<PurchaseOrder> orders = purchaseOrderMapper.selectList(wrapper);

        long total = orders.size();
        long completed = orders.stream().filter(o -> OrderStatusEnum.COMPLETED.getCode().equals(o.getStatus())).count();

        BigDecimal completionRate = total > 0 ?
                new BigDecimal(completed).divide(new BigDecimal(total), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100))
                : BigDecimal.ZERO;

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        result.put("uncompleted", total - completed);
        result.put("completionRate", completionRate);

        return result;
    }

    @Override
    public Map<String, Object> salesOrderCountReport(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, SalesOrder::getOrderDate, startDate)
                .le(endDate != null, SalesOrder::getOrderDate, endDate);

        List<SalesOrder> orders = salesOrderMapper.selectList(wrapper);

        long total = orders.size();
        long pending = orders.stream().filter(o -> OrderStatusEnum.PENDING.getCode().equals(o.getStatus())).count();
        long audited = orders.stream().filter(o -> OrderStatusEnum.AUDITED.getCode().equals(o.getStatus())).count();
        long completed = orders.stream().filter(o -> OrderStatusEnum.COMPLETED.getCode().equals(o.getStatus())).count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pending", pending);
        result.put("audited", audited);
        result.put("completed", completed);

        return result;
    }

    @Override
    public List<Map<String, Object>> salesAmountReport(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, SalesOrder::getOrderDate, startDate)
                .le(endDate != null, SalesOrder::getOrderDate, endDate)
                .in(SalesOrder::getStatus, Arrays.asList(
                        OrderStatusEnum.AUDITED.getCode(),
                        OrderStatusEnum.PARTIAL_SHIPPED.getCode(),
                        OrderStatusEnum.COMPLETED.getCode()
                ))
                .groupBy(SalesOrder::getCustomerName);

        List<SalesOrder> orders = salesOrderMapper.selectList(wrapper);

        // 按客户分组统计
        Map<String, List<SalesOrder>> customerMap = orders.stream()
                .collect(Collectors.groupingBy(SalesOrder::getCustomerName));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<SalesOrder>> entry : customerMap.entrySet()) {
            String customerName = entry.getKey();
            List<SalesOrder> customerOrders = entry.getValue();

            BigDecimal totalAmount = customerOrders.stream()
                    .map(SalesOrder::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> item = new HashMap<>();
            item.put("customerName", customerName);
            item.put("orderCount", customerOrders.size());
            item.put("totalAmount", totalAmount);

            result.add(item);
        }

        // 按金额降序排序
        result.sort((a, b) -> ((BigDecimal) b.get("totalAmount")).compareTo((BigDecimal) a.get("totalAmount")));

        return result;
    }

    @Override
    public Map<String, Object> salesShipmentReport(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(startDate != null, SalesOrder::getOrderDate, startDate)
                .le(endDate != null, SalesOrder::getOrderDate, endDate);

        List<SalesOrder> orders = salesOrderMapper.selectList(wrapper);

        long total = orders.size();
        long completed = orders.stream().filter(o -> OrderStatusEnum.COMPLETED.getCode().equals(o.getStatus())).count();

        BigDecimal shipmentRate = total > 0 ?
                new BigDecimal(completed).divide(new BigDecimal(total), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100))
                : BigDecimal.ZERO;

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        result.put("uncompleted", total - completed);
        result.put("shipmentRate", shipmentRate);

        return result;
    }

    @Override
    public Map<String, Object> inventoryCountReport(Long warehouseId, Long categoryId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(warehouseId != null, Inventory::getWarehouseId, warehouseId);

        List<Inventory> inventories = inventoryMapper.selectList(wrapper);

        // 如果指定了物料分类，过滤
        if (categoryId != null) {
            List<Long> itemIds = itemMapper.selectList(
                            Wrappers.<Item>lambdaQuery()
                                    .eq(Item::getCategoryId, categoryId)
                    ).stream()
                    .map(Item::getId)
                    .collect(Collectors.toList());

            if (!itemIds.isEmpty()) {
                inventories = inventories.stream()
                        .filter(inv -> itemIds.contains(inv.getItemId()))
                        .collect(Collectors.toList());
            }
        }

        BigDecimal totalQty = inventories.stream()
                .map(Inventory::getQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLockedQty = inventories.stream()
                .map(Inventory::getLockedQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAvailableQty = inventories.stream()
                .map(Inventory::getAvailableQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("totalSku", inventories.size());
        result.put("totalQty", totalQty);
        result.put("totalLockedQty", totalLockedQty);
        result.put("totalAvailableQty", totalAvailableQty);

        return result;
    }

    @Override
    public List<Map<String, Object>> inventoryWarnReport() {
        // 查询所有物料及其预警设置
        List<Item> items = itemMapper.selectList(
                Wrappers.<Item>lambdaQuery()
                        .eq(Item::getStatus, 1)
        );

        List<Map<String, Object>> result = new ArrayList<>();

        for (Item item : items) {
            // 查询该物料的库存
            List<Inventory> inventories = inventoryMapper.selectList(
                    Wrappers.<Inventory>lambdaQuery()
                            .eq(Inventory::getItemId, item.getId())
            );

            for (Inventory inv : inventories) {
                BigDecimal availableQty = inv.getAvailableQty();

                // 低库存预警
                if (availableQty.compareTo(item.getMinStock()) < 0) {
                    Map<String, Object> warnItem = new HashMap<>();
                    warnItem.put("warnType", "low");
                    warnItem.put("itemId", item.getId());
                    warnItem.put("itemName", item.getItemName());
                    warnItem.put("itemCode", item.getItemCode());
                    warnItem.put("warehouseId", inv.getWarehouseId());
                    warnItem.put("availableQty", availableQty);
                    warnItem.put("minStock", item.getMinStock());
                    result.add(warnItem);
                }

                // 超库存预警
                if (item.getMaxStock().compareTo(BigDecimal.ZERO) > 0 &&
                        inv.getQty().compareTo(item.getMaxStock()) > 0) {
                    Map<String, Object> warnItem = new HashMap<>();
                    warnItem.put("warnType", "high");
                    warnItem.put("itemId", item.getId());
                    warnItem.put("itemName", item.getItemName());
                    warnItem.put("itemCode", item.getItemCode());
                    warnItem.put("warehouseId", inv.getWarehouseId());
                    warnItem.put("qty", inv.getQty());
                    warnItem.put("maxStock", item.getMaxStock());
                    result.add(warnItem);
                }
            }
        }

        return result;
    }
}
