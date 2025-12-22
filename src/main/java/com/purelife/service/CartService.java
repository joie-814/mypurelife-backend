package com.purelife.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.purelife.controller.dto.request.AddToCartRequest;
import com.purelife.controller.dto.response.CartItemResponse;
import com.purelife.entity.Cart;
import com.purelife.entity.Product;
import com.purelife.repository.CartRepository;
import com.purelife.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * 取得會員的購物車列表
     */
    public List<CartItemResponse> getCartItems(Integer memberId) {
        List<Cart> carts = cartRepository.findByMemberId(memberId);

        return carts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 新增商品到購物車
     */
    @Transactional
    public CartItemResponse addToCart(Integer memberId, AddToCartRequest request) {
        // 1. 檢查商品是否存在
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 2. 檢查商品狀態
        if (!"available".equals(product.getProductStatus())) {
            throw new RuntimeException("商品目前無法購買");
        }

        // 3. 檢查是否已在購物車中
        Cart cart = cartRepository.findByMemberIdAndProductId(memberId, request.getProductId())
                .orElse(null);

        if (cart != null) {
            // 已存在，增加數量
            cart.setQuantity(cart.getQuantity() + request.getQuantity());
        } else {
            // 不存在，新增
            cart = new Cart();
            cart.setMemberId(memberId);
            cart.setProductId(request.getProductId());
            cart.setQuantity(request.getQuantity());
            cart.setAddedTime(LocalDateTime.now());
        }

        // 4. 檢查庫存
        if (cart.getQuantity() > product.getStockQuantity()) {
            throw new RuntimeException("庫存不足，目前庫存：" + product.getStockQuantity());
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToResponse(savedCart);
    }

    /**
     * 更新購物車數量
     */
    @Transactional
    public CartItemResponse updateQuantity(Integer memberId, Integer cartId, Integer quantity) {
        // 1. 檢查購物車項目是否存在且屬於該會員
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("購物車項目不存在"));

        if (!cart.getMemberId().equals(memberId)) {
            throw new RuntimeException("無權限操作此購物車項目");
        }

        // 2. 檢查庫存
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (quantity > product.getStockQuantity()) {
            throw new RuntimeException("庫存不足，目前庫存：" + product.getStockQuantity());
        }

        // 3. 更新數量
        cart.setQuantity(quantity);
        Cart savedCart = cartRepository.save(cart);

        return convertToResponse(savedCart);
    }

    /**
     * 刪除購物車項目
     */
    @Transactional
    public void removeFromCart(Integer memberId, Integer cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("購物車項目不存在"));

        if (!cart.getMemberId().equals(memberId)) {
            throw new RuntimeException("無權限操作此購物車項目");
        }

        cartRepository.delete(cart);
    }

    /**
     * 清空購物車
     */
    @Transactional
    public void clearCart(Integer memberId) {
        cartRepository.deleteByMemberId(memberId);
    }

    /**
     * 計算購物車總金額
     */
    public BigDecimal getCartTotal(Integer memberId) {
        List<CartItemResponse> items = getCartItems(memberId);
        return items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 轉換 Cart Entity 為 Response DTO
     */
    private CartItemResponse convertToResponse(Cart cart) {
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 計算實際價格（有促銷價就用促銷價）
        BigDecimal actualPrice = product.getPromotionPrice() != null
                ? product.getPromotionPrice()
                : product.getPrice();

        // 計算小計
        BigDecimal subtotal = actualPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));

        return CartItemResponse.builder()
                .cartId(cart.getCartId())
                .productId(product.getProductId())
                .productName(product.getProductName())
                .category(product.getCategory())
                .price(product.getPrice())
                .promotionPrice(product.getPromotionPrice())
                .actualPrice(actualPrice)
                .quantity(cart.getQuantity())
                .subtotal(subtotal)
                .productStatus(product.getProductStatus())
                .stockQuantity(product.getStockQuantity())
                .addedTime(cart.getAddedTime())
                .imageUrl(product.getImageUrl())
                .build();
    }
}
