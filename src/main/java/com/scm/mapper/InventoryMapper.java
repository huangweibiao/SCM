package com.scm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scm.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 库存Mapper
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    /**
     * 查询库存（使用行锁）
     */
    @Select("SELECT * FROM inventory WHERE item_id = #{itemId} AND warehouse_id = #{warehouseId} AND batch_no = #{batchNo} FOR UPDATE")
    Inventory selectForUpdate(@Param("itemId") Long itemId, @Param("warehouseId") Long warehouseId, @Param("batchNo") String batchNo);
}
