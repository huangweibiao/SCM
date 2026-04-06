package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.dto.PurchaseOrderDTO;
import com.scm.entity.PurchaseOrder;
import com.scm.entity.PurchaseOrderDetail;
import com.scm.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购订单Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "采购管理")
@RestController
@RequestMapping("/v1/purchase")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Operation(summary = "分页查询采购订单列表")
    @GetMapping("/order")
    public ApiResponse<PageResult<PurchaseOrder>> pageQuery(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String poNo,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<PurchaseOrder> result = purchaseOrderService.pageQuery(
                supplierId, status, poNo, startDate, endDate, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建采购订单")
    @PostMapping("/order")
    public ApiResponse<Long> createOrder(@Valid @RequestBody PurchaseOrderDTO orderDTO) {
        Long id = purchaseOrderService.createOrder(orderDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新采购订单")
    @PutMapping("/order/{id}")
    public ApiResponse<Boolean> updateOrder(@PathVariable Long id, @Valid @RequestBody PurchaseOrderDTO orderDTO) {
        orderDTO.setId(id);
        Boolean result = purchaseOrderService.updateOrder(orderDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除采购订单")
    @DeleteMapping("/order/{id}")
    public ApiResponse<Boolean> deleteOrder(@PathVariable Long id) {
        Boolean result = purchaseOrderService.deleteOrder(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "审核采购订单")
    @PutMapping("/order/{id}/audit")
    public ApiResponse<Boolean> auditOrder(@PathVariable Long id, @RequestParam Long auditBy) {
        Boolean result = purchaseOrderService.auditOrder(id, auditBy);
        return ApiResponse.success(result);
    }

    @Operation(summary = "关闭采购订单")
    @PutMapping("/order/{id}/close")
    public ApiResponse<Boolean> closeOrder(@PathVariable Long id) {
        Boolean result = purchaseOrderService.closeOrder(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询采购订单详情")
    @GetMapping("/order/{id}")
    public ApiResponse<PurchaseOrder> getOrder(@PathVariable Long id) {
        PurchaseOrder order = purchaseOrderService.getDetailWithItems(id);
        return ApiResponse.success(order);
    }

    @Operation(summary = "查询采购订单明细列表")
    @GetMapping("/order/{id}/details")
    public ApiResponse<List<PurchaseOrderDetail>> getOrderDetails(@PathVariable Long id) {
        List<PurchaseOrderDetail> details = purchaseOrderService.getDetailList(id);
        return ApiResponse.success(details);
    }
}
