package com.purelife.controller.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class ProductRequest {
    private String productName;
    private String category;
    private Integer price;
    private Double promotionPrice;
    private Integer stockQuantity;
    private String description;
    private String imageUrl; 
    private String productStatus;

    // 定期購方案
    private List<SubscriptionPlanRequest> subscriptionPlans;
    @Data
    public static class SubscriptionPlanRequest {
        private Integer planId;      // 編輯時會有值
        private String cycleType;    
        private Integer cycleDays;   // 週期天數
        private Double discountRate; 
    }
}