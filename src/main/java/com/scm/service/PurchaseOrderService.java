package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.dto.PurchaseOrderDTO;
import com.scm.entity.PurchaseOrder;
import com.scm.entity.PurchaseOrderDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface PurchaseOrderService extends IService<PurchaseOrder> {

    /**
     * 分页查询采购订单列表
     */
    PageResult<PurchaseOrder> pageQuery(Long supplierId, Integer status, String poNo,
                                       String startDate, String endDate,
                                       Integer pageNum, Integer pageSize);

    /**
     * 创建采购订单
     */
    Long createOrder(PurchaseOrderDTO orderDTO);

    /**
     * 更新采购订单
     */
    Boolean updateOrder(PurchaseOrderDTO orderDTO);

    /**
     * 删除采购订单
     */
    Boolean deleteOrder(Long id);

    /**
     * 审核采购订单
     */
    Boolean auditOrder(Long id, Long auditBy);

    /**
     * 关闭采购订单
     */
    Boolean closeOrder(Long id);

    /**
     * 查询采购订单详情（包含明细）
     */
    PurchaseOrder getDetailWithItems(Long id);

    /**
     * 获取采购订单明细列表
     */
    List<PurchaseOrderDetail> getDetailList(Long poId);
}
