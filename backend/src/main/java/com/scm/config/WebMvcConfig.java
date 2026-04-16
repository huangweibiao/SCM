package com.scm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 配置静态资源访问和默认页面
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

        // 前端打包资源 - 所有路径都映射到static目录
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 配置默认首页
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 根路径和api路径都映射到index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
