//給商品頁用的訂閱方案回傳DTO
package com.purelife.controller.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionPlanResponse {

    private Integer planId;
    private Integer productId;
    private String cycleType;
    private Integer cycleDays;
    private BigDecimal discountRate;
    private String cycleText;           
    private BigDecimal originalPrice;   // 原價
    private BigDecimal subscriptionPrice; // 訂閱價
}