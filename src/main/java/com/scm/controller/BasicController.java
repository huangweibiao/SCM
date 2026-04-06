package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.dto.DictDataDTO;
import com.scm.dto.DictTypeDTO;
import com.scm.dto.ItemDTO;
import com.scm.entity.DictData;
import com.scm.entity.DictType;
import com.scm.entity.Item;
import com.scm.entity.Warehouse;
import com.scm.service.DictService;
import com.scm.service.ItemService;
import com.scm.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 基础数据Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "基础数据管理")
@RestController
@RequestMapping("/v1/basic")
@RequiredArgsConstructor
public class BasicController {

    private final ItemService itemService;
    private final WarehouseService warehouseService;
    private final DictService dictService;

    @Operation(summary = "分页查询物料列表")
    @GetMapping("/item")
    public ApiResponse<PageResult<Item>> pageItem(
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Item> result = itemService.pageQuery(itemCode, itemName, categoryId, status, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建物料")
    @PostMapping("/item")
    public ApiResponse<Long> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        Long id = itemService.create(itemDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新物料")
    @PutMapping("/item/{id}")
    public ApiResponse<Boolean> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDTO itemDTO) {
        itemDTO.setId(id);
        Boolean result = itemService.update(itemDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除物料")
    @DeleteMapping("/item/{id}")
    public ApiResponse<Boolean> deleteItem(@PathVariable Long id) {
        Boolean result = itemService.delete(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询物料详情")
    @GetMapping("/item/{id}")
    public ApiResponse<Item> getItem(@PathVariable Long id) {
        Item item = itemService.getById(id);
        return ApiResponse.success(item);
    }

    @Operation(summary = "分页查询仓库列表")
    @GetMapping("/warehouse")
    public ApiResponse<PageResult<Warehouse>> pageWarehouse(
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Warehouse> result = warehouseService.pageQuery(warehouseCode, warehouseName, status, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    // ========== 数据字典管理 ==========

    @Operation(summary = "分页查询字典类型列表")
    @GetMapping("/dict-type")
    public ApiResponse<PageResult<DictType>> pageDictType(
            @RequestParam(required = false) String dictCode,
            @RequestParam(required = false) String dictName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<DictType> result = dictService.pageDictType(dictCode, dictName, status, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建字典类型")
    @PostMapping("/dict-type")
    public ApiResponse<Long> createDictType(@Valid @RequestBody DictTypeDTO dictTypeDTO) {
        Long id = dictService.createDictType(dictTypeDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新字典类型")
    @PutMapping("/dict-type/{id}")
    public ApiResponse<Boolean> updateDictType(@PathVariable Long id, @Valid @RequestBody DictTypeDTO dictTypeDTO) {
        dictTypeDTO.setId(id);
        Boolean result = dictService.updateDictType(dictTypeDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/dict-type/{id}")
    public ApiResponse<Boolean> deleteDictType(@PathVariable Long id) {
        Boolean result = dictService.deleteDictType(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "根据字典编码查询字典数据")
    @GetMapping("/dict/{dictCode}")
    public ApiResponse<List<DictData>> getDictDataByCode(@PathVariable String dictCode) {
        List<DictData> result = dictService.getDictDataByCode(dictCode);
        return ApiResponse.success(result);
    }

    @Operation(summary = "获取所有字典数据Map")
    @GetMapping("/dict/map")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getAllDictDataMap() {
        Map<String, List<Map<String, Object>>> result = dictService.getAllDictDataMap();
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建字典数据")
    @PostMapping("/dict-data")
    public ApiResponse<Long> createDictData(@Valid @RequestBody DictDataDTO dictDataDTO) {
        Long id = dictService.createDictData(dictDataDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新字典数据")
    @PutMapping("/dict-data/{id}")
    public ApiResponse<Boolean> updateDictData(@PathVariable Long id, @Valid @RequestBody DictDataDTO dictDataDTO) {
        dictDataDTO.setId(id);
        Boolean result = dictService.updateDictData(dictDataDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除字典数据")
    @DeleteMapping("/dict-data/{id}")
    public ApiResponse<Boolean> deleteDictData(@PathVariable Long id) {
        Boolean result = dictService.deleteDictData(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "根据字典类型ID查询字典数据")
    @GetMapping("/dict-data/by-type/{dictTypeId}")
    public ApiResponse<List<DictData>> getDictDataByTypeId(@PathVariable Long dictTypeId) {
        List<DictData> result = dictService.getDictDataByTypeId(dictTypeId);
        return ApiResponse.success(result);
    }
}
