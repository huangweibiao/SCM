package com.scm.controller;

import com.scm.common.result.ApiResponse;
import com.scm.common.result.PageResult;
import com.scm.dto.LogisticsOrderDTO;
import com.scm.entity.LogisticsOrder;
import com.scm.service.LogisticsOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 物流订单Controller
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Tag(name = "物流管理")
@RestController
@RequestMapping("/v1/logistics")
@RequiredArgsConstructor
public class LogisticsOrderController {

    private final LogisticsOrderService logisticsOrderService;

    @Operation(summary = "分页查询物流订单列表")
    @GetMapping("/order")
    public ApiResponse<PageResult<LogisticsOrder>> pageQuery(
            @RequestParam(required = false) Integer businessType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<LogisticsOrder> result = logisticsOrderService.pageQuery(
                businessType, status, startDate, endDate, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Operation(summary = "创建物流订单")
    @PostMapping("/order")
    public ApiResponse<Long> createOrder(@Valid @RequestBody LogisticsOrderDTO orderDTO) {
        Long id = logisticsOrderService.createOrder(orderDTO);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新物流订单")
    @PutMapping("/order/{id}")
    public ApiResponse<Boolean> updateOrder(@PathVariable Long id, @Valid @RequestBody LogisticsOrderDTO orderDTO) {
        orderDTO.setId(id);
        Boolean result = logisticsOrderService.updateOrder(orderDTO);
        return ApiResponse.success(result);
    }

    @Operation(summary = "更新物流状态")
    @PutMapping("/order/{id}/status")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Boolean result = logisticsOrderService.updateStatus(id, status);
        return ApiResponse.success(result);
    }

    @Operation(summary = "查询物流订单详情")
    @GetMapping("/order/{id}")
    public ApiResponse<LogisticsOrder> getOrder(@PathVariable Long id) {
        LogisticsOrder order = logisticsOrderService.getById(id);
        return ApiResponse.success(order);
    }
}
