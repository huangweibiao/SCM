package com.scm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scm.entity.ProductionOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 生产工单Mapper
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Mapper
public interface ProductionOrderMapper extends BaseMapper<ProductionOrder> {
}
