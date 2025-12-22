package com.purelife.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.purelife.controller.dto.request.AddToCartRequest;
import com.purelife.controller.dto.response.ApiResponse;
import com.purelife.controller.dto.response.CartItemResponse;
import com.purelife.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

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
     * 取得購物車列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart() {
        Integer memberId = getCurrentMemberId();
        List<CartItemResponse> items = cartService.getCartItems(memberId);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    /**
     * 取得購物車總金額
     */
    @GetMapping("/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getCartTotal() {
        Integer memberId = getCurrentMemberId();
        BigDecimal total = cartService.getCartTotal(memberId);
        return ResponseEntity.ok(ApiResponse.success(total));
    }

    /**
     * 新增商品到購物車
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request) {
        Integer memberId = getCurrentMemberId();
        CartItemResponse item = cartService.addToCart(memberId, request);
        return ResponseEntity.ok(ApiResponse.success("已加入購物車", item));
    }

    /**
     * 更新購物車數量
     */
    @PutMapping("/{cartId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            @PathVariable Integer cartId,
            @RequestParam Integer quantity) {

        if (quantity < 1) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("數量不能小於 1"));
        }

        Integer memberId = getCurrentMemberId();
        CartItemResponse item = cartService.updateQuantity(memberId, cartId, quantity);
        return ResponseEntity.ok(ApiResponse.success("數量已更新", item));
    }

    /**
     * 刪除購物車項目
     */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable Integer cartId) {
        Integer memberId = getCurrentMemberId();
        cartService.removeFromCart(memberId, cartId);
        return ResponseEntity.ok(ApiResponse.success("已從購物車移除", null));
    }

    /**
     * 清空購物車
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        Integer memberId = getCurrentMemberId();
        cartService.clearCart(memberId);
        return ResponseEntity.ok(ApiResponse.success("購物車已清空", null));
    }
}