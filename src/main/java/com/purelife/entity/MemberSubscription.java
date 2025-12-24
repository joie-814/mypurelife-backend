package com.purelife.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("member_subscriptions")
public class MemberSubscription {

    @Id
    @Column("subscription_id")
    private Integer subscriptionId;

    @Column("member_id")
    private Integer memberId;

    @Column("plan_id")
    private Integer planId;

    @Column("quantity")
    private Integer quantity;

    @Column("recipient_name")
    private String recipientName;

    @Column("recipient_phone")
    private String recipientPhone;

    @Column("recipient_address")
    private String recipientAddress;
    
    @Column("payment_method")
    private String paymentMethod;

    @Column("subscription_status")
    private String subscriptionStatus;  // active, paused, cancelled

    @Column("next_delivery_date")
    private LocalDate nextDeliveryDate;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("created_at")
    private LocalDateTime createdAt;
}
