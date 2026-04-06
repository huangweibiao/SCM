package com.scm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SCM供应链管理系统启动类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
@MapperScan("com.scm.mapper")
public class ScmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScmApplication.class, args);
        System.out.println("========================================");
        System.out.println("SCM供应链管理系统启动成功!");
        System.out.println("接口文档地址: http://localhost:8080/api/doc.html");
        System.out.println("Druid监控地址: http://localhost:8080/api/druid/");
        System.out.println("========================================");
    }
}
