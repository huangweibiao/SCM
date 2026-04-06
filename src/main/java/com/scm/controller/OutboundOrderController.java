package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.dto.OutboundOrderDTO;
import com.scm.entity.OutboundOrder;
import com.scm.service.OutboundOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 出库单Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "出库单管理")
@RestController
@RequestMapping("/v1/outbound")
@RequiredArgsConstructor
public class OutboundOrderController {

    private final OutboundOrderService outboundOrderService;

    @Operation(summary = "分页查询出库单列表")
    @GetMapping
    public ApiResponse<PageResult<OutboundOrder>> pageQuery(
            @RequestParam(required = false) Long soId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer outboundType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<OutboundOrder> result = outboundOrderService.pageQuery(
                soId, warehouseId, outboundType, status, startDate, endDate, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建出库单")
    @PostMapping
    public ApiResponse<Long> createOutboundOrder(@Valid @RequestBody OutboundOrderDTO outboundOrderDTO) {
        Long id = outboundOrderService.createOutboundOrder(outboundOrderDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "确认出库单")
    @PutMapping("/{id}/confirm")
    public ApiResponse<Boolean> confirmOutboundOrder(@PathVariable Long id) {
        Boolean result = outboundOrderService.confirmOutboundOrder(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询出库单详情")
    @GetMapping("/{id}")
    public ApiResponse<OutboundOrder> getOutboundOrder(@PathVariable Long id) {
        OutboundOrder outboundOrder = outboundOrderService.getById(id);
        return ApiResponse.success(outboundOrder);
    }
}
