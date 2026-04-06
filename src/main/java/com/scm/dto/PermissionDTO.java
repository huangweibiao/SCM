package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "权限DTO")
public class PermissionDTO implements Serializable {

    @Schema(description = "权限id")
    private Long id;

    @Schema(description = "父权限id")
    private Long parentId;

    @Schema(description = "权限编码", required = true)
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    @Schema(description = "权限名称", required = true)
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    @Schema(description = "权限类型", required = true)
    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "1启用 0停用")
    private Integer status;
}
