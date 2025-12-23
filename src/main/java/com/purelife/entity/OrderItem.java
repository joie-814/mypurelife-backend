package com.purelife.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("order_items")
public class OrderItem {

    @Id
    @Column("item_id")
    private Integer itemId;

    @Column("order_id")
    private Integer orderId;

    @Column("product_id")
    private Integer productId;

    @Column("spec_info")
    private String specInfo;

    @Column("quantity")
    private Integer quantity;

    @Column("unit_price")
    private BigDecimal unitPrice; //下單當時的單價

    @Column("subtotal")
    private BigDecimal subtotal;
}
