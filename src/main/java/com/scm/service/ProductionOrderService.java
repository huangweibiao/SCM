package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.dto.ProductionOrderDTO;
import com.scm.entity.ProductionOrder;

/**
 * 生产工单Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface ProductionOrderService extends IService<ProductionOrder> {

    /**
     * 分页查询生产工单列表
     */
    PageResult<ProductionOrder> pageQuery(Long itemId, Integer status,
                                          String startDate, String endDate,
                                          Integer pageNum, Integer pageSize);

    /**
     * 创建生产工单
     */
    Long createOrder(ProductionOrderDTO orderDTO);

    /**
     * 更新生产工单
     */
    Boolean updateOrder(ProductionOrderDTO orderDTO);

    /**
     * 开始生产
     */
    Boolean startProduction(Long id);

    /**
     * 完工确认
     */
    Boolean completeProduction(Long id, BigDecimal finishedQty, Long warehouseId);

    /**
     * 删除生产工单
     */
    Boolean deleteOrder(Long id);
}
