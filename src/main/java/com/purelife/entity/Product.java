package com.purelife.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("products")
public class Product {

    @Id
    @Column("product_id")
    private Integer productId;

    @Column("category")
    private String category;

    @Column("product_name")
    private String productName;

    @Column("description")
    private String description;

    @Column("price")
    private BigDecimal price;

    @Column("promotion_price")
    private BigDecimal promotionPrice;

    @Column("stock_quantity")
    private Integer stockQuantity;

    @Column("product_status")
    private String productStatus;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("image_url")
    private String imageUrl;

    @Column("sales_count")
    private Integer salesCount;
}