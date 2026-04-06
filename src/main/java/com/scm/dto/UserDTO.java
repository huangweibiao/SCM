package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "用户DTO")
public class UserDTO implements Serializable {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "1启用 0停用")
    private Integer status;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
