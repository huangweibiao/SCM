package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.dto.ProductionOrderDTO;
import com.scm.entity.ProductionOrder;
import com.scm.service.ProductionOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 生产工单Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "生产管理")
@RestController
@RequestMapping("/v1/production")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @Operation(summary = "分页查询生产工单列表")
    @GetMapping("/order")
    public ApiResponse<PageResult<ProductionOrder>> pageQuery(
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<ProductionOrder> result = productionOrderService.pageQuery(
                itemId, status, startDate, endDate, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建生产工单")
    @PostMapping("/order")
    public ApiResponse<Long> createOrder(@Valid @RequestBody ProductionOrderDTO orderDTO) {
        Long id = productionOrderService.createOrder(orderDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新生产工单")
    @PutMapping("/order/{id}")
    public ApiResponse<Boolean> updateOrder(@PathVariable Long id, @Valid @RequestBody ProductionOrderDTO orderDTO) {
        orderDTO.setId(id);
        Boolean result = productionOrderService.updateOrder(orderDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "删除生产工单")
    @DeleteMapping("/order/{id}")
    public ApiResponse<Boolean> deleteOrder(@PathVariable Long id) {
        Boolean result = productionOrderService.deleteOrder(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "开始生产")
    @PutMapping("/order/{id}/start")
    public ApiResponse<Boolean> startProduction(@PathVariable Long id) {
        Boolean result = productionOrderService.startProduction(id);
        return ApiResponse.success(result);
    }

    @Operation(summary = "完工确认")
    @PutMapping("/order/{id}/complete")
    public ApiResponse<Boolean> completeProduction(
            @PathVariable Long id,
            @RequestParam BigDecimal finishedQty,
            @RequestParam Long warehouseId) {
        Boolean result = productionOrderService.completeProduction(id, finishedQty, warehouseId);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询生产工单详情")
    @GetMapping("/order/{id}")
    public ApiResponse<ProductionOrder> getOrder(@PathVariable Long id) {
        ProductionOrder order = productionOrderService.getById(id);
        return ApiResponse.success(order);
    }
}
