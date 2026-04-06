package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.dto.InboundOrderDTO;
import com.scm.entity.InboundOrder;

/**
 * 入库单Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface InboundOrderService extends IService<InboundOrder> {

    /**
     * 分页查询入库单列表
     */
    PageResult<InboundOrder> pageQuery(Long poId, Long warehouseId, Integer inboundType,
                                        Integer status, String startDate, String endDate,
                                        Integer pageNum, Integer pageSize);

    /**
     * 创建入库单
     */
    Long createInboundOrder(InboundOrderDTO inboundOrderDTO);

    /**
     * 确认入库单
     */
    Boolean confirmInboundOrder(Long id);

    /**
     * 采购收货
     */
    Long purchaseReceipt(Long poId, InboundOrderDTO inboundOrderDTO);
}
