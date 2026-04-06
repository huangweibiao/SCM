package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据字典类型DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "数据字典类型DTO")
public class DictTypeDTO implements Serializable {

    @Schema(description = "字典类型id")
    private Long id;

    @Schema(description = "字典类型编码", required = true)
    @NotBlank(message = "字典类型编码不能为空")
    private String dictCode;

    @Schema(description = "字典类型名称", required = true)
    @NotBlank(message = "字典类型名称不能为空")
    private String dictName;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "1启用 0停用")
    private Integer status;
}
