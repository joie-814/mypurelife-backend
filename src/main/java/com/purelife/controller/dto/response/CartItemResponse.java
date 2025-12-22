package com.purelife.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private Integer cartId;
    private Integer productId;
    private String productName;
    private String category;
    private BigDecimal price;           // 原價
    private BigDecimal promotionPrice;  // 促銷價（可能為 null）
    private BigDecimal actualPrice;     // 實際單價（有促銷價就用促銷價）
    private Integer quantity;
    private BigDecimal subtotal;        // 小計 = actualPrice * quantity
    private String productStatus;
    private Integer stockQuantity;      // 庫存（讓前端判斷是否超買）
    private LocalDateTime addedTime;
    private String imageUrl;
}
