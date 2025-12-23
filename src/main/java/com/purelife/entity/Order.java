package com.purelife.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("orders")
public class Order {

    @Id
    @Column("order_id")
    private Integer orderId;

    @Column("member_id")
    private Integer memberId;

    @Column("order_number")
    private String orderNumber;

    @Column("order_status")
    private String orderStatus;

    @Column("payment_status")
    private String paymentStatus;

    @Column("total_amount")
    private BigDecimal totalAmount;

    @Column("shipping_fee")
    private BigDecimal shippingFee;

    @Column("recipient_name")
    private String recipientName;

    @Column("recipient_phone")
    private String recipientPhone;

    @Column("recipient_address")
    private String recipientAddress;

    @Column("payment_method")
    private String paymentMethod;

    @Column("order_time")
    private LocalDateTime orderTime;

    @Column("shipping_time")
    private LocalDateTime shippingTime;
}