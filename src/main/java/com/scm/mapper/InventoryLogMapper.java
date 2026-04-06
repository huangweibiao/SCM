package com.scm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scm.entity.InventoryLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存流水Mapper
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Mapper
public interface InventoryLogMapper extends BaseMapper<InventoryLog> {
}
