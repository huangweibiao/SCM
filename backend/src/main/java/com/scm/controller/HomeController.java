package com.scm.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 主控制器 - 处理前端路由
 * 直接将index.html内容写入响应，避免forward/redirect导致的异常处理问题
 */
@Controller
public class HomeController {

    /**
     * SPA fallback - 将所有Vue Router路径直接返回index.html内容
     * 使Vue Router能在客户端处理路由
     */
    @GetMapping(value = {
        "/", "/login", "/dashboard",
        "/basic/**", "/supplier", "/purchase",
        "/inbound", "/inventory/**", "/sales",
        "/customer", "/outbound", "/production",
        "/logistics", "/report", "/system/**"
    })
    public void spaFallback(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("static/index.html");
        response.setContentType("text/html;charset=UTF-8");
        try (InputStream is = resource.getInputStream()) {
            FileCopyUtils.copy(is, response.getOutputStream());
        }
    }
}