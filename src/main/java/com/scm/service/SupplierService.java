package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.dto.SupplierDTO;
import com.scm.common.result.PageResult;
import com.scm.entity.Supplier;

/**
 * 供应商Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface SupplierService extends IService<Supplier> {

    /**
     * 分页查询供应商列表
     */
    PageResult<Supplier> pageQuery(String supplierCode, String supplierName, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 创建供应商
     */
    Long create(SupplierDTO supplierDTO);

    /**
     * 更新供应商
     */
    Boolean update(SupplierDTO supplierDTO);

    /**
     * 删除供应商
     */
    Boolean delete(Long id);

    /**
     * 生成供应商编码
     */
    String generateSupplierCode();
}
