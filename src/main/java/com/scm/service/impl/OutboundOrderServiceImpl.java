package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.constant.BusinessTypeConstant;
import com.scm.common.enums.OutboundTypeEnum;
import com.scm.common.exception.BusinessException;
import com.scm.common.result.PageResult;
import com.scm.common.util.DateUtils;
import com.scm.common.util.StringUtils;
import com.scm.dto.OutboundOrderDTO;
import com.scm.entity.*;
import com.scm.mapper.*;
import com.scm.service.InventoryService;
import com.scm.service.OutboundOrderService;
import com.scm.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 出库单Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundOrderServiceImpl extends ServiceImpl<OutboundOrderMapper, OutboundOrder> implements OutboundOrderService {

    private static final AtomicLong SEQ = new AtomicLong(1);

    private final OutboundOrderDetailMapper outboundOrderDetailMapper;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderDetailMapper salesOrderDetailMapper;
    private final InventoryService inventoryService;
    private final ItemMapper itemMapper;

    @Override
    public PageResult<OutboundOrder> pageQuery(Long soId, Long warehouseId, Integer outboundType,
                                                 Integer status, String startDate, String endDate,
                                                 Integer pageNum, Integer pageSize) {
        Page<OutboundOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(soId != null, OutboundOrder::getSoId, soId)
                .eq(warehouseId != null, OutboundOrder::getWarehouseId, warehouseId)
                .eq(outboundType != null, OutboundOrder::getOutboundType, outboundType)
                .eq(status != null, OutboundOrder::getStatus, status)
                .ge(StringUtils.isNotEmpty(startDate), OutboundOrder::getOutboundDate,
                    DateUtils.parseDateTime(startDate))
                .le(StringUtils.isNotEmpty(endDate), OutboundOrder::getOutboundDate,
                    DateUtils.parseDateTime(endDate))
                .orderByDesc(OutboundOrder::getCreateTime);
        Page<OutboundOrder> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOutboundOrder(OutboundOrderDTO outboundOrderDTO) {
        // 如果关联销售订单，验证销售订单
        if (outboundOrderDTO.getSoId() != null) {
            SalesOrder so = salesOrderMapper.selectById(outboundOrderDTO.getSoId());
            if (so == null) {
                throw new BusinessException("销售订单不存在");
            }
        }

        // 验证物料
        if (outboundOrderDTO.getItems() == null || outboundOrderDTO.getItems().isEmpty()) {
            throw new BusinessException("出库明细不能为空");
        }

        // 创建出库单主表
        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setOutboundNo(generateOutboundNo());
        outboundOrder.setSoId(outboundOrderDTO.getSoId());
        outboundOrder.setMoId(outboundOrderDTO.getMoId());
        outboundOrder.setWarehouseId(outboundOrderDTO.getWarehouseId());
        outboundOrder.setOutboundType(outboundOrderDTO.getOutboundType());
        outboundOrder.setOutboundDate(outboundOrderDTO.getOutboundDate());
        outboundOrder.setRemark(outboundOrderDTO.getRemark());
        outboundOrder.setStatus(10); // 草稿状态
        outboundOrder.setTotalQty(BigDecimal.ZERO);
        outboundOrder.setTotalAmount(BigDecimal.ZERO);
        outboundOrder.setTenantId(0L);

        this.save(outboundOrder);
        log.info("创建出库单主表成功，出库单号: {}, id: {}", outboundOrder.getOutboundNo(), outboundOrder.getId());

        // 创建出库单明细
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OutboundOrderDetail> details = new ArrayList<>();

        for (OutboundOrderDTO.OutboundOrderDetailDTO itemDTO : outboundOrderDTO.getItems()) {
            OutboundOrderDetail detail = new OutboundOrderDetail();
            detail.setOutboundId(outboundOrder.getId());
            detail.setItemId(itemDTO.getItemId());
            detail.setSoDetailId(itemDTO.getSoDetailId());
            detail.setQty(itemDTO.getQty());
            detail.setPrice(itemDTO.getPrice() != null ? itemDTO.getPrice() : BigDecimal.ZERO);
            detail.setBatchNo(itemDTO.getBatchNo());

            // 计算出库金额
            BigDecimal amount = detail.getQty().multiply(detail.getPrice())
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            detail.setAmount(amount);

            details.add(detail);
            totalQty = totalQty.add(detail.getQty());
            totalAmount = totalAmount.add(amount);
        }

        // 批量插入明细
        for (OutboundOrderDetail detail : details) {
            outboundOrderDetailMapper.insert(detail);
        }

        // 更新出库单总数量和总金额
        outboundOrder.setTotalQty(totalQty);
        outboundOrder.setTotalAmount(totalAmount);
        this.updateById(outboundOrder);

        log.info("创建出库单成功，出库单号: {}, 总数量: {}, 总金额: {}",
                outboundOrder.getOutboundNo(), totalQty, totalAmount);

        return outboundOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmOutboundOrder(Long id) {
        OutboundOrder outboundOrder = this.getById(id);
        if (outboundOrder == null) {
            throw new BusinessException("出库单不存在");
        }

        // 只能确认草稿状态的出库单
        if (outboundOrder.getStatus() != 10) {
            throw new BusinessException("只能确认草稿状态的出库单");
        }

        // 查询出库单明细
        List<OutboundOrderDetail> details = outboundOrderDetailMapper.selectList(
                new LambdaQueryWrapper<OutboundOrderDetail>()
                        .eq(OutboundOrderDetail::getOutboundId, id)
        );

        // 出库业务类型：20出库
        Integer businessType = BusinessTypeConstant.InventoryLogType.OUTBOUND;

        // 更新库存
        for (OutboundOrderDetail detail : details) {
            // 出库扣减库存
            inventoryService.outbound(
                    detail.getItemId(),
                    outboundOrder.getWarehouseId(),
                    detail.getQty(),
                    detail.getBatchNo(),
                    outboundOrder.getId(),
                    businessType,
                    1L, // 操作人，实际应该从登录用户获取
                    "出库单" + outboundOrder.getOutboundNo() + "出库"
            );

            // 释放锁定库存（销售出库时）
            if (OutboundTypeEnum.SALES.getCode().equals(outboundOrder.getOutboundType())) {
                inventoryService.unlockInventory(
                        detail.getItemId(),
                        outboundOrder.getWarehouseId(),
                        detail.getQty()
                );
            }

            // 如果关联销售订单，更新销售订单明细的已发货数量
            if (detail.getSoDetailId() != null) {
                SalesOrderDetail soDetail = salesOrderDetailMapper.selectById(detail.getSoDetailId());
                if (soDetail != null) {
                    // 更新已发货数量
                    BigDecimal newShippedQty = soDetail.getShippedQty().add(detail.getQty());
                    // 不能超过销售数量
                    if (newShippedQty.compareTo(soDetail.getQty()) > 0) {
                        throw new BusinessException("发货数量不能超过销售数量");
                    }
                    soDetail.setShippedQty(newShippedQty);
                    soDetail.setRemainQty(soDetail.getQty().subtract(newShippedQty));
                    salesOrderDetailMapper.updateById(soDetail);
                }
            }
        }

        // 更新出库单状态为已确认
        outboundOrder.setStatus(20);
        boolean result = this.updateById(outboundOrder);

        log.info("确认出库单成功，出库单号: {}", outboundOrder.getOutboundNo());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long salesShipment(Long soId, OutboundOrderDTO outboundOrderDTO) {
        // 验证销售订单
        SalesOrder so = salesOrderMapper.selectById(soId);
        if (so == null) {
            throw new BusinessException("销售订单不存在");
        }

        // 查询销售订单明细
        List<SalesOrderDetail> soDetails = salesOrderDetailMapper.selectList(
                new LambdaQueryWrapper<SalesOrderDetail>()
                        .eq(SalesOrderDetail::getSoId, soId)
        );

        // 设置销售出库类型
        outboundOrderDTO.setSoId(soId);
        outboundOrderDTO.setWarehouseId(so.getWarehouseId());
        outboundOrderDTO.setOutboundType(OutboundTypeEnum.SALES.getCode());
        outboundOrderDTO.setOutboundDate(LocalDateTime.now());

        // 验证发货数量
        for (OutboundOrderDTO.OutboundOrderDetailDTO itemDTO : outboundOrderDTO.getItems()) {
            // 查找对应的销售订单明细
            SalesOrderDetail soDetail = soDetails.stream()
                    .filter(d -> d.getItemId().equals(itemDTO.getItemId()))
                    .findFirst()
                    .orElse(null);

            if (soDetail == null) {
                throw new BusinessException("物料不在销售订单中");
            }

            // 检查发货数量是否超过剩余未发货数量
            BigDecimal remainQty = soDetail.getQty().subtract(soDetail.getShippedQty());
            if (itemDTO.getQty().compareTo(remainQty) > 0) {
                throw new BusinessException("发货数量不能超过剩余未发货数量: " + remainQty);
            }
        }

        // 创建出库单
        Long outboundId = createOutboundOrder(outboundOrderDTO);

        // 立即确认出库单
        confirmOutboundOrder(outboundId);

        // 检查销售订单是否全部发货，如果全部发货则更新订单状态
        boolean allShipped = soDetails.stream()
                .allMatch(d -> d.getShippedQty().compareTo(d.getQty()) >= 0);

        if (allShipped) {
            so.setStatus(40); // 已完成
            salesOrderMapper.updateById(so);
            log.info("销售订单全部发货，状态更新为已完成，订单号: {}", so.getSoNo());
        } else {
            // 部分发货
            boolean hasShipped = soDetails.stream()
                    .anyMatch(d -> d.getShippedQty().compareTo(BigDecimal.ZERO) > 0);
            if (hasShipped) {
                so.setStatus(30); // 部分发货
                salesOrderMapper.updateById(so);
                log.info("销售订单部分发货，状态更新为部分发货，订单号: {}", so.getSoNo());
            }
        }

        return outboundId;
    }

    /**
     * 生成出库单号
     * 格式: OUT + yyyyMMdd + 6位序号
     */
    private String generateOutboundNo() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = SEQ.getAndIncrement();
        return "OUT" + date + String.format("%06d", seq);
    }
}
