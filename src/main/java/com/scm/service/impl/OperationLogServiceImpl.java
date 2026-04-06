package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.result.PageResult;
import com.scm.common.util.StringUtils;
import com.scm.entity.SysOperationLog;
import com.scm.mapper.SysOperationLogMapper;
import com.scm.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements OperationLogService {

    @Override
    public PageResult<SysOperationLog> pageQuery(String module, String operationType, String username, Integer status, Integer pageNum, Integer pageSize) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(module), SysOperationLog::getModule, module)
                .eq(StringUtils.isNotEmpty(operationType), SysOperationLog::getOperationType, operationType)
                .like(StringUtils.isNotEmpty(username), SysOperationLog::getUsername, username)
                .eq(status != null, SysOperationLog::getStatus, status)
                .orderByDesc(SysOperationLog::getCreateTime);
        Page<SysOperationLog> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Async
    public void saveAsync(SysOperationLog operationLog) {
        try {
            this.save(operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }

    @Override
    public Boolean deleteOperationLog(Long id) {
        boolean result = this.removeById(id);
        log.info("删除操作日志成功，id: {}", id);
        return result;
    }
}
