package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.dto.SupplierDTO;
import com.scm.entity.Supplier;
import com.scm.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "供应商管理")
@RestController
@RequestMapping("/v1/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "分页查询供应商列表")
    @GetMapping
    public ApiResponse<PageResult<Supplier>> pageQuery(
            @RequestParam(required = false) String supplierCode,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Supplier> result = supplierService.pageQuery(supplierCode, supplierName, status, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建供应商")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody SupplierDTO supplierDTO) {
        Long id = supplierService.create(supplierDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新供应商")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody SupplierDTO supplierDTO) {
        supplierDTO.setId(id);
        Boolean result = supplierService.update(supplierDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除供应商")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        Boolean result = supplierService.delete(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询供应商详情")
    @GetMapping("/{id}")
    public ApiResponse<Supplier> get(@PathVariable Long id) {
        Supplier supplier = supplierService.getById(id);
        return ApiResponse.success(supplier);
    }
}
