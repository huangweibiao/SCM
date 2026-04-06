package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据字典数据DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "数据字典数据DTO")
public class DictDataDTO implements Serializable {

    @Schema(description = "字典数据id")
    private Long id;

    @Schema(description = "字典类型id", required = true)
    @NotNull(message = "字典类型id不能为空")
    private Long dictTypeId;

    @Schema(description = "字典标签", required = true)
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    @Schema(description = "字典值", required = true)
    @NotBlank(message = "字典值不能为空")
    private String dictValue;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "1启用 0停用")
    private Integer status;
}
