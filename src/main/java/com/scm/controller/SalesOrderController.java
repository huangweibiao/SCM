package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.dto.OutboundOrderDTO;
import com.scm.dto.SalesOrderDTO;
import com.scm.entity.OutboundOrder;
import com.scm.entity.SalesOrder;
import com.scm.entity.SalesOrderDetail;
import com.scm.service.OutboundOrderService;
import com.scm.service.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 销售管理Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "销售管理")
@RestController
@RequestMapping("/v1/sales")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @Operation(summary = "分页查询销售订单列表")
    @GetMapping("/order")
    public ApiResponse<PageResult<SalesOrder>> pageQuery(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String soNo,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SalesOrder> result = salesOrderService.pageQuery(
                customerName, status, soNo, startDate, endDate, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建销售订单")
    @PostMapping("/order")
    public ApiResponse<Long> createOrder(@Valid @RequestBody SalesOrderDTO orderDTO) {
        Long id = salesOrderService.createOrder(orderDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新销售订单")
    @PutMapping("/order/{id}")
    public ApiResponse<Boolean> updateOrder(@PathVariable Long id, @Valid @RequestBody SalesOrderDTO orderDTO) {
        orderDTO.setId(id);
        Boolean result = salesOrderService.updateOrder(orderDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除销售订单")
    @DeleteMapping("/order/{id}")
    public ApiResponse<Boolean> deleteOrder(@PathVariable Long id) {
        Boolean result = salesOrderService.deleteOrder(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "审核销售订单")
    @PutMapping("/order/{id}/audit")
    public ApiResponse<Boolean> auditOrder(@PathVariable Long id, @RequestParam Long auditBy) {
        Boolean result = salesOrderService.auditOrder(id, auditBy);
        return ApiResponse.success(result);
    }

    @Operation(summary = "关闭销售订单")
    @PutMapping("/order/{id}/close")
    public ApiResponse<Boolean> closeOrder(@PathVariable Long id) {
        Boolean result = salesOrderService.closeOrder(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询销售订单详情")
    @GetMapping("/order/{id}")
    public ApiResponse<SalesOrder> getOrder(@PathVariable Long id) {
        SalesOrder order = salesOrderService.getDetailWithItems(id);
        return ApiResponse.success(order);
    }

    @Operation(summary = "查询销售订单明细列表")
    @GetMapping("/order/{id}/details")
    public ApiResponse<List<SalesOrderDetail>> getOrderDetails(@PathVariable Long id) {
        List<SalesOrderDetail> details = salesOrderService.getDetailList(id);
        return ApiResponse.success(details);
    }

    @Operation(summary = "销售发货")
    @PostMapping("/shipment")
    public ApiResponse<Long> salesShipment(
            @RequestParam Long soId,
            @Valid @RequestBody OutboundOrderDTO outboundOrderDTO) {
        Long id = salesOrderService.salesShipment(soId, outboundOrderDTO);
        return ApiResponse.success(id);
    }
}
