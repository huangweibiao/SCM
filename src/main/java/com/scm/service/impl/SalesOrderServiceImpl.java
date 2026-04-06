package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.enums.OutboundTypeEnum;
import com.scm.common.enums.OrderStatusEnum;
import com.scm.common.exception.BusinessException;
import com.scm.common.result.PageResult;
import com.scm.common.util.DateUtils;
import com.scm.common.util.StringUtils;
import com.scm.dto.SalesOrderDTO;
import com.scm.entity.SalesOrder;
import com.scm.entity.SalesOrderDetail;
import com.scm.mapper.SalesOrderDetailMapper;
import com.scm.mapper.SalesOrderMapper;
import com.scm.mapper.WarehouseMapper;
import com.scm.service.InventoryService;
import com.scm.service.SalesOrderService;
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
 * 销售订单Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl extends ServiceImpl<SalesOrderMapper, SalesOrder> implements SalesOrderService {

    private static final AtomicLong SEQ = new AtomicLong(1);

    private final SalesOrderDetailMapper salesOrderDetailMapper;
    private final WarehouseMapper warehouseMapper;
    private final ItemMapper itemMapper;
    private final InventoryService inventoryService;

    @Override
    public PageResult<SalesOrder> pageQuery(String customerName, Integer status, String soNo,
                                               String startDate, String endDate,
                                               Integer pageNum, Integer pageSize) {
        Page<SalesOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(customerName), SalesOrder::getCustomerName, customerName)
                .eq(status != null, SalesOrder::getStatus, status)
                .like(StringUtils.isNotEmpty(soNo), SalesOrder::getSoNo, soNo)
                .ge(StringUtils.isNotEmpty(startDate), SalesOrder::getOrderDate,
                    DateUtils.parseDate(startDate))
                .le(StringUtils.isNotEmpty(endDate), SalesOrder::getOrderDate,
                    DateUtils.parseDate(endDate))
                .orderByDesc(SalesOrder::getCreateTime);
        Page<SalesOrder> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(SalesOrderDTO orderDTO) {
        // 验证仓库
        Warehouse warehouse = warehouseMapper.selectById(orderDTO.getWarehouseId());
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }

        // 验证物料
        if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            throw new BusinessException("订单明细不能为空");
        }

        List<Long> itemIds = orderDTO.getItems().stream()
                .map(SalesOrderDTO.SalesOrderDetailDTO::getItemId)
                .collect(Collectors.toList());

        List<Item> items = itemMapper.selectBatchIds(itemIds);
        if (items.size() != itemIds.size()) {
            throw new BusinessException("存在不存在的物料");
        }

        // 验证物料状态
        for (Item item : items) {
            if (item.getStatus() == 0) {
                throw new BusinessException("物料[" + item.getItemName() + "]已停用，无法销售");
            }
        }

        // 创建销售订单主表
        SalesOrder order = new SalesOrder();
        order.setSoNo(generateSoNo());
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerPhone(orderDTO.getCustomerPhone());
        order.setWarehouseId(orderDTO.getWarehouseId());
        order.setOrderDate(orderDTO.getOrderDate() != null ? orderDTO.getOrderDate() : LocalDate.now());
        order.setRemark(orderDTO.getRemark());
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setTenantId(0L);

        this.save(order);
        log.info("创建销售订单主表成功，订单号: {}, id: {}", order.getSoNo(), order.getId());

        // 创建销售订单明细
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SalesOrderDetail> details = new ArrayList<>();

        for (SalesOrderDTO.SalesOrderDetailDTO itemDTO : orderDTO.getItems()) {
            SalesOrderDetail detail = new SalesOrderDetail();
            detail.setSoId(order.getId());
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

            // 初始发货数量为0
            detail.setShippedQty(BigDecimal.ZERO);
            detail.setRemainQty(detail.getQty());

            details.add(detail);
            totalAmount = totalAmount.add(amount);
        }

        // 批量插入明细
        for (SalesOrderDetail detail : details) {
            salesOrderDetailMapper.insert(detail);
        }

        // 更新订单总金额
        order.setTotalAmount(totalAmount);
        this.updateById(order);

        log.info("创建销售订单成功，订单号: {}, 总金额: {}, 明细数量: {}",
                order.getSoNo(), totalAmount, details.size());

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrder(SalesOrderDTO orderDTO) {
        SalesOrder exist = this.getById(orderDTO.getId());
        if (exist == null) {
            throw new BusinessException("销售订单不存在");
        }

        // 只能更新待审核状态的订单
        if (!OrderStatusEnum.PENDING.getCode().equals(exist.getStatus())) {
            throw new BusinessException("只能修改待审核状态的订单");
        }

        // 更新订单主表
        exist.setCustomerName(orderDTO.getCustomerName());
        exist.setCustomerPhone(orderDTO.getCustomerPhone());
        exist.setWarehouseId(orderDTO.getWarehouseId());
        exist.setOrderDate(orderDTO.getOrderDate());
        exist.setRemark(orderDTO.getRemark());

        // 删除旧明细
        salesOrderDetailMapper.delete(
                new LambdaQueryWrapper<SalesOrderDetail>()
                        .eq(SalesOrderDetail::getSoId, exist.getId())
        );

        // 创建新明细
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SalesOrderDetail> details = new ArrayList<>();

        for (SalesOrderDTO.SalesOrderDetailDTO itemDTO : orderDTO.getItems()) {
            SalesOrderDetail detail = new SalesOrderDetail();
            detail.setSoId(exist.getId());
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

            detail.setShippedQty(BigDecimal.ZERO);
            detail.setRemainQty(detail.getQty());

            details.add(detail);
            totalAmount = totalAmount.add(amount);
        }

        // 批量插入明细
        for (SalesOrderDetail detail : details) {
            salesOrderDetailMapper.insert(detail);
        }

        // 更新订单总金额
        exist.setTotalAmount(totalAmount);
        boolean result = this.updateById(exist);

        log.info("更新销售订单成功，订单号: {}, 总金额: {}", exist.getSoNo(), totalAmount);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrder(Long id) {
        SalesOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("销售订单不存在");
        }

        // 只能删除待审核状态的订单
        if (!OrderStatusEnum.PENDING.getCode().equals(order.getStatus())) {
            throw new BusinessException("只能删除待审核状态的订单");
        }

        // 删除明细
        salesOrderDetailMapper.delete(
                new LambdaQueryWrapper<SalesOrderDetail>()
                        .eq(SalesOrderDetail::getSoId, id)
        );

        // 删除主表
        boolean result = this.removeById(id);
        log.info("删除销售订单成功，订单号: {}", order.getSoNo());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditOrder(Long id, Long auditBy) {
        SalesOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("销售订单不存在");
        }

        // 只能审核待审核状态的订单
        if (!OrderStatusEnum.PENDING.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许审核");
        }

        // 查询订单明细
        List<SalesOrderDetail> details = salesOrderDetailMapper.selectList(
                new LambdaQueryWrapper<SalesOrderDetail>()
                        .eq(SalesOrderDetail::getSoId, id)
        );

        // 校验并锁定库存
        for (SalesOrderDetail detail : details) {
            inventoryService.lockInventory(
                    detail.getItemId(),
                    order.getWarehouseId(),
                    detail.getQty()
            );
        }

        // 更新订单状态
        order.setStatus(OrderStatusEnum.AUDITED.getCode());
        order.setAuditBy(auditBy);
        order.setAuditTime(java.time.LocalDateTime.now());

        boolean result = this.updateById(order);
        log.info("审核销售订单成功，订单号: {}, 审核人id: {}", order.getSoNo(), auditBy);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean closeOrder(Long id) {
        SalesOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("销售订单不存在");
        }

        // 只有非完成、非关闭状态的订单才能关闭
        if (OrderStatusEnum.COMPLETED.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单已完成，无法关闭");
        }

        order.setStatus(OrderStatusEnum.CLOSED.getCode());
        boolean result = this.updateById(order);
        log.info("关闭销售订单成功，订单号: {}", order.getSoNo());
        return result;
    }

    @Override
    public SalesOrder getDetailWithItems(Long id) {
        SalesOrder order = this.getById(id);
        if (order != null) {
            List<SalesOrderDetail> details = salesOrderDetailMapper.selectList(
                    new LambdaQueryWrapper<SalesOrderDetail>()
                            .eq(SalesOrderDetail::getSoId, id)
                            .orderByAsc(SalesOrderDetail::getId)
            );
        }
        return order;
    }

    @Override
    public List<SalesOrderDetail> getDetailList(Long soId) {
        return salesOrderDetailMapper.selectList(
                new LambdaQueryWrapper<SalesOrderDetail>()
                        .eq(SalesOrderDetail::getSoId, soId)
                        .orderByAsc(SalesOrderDetail::getId)
        );
    }

    /**
     * 生成销售订单号
     * 格式: SO + yyyyMMdd + 6位序号
     */
    private String generateSoNo() {
        String date = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = SEQ.getAndIncrement();
        return "SO" + date + String.format("%06d", seq);
    }
}
