package com.purelife.service;

import com.purelife.controller.dto.response.ProductResponse;
import com.purelife.entity.Product;
import com.purelife.exception.ResourceNotFoundException;
import com.purelife.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor 
public class ProductService {

    private final ProductRepository productRepository;

    // 取得所有商品（可選分類篩選）
    public List<ProductResponse> getAllProducts(String category) {
        List<Product> products;

        if (category != null && !category.trim().isEmpty()) {
            products = productRepository.findByCategory(category);
        } else {
            products = productRepository.findAllAvailable();
        }

        //一整個 List → List<ProductResponse>
        return products.stream() // 開始流水線
                       .map(this::toResponse) //轉型 = .map(product -> this.toResponse(product))，:: = 把這個方法當成參數丟進去用
                       .collect(Collectors.toList()); // 收集結果並轉成List<ProductResponse>
    }

    // 取得單一商品（用id）
    public ProductResponse getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到商品，ID: " + productId));

        return toResponse(product);
    }

    // 取得所有分類
    // 回傳 List<String> 所以不用轉型
    public List<String> getAllCategories() {
        return productRepository.findAllCategories(); 
    }


    // 取得新品
    public List<ProductResponse> getNewProducts() {
        return productRepository.findNewProducts()
                                .stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList());
    }

    // 取得熱銷商品
    public List<ProductResponse> getHotProducts() {
        return productRepository.findHotProducts()
                                .stream() //一個一個處理
                                .map(this::toResponse) // = .map(product -> this.toResponse(product))
                                .collect(Collectors.toList()); //裝回集合
    }


    // Entity 轉 Response（List<Product> -> List<ProductResponse>）
    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                            .productId(product.getProductId())
                            .category(product.getCategory())
                            .productName(product.getProductName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .promotionPrice(product.getPromotionPrice())
                            .stockQuantity(product.getStockQuantity())
                            .productStatus(product.getProductStatus())
                            .imageUrl(product.getImageUrl())
                            // .salesCount(product.getSalesCount()) 前端不需要看到銷量所以不加
                            .build();
    }
}
