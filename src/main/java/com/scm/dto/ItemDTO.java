package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物料DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "物料DTO")
public class ItemDTO implements Serializable {

    @Schema(description = "物料id")
    private Long id;

    @Schema(description = "物料编码", required = true)
    @NotBlank(message = "物料编码不能为空")
    private String itemCode;

    @Schema(description = "物料名称", required = true)
    @NotBlank(message = "物料名称不能为空")
    private String itemName;

    @Schema(description = "规格型号")
    private String spec;

    @Schema(description = "单位", required = true)
    @NotBlank(message = "单位不能为空")
    private String unit;

    @Schema(description = "物料分类id")
    private Long categoryId;

    @Schema(description = "安全库存")
    private BigDecimal safetyStock;

    @Schema(description = "最高库存")
    private BigDecimal maxStock;

    @Schema(description = "最低库存")
    private BigDecimal minStock;
}
