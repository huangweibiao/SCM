package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.enums.OrderStatusEnum;
import com.scm.common.exception.BusinessException;
import com.scm.common.result.PageResult;
import com.scm.common.util.DateUtils;
import com.scm.common.util.StringUtils;
import com.scm.dto.PurchaseOrderDTO;
import com.scm.entity.*;
import com.scm.mapper.*;
import com.scm.service.InventoryService;
import com.scm.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 采购订单Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    private static final AtomicLong SEQ = new AtomicLong(1);

    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    private final SupplierMapper supplierMapper;
    private final ItemMapper itemMapper;
    private final InventoryService inventoryService;

    @Override
    public PageResult<PurchaseOrder> pageQuery(Long supplierId, Integer status, String poNo,
                                               String startDate, String endDate,
                                               Integer pageNum, Integer pageSize) {
        Page<PurchaseOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(supplierId != null, PurchaseOrder::getSupplierId, supplierId)
                .eq(status != null, PurchaseOrder::getStatus, status)
                .like(StringUtils.isNotEmpty(poNo), PurchaseOrder::getPoNo, poNo)
                .ge(StringUtils.isNotEmpty(startDate), PurchaseOrder::getOrderDate,
                    DateUtils.parseDate(startDate))
                .le(StringUtils.isNotEmpty(endDate), PurchaseOrder::getOrderDate,
                    DateUtils.parseDate(endDate))
                .orderByDesc(PurchaseOrder::getCreateTime);
        Page<PurchaseOrder> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(PurchaseOrderDTO orderDTO) {
        // 验证供应商
        Supplier supplier = supplierMapper.selectById(orderDTO.getSupplierId());
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }
        if (supplier.getStatus() == 0) {
            throw new BusinessException("供应商已停用，无法创建采购订单");
        }

        // 验证仓库
        Warehouse warehouse = new Warehouse();
        warehouse.setId(orderDTO.getWarehouseId());
        // 简化处理，实际需要查询

        // 验证物料
        if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            throw new BusinessException("订单明细不能为空");
        }

        List<Long> itemIds = orderDTO.getItems().stream()
                .map(PurchaseOrderDTO.PurchaseOrderDetailDTO::getItemId)
                .collect(Collectors.toList());

        List<Item> items = itemMapper.selectBatchIds(itemIds);
        if (items.size() != itemIds.size()) {
            throw new BusinessException("存在不存在的物料");
        }

        // 验证物料状态
        for (Item item : items) {
            if (item.getStatus() == 0) {
                throw new BusinessException("物料[" + item.getItemName() + "]已停用，无法采购");
            }
        }

        // 创建采购订单主表
        PurchaseOrder order = new PurchaseOrder();
        order.setPoNo(generatePoNo());
        order.setSupplierId(orderDTO.getSupplierId());
        order.setWarehouseId(orderDTO.getWarehouseId());
        order.setOrderDate(orderDTO.getOrderDate());
        order.setRemark(orderDTO.getRemark());
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setTenantId(0L);

        this.save(order);
        log.info("创建采购订单主表成功，订单号: {}, id: {}", order.getPoNo(), order.getId());

        // 创建采购订单明细
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderDetail> details = new ArrayList<>();

        for (PurchaseOrderDTO.PurchaseOrderDetailDTO itemDTO : orderDTO.getItems()) {
            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setPoId(order.getId());
            detail.setItemId(itemDTO.getItemId());
            detail.setQty(itemDTO.getQty());
            detail.setPrice(itemDTO.getPrice() != null ? itemDTO.getPrice() : BigDecimal.ZERO);
            detail.setTaxRate(itemDTO.getTaxRate() != null ? itemDTO.getTaxRate() : BigDecimal.ZERO);

            // 计算税额
            BigDecimal taxAmount = detail.getQty()
                    .multiply(detail.getPrice())
                    .multiply(detail.getTaxRate())
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            detail.setTaxAmount(taxAmount);

            // 计算含税金额
            BigDecimal amount = detail.getQty()
                    .multiply(detail.getPrice())
                    .multiply(BigDecimal.ONE.add(detail.getTaxRate().divide(new BigDecimal("100"))))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            detail.setAmount(amount);

            // 初始收货数量为0
            detail.setReceivedQty(BigDecimal.ZERO);
            detail.setRemainQty(detail.getQty());

            details.add(detail);
            totalAmount = totalAmount.add(amount);
        }

        // 批量插入明细
        for (PurchaseOrderDetail detail : details) {
            purchaseOrderDetailMapper.insert(detail);
        }

        // 更新订单总金额
        order.setTotalAmount(totalAmount);
        this.updateById(order);

        log.info("创建采购订单成功，订单号: {}, 总金额: {}, 明细数量: {}",
                order.getPoNo(), totalAmount, details.size());

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrder(PurchaseOrderDTO orderDTO) {
        PurchaseOrder exist = this.getById(orderDTO.getId());
        if (exist == null) {
            throw new BusinessException("采购订单不存在");
        }

        // 只能更新待审核状态的订单
        if (!OrderStatusEnum.PENDING.getCode().equals(exist.getStatus())) {
            throw new BusinessException("只能修改待审核状态的订单");
        }

        // 更新订单主表
        exist.setSupplierId(orderDTO.getSupplierId());
        exist.setWarehouseId(orderDTO.getWarehouseId());
        exist.setOrderDate(orderDTO.getOrderDate());
        exist.setRemark(orderDTO.getRemark());

        // 删除旧明细
        purchaseOrderDetailMapper.delete(
                new LambdaQueryWrapper<PurchaseOrderDetail>()
                        .eq(PurchaseOrderDetail::getPoId, exist.getId())
        );

        // 创建新明细
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderDetail> details = new ArrayList<>();

        for (PurchaseOrderDTO.PurchaseOrderDetailDTO itemDTO : orderDTO.getItems()) {
            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setPoId(exist.getId());
            detail.setItemId(itemDTO.getItemId());
            detail.setQty(itemDTO.getQty());
            detail.setPrice(itemDTO.getPrice() != null ? itemDTO.getPrice() : BigDecimal.ZERO);
            detail.setTaxRate(itemDTO.getTaxRate() != null ? itemDTO.getTaxRate() : BigDecimal.ZERO);

            BigDecimal taxAmount = detail.getQty()
                    .multiply(detail.getPrice())
                    .multiply(detail.getTaxRate())
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            detail.setTaxAmount(taxAmount);

            BigDecimal amount = detail.getQty()
                    .multiply(detail.getPrice())
                    .multiply(BigDecimal.ONE.add(detail.getTaxRate().divide(new BigDecimal("100"))))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            detail.setAmount(amount);

            detail.setReceivedQty(BigDecimal.ZERO);
            detail.setRemainQty(detail.getQty());

            details.add(detail);
            totalAmount = totalAmount.add(amount);
        }

        // 批量插入明细
        for (PurchaseOrderDetail detail : details) {
            purchaseOrderDetailMapper.insert(detail);
        }

        // 更新订单总金额
        exist.setTotalAmount(totalAmount);
        boolean result = this.updateById(exist);

        log.info("更新采购订单成功，订单号: {}, 总金额: {}", exist.getPoNo(), totalAmount);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrder(Long id) {
        PurchaseOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }

        // 只能删除待审核状态的订单
        if (!OrderStatusEnum.PENDING.getCode().equals(order.getStatus())) {
            throw new BusinessException("只能删除待审核状态的订单");
        }

        // 删除明细
        purchaseOrderDetailMapper.delete(
                new LambdaQueryWrapper<PurchaseOrderDetail>()
                        .eq(PurchaseOrderDetail::getPoId, id)
        );

        // 删除主表
        boolean result = this.removeById(id);
        log.info("删除采购订单成功，订单号: {}", order.getPoNo());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditOrder(Long id, Long auditBy) {
        PurchaseOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }

        // 只能审核待审核状态的订单
        if (!OrderStatusEnum.PENDING.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许审核");
        }

        // 更新订单状态
        order.setStatus(OrderStatusEnum.AUDITED.getCode());
        order.setAuditBy(auditBy);
        order.setAuditTime(java.time.LocalDateTime.now());

        boolean result = this.updateById(order);
        log.info("审核采购订单成功，订单号: {}, 审核人id: {}", order.getPoNo(), auditBy);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean closeOrder(Long id) {
        PurchaseOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }

        // 只有非完成、非关闭状态的订单才能关闭
        if (OrderStatusEnum.COMPLETED.getCode().equals(order.getStatus()) ||
                OrderStatusEnum.CLOSED.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许关闭");
        }

        order.setStatus(OrderStatusEnum.CLOSED.getCode());
        boolean result = this.updateById(order);
        log.info("关闭采购订单成功，订单号: {}", order.getPoNo());
        return result;
    }

    @Override
    public PurchaseOrder getDetailWithItems(Long id) {
        PurchaseOrder order = this.getById(id);
        if (order != null) {
            List<PurchaseOrderDetail> details = purchaseOrderDetailMapper.selectList(
                    new LambdaQueryWrapper<PurchaseOrderDetail>()
                            .eq(PurchaseOrderDetail::getPoId, id)
                            .orderByAsc(PurchaseOrderDetail::getId)
            );
            // 可以设置到order中，但需要扩展order对象
        }
        return order;
    }

    @Override
    public List<PurchaseOrderDetail> getDetailList(Long poId) {
        return purchaseOrderDetailMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderDetail>()
                        .eq(PurchaseOrderDetail::getPoId, poId)
                        .orderByAsc(PurchaseOrderDetail::getId)
        );
    }

    /**
     * 生成采购订单号
     * 格式: PO + yyyyMMdd + 6位序号
     */
    private String generatePoNo() {
        String date = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = SEQ.getAndIncrement();
        return "PO" + date + String.format("%06d", seq);
    }
}
