package com.scm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scm.entity.Item;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物料Mapper
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Mapper
public interface ItemMapper extends BaseMapper<Item> {
}
