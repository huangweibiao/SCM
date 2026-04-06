package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.constant.BusinessTypeConstant;
import com.scm.common.enums.InboundTypeEnum;
import com.scm.common.exception.BusinessException;
import com.scm.common.result.PageResult;
import com.scm.common.util.DateUtils;
import com.scm.common.util.StringUtils;
import com.scm.dto.InboundOrderDTO;
import com.scm.entity.*;
import com.scm.mapper.*;
import com.scm.service.InboundOrderService;
import com.scm.service.InventoryService;
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
 * 入库单Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InboundOrderServiceImpl extends ServiceImpl<InboundOrderMapper, InboundOrder> implements InboundOrderService {

    private static final AtomicLong SEQ = new AtomicLong(1);

    private final InboundOrderDetailMapper inboundOrderDetailMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    private final InventoryService inventoryService;
    private final ItemMapper itemMapper;

    @Override
    public PageResult<InboundOrder> pageQuery(Long poId, Long warehouseId, Integer inboundType,
                                               Integer status, String startDate, String endDate,
                                               Integer pageNum, Integer pageSize) {
        Page<InboundOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(poId != null, InboundOrder::getPoId, poId)
                .eq(warehouseId != null, InboundOrder::getWarehouseId, warehouseId)
                .eq(inboundType != null, InboundOrder::getInboundType, inboundType)
                .eq(status != null, InboundOrder::getStatus, status)
                .ge(StringUtils.isNotEmpty(startDate), InboundOrder::getInboundDate,
                    DateUtils.parseDateTime(startDate))
                .le(StringUtils.isNotEmpty(endDate), InboundOrder::getInboundDate,
                    DateUtils.parseDateTime(endDate))
                .orderByDesc(InboundOrder::getCreateTime);
        Page<InboundOrder> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInboundOrder(InboundOrderDTO inboundOrderDTO) {
        // 如果关联采购订单，验证采购订单
        if (inboundOrderDTO.getPoId() != null) {
            PurchaseOrder po = purchaseOrderMapper.selectById(inboundOrderDTO.getPoId());
            if (po == null) {
                throw new BusinessException("采购订单不存在");
            }
        }

        // 验证物料
        if (inboundOrderDTO.getItems() == null || inboundOrderDTO.getItems().isEmpty()) {
            throw new BusinessException("入库明细不能为空");
        }

        // 创建入库单主表
        InboundOrder inboundOrder = new InboundOrder();
        inboundOrder.setInboundNo(generateInboundNo());
        inboundOrder.setPoId(inboundOrderDTO.getPoId());
        inboundOrder.setMoId(inboundOrderDTO.getMoId());
        inboundOrder.setWarehouseId(inboundOrderDTO.getWarehouseId());
        inboundOrder.setInboundType(inboundOrderDTO.getInboundType());
        inboundOrder.setInboundDate(inboundOrderDTO.getInboundDate());
        inboundOrder.setRemark(inboundOrderDTO.getRemark());
        inboundOrder.setStatus(10); // 草稿状态
        inboundOrder.setTotalQty(BigDecimal.ZERO);
        inboundOrder.setTotalAmount(BigDecimal.ZERO);
        inboundOrder.setTenantId(0L);

        this.save(inboundOrder);
        log.info("创建入库单主表成功，入库单号: {}, id: {}", inboundOrder.getInboundNo(), inboundOrder.getId());

        // 创建入库单明细
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<InboundOrderDetail> details = new ArrayList<>();

        for (InboundOrderDTO.InboundOrderDetailDTO itemDTO : inboundOrderDTO.getItems()) {
            InboundOrderDetail detail = new InboundOrderDetail();
            detail.setInboundId(inboundOrder.getId());
            detail.setItemId(itemDTO.getItemId());
            detail.setPoDetailId(itemDTO.getPoDetailId());
            detail.setQty(itemDTO.getQty());
            detail.setPrice(itemDTO.getPrice() != null ? itemDTO.getPrice() : BigDecimal.ZERO);
            detail.setBatchNo(itemDTO.getBatchNo());
            detail.setExpireDate(itemDTO.getExpireDate());

            // 计算入库金额
            BigDecimal amount = detail.getQty().multiply(detail.getPrice())
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            detail.setAmount(amount);

            details.add(detail);
            totalQty = totalQty.add(detail.getQty());
            totalAmount = totalAmount.add(amount);
        }

        // 批量插入明细
        for (InboundOrderDetail detail : details) {
            inboundOrderDetailMapper.insert(detail);
        }

        // 更新入库单总数量和总金额
        inboundOrder.setTotalQty(totalQty);
        inboundOrder.setTotalAmount(totalAmount);
        this.updateById(inboundOrder);

        log.info("创建入库单成功，入库单号: {}, 总数量: {}, 总金额: {}",
                inboundOrder.getInboundNo(), totalQty, totalAmount);

        return inboundOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmInboundOrder(Long id) {
        InboundOrder inboundOrder = this.getById(id);
        if (inboundOrder == null) {
            throw new BusinessException("入库单不存在");
        }

        // 只能确认草稿状态的入库单
        if (inboundOrder.getStatus() != 10) {
            throw new BusinessException("只能确认草稿状态的入库单");
        }

        // 查询入库单明细
        List<InboundOrderDetail> details = inboundOrderDetailMapper.selectList(
                new LambdaQueryWrapper<InboundOrderDetail>()
                        .eq(InboundOrderDetail::getInboundId, id)
        );

        // 更新库存
        for (InboundOrderDetail detail : details) {
            // 入库业务类型：10入库
            inventoryService.inbound(
                    detail.getItemId(),
                    inboundOrder.getWarehouseId(),
                    detail.getQty(),
                    detail.getBatchNo(),
                    inboundOrder.getId(),
                    BusinessTypeConstant.InventoryLogType.INBOUND,
                    1L, // 操作人，实际应该从登录用户获取
                    "入库单" + inboundOrder.getInboundNo() + "入库"
            );

            // 如果关联采购订单，更新采购订单明细的已收货数量
            if (detail.getPoDetailId() != null) {
                PurchaseOrderDetail poDetail = purchaseOrderDetailMapper.selectById(detail.getPoDetailId());
                if (poDetail != null) {
                    // 更新已收货数量
                    BigDecimal newReceivedQty = poDetail.getReceivedQty().add(detail.getQty());
                    // 不能超过采购数量
                    if (newReceivedQty.compareTo(poDetail.getQty()) > 0) {
                        throw new BusinessException("收货数量不能超过采购数量");
                    }
                    poDetail.setReceivedQty(newReceivedQty);
                    poDetail.setRemainQty(poDetail.getQty().subtract(newReceivedQty));
                    purchaseOrderDetailMapper.updateById(poDetail);
                }
            }
        }

        // 更新入库单状态为已确认
        inboundOrder.setStatus(20);
        boolean result = this.updateById(inboundOrder);

        log.info("确认入库单成功，入库单号: {}", inboundOrder.getInboundNo());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long purchaseReceipt(Long poId, InboundOrderDTO inboundOrderDTO) {
        // 验证采购订单
        PurchaseOrder po = purchaseOrderMapper.selectById(poId);
        if (po == null) {
            throw new BusinessException("采购订单不存在");
        }

        // 查询采购订单明细
        List<PurchaseOrderDetail> poDetails = purchaseOrderDetailMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderDetail>()
                        .eq(PurchaseOrderDetail::getPoId, poId)
        );

        // 设置采购入库类型
        inboundOrderDTO.setPoId(poId);
        inboundOrderDTO.setWarehouseId(po.getWarehouseId());
        inboundOrderDTO.setInboundType(InboundTypeEnum.PURCHASE.getCode());
        inboundOrderDTO.setInboundDate(LocalDateTime.now());

        // 验证收货数量
        for (InboundOrderDTO.InboundOrderDetailDTO itemDTO : inboundOrderDTO.getItems()) {
            // 查找对应的采购订单明细
            PurchaseOrderDetail poDetail = poDetails.stream()
                    .filter(d -> d.getItemId().equals(itemDTO.getItemId()))
                    .findFirst()
                    .orElse(null);

            if (poDetail == null) {
                throw new BusinessException("物料不在采购订单中");
            }

            // 检查收货数量是否超过剩余未收货数量
            BigDecimal remainQty = poDetail.getQty().subtract(poDetail.getReceivedQty());
            if (itemDTO.getQty().compareTo(remainQty) > 0) {
                throw new BusinessException("收货数量不能超过剩余未收货数量: " + remainQty);
            }
        }

        // 创建入库单
        Long inboundId = createInboundOrder(inboundOrderDTO);

        // 立即确认入库单
        confirmInboundOrder(inboundId);

        // 检查采购订单是否全部收货，如果全部收货则更新订单状态
        boolean allReceived = poDetails.stream()
                .allMatch(d -> d.getReceivedQty().compareTo(d.getQty()) >= 0);

        if (allReceived) {
            po.setStatus(40); // 已完成
            purchaseOrderMapper.updateById(po);
            log.info("采购订单全部收货，状态更新为已完成，订单号: {}", po.getPoNo());
        } else {
            // 部分收货
            boolean hasReceived = poDetails.stream()
                    .anyMatch(d -> d.getReceivedQty().compareTo(BigDecimal.ZERO) > 0);
            if (hasReceived) {
                po.setStatus(30); // 部分收货
                purchaseOrderMapper.updateById(po);
                log.info("采购订单部分收货，状态更新为部分收货，订单号: {}", po.getPoNo());
            }
        }

        return inboundId;
    }

    /**
     * 生成入库单号
     * 格式: IN + yyyyMMdd + 6位序号
     */
    private String generateInboundNo() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = SEQ.getAndIncrement();
        return "IN" + date + String.format("%06d", seq);
    }
}
