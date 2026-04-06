package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.dto.OutboundOrderDTO;
import com.scm.entity.OutboundOrder;

/**
 * 出库单Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface OutboundOrderService extends IService<OutboundOrder> {

    /**
     * 分页查询出库单列表
     */
    PageResult<OutboundOrder> pageQuery(Long soId, Long warehouseId, Integer outboundType,
                                         Integer status, String startDate, String endDate,
                                         Integer pageNum, Integer pageSize);

    /**
     * 创建出库单
     */
    Long createOutboundOrder(OutboundOrderDTO outboundOrderDTO);

    /**
     * 确认出库单
     */
    Boolean confirmOutboundOrder(Long id);

    /**
     * 销售发货
     */
    Long salesShipment(Long soId, OutboundOrderDTO outboundOrderDTO);
}
