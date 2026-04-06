package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 生产工单DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "生产工单DTO")
public class ProductionOrderDTO implements Serializable {

    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "产成品物料id", required = true)
    @NotNull(message = "产成品物料不能为空")
    private Long itemId;

    @Schema(description = "计划生产数量", required = true)
    @NotNull(message = "计划生产数量不能为空")
    private BigDecimal qty;

    @Schema(description = "计划开始日期")
    private LocalDate startDate;

    @Schema(description = "计划结束日期")
    private LocalDate endDate;

    @Schema(description = "备注")
    private String remark;
}
