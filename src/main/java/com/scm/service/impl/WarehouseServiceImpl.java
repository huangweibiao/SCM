package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.util.StringUtils;
import com.scm.common.result.PageResult;
import com.scm.entity.Warehouse;
import com.scm.mapper.WarehouseMapper;
import com.scm.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 仓库Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Override
    public PageResult<Warehouse> pageQuery(String warehouseCode, String warehouseName, Integer status, Integer pageNum, Integer pageSize) {
        Page<Warehouse> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(warehouseCode), Warehouse::getWarehouseCode, warehouseCode)
                .like(StringUtils.isNotEmpty(warehouseName), Warehouse::getWarehouseName, warehouseName)
                .eq(status != null, Warehouse::getStatus, status)
                .orderByDesc(Warehouse::getCreateTime);
        Page<Warehouse> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }
}
