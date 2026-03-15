package com.scm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SPA Forward Controller
 * Forwards frontend routes to index.html for Vue Router to handle
 */
@Controller
public class SpaForwardController {

    /**
     * Forward all frontend routes to index.html
     * This allows Vue Router to handle client-side routing
     */
    @GetMapping(value = {
        "/",
        "/dashboard",
        "/login",
        "/supplier/**",
        "/purchase/**",
        "/inventory/**",
        "/report/**",
        "/settings/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
