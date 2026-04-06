package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.enums.InboundTypeEnum;
import com.scm.common.enums.OutboundTypeEnum;
import com.scm.common.enums.ProductionStatusEnum;
import com.scm.common.exception.BusinessException;
import com.scm.common.result.PageResult;
import com.scm.common.util.DateUtils;
import com.scm.common.util.StringUtils;
import com.scm.dto.InboundOrderDTO;
import com.scm.dto.OutboundOrderDTO;
import com.scm.dto.ProductionOrderDTO;
import com.scm.entity.InboundOrder;
import com.scm.entity.Item;
import com.scm.entity.OutboundOrder;
import com.scm.entity.ProductionOrder;
import com.scm.mapper.InboundOrderMapper;
import com.scm.mapper.ItemMapper;
import com.scm.mapper.OutboundOrderMapper;
import com.scm.mapper.ProductionOrderMapper;
import com.scm.service.InboundOrderService;
import com.scm.service.OutboundOrderService;
import com.scm.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 生产工单Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionOrderServiceImpl extends ServiceImpl<ProductionOrderMapper, ProductionOrder> implements ProductionOrderService {

    private static final AtomicLong SEQ = new AtomicLong(1);

    private final ItemMapper itemMapper;
    private final InboundOrderService inboundOrderService;
    private final OutboundOrderService outboundOrderService;

    @Override
    public PageResult<ProductionOrder> pageQuery(Long itemId, Integer status,
                                                   String startDate, String endDate,
                                                   Integer pageNum, Integer pageSize) {
        Page<ProductionOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(itemId != null, ProductionOrder::getItemId, itemId)
                .eq(status != null, ProductionOrder::getStatus, status)
                .ge(StringUtils.isNotEmpty(startDate), ProductionOrder::getStartDate,
                    DateUtils.parseDate(startDate))
                .le(StringUtils.isNotEmpty(endDate), ProductionOrder::getEndDate,
                    DateUtils.parseDate(endDate))
                .orderByDesc(ProductionOrder::getCreateTime);
        Page<ProductionOrder> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(ProductionOrderDTO orderDTO) {
        // 验证物料
        Item item = itemMapper.selectById(orderDTO.getItemId());
        if (item == null) {
            throw new BusinessException("物料不存在");
        }

        // 创建生产工单
        ProductionOrder order = new ProductionOrder();
        order.setMoNo(generateMoNo());
        order.setItemId(orderDTO.getItemId());
        order.setQty(orderDTO.getQty());
        order.setStartDate(orderDTO.getStartDate());
        order.setEndDate(orderDTO.getEndDate());
        order.setRemark(orderDTO.getRemark());
        order.setStatus(ProductionStatusEnum.PLANNED.getCode());
        order.setFinishedQty(BigDecimal.ZERO);
        order.setTenantId(0L);

        this.save(order);
        log.info("创建生产工单成功，工单号: {}, id: {}", order.getMoNo(), order.getId());

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrder(ProductionOrderDTO orderDTO) {
        ProductionOrder exist = this.getById(orderDTO.getId());
        if (exist == null) {
            throw new BusinessException("生产工单不存在");
        }

        // 只能更新计划状态的工单
        if (!ProductionStatusEnum.PLANNED.getCode().equals(exist.getStatus())) {
            throw new BusinessException("只能修改计划状态的生产工单");
        }

        exist.setItemId(orderDTO.getItemId());
        exist.setQty(orderDTO.getQty());
        exist.setStartDate(orderDTO.getStartDate());
        exist.setEndDate(orderDTO.getEndDate());
        exist.setRemark(orderDTO.getRemark());

        boolean result = this.updateById(exist);
        log.info("更新生产工单成功，工单号: {}", exist.getMoNo());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startProduction(Long id) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产工单不存在");
        }

        // 只能开始计划状态的工单
        if (!ProductionStatusEnum.PLANNED.getCode().equals(order.getStatus())) {
            throw new BusinessException("只能开始计划状态的生产工单");
        }

        order.setStatus(ProductionStatusEnum.IN_PROGRESS.getCode());
        order.setActualStart(LocalDateTime.now());

        boolean result = this.updateById(order);
        log.info("开始生产工单成功，工单号: {}", order.getMoNo());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeProduction(Long id, BigDecimal finishedQty, Long warehouseId) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产工单不存在");
        }

        // 只能完工生产中的工单
        if (!ProductionStatusEnum.IN_PROGRESS.getCode().equals(order.getStatus())) {
            throw new BusinessException("只能完工生产中的工单");
        }

        // 检查完工数量
        BigDecimal newFinishedQty = order.getFinishedQty().add(finishedQty);
        if (newFinishedQty.compareTo(order.getQty()) > 0) {
            throw new BusinessException("完工数量不能超过计划生产数量");
        }

        // 创建生产入库单
        InboundOrderDTO inboundOrderDTO = new InboundOrderDTO();
        inboundOrderDTO.setMoId(order.getId());
        inboundOrderDTO.setWarehouseId(warehouseId);
        inboundOrderDTO.setInboundType(InboundTypeEnum.PRODUCTION.getCode());
        inboundOrderDTO.setInboundDate(LocalDateTime.now());

        InboundOrderDTO.InboundOrderDetailDTO detail = new InboundOrderDTO.InboundOrderDetailDTO();
        detail.setItemId(order.getItemId());
        detail.setQty(finishedQty);
        detail.setPrice(BigDecimal.ZERO); // 生产入库不涉及价格

        inboundOrderDTO.setItems(new ArrayList<>());
        inboundOrderDTO.getItems().add(detail);

        Long inboundId = inboundOrderService.createInboundOrder(inboundOrderDTO);
        inboundOrderService.confirmInboundOrder(inboundId);

        // 更新工单
        order.setFinishedQty(newFinishedQty);
        order.setActualEnd(LocalDateTime.now());

        // 检查是否全部完工
        if (newFinishedQty.compareTo(order.getQty()) >= 0) {
            order.setStatus(ProductionStatusEnum.COMPLETED.getCode());
            log.info("生产工单全部完工，状态更新为完工，工单号: {}", order.getMoNo());
        }

        boolean result = this.updateById(order);
        log.info("生产工单完工确认成功，工单号: {}, 完工数量: {}", order.getMoNo(), finishedQty);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrder(Long id) {
        ProductionOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("生产工单不存在");
        }

        // 只能删除计划状态的工单
        if (!ProductionStatusEnum.PLANNED.getCode().equals(order.getStatus())) {
            throw new BusinessException("只能删除计划状态的生产工单");
        }

        boolean result = this.removeById(id);
        log.info("删除生产工单成功，工单号: {}", order.getMoNo());
        return result;
    }

    /**
     * 生成生产工单号
     * 格式: MO + yyyyMMdd + 6位序号
     */
    private String generateMoNo() {
        String date = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = SEQ.getAndIncrement();
        return "MO" + date + String.format("%06d", seq);
    }
}
