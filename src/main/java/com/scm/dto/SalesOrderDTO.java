package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 销售订单DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "销售订单DTO")
public class SalesOrderDTO implements Serializable {

    @Schema(description = "销售订单id")
    private Long id;

    @Schema(description = "客户名称", required = true)
    @NotNull(message = "客户名称不能为空")
    private String customerName;

    @Schema(description = "客户电话")
    private String customerPhone;

    @Schema(description = "发货仓库id", required = true)
    @NotNull(message = "发货仓库不能为空")
    private Long warehouseId;

    @Schema(description = "下单日期")
    private LocalDate orderDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "订单明细", required = true)
    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<SalesOrderDetailDTO> items;

    /**
     * 销售订单明细DTO
     */
    @Data
    @Schema(description = "销售订单明细DTO")
    public static class SalesOrderDetailDTO implements Serializable {

        @Schema(description = "明细id")
        private Long id;

        @Schema(description = "物料id", required = true)
        @NotNull(message = "物料不能为空")
        private Long itemId;

        @Schema(description = "销售数量", required = true)
        @NotNull(message = "销售数量不能为空")
        private BigDecimal qty;

        @Schema(description = "不含税单价")
        private BigDecimal price;

        @Schema(description = "税率%")
        private BigDecimal taxRate;
    }
}
