package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物流订单DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "物流订单DTO")
public class LogisticsOrderDTO implements Serializable {

    @Schema(description = "物流订单id")
    private Long id;

    @Schema(description = "关联业务类型", required = true)
    @NotNull(message = "关联业务类型不能为空")
    private Integer businessType;

    @Schema(description = "关联业务单id", required = true)
    @NotNull(message = "关联业务单id不能为空")
    private Long businessId;

    @Schema(description = "快递公司名称")
    private String courierName;

    @Schema(description = "快递单号")
    private String courierNo;

    @Schema(description = "发货地址")
    private String sendAddress;

    @Schema(description = "收货地址", required = true)
    @NotBlank(message = "收货地址不能为空")
    private String receiveAddress;

    @Schema(description = "收货人", required = true)
    @NotBlank(message = "收货人不能为空")
    private String receivePerson;

    @Schema(description = "收货人电话", required = true)
    @NotBlank(message = "收货人电话不能为空")
    private String receivePhone;

    @Schema(description = "物流费用")
    private BigDecimal logisticsFee;
}
