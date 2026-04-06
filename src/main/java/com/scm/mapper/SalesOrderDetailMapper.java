package com.scm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scm.entity.SalesOrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售订单明细Mapper
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Mapper
public interface SalesOrderDetailMapper extends BaseMapper<SalesOrderDetail> {
}
