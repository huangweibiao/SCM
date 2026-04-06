package com.scm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.common.result.PageResult;
import com.scm.entity.SysOperationLog;

/**
 * 操作日志Service接口
 *
 * @author SCM System
 * @since 2026-04-06
 */
public interface OperationLogService extends IService<SysOperationLog> {

    /**
     * 分页查询操作日志
     */
    PageResult<SysOperationLog> pageQuery(String module, String operationType, String username, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 异步保存操作日志
     */
    void saveAsync(SysOperationLog operationLog);

    /**
     * 删除操作日志
     */
    Boolean deleteOperationLog(Long id);
}
