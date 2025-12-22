package com.purelife.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("carts")
public class Cart {

    @Id
    @Column("cart_id")
    private Integer cartId;

    @Column("member_id")
    private Integer memberId;

    @Column("product_id")
    private Integer productId;

    @Column("quantity")
    private Integer quantity;

    @Column("added_time")
    private LocalDateTime addedTime;
}
