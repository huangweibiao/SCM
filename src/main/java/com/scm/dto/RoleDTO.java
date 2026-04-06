package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "角色DTO")
public class RoleDTO implements Serializable {

    @Schema(description = "角色id")
    private Long id;

    @Schema(description = "角色编码", required = true)
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @Schema(description = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "1启用 0停用")
    private Integer status;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
