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
 * 采购订单DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "采购订单DTO")
public class PurchaseOrderDTO implements Serializable {

    @Schema(description = "采购订单id")
    private Long id;

    @Schema(description = "供应商id", required = true)
    @NotNull(message = "供应商不能为空")
    private Long supplierId;

    @Schema(description = "收货仓库id", required = true)
    @NotNull(message = "收货仓库不能为空")
    private Long warehouseId;

    @Schema(description = "下单日期", required = true)
    @NotNull(message = "下单日期不能为空")
    private LocalDate orderDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "订单明细", required = true)
    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<PurchaseOrderDetailDTO> items;

    /**
     * 采购订单明细DTO
     */
    @Data
    @Schema(description = "采购订单明细DTO")
    public static class PurchaseOrderDetailDTO implements Serializable {

        @Schema(description = "明细id")
        private Long id;

        @Schema(description = "物料id", required = true)
        @NotNull(message = "物料不能为空")
        private Long itemId;

        @Schema(description = "采购数量", required = true)
        @NotNull(message = "采购数量不能为空")
        private BigDecimal qty;

        @Schema(description = "不含税单价")
        private BigDecimal price;

        @Schema(description = "税率%")
        private BigDecimal taxRate;
    }
}
