package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 客户控制器
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 分页查询客户列表
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(customerService.list(pageNum, pageSize, keyword, status));
    }

    /**
     * 根据ID查询客户
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(customerService.getById(id));
    }

    /**
     * 创建客户
     */
    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> params) {
        return Result.success("创建成功", customerService.create(params));
    }

    /**
     * 更新客户
     */
    @PutMapping("/{id}")
    public Result<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return Result.success("更新成功", customerService.update(id, params));
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public Result<Map<String, Object>> delete(@PathVariable Long id) {
        return Result.success("删除成功", customerService.delete(id));
    }
}
