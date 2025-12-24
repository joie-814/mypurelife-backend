package com.purelife.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionResponse {

    private Integer subscriptionId;
    private Integer planId;
    private String subscriptionStatus;
    private Integer quantity;
    private LocalDate nextDeliveryDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;

    // 收件資訊
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String paymentMethod;

    // 方案資訊
    private String cycleType;
    private Integer cycleDays;
    private BigDecimal discountRate;

    // 商品資訊
    private Integer productId;
    private String productName;
    private String category;
    private BigDecimal originalPrice;
    private BigDecimal subscriptionPrice;  
}