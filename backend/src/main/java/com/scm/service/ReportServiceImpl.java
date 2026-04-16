package com.scm.service;

import com.scm.entity.*;
import com.scm.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表服务实现类
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ItemRepository itemRepository;

    public ReportServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                           SalesOrderRepository salesOrderRepository,
                           InventoryRepository inventoryRepository,
                           SupplierRepository supplierRepository,
                           WarehouseRepository warehouseRepository,
                           ItemRepository itemRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.inventoryRepository = inventoryRepository;
        this.supplierRepository = supplierRepository;
        this.warehouseRepository = warehouseRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 采购订单统计
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
        data.put("purchaseTotal", purchaseOrders.size());
        data.put("purchasePending", purchaseOrders.stream().filter(p -> p.getStatus() == 10).count());
        data.put("purchaseCompleted", purchaseOrders.stream().filter(p -> p.getStatus() == 40).count());

        // 销售订单统计
        List<SalesOrder> salesOrders = salesOrderRepository.findAll();
        data.put("salesTotal", salesOrders.size());
        data.put("salesPending", salesOrders.stream().filter(s -> s.getStatus() == 10).count());
        data.put("salesCompleted", salesOrders.stream().filter(s -> s.getStatus() == 40).count());

        // 库存统计
        List<Inventory> inventories = inventoryRepository.findAll();
        data.put("inventoryTotal", inventories.size());
        BigDecimal totalValue = inventories.stream()
                .map(i -> i.getQty())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalQty", totalValue);

        // 仓库数量
        List<Warehouse> warehouses = warehouseRepository.findAll();
        data.put("warehouseCount", warehouses.size());

        // 供应商数量
        List<Supplier> suppliers = supplierRepository.findAll();
        data.put("supplierCount", suppliers.size());

        return data;
    }

    @Override
    public Map<String, Object> getPurchaseReport(String startDate, String endDate) {
        Map<String, Object> report = new HashMap<>();

        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        // 按状态统计
        Map<Integer, Long> statusCount = orders.stream()
                .collect(Collectors.groupingBy(PurchaseOrder::getStatus, Collectors.counting()));
        report.put("statusCount", statusCount);

        // 总金额
        BigDecimal totalAmount = orders.stream()
                .filter(o -> o.getTotalAmount() != null)
                .map(PurchaseOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.put("totalAmount", totalAmount);

        // 订单数量
        report.put("totalCount", orders.size());

        return report;
    }

    @Override
    public Map<String, Object> getSalesReport(String startDate, String endDate) {
        Map<String, Object> report = new HashMap<>();

        List<SalesOrder> orders = salesOrderRepository.findAll();

        // 按状态统计
        Map<Integer, Long> statusCount = orders.stream()
                .collect(Collectors.groupingBy(SalesOrder::getStatus, Collectors.counting()));
        report.put("statusCount", statusCount);

        // 总金额
        BigDecimal totalAmount = orders.stream()
                .filter(o -> o.getTotalAmount() != null)
                .map(SalesOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.put("totalAmount", totalAmount);

        // 订单数量
        report.put("totalCount", orders.size());

        return report;
    }

    @Override
    public Map<String, Object> getInventoryReport() {
        Map<String, Object> report = new HashMap<>();

        List<Inventory> inventories = inventoryRepository.findAll();

        // 总库存量
        BigDecimal totalQty = inventories.stream()
                .map(Inventory::getQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.put("totalQty", totalQty);

        // 库存记录数
        report.put("totalRecords", inventories.size());

        // 按仓库统计
        Map<Long, BigDecimal> byWarehouse = inventories.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getWarehouseId,
                        Collectors.reducing(BigDecimal.ZERO, Inventory::getQty, BigDecimal::add)
                ));
        report.put("byWarehouse", byWarehouse);

        // 库存明细
        List<Map<String, Object>> details = new ArrayList<>();
        for (Inventory inv : inventories) {
            Optional<Item> itemOpt = itemRepository.findById(inv.getItemId());
            Map<String, Object> item = new HashMap<>();
            item.put("itemId", inv.getItemId());
            item.put("warehouseId", inv.getWarehouseId());
            item.put("qty", inv.getQty());
            item.put("availableQty", inv.getAvailableQty());
            item.put("lockedQty", inv.getLockedQty());
            itemOpt.ifPresent(i -> {
                item.put("itemName", i.getItemName());
                item.put("itemCode", i.getItemCode());
            });
            details.add(item);
        }
        report.put("details", details);

        return report;
    }

    @Override
    public Map<String, Object> getSupplierReport() {
        Map<String, Object> report = new HashMap<>();

        List<Supplier> suppliers = supplierRepository.findAll();

        // 供应商总数
        report.put("totalCount", suppliers.size());

        // 按状态统计
        Map<Integer, Long> statusCount = suppliers.stream()
                .collect(Collectors.groupingBy(Supplier::getStatus, Collectors.counting()));
        report.put("statusCount", statusCount);

        return report;
    }
}
