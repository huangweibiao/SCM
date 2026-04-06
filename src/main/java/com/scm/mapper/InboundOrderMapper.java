package com.scm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scm.entity.InboundOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库单Mapper
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Mapper
public interface InboundOrderMapper extends BaseMapper<InboundOrder> {
}
