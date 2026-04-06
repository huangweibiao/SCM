package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.enums.LogisticsStatusEnum;
import com.scm.common.exception.BusinessException;
import com.scm.common.result.PageResult;
import com.scm.common.util.DateUtils;
import com.scm.common.util.StringUtils;
import com.scm.dto.LogisticsOrderDTO;
import com.scm.entity.LogisticsOrder;
import com.scm.mapper.LogisticsOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 物流订单Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsOrderServiceImpl extends ServiceImpl<LogisticsOrderMapper, LogisticsOrder> implements LogisticsOrderService {

    private static final AtomicLong SEQ = new AtomicLong(1);

    @Override
    public PageResult<LogisticsOrder> pageQuery(Integer businessType, Integer status,
                                                  String startDate, String endDate,
                                                  Integer pageNum, Integer pageSize) {
        Page<LogisticsOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LogisticsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(businessType != null, LogisticsOrder::getBusinessType, businessType)
                .eq(status != null, LogisticsOrder::getStatus, status)
                .ge(StringUtils.isNotEmpty(startDate), LogisticsOrder::getCreateTime,
                    DateUtils.parseDateTime(startDate))
                .le(StringUtils.isNotEmpty(endDate), LogisticsOrder::getCreateTime,
                    DateUtils.parseDateTime(endDate))
                .orderByDesc(LogisticsOrder::getCreateTime);
        Page<LogisticsOrder> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(LogisticsOrderDTO orderDTO) {
        // 创建物流订单
        LogisticsOrder order = new LogisticsOrder();
        order.setLogisticsNo(generateLogisticsNo());
        order.setBusinessType(orderDTO.getBusinessType());
        order.setBusinessId(orderDTO.getBusinessId());
        order.setCourierName(orderDTO.getCourierName());
        order.setCourierNo(orderDTO.getCourierNo());
        order.setSendAddress(orderDTO.getSendAddress());
        order.setReceiveAddress(orderDTO.getReceiveAddress());
        order.setReceivePerson(orderDTO.getReceivePerson());
        order.setReceivePhone(orderDTO.getReceivePhone());
        order.setLogisticsFee(orderDTO.getLogisticsFee() != null ? orderDTO.getLogisticsFee() : java.math.BigDecimal.ZERO);
        order.setStatus(LogisticsStatusEnum.PENDING.getCode());
        order.setTenantId(0L);

        this.save(order);
        log.info("创建物流订单成功，物流单号: {}, id: {}", order.getLogisticsNo(), order.getId());

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrder(LogisticsOrderDTO orderDTO) {
        LogisticsOrder exist = this.getById(orderDTO.getId());
        if (exist == null) {
            throw new BusinessException("物流订单不存在");
        }

        // 更新物流订单
        exist.setCourierName(orderDTO.getCourierName());
        exist.setCourierNo(orderDTO.getCourierNo());
        exist.setSendAddress(orderDTO.getSendAddress());
        exist.setReceiveAddress(orderDTO.getReceiveAddress());
        exist.setReceivePerson(orderDTO.getReceivePerson());
        exist.setReceivePhone(orderDTO.getReceivePhone());
        exist.setLogisticsFee(orderDTO.getLogisticsFee());

        boolean result = this.updateById(exist);
        log.info("更新物流订单成功，物流单号: {}", exist.getLogisticsNo());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStatus(Long id, Integer status) {
        LogisticsOrder order = this.getById(id);
        if (order == null) {
            throw new BusinessException("物流订单不存在");
        }

        // 只能更新非完成、非拒收状态的订单
        LogisticsStatusEnum currentStatus = LogisticsStatusEnum.getByCode(order.getStatus());
        if (currentStatus == null ||
                !LogisticsStatusEnum.RECEIVED.equals(currentStatus) &&
                !LogisticsStatusEnum.REJECTED.equals(currentStatus)) {

            order.setStatus(status);
            boolean result = this.updateById(order);
            log.info("更新物流订单状态成功，物流单号: {}, 新状态: {}",
                    order.getLogisticsNo(), status);
            return result;
        }

        return false;
    }

    /**
     * 生成物流单号
     * 格式: LG + yyyyMMdd + 6位序号
     */
    private String generateLogisticsNo() {
        String date = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = SEQ.getAndIncrement();
        return "LG" + date + String.format("%06d", seq);
    }
}
