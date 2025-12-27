package com.purelife.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.purelife.controller.dto.request.AdminLoginRequest;
import com.purelife.controller.dto.response.AdminLoginResponse;
import com.purelife.controller.dto.response.MemberResponse;
import com.purelife.controller.dto.response.OrderResponse;
import com.purelife.controller.dto.response.SubscriptionResponse;
import com.purelife.entity.Admin;
import com.purelife.entity.Member;
import com.purelife.entity.MemberSubscription;
import com.purelife.entity.Order;
import com.purelife.entity.Product;
import com.purelife.entity.SubscriptionPlan;
import com.purelife.repository.AdminRepository;
import com.purelife.repository.MemberRepository;
import com.purelife.repository.MemberSubscriptionRepository;
import com.purelife.repository.OrderRepository;
import com.purelife.repository.ProductRepository;
import com.purelife.repository.SubscriptionPlanRepository;
import com.purelife.util.JwtUtil;
import com.purelife.util.PasswordUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MemberSubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final JwtUtil jwtUtil;

    /**
     * 管理員登入
     */
    public AdminLoginResponse login(AdminLoginRequest request) {
        // 查詢管理員
        Admin admin = adminRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new RuntimeException("帳號或密碼錯誤"));

        // 檢查是否啟用
        if (!admin.getIsActive()) {
            throw new RuntimeException("帳號已被停用");
        }

        // 驗證密碼
        String[] parts = admin.getPasswordHash().split(":");
        String salt = parts[0];
        String storedHash = parts[1];

        if (!PasswordUtil.verifyPassword(request.getPassword(), salt, storedHash)) {
            throw new RuntimeException("帳號或密碼錯誤");
        }

        // 產生 JWT Token（加上 admin 標記）
        String token = jwtUtil.generateToken(admin.getAdminId(), "admin:" + admin.getAccount());

        return AdminLoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .adminId(admin.getAdminId())
                .account(admin.getAccount())
                .name(admin.getName())
                .role(admin.getRole())
                .build();
    }

    /**
     * 取得所有會員
     */
    public List<MemberResponse> getAllMembers() {
        return StreamSupport.stream(memberRepository.findAll().spliterator(), false)
                .map(this::convertMemberToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 取得所有訂單
     */
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByOrderTimeDesc();
        return orders.stream()
                .map(this::convertOrderToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 更新訂單狀態
     */
    @Transactional
    public void updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        
        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    /**
     * 更新付款狀態
     */
    @Transactional
    public void updatePaymentStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        
        order.setPaymentStatus(status);
        orderRepository.save(order);
    }

    /**
     * 取得所有訂閱
     */
    public List<SubscriptionResponse> getAllSubscriptions() {
        return StreamSupport.stream(subscriptionRepository.findAll().spliterator(), false)
                .map(this::convertSubscriptionToResponse)
                .collect(Collectors.toList());
    }

    // ===== 轉換方法 =====

    private MemberResponse convertMemberToResponse(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .account(member.getAccount())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .memberLevel(member.getMemberLevel())
                .registrationTime(member.getRegistrationTime())
                .build();
    }

    private OrderResponse convertOrderToResponse(Order order) {
        // 取得會員名稱
        String memberName = memberRepository.findById(order.getMemberId())
                .map(Member::getName)
                .orElse("未知");

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .grandTotal(order.getTotalAmount().add(order.getShippingFee()))
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .recipientAddress(order.getRecipientAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderTime(order.getOrderTime())
                .shippingTime(order.getShippingTime())
                .build();
    }

    private SubscriptionResponse convertSubscriptionToResponse(MemberSubscription subscription) {
        SubscriptionPlan plan = planRepository.findById(subscription.getPlanId())
                .orElse(null);
        
        Product product = plan != null 
                ? productRepository.findById(plan.getProductId()).orElse(null)
                : null;

        String memberName = memberRepository.findById(subscription.getMemberId())
                .map(Member::getName)
                .orElse("未知");

        return SubscriptionResponse.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .planId(subscription.getPlanId())
                .subscriptionStatus(subscription.getSubscriptionStatus())
                .quantity(subscription.getQuantity())
                .nextDeliveryDate(subscription.getNextDeliveryDate())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .createdAt(subscription.getCreatedAt())
                .recipientName(subscription.getRecipientName())
                .recipientPhone(subscription.getRecipientPhone())
                .recipientAddress(subscription.getRecipientAddress())
                .paymentMethod(subscription.getPaymentMethod())
                .cycleType(plan != null ? plan.getCycleType() : null)
                .cycleDays(plan != null ? plan.getCycleDays() : null)
                .discountRate(plan != null ? plan.getDiscountRate() : null)
                .productId(product != null ? product.getProductId() : null)
                .productName(product != null ? product.getProductName() : null)
                .category(product != null ? product.getCategory() : null)
                .build();
    }
    // ===== 商品管理 =====

    /**
     * 取得所有商品
     */
    public List<Product> getAllProducts() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * 新增商品
     */
    @Transactional
    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        if (product.getProductStatus() == null) {
            product.setProductStatus("available");
        }
        return productRepository.save(product);
    }

    /**
     * 更新商品
     */
    @Transactional
    public Product updateProduct(Integer productId, Product productData) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        product.setCategory(productData.getCategory());
        product.setProductName(productData.getProductName());
        product.setDescription(productData.getDescription());
        product.setPrice(productData.getPrice());
        product.setPromotionPrice(productData.getPromotionPrice());
        product.setStockQuantity(productData.getStockQuantity());
        product.setProductStatus(productData.getProductStatus());
      
        // 更新圖片
        if (productData.getImageUrl() != null) {
            product.setImageUrl("/Users/linchiehying/Desktop/商研院上課/專題/mypureLife/backend/uploads/products/" + productData.getImageUrl());
        }
    
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * 更新商品狀態（上下架）
     */
    @Transactional
    public void updateProductStatus(Integer productId, String status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        product.setProductStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    /**
     * 刪除商品
     */
    @Transactional
    public void deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("商品不存在");
        }
        productRepository.deleteById(productId);
    }
}