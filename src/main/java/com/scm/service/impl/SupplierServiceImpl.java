package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.exception.BusinessException;
import com.scm.common.util.BeanUtils;
import com.scm.common.util.StringUtils;
import com.scm.dto.SupplierDTO;
import com.scm.entity.PurchaseOrder;
import com.scm.entity.Supplier;
import com.scm.mapper.PurchaseOrderMapper;
import com.scm.mapper.SupplierMapper;
import com.scm.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 供应商Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    private static final AtomicLong SEQ = new AtomicLong(1);

    private final PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public PageResult<Supplier> pageQuery(String supplierCode, String supplierName, Integer status, Integer pageNum, Integer pageSize) {
        Page<Supplier> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(supplierCode), Supplier::getSupplierCode, supplierCode)
                .like(StringUtils.isNotEmpty(supplierName), Supplier::getSupplierName, supplierName)
                .eq(status != null, Supplier::getStatus, status)
                .orderByDesc(Supplier::getCreateTime);
        Page<Supplier> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SupplierDTO supplierDTO) {
        String supplierCode = supplierDTO.getSupplierCode();
        if (supplierCode == null || supplierCode.isEmpty()) {
            supplierCode = generateSupplierCode();
        } else {
            Long count = this.lambdaQuery()
                    .eq(Supplier::getSupplierCode, supplierCode)
                    .count();
            if (count > 0) {
                throw new BusinessException("供应商编码已存在");
            }
        }

        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDTO, supplier);
        supplier.setSupplierCode(supplierCode);
        supplier.setRating(3); // 默认3星
        supplier.setStatus(1); // 默认启用
        this.save(supplier);
        log.info("创建供应商成功，供应商id: {}, 供应商编码: {}", supplier.getId(), supplier.getSupplierCode());
        return supplier.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(SupplierDTO supplierDTO) {
        Supplier existSupplier = this.getById(supplierDTO.getId());
        if (existSupplier == null) {
            throw new BusinessException("供应商不存在");
        }

        // 检查供应商编码是否重复
        if (!existSupplier.getSupplierCode().equals(supplierDTO.getSupplierCode())) {
            Long count = this.lambdaQuery()
                    .eq(Supplier::getSupplierCode, supplierDTO.getSupplierCode())
                    .ne(Supplier::getId, supplierDTO.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("供应商编码已存在");
            }
        }

        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDTO, supplier);
        boolean result = this.updateById(supplier);
        log.info("更新供应商成功，供应商id: {}", supplier.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        Supplier supplier = this.getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }

        // 校验供应商是否有关联的采购订单
        Long orderCount = purchaseOrderMapper.selectCount(
                new LambdaQueryWrapper<PurchaseOrder>()
                        .eq(PurchaseOrder::getSupplierId, id)
        );
        if (orderCount > 0) {
            throw new BusinessException("供应商存在关联的采购订单，无法删除");
        }

        boolean result = this.removeById(id);
        log.info("删除供应商成功，供应商id: {}", id);
        return result;
    }

    @Override
    public String generateSupplierCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = SEQ.getAndIncrement();
        return "SUP" + date + String.format("%04d", seq);
    }
}
