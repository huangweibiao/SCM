package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.entity.Warehouse;

/**
 * 仓库Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface WarehouseService extends IService<Warehouse> {

    /**
     * 分页查询仓库列表
     */
    PageResult<Warehouse> pageQuery(String warehouseCode, String warehouseName, Integer status, Integer pageNum, Integer pageSize);
}
