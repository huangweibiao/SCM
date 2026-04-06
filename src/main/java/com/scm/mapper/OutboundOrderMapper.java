package com.scm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scm.entity.OutboundOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库单Mapper
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Mapper
public interface OutboundOrderMapper extends BaseMapper<OutboundOrder> {
}
