package com.purelife.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.purelife.controller.dto.request.CreateOrderRequest;
import com.purelife.controller.dto.response.CartItemResponse;
import com.purelife.controller.dto.response.OrderResponse;
import com.purelife.entity.Order;
import com.purelife.entity.OrderItem;
import com.purelife.entity.Product;
import com.purelife.repository.OrderItemRepository;
import com.purelife.repository.OrderRepository;
import com.purelife.repository.ProductRepository;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@Builder
@Data
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    // 運費門檻
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("1200");
    private static final BigDecimal SHIPPING_FEE = new BigDecimal("60");

    /**
     * 建立訂單
     */
    @Transactional
    public OrderResponse createOrder(Integer memberId, CreateOrderRequest request) {
        // 1. 取得購物車內容
        List<CartItemResponse> cartItems = cartService.getCartItems(memberId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("購物車是空的");
        }

        // 2. 計算金額
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItemResponse::getSubtotal) // 把 cartItems 裡每個 CartItemResponse 的 getSubtotal() 取出來
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 把所有小計依序相加，初始值是 BigDecimal.ZERO

        BigDecimal shippingFee = totalAmount.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                ? BigDecimal.ZERO
                : SHIPPING_FEE;

        // 3. 產生訂單編號
        String orderNumber = generateOrderNumber();

        // 4. 建立訂單
        Order order = new Order();
        order.setMemberId(memberId);
        order.setOrderNumber(orderNumber);
        order.setOrderStatus("pending");        // 待處理
        order.setPaymentStatus("unpaid");       // 未付款
        order.setTotalAmount(totalAmount);
        order.setShippingFee(shippingFee);
        order.setRecipientName(request.getRecipientName());
        order.setRecipientPhone(request.getRecipientPhone());
        order.setRecipientAddress(request.getRecipientAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 5. 建立訂單明細
        for (CartItemResponse cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getOrderId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getActualPrice());
            orderItem.setSubtotal(cartItem.getSubtotal());

            orderItemRepository.save(orderItem);
        }

        // 6. 清空購物車
        cartService.clearCart(memberId);

        // 7. 回傳訂單資訊
        return convertToResponse(savedOrder);
    }

    /**
     * 取得會員的所有訂單
     */
    public List<OrderResponse> getOrders(Integer memberId) {
        List<Order> orders = orderRepository.findByMemberIdOrderByOrderTimeDesc(memberId);

        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 取得單筆訂單詳情
     */
    public OrderResponse getOrderDetail(Integer memberId, Integer orderId) {
        Order order = orderRepository.findByOrderIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));

        return convertToResponse(order);
    }

    /**
     * 產生訂單編號：PL + 日期 + 隨機碼
     */
    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PL" + date + random;
    }

    /**
     * 轉換為回傳 DTO
     */
    private OrderResponse convertToResponse(Order order) {
        // 取得訂單明細
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrderId());

        List<OrderResponse.OrderItemResponse> itemResponses = items.stream().map(item -> {
            // 取得商品名稱
            String productName = productRepository.findById(item.getProductId())
                    .map(Product::getProductName)
                    .orElse("未知商品");

            return OrderResponse.OrderItemResponse.builder()
                    .productId(item.getProductId())
                    .productName(productName)
                    .specInfo(item.getSpecInfo())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subtotal(item.getSubtotal())
                    .build();
        }).collect(Collectors.toList());

        BigDecimal grandTotal = order.getTotalAmount().add(order.getShippingFee());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .grandTotal(grandTotal)
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .recipientAddress(order.getRecipientAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderTime(order.getOrderTime())
                .shippingTime(order.getShippingTime())
                .items(itemResponses)
                .build();
    }
}