package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 库存预警DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "库存预警DTO")
public class InventoryWarningDTO implements Serializable {

    @Schema(description = "库存ID")
    private Long id;

    @Schema(description = "商品ID")
    private Long itemId;

    @Schema(description = "商品编码")
    private String itemCode;

    @Schema(description = "商品名称")
    private String itemName;

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "当前库存数量")
    private BigDecimal qty;

    @Schema(description = "安全库存下限")
    private BigDecimal minQty;

    @Schema(description = "安全库存上限")
    private BigDecimal maxQty;

    @Schema(description = "预警类型")
    private String warningType;

    @Schema(description = "预警信息")
    private String warningMessage;
}
