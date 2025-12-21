package com.purelife.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
}
