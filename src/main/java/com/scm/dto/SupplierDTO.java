package com.scm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 供应商DTO
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Data
@Schema(description = "供应商DTO")
public class SupplierDTO implements Serializable {

    @Schema(description = "供应商id")
    private Long id;

    @Schema(description = "供应商编码")
    private String supplierCode;

    @Schema(description = "供应商名称", required = true)
    @NotBlank(message = "供应商名称不能为空")
    private String supplierName;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "付款条件")
    private String paymentTerms;

    @Schema(description = "供应商评级（1-5星）")
    private Integer rating;
}
