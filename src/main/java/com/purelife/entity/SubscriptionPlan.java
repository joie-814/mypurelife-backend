package com.purelife.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("subscription_plans")
public class SubscriptionPlan {

    @Id
    @Column("plan_id")
    private Integer planId;

    @Column("product_id")
    private Integer productId;

    @Column("cycle_type")
    private String cycleType;  // monthly, quarterly, biannual

    @Column("cycle_days")
    private Integer cycleDays;  // 配送週期天數

    @Column("discount_rate")
    private BigDecimal discountRate;  // 折扣比例，如 0.10 代表 9 折

    @Column("created_at")
    private LocalDateTime createdAt;
}
