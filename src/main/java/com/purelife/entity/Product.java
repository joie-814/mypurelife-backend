package com.purelife.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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


    // 安全的 getter，避免 null 造成排序錯誤
    @Column("sales_count")
    private Integer salesCount;
        public Integer getSalesCount() {
        return salesCount != null ? salesCount : 0;
    }

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public String getProductStatus() {
        return productStatus != null ? productStatus : "available";
    }
}