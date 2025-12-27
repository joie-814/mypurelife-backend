package com.purelife.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.purelife.controller.dto.request.CreateSubscriptionRequest;
import com.purelife.controller.dto.response.ApiResponse;
import com.purelife.controller.dto.response.SubscriptionPlanResponse;
import com.purelife.controller.dto.response.SubscriptionResponse;
import com.purelife.service.SubscriptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * 從 SecurityContext 取得當前登入的 memberId
     */
    private Integer getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("請先登入");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Integer) {
            return (Integer) principal;
        } else if (principal instanceof Long) {
            return ((Long) principal).intValue();
        } else if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                throw new RuntimeException("無法解析會員ID");
            }
        } else {
            throw new RuntimeException("無法取得會員資訊");
        }
    }

    /**
     * 取得商品的訂閱方案（公開 API）
     */
    @GetMapping("/plans/{productId}")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponse>>> getProductPlans(
            @PathVariable Integer productId) {
        List<SubscriptionPlanResponse> plans = subscriptionService.getProductPlans(productId);
        if (plans == null || plans.isEmpty()) {
        return ResponseEntity.status(404)
                .body(ApiResponse.error("找不到該商品的訂閱方案"));
        }
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/plans/byPlanId/{planId}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> getPlanById(
            @PathVariable Integer planId) {

        SubscriptionPlanResponse plan = subscriptionService.getPlanById(planId);

        if (plan == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("找不到該方案"));
        }

        return ResponseEntity.ok(ApiResponse.success(plan));
    }
    /**
     * 建立訂閱
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request) {

        Integer memberId = getCurrentMemberId();
        SubscriptionResponse subscription = subscriptionService.createSubscription(memberId, request);
        return ResponseEntity.ok(ApiResponse.success("訂閱成功", subscription));
    }

    /**
     * 取得我的訂閱列表
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getMySubscriptions() {
        Integer memberId = getCurrentMemberId();
        List<SubscriptionResponse> subscriptions = subscriptionService.getMySubscriptions(memberId);
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }

    /**
     * 暫停訂閱
     */
    @PutMapping("/{subscriptionId}/pause")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> pauseSubscription(
            @PathVariable Integer subscriptionId) {

        Integer memberId = getCurrentMemberId();
        SubscriptionResponse subscription = subscriptionService.pauseSubscription(memberId, subscriptionId);
        return ResponseEntity.ok(ApiResponse.success("訂閱已暫停", subscription));
    }

    /**
     * 恢復訂閱
     */
    @PutMapping("/{subscriptionId}/resume")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> resumeSubscription(
            @PathVariable Integer subscriptionId) {

        Integer memberId = getCurrentMemberId();
        SubscriptionResponse subscription = subscriptionService.resumeSubscription(memberId, subscriptionId);
        return ResponseEntity.ok(ApiResponse.success("訂閱已恢復", subscription));
    }

    /**
     * 取消訂閱
     */
    @PutMapping("/{subscriptionId}/cancel")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancelSubscription(
            @PathVariable Integer subscriptionId) {

        Integer memberId = getCurrentMemberId();
        SubscriptionResponse subscription = subscriptionService.cancelSubscription(memberId, subscriptionId);
        return ResponseEntity.ok(ApiResponse.success("訂閱已取消", subscription));
    }
}