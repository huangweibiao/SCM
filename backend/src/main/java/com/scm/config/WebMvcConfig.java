package com.scm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 配置静态资源访问
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源目录
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 前端资源
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        // favicon
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/");

        // 首页
        registry.addResourceHandler("/index.html")
                .addResourceLocations("classpath:/static/index.html");
    }
}