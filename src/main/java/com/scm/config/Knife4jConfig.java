package com.scm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j/Swagger 配置
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("全部接口")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi basicApi() {
        return GroupedOpenApi.builder()
                .group("基础管理")
                .pathsToMatch("/v1/basic/**")
                .build();
    }

    @Bean
    public GroupedOpenApi inventoryApi() {
        return GroupedOpenApi.builder()
                .group("库存管理")
                .pathsToMatch("/v1/inventory/**", "/v1/inventory-warning/**")
                .build();
    }

    @Bean
    public GroupedOpenApi purchaseApi() {
        return GroupedOpenApi.builder()
                .group("采购管理")
                .pathsToMatch("/v1/purchase/**", "/v1/inbound/**")
                .build();
    }

    @Bean
    public GroupedOpenApi salesApi() {
        return GroupedOpenApi.builder()
                .group("销售管理")
                .pathsToMatch("/v1/sales/**", "/v1/outbound/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productionApi() {
        return GroupedOpenApi.builder()
                .group("生产管理")
                .pathsToMatch("/v1/production/**")
                .build();
    }

    @Bean
    public GroupedOpenApi logisticsApi() {
        return GroupedOpenApi.builder()
                .group("物流管理")
                .pathsToMatch("/v1/logistics/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reportApi() {
        return GroupedOpenApi.builder()
                .group("报表管理")
                .pathsToMatch("/v1/report/**")
                .build();
    }

    @Bean
    public GroupedOpenApi permissionApi() {
        return GroupedOpenApi.builder()
                .group("权限管理")
                .pathsToMatch("/v1/permission/**", "/v1/operation-log/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("认证管理")
                .pathsToMatch("/v1/auth/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SCM供应链管理系统 API文档")
                        .version("1.0.0")
                        .description("基于Spring Boot 3.5 + Vue3的供应链管理系统API接口文档")
                        .contact(new Contact()
                                .name("SCM开发团队")
                                .email("support@scm.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请输入JWT Token，格式：Bearer {token}")));
    }
}
