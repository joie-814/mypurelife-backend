package com.purelife.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

    private Integer orderId;
    private String orderNumber;
    private String orderStatus;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal grandTotal;      // 總計（含運費）
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String paymentMethod;
    private LocalDateTime orderTime;
    private LocalDateTime shippingTime;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    public static class OrderItemResponse {
        private Integer productId;
        private String productName;
        private String specInfo;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}