package com.purelife.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.purelife.controller.dto.request.CreateOrderRequest;
import com.purelife.controller.dto.response.ApiResponse;
import com.purelife.controller.dto.response.OrderResponse;
import com.purelife.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 從 SecurityContext 取得當前登入的 memberId
     */
    private Integer getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("請先登入");
        }
        return (Integer) authentication.getPrincipal();
    }

    /**
     * 建立訂單
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        Integer memberId = getCurrentMemberId();
        OrderResponse order = orderService.createOrder(memberId, request);
        return ResponseEntity.ok(ApiResponse.success("訂單建立成功", order));
    }

    /**
     * 取得會員的所有訂單
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders() {
        Integer memberId = getCurrentMemberId();
        List<OrderResponse> orders = orderService.getOrders(memberId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * 取得單筆訂單詳情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(
            @PathVariable Integer orderId) {

        Integer memberId = getCurrentMemberId();
        OrderResponse order = orderService.getOrderDetail(memberId, orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
}
