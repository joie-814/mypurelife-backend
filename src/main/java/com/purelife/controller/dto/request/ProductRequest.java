package com.purelife.controller.dto.request;

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
}