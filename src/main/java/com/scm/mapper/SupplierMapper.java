package com.scm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scm.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供应商Mapper
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
}
