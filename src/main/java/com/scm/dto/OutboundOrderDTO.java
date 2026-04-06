package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "出库单DTO")
public class OutboundOrderDTO implements Serializable {

    @Schema(description = "出库单id")
    private Long id;

    @Schema(description = "关联销售订单id")
    private Long soId;

    @Schema(description = "关联生产工单id")
    private Long moId;

    @Schema(description = "出库仓库id", required = true)
    @NotNull(message = "出库仓库不能为空")
    private Long warehouseId;

    @Schema(description = "出库类型", required = true)
    @NotNull(message = "出库类型不能为空")
    private Integer outboundType;

    @Schema(description = "出库日期", required = true)
    @NotNull(message = "出库日期不能为空")
    private LocalDateTime outboundDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "出库明细", required = true)
    @NotEmpty(message = "出库明细不能为空")
    @Valid
    private List<OutboundOrderDetailDTO> items;

    /**
     * 出库单明细DTO
     */
    @Data
    @Schema(description = "出库单明细DTO")
    public static class OutboundOrderDetailDTO implements Serializable {

        @Schema(description = "明细id")
        private Long id;

        @Schema(description = "物料id", required = true)
        @NotNull(message = "物料不能为空")
        private Long itemId;

        @Schema(description = "关联销售订单明细id")
        private Long soDetailId;

        @Schema(description = "出库数量", required = true)
        @NotNull(message = "出库数量不能为空")
        private BigDecimal qty;

        @Schema(description = "出库单价")
        private BigDecimal price;

        @Schema(description = "物料批次号")
        private String batchNo;
    }
}
