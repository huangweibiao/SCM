package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.enums.OrderStatusEnum;
import com.scm.common.enums.ProductionStatusEnum;
import com.scm.common.util.StringUtils;
import com.scm.entity.*;
import com.scm.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单状态自动流转Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusAutoService extends ServiceImpl {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderDetailMapper salesOrderDetailMapper;
    private final ProductionOrderMapper productionOrderMapper;

    /**
     * 定时检查采购订单状态（全部收货后自动完成）
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void autoCompletePurchaseOrders() {
        log.info("开始定时检查采购订单状态");

        // 查询所有已审核但未完成的采购订单
        List<Integer> statuses = Arrays.asList(
                OrderStatusEnum.AUDITED.getCode(),
                OrderStatusEnum.PARTIAL_RECEIVED.getCode()
        );

        List<PurchaseOrder> orders = purchaseOrderMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrder>()
                        .in(PurchaseOrder::getStatus, statuses)
        );

        for (PurchaseOrder order : orders) {
            try {
                // 查询订单明细
                List<PurchaseOrderDetail> details = purchaseOrderDetailMapper.selectList(
                        new LambdaQueryWrapper<PurchaseOrderDetail>()
                                .eq(PurchaseOrderDetail::getPoId, order.getId())
                );

                // 检查是否全部收货
                boolean allReceived = details.stream()
                        .allMatch(d -> d.getReceivedQty().compareTo(d.getQty()) >= 0);

                if (allReceived && !OrderStatusEnum.COMPLETED.getCode().equals(order.getStatus())) {
                    // 更新订单状态为已完成
                    order.setStatus(OrderStatusEnum.COMPLETED.getCode());
                    purchaseOrderMapper.updateById(order);
                    log.info("采购订单自动完成，订单号: {}", order.getPoNo());
                }
            } catch (Exception e) {
                log.error("处理采购订单状态失败，订单号: {}, 错误: {}", order.getPoNo(), e.getMessage(), e);
            }
        }

        log.info("定时检查采购订单状态完成，处理订单数: {}", orders.size());
    }

    /**
     * 定时检查销售订单状态（全部发货后自动完成）
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void autoCompleteSalesOrders() {
        log.info("开始定时检查销售订单状态");

        // 查询所有已审核但未完成的销售订单
        List<Integer> statuses = Arrays.asList(
                OrderStatusEnum.AUDITED.getCode(),
                OrderStatusEnum.PARTIAL_SHIPPED.getCode()
        );

        List<SalesOrder> orders = salesOrderMapper.selectList(
                new LambdaQueryWrapper<SalesOrder>()
                        .in(SalesOrder::getStatus, statuses)
        );

        for (SalesOrder order : orders) {
            try {
                // 查询订单明细
                List<SalesOrderDetail> details = salesOrderDetailMapper.selectList(
                        new LambdaQueryWrapper<SalesOrderDetail>()
                                .eq(SalesOrderDetail::getSoId, order.getId())
                );

                // 检查是否全部发货
                boolean allShipped = details.stream()
                        .allMatch(d -> d.getShippedQty().compareTo(d.getQty()) >= 0);

                if (allShipped && !OrderStatusEnum.COMPLETED.getCode().equals(order.getStatus())) {
                    // 更新订单状态为已完成
                    order.setStatus(OrderStatusEnum.COMPLETED.getCode());
                    salesOrderMapper.updateById(order);
                    log.info("销售订单自动完成，订单号: {}", order.getSoNo());
                }
            } catch (Exception e) {
                log.error("处理销售订单状态失败，订单号: {}, 错误: {}", order.getSoNo(), e.getMessage(), e);
            }
        }

        log.info("定时检查销售订单状态完成，处理订单数: {}", orders.size());
    }

    /**
     * 定时检查生产工单状态（全部完工后自动完成）
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void autoCompleteProductionOrders() {
        log.info("开始定时检查生产工单状态");

        // 查询所有生产中的工单
        List<ProductionOrder> orders = productionOrderMapper.selectList(
                new LambdaQueryWrapper<ProductionOrder>()
                        .eq(ProductionOrder::getStatus, ProductionStatusEnum.IN_PROGRESS.getCode())
        );

        for (ProductionOrder order : orders) {
            try {
                // 检查是否全部完工
                if (order.getFinishedQty().compareTo(order.getQty()) >= 0 &&
                        !ProductionStatusEnum.COMPLETED.getCode().equals(order.getStatus())) {
                    // 更新工单状态为完工
                    order.setStatus(ProductionStatusEnum.COMPLETED.getCode());
                    order.setActualEnd(LocalDateTime.now());
                    productionOrderMapper.updateById(order);
                    log.info("生产工单自动完工，工单号: {}", order.getMoNo());
                }
            } catch (Exception e) {
                log.error("处理生产工单状态失败，工单号: {}, 错误: {}", order.getMoNo(), e.getMessage(), e);
            }
        }

        log.info("定时检查生产工单状态完成，处理工单数: {}", orders.size());
    }

    /**
     * 手动关闭超时未完成的订单
     * 每天凌晨2点执行一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoCloseExpiredOrders() {
        log.info("开始自动关闭超时订单");

        // 关闭超过30天未完成的采购订单
        LocalDate expireDate = LocalDate.now().minusDays(30);

        List<PurchaseOrder> purchaseOrders = purchaseOrderMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrder>()
                        .lt(PurchaseOrder::getOrderDate, expireDate)
                        .in(PurchaseOrder::getStatus, Arrays.asList(
                                OrderStatusEnum.AUDITED.getCode(),
                                OrderStatusEnum.PARTIAL_RECEIVED.getCode()
                        ))
        );

        for (PurchaseOrder order : purchaseOrders) {
            try {
                order.setStatus(OrderStatusEnum.CLOSED.getCode());
                purchaseOrderMapper.updateById(order);
                log.info("自动关闭采购订单，订单号: {}", order.getPoNo());
            } catch (Exception e) {
                log.error("关闭采购订单失败，订单号: {}, 错误: {}", order.getPoNo(), e.getMessage(), e);
            }
        }

        // 关闭超过30天未完成的销售订单
        List<SalesOrder> salesOrders = salesOrderMapper.selectList(
                new LambdaQueryWrapper<SalesOrder>()
                        .lt(SalesOrder::getOrderDate, expireDate)
                        .in(SalesOrder::getStatus, Arrays.asList(
                                OrderStatusEnum.AUDITED.getCode(),
                                OrderStatusEnum.PARTIAL_SHIPPED.getCode()
                        ))
        );

        for (SalesOrder order : salesOrders) {
            try {
                order.setStatus(OrderStatusEnum.CLOSED.getCode());
                salesOrderMapper.updateById(order);
                log.info("自动关闭销售订单，订单号: {}", order.getSoNo());
            } catch (Exception e) {
                log.error("关闭销售订单失败，订单号: {}, 错误: {}", order.getSoNo(), e.getMessage(), e);
            }
        }

        log.info("自动关闭超时订单完成，采购订单: {}, 销售订单: {}",
                purchaseOrders.size(), salesOrders.size());
    }
}
