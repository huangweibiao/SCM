package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.dto.LogisticsOrderDTO;
import com.scm.entity.LogisticsOrder;

/**
 * 物流订单Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface LogisticsOrderService extends IService<LogisticsOrder> {

    /**
     * 分页查询物流订单列表
     */
    PageResult<LogisticsOrder> pageQuery(Integer businessType, Integer status,
                                          String startDate, String endDate,
                                          Integer pageNum, Integer pageSize);

    /**
     * 创建物流订单
     */
    Long createOrder(LogisticsOrderDTO orderDTO);

    /**
     * 更新物流订单
     */
    Boolean updateOrder(LogisticsOrderDTO orderDTO);

    /**
     * 更新物流状态
     */
    Boolean updateStatus(Long id, Integer status);
}
