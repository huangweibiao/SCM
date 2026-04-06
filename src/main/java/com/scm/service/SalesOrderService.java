package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.dto.SalesOrderDTO;
import com.scm.entity.SalesOrder;
import com.scm.entity.SalesOrderDetail;

import java.util.List;

/**
 * 销售订单Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface SalesOrderService extends IService<SalesOrder> {

    /**
     * 分页查询销售订单列表
     */
    PageResult<SalesOrder> pageQuery(String customerName, Integer status, String soNo,
                                       String startDate, String endDate,
                                       Integer pageNum, Integer pageSize);

    /**
     * 创建销售订单
     */
    Long createOrder(SalesOrderDTO orderDTO);

    /**
     * 更新销售订单
     */
    Boolean updateOrder(SalesOrderDTO orderDTO);

    /**
     * 删除销售订单
     */
    Boolean deleteOrder(Long id);

    /**
     * 审核销售订单
     */
    Boolean auditOrder(Long id, Long auditBy);

    /**
     * 关闭销售订单
     */
    Boolean closeOrder(Long id);

    /**
     * 查询销售订单详情（包含明细）
     */
    SalesOrder getDetailWithItems(Long id);

    /**
     * 获取销售订单明细列表
     */
    List<SalesOrderDetail> getDetailList(Long soId);
}
