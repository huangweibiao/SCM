package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.DictService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据字典控制器
 */
@RestController
@RequestMapping("/system/dict")
public class DictController {

    private final DictService dictService;

    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    /**
     * 查询字典类型列表
     */
    @GetMapping("/types")
    public Result<Map<String, Object>> listTypes(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(dictService.listTypes(pageNum, pageSize));
    }

    /**
     * 创建字典类型
     */
    @PostMapping("/types")
    public Result<Map<String, Object>> createType(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", dictService.createType(params));
    }

    /**
     * 更新字典类型
     */
    @PutMapping("/types/{id}")
    public Result<Map<String, Object>> updateType(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", dictService.updateType(id, params));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/types/{id}")
    public Result<Map<String, Object>> deleteType(@PathVariable Long id) {
        return Result.success("删除成功", dictService.deleteType(id));
    }

    /**
     * 查询字典数据列表
     */
    @GetMapping("/data")
    public Result<Map<String, Object>> listData(@RequestParam Long dictTypeId) {
        return Result.success(dictService.listData(dictTypeId));
    }

    /**
     * 创建字典数据
     */
    @PostMapping("/data")
    public Result<Map<String, Object>> createData(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", dictService.createData(params));
    }

    /**
     * 更新字典数据
     */
    @PutMapping("/data/{id}")
    public Result<Map<String, Object>> updateData(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", dictService.updateData(id, params));
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/data/{id}")
    public Result<Map<String, Object>> deleteData(@PathVariable Long id) {
        return Result.success("删除成功", dictService.deleteData(id));
    }

    /**
     * 根据类型编码获取字典数据
     */
    @GetMapping("/data/{typeCode}")
    public Result<List<Map<String, Object>>> getDataByTypeCode(@PathVariable String typeCode) {
        return Result.success(dictService.getDataByTypeCode(typeCode));
    }
}
