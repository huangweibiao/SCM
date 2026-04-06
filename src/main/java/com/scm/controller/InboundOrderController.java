package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.dto.InboundOrderDTO;
import com.scm.entity.InboundOrder;
import com.scm.service.InboundOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 入库单Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "入库单管理")
@RestController
@RequestMapping("/v1/inbound")
@RequiredArgsConstructor
public class InboundOrderController {

    private final InboundOrderService inboundOrderService;

    @Operation(summary = "分页查询入库单列表")
    @GetMapping
    public ApiResponse<PageResult<InboundOrder>> pageQuery(
            @RequestParam(required = false) Long poId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer inboundType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<InboundOrder> result = inboundOrderService.pageQuery(
                poId, warehouseId, inboundType, status, startDate, endDate, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建入库单")
    @PostMapping
    public ApiResponse<Long> createInboundOrder(@Valid @RequestBody InboundOrderDTO inboundOrderDTO) {
        Long id = inboundOrderService.createInboundOrder(inboundOrderDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "确认入库单")
    @PutMapping("/{id}/confirm")
    public ApiResponse<Boolean> confirmInboundOrder(@PathVariable Long id) {
        Boolean result = inboundOrderService.confirmInboundOrder(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "采购收货")
    @PostMapping("/receipt")
    public ApiResponse<Long> purchaseReceipt(
            @RequestParam Long poId,
            @Valid @RequestBody InboundOrderDTO inboundOrderDTO) {
        Long id = inboundOrderService.purchaseReceipt(poId, inboundOrderDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "查询入库单详情")
    @GetMapping("/{id}")
    public ApiResponse<InboundOrder> getInboundOrder(@PathVariable Long id) {
        InboundOrder inboundOrder = inboundOrderService.getById(id);
        return ApiResponse.success(inboundOrder);
    }
}
