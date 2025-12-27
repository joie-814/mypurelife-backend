package com.purelife.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Integer productId;
    private String category;
    private String productName;
    private String description;
    private BigDecimal price;
    private BigDecimal promotionPrice;
    private Integer stockQuantity;
    private String productStatus;
    private String imageUrl;

    // 定期購方案
    private List<SubscriptionPlanResponse> subscriptionPlans;
    @Data
    public static class SubscriptionPlanResponse {
        private Integer planId;
        private String cycleType;
        private Integer cycleDays;
        private BigDecimal discountRate;
    }
}
