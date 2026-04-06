package com.scm.aspect;

import com.alibaba.fastjson2.JSON;
import com.scm.annotation.OperationLog;
import com.scm.entity.SysOperationLog;
import com.scm.entity.SysUser;
import com.scm.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 操作日志切面
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @Around("@annotation(com.scm.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;

            // 保存操作日志
            saveOperationLog(joinPoint, result, exception, costTime);
        }
    }

    private void saveOperationLog(ProceedingJoinPoint joinPoint, Object result, Exception exception, long costTime) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog operationLog = method.getAnnotation(OperationLog.class);

            if (operationLog == null) {
                return;
            }

            // 获取当前登录用户
            SysUser currentUser = null;
            try {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof SysUser) {
                    currentUser = (SysUser) principal;
                }
            } catch (Exception e) {
                log.debug("获取当前用户失败: {}", e.getMessage());
            }

            // 构建日志对象
            SysOperationLog logEntity = new SysOperationLog();
            logEntity.setModule(operationLog.module());
            logEntity.setOperationType(operationLog.type().name());
            logEntity.setDescription(operationLog.description());
            logEntity.setMethod(className(joinPoint.getTarget()) + "." + method.getName());
            logEntity.setRequestMethod(request.getMethod());
            logEntity.setRequestUrl(request.getRequestURI());
            logEntity.setIp(getIpAddr(request));

            if (currentUser != null) {
                logEntity.setUserId(currentUser.getId());
                logEntity.setUsername(currentUser.getUsername());
                logEntity.setRealName(currentUser.getRealName());
            }

            // 请求参数
            if (operationLog.saveParams()) {
                Object[] args = joinPoint.getArgs();
                try {
                    String params = JSON.toJSONString(args);
                    // 限制参数长度
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000);
                    }
                    logEntity.setRequestParams(params);
                } catch (Exception e) {
                    log.debug("序列化请求参数失败", e);
                }
            }

            // 响应结果
            if (operationLog.saveResult() && result != null) {
                try {
                    String response = JSON.toJSONString(result);
                    // 限制结果长度
                    if (response.length() > 2000) {
                        response = response.substring(0, 2000);
                    }
                    logEntity.setResponseData(response);
                } catch (Exception e) {
                    log.debug("序列化响应结果失败", e);
                }
            }

            // 执行状态
            if (exception != null) {
                logEntity.setStatus(0);
                logEntity.setErrorMsg(exception.getMessage());
            } else {
                logEntity.setStatus(1);
            }

            logEntity.setCostTime(costTime);
            logEntity.setCreateTime(new Date());

            // 异步保存日志
            operationLogService.saveAsync(logEntity);

        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    /**
     * 获取客户端IP
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 获取类名
     */
    private String className(Object target) {
        return target.getClass().getName();
    }
}
