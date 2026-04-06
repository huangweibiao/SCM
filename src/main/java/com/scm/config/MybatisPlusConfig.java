package com.scm.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Configuration
@MapperScan("com.scm.mapper")
public class MybatisPlusConfig {

    /**
     * 配置MyBatis-Plus插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置单页分页条数限制（默认500条，-1不限制）
        paginationInnerInterceptor.setMaxLimit(1000L);
        // 溢出总页数后是否进行处理（默认不处理）
        paginationInnerInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 防止全表更新和删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        // 多租户插件（如需要可启用）
        // TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
        // tenantLineInnerInterceptor.setTenantLineHandler(new TenantLineHandler() {
        //     @Override
        //     public Expression getTenantId() {
        //         return new LongValue(1L); // 返回租户ID
        //     }
        //     @Override
        //     public boolean ignoreTable(String tableName) {
        //         return "sys_dict_data".equalsIgnoreCase(tableName); // 忽略某些表
        //     }
        // });
        // interceptor.addInnerInterceptor(tenantLineInnerInterceptor);

        return interceptor;
    }
}
