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
 * 入库单DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "入库单DTO")
public class InboundOrderDTO implements Serializable {

    @Schema(description = "入库单id")
    private Long id;

    @Schema(description = "关联采购订单id")
    private Long poId;

    @Schema(description = "关联生产工单id")
    private Long moId;

    @Schema(description = "入库仓库id", required = true)
    @NotNull(message = "入库仓库不能为空")
    private Long warehouseId;

    @Schema(description = "入库类型", required = true)
    @NotNull(message = "入库类型不能为空")
    private Integer inboundType;

    @Schema(description = "入库日期", required = true)
    @NotNull(message = "入库日期不能为空")
    private LocalDateTime inboundDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "入库明细", required = true)
    @NotEmpty(message = "入库明细不能为空")
    @Valid
    private List<InboundOrderDetailDTO> items;

    /**
     * 入库单明细DTO
     */
    @Data
    @Schema(description = "入库单明细DTO")
    public static class InboundOrderDetailDTO implements Serializable {

        @Schema(description = "明细id")
        private Long id;

        @Schema(description = "物料id", required = true)
        @NotNull(message = "物料不能为空")
        private Long itemId;

        @Schema(description = "关联采购订单明细id")
        private Long poDetailId;

        @Schema(description = "入库数量", required = true)
        @NotNull(message = "入库数量不能为空")
        private BigDecimal qty;

        @Schema(description = "入库单价")
        private BigDecimal price;

        @Schema(description = "物料批次号")
        private String batchNo;

        @Schema(description = "物料过期日期")
        private java.time.LocalDate expireDate;
    }
}
