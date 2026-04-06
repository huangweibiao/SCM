package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.entity.SysOperationLog;
import com.scm.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "操作日志")
@RestController
@RequestMapping("/v1/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping
    public ApiResponse<PageResult<SysOperationLog>> pageQuery(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SysOperationLog> result = operationLogService.pageQuery(module, operationType, username, status, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询操作日志详情")
    @GetMapping("/{id}")
    public ApiResponse<SysOperationLog> getById(@PathVariable Long id) {
        SysOperationLog operationLog = operationLogService.getById(id);
        return ApiResponse.success(operationLog);
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        Boolean result = operationLogService.deleteOperationLog(id);
        return ApiResponse.success(result);
    }
}
