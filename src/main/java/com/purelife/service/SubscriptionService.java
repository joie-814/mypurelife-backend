package com.purelife.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.purelife.controller.dto.request.CreateSubscriptionRequest;
import com.purelife.controller.dto.response.SubscriptionPlanResponse;
import com.purelife.controller.dto.response.SubscriptionResponse;
import com.purelife.entity.MemberSubscription;
import com.purelife.entity.Product;
import com.purelife.entity.SubscriptionPlan;
import com.purelife.repository.MemberSubscriptionRepository;
import com.purelife.repository.ProductRepository;
import com.purelife.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final MemberSubscriptionRepository subscriptionRepository;
    private final ProductRepository productRepository;

    /**
     * 取得商品的訂閱方案
     */
    public List<SubscriptionPlanResponse> getProductPlans(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        List<SubscriptionPlan> plans = planRepository.findByProductId(productId);

        // 取得實際價格（有促銷價就用促銷價）
        BigDecimal basePrice = product.getPromotionPrice() != null 
                ? product.getPromotionPrice() 
                : product.getPrice();

        return plans.stream()
                .map(plan -> {
                    BigDecimal subscriptionPrice = calculateSubscriptionPrice(basePrice, plan.getDiscountRate());
                    
                    return SubscriptionPlanResponse.builder()
                            .planId(plan.getPlanId())
                            .productId(plan.getProductId())
                            .cycleType(plan.getCycleType())
                            .cycleDays(plan.getCycleDays())
                            .discountRate(plan.getDiscountRate())
                            .cycleText(getCycleText(plan.getCycleType()))
                            .originalPrice(basePrice)
                            .subscriptionPrice(subscriptionPrice)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public SubscriptionPlanResponse getPlanById(Integer planId) {
        Optional<SubscriptionPlan> planOpt = planRepository.findById(planId);
        if (planOpt.isEmpty()) {
            return null; // 找不到方案就回 null
        }

        SubscriptionPlan plan = planOpt.get();

        Optional<Product> productOpt = productRepository.findById(plan.getProductId());
        if (productOpt.isEmpty()) {
            return null; // 找不到商品也回 null
        }

        Product product = productOpt.get();

        BigDecimal basePrice = product.getPromotionPrice() != null
                ? product.getPromotionPrice()
                : product.getPrice();

        BigDecimal subscriptionPrice = calculateSubscriptionPrice(basePrice, plan.getDiscountRate());

        return SubscriptionPlanResponse.builder()
                .planId(plan.getPlanId())
                .productId(plan.getProductId())
                .cycleType(plan.getCycleType())
                .cycleDays(plan.getCycleDays())
                .discountRate(plan.getDiscountRate())
                .cycleText(getCycleText(plan.getCycleType()))
                .originalPrice(basePrice)
                .subscriptionPrice(subscriptionPrice)
                .build();
    }

    /**
     * 建立訂閱
     */
    @Transactional
    public SubscriptionResponse createSubscription(Integer memberId, CreateSubscriptionRequest request) {
        // 1. 檢查方案是否存在
        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("訂閱方案不存在"));

        // 2. 檢查是否已訂閱此方案
        subscriptionRepository.findByMemberIdAndPlanId(memberId, request.getPlanId())
                .ifPresent(s -> {
                    throw new RuntimeException("您已訂閱此方案，請先取消後再重新訂閱");
                });

        // 3. 建立訂閱
        MemberSubscription subscription = new MemberSubscription();
        subscription.setMemberId(memberId);
        subscription.setPlanId(request.getPlanId());
        subscription.setQuantity(request.getQuantity());
        subscription.setRecipientName(request.getRecipientName());
        subscription.setRecipientPhone(request.getRecipientPhone());
        subscription.setRecipientAddress(request.getRecipientAddress());
        subscription.setPaymentMethod(request.getPaymentMethod());
        subscription.setSubscriptionStatus("active");
        subscription.setStartDate(LocalDate.now());
        subscription.setNextDeliveryDate(LocalDate.now().plusDays(plan.getCycleDays()));
        subscription.setCreatedAt(LocalDateTime.now());

        MemberSubscription saved = subscriptionRepository.save(subscription);

        return convertToResponse(saved);
    }

    /**
     * 取得會員的訂閱列表
     */
    public List<SubscriptionResponse> getMySubscriptions(Integer memberId) {
        List<MemberSubscription> subscriptions = subscriptionRepository.findActiveByMemberId(memberId);

        return subscriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 暫停訂閱
     */
    @Transactional
    public SubscriptionResponse pauseSubscription(Integer memberId, Integer subscriptionId) {
        MemberSubscription subscription = subscriptionRepository
                .findBySubscriptionIdAndMemberId(subscriptionId, memberId)
                .orElseThrow(() -> new RuntimeException("訂閱不存在"));

        if (!"active".equals(subscription.getSubscriptionStatus())) {
            throw new RuntimeException("只有啟用中的訂閱可以暫停");
        }

        subscription.setSubscriptionStatus("paused");
        MemberSubscription saved = subscriptionRepository.save(subscription);

        return convertToResponse(saved);
    }

    /**
     * 恢復訂閱
     */
    @Transactional
    public SubscriptionResponse resumeSubscription(Integer memberId, Integer subscriptionId) {
        MemberSubscription subscription = subscriptionRepository
                .findBySubscriptionIdAndMemberId(subscriptionId, memberId)
                .orElseThrow(() -> new RuntimeException("訂閱不存在"));

        if (!"paused".equals(subscription.getSubscriptionStatus())) {
            throw new RuntimeException("只有暫停中的訂閱可以恢復");
        }

        // 恢復時重新計算下次配送日
        SubscriptionPlan plan = planRepository.findById(subscription.getPlanId())
                .orElseThrow(() -> new RuntimeException("訂閱方案不存在"));

        subscription.setSubscriptionStatus("active");
        subscription.setNextDeliveryDate(LocalDate.now().plusDays(plan.getCycleDays()));
        MemberSubscription saved = subscriptionRepository.save(subscription);

        return convertToResponse(saved);
    }

    /**
     * 取消訂閱
     */
    @Transactional
    public SubscriptionResponse cancelSubscription(Integer memberId, Integer subscriptionId) {
        MemberSubscription subscription = subscriptionRepository
                .findBySubscriptionIdAndMemberId(subscriptionId, memberId)
                .orElseThrow(() -> new RuntimeException("訂閱不存在"));

        if ("cancelled".equals(subscription.getSubscriptionStatus())) {
            throw new RuntimeException("此訂閱已取消");
        }

        subscription.setSubscriptionStatus("cancelled");
        subscription.setEndDate(LocalDate.now());
        MemberSubscription saved = subscriptionRepository.save(subscription);

        return convertToResponse(saved);
    }

    /**
     * 計算訂閱價格
     */
    private BigDecimal calculateSubscriptionPrice(BigDecimal originalPrice, BigDecimal discountRate) {
        if (discountRate == null) {
            return originalPrice;
        }
        // 直接乘就好：原價 × 0.95 = 95折價格
        return originalPrice.multiply(discountRate).setScale(0, RoundingMode.FLOOR);
    }

    /**
     * 取得週期顯示文字
     */
    private String getCycleText(String cycleType) {
        switch (cycleType) {
            case "monthly":
                return "每月配送";
            case "quarterly":
                return "每三個月配送";
            case "biannual":
                return "每六個月配送";
            default:
                return cycleType;
        }
    }

    /**
     * 轉換為回傳 DTO
     */
    private SubscriptionResponse convertToResponse(MemberSubscription subscription) {
        SubscriptionPlan plan = planRepository.findById(subscription.getPlanId())
                .orElseThrow(() -> new RuntimeException("訂閱方案不存在"));

        Product product = productRepository.findById(plan.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        BigDecimal basePrice = product.getPromotionPrice() != null 
                ? product.getPromotionPrice() 
                : product.getPrice();

        BigDecimal subscriptionPrice = calculateSubscriptionPrice(basePrice, plan.getDiscountRate());

        return SubscriptionResponse.builder()
            .subscriptionId(subscription.getSubscriptionId())
            .planId(subscription.getPlanId())
            .subscriptionStatus(subscription.getSubscriptionStatus())
            .quantity(subscription.getQuantity())
            .nextDeliveryDate(subscription.getNextDeliveryDate())
            .startDate(subscription.getStartDate())
            .endDate(subscription.getEndDate())
            .createdAt(subscription.getCreatedAt())
            // 收件資訊
            .recipientName(subscription.getRecipientName())
            .recipientPhone(subscription.getRecipientPhone())
            .recipientAddress(subscription.getRecipientAddress())
            .paymentMethod(subscription.getPaymentMethod())
            // 方案資訊
            .cycleType(plan.getCycleType())
            .cycleDays(plan.getCycleDays())
            .discountRate(plan.getDiscountRate())
            // 商品資訊
            .productId(product.getProductId())
            .productName(product.getProductName())
            .category(product.getCategory())
            .originalPrice(basePrice)
            .subscriptionPrice(subscriptionPrice)
            .build();
    }
}