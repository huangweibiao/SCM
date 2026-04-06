package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.dto.ItemDTO;
import com.scm.entity.Item;
import com.scm.common.result.PageResult;

import java.util.List;

/**
 * 物料Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface ItemService extends IService<Item> {

    /**
     * 分页查询物料列表
     */
    PageResult<Item> pageQuery(String itemCode, String itemName, Long categoryId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 创建物料
     */
    Long create(ItemDTO itemDTO);

    /**
     * 更新物料
     */
    Boolean update(ItemDTO itemDTO);

    /**
     * 删除物料
     */
    Boolean delete(Long id);
}
