package com.purelife.service;

import com.purelife.controller.dto.request.ProductRequest;
import com.purelife.controller.dto.request.ProductRequest.SubscriptionPlanRequest;
import com.purelife.controller.dto.response.ProductResponse;
import com.purelife.entity.Product;
import com.purelife.entity.SubscriptionPlan;
import com.purelife.repository.ProductRepository;
import com.purelife.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final FileUploadService fileUploadService;

    // ===================== 查詢 =====================
    /**
     * 取得所有商品（前台用）
     */
    public List<ProductResponse> getAllProducts(String category) {
        List<Product> products;
        if (category != null && !category.isBlank()) {
            products = productRepository.findByCategory(category);  // 已過濾上架
        } else {
            products = productRepository.findAllAvailable();  // 已過濾上架
        }
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 取得所有商品（後台用，包含下架）
     */
    public List<ProductResponse> getAllProductsForAdmin() {
        List<Product> products = iterableToList(productRepository.findAllForAdmin());
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    /**
     * 根據 ID 取得商品（回傳 ProductResponse）
     */
    public ProductResponse getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("找不到商品，ID: " + productId));
        return convertToResponse(product);
    }

    /**
     * 根據 ID 取得商品（回傳 Product Entity）
     */
    public Product getProductEntityById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("找不到商品，ID: " + productId));
    }

    /**
     * 取得所有分類
     */
    public List<String> getAllCategories() {
        List<Product> products = iterableToList(productRepository.findAll());
        
        return products.stream()
                .map(Product::getCategory)
                .filter(category -> category != null && !category.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 取得新品
     */
    public List<ProductResponse> getNewProducts() {
        List<Product> products = iterableToList(productRepository.findNewProducts());
        
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 取得熱銷商品（依銷售量排序）
     */
    public List<ProductResponse> getHotProducts() {
        List<Product> products = iterableToList(productRepository.findHotProducts());
        
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    // ===================== 新增 =====================
    /**
     * 新增商品（管理員用）
     */
    @Transactional
    public Product createProduct(
        String productName,
        String category,
        Integer price,
        Double promotionPrice,
        Integer stockQuantity,
        String description,
        String imageUrl,
        String productStatus,
        List<SubscriptionPlanRequest> subscriptionPlans,
        MultipartFile file){

        Product product = new Product();
        product.setProductName(productName);
        product.setCategory(category);
        product.setDescription(description);

        // 圖片上傳
        if (file != null && !file.isEmpty()) {
            try {
                String fileNewName = fileUploadService.uploadProductImage(file);
                product.setImageUrl(fileNewName);
            } catch (IOException e) {
                throw new RuntimeException("圖片上傳失敗", e);
            }
        } else if (imageUrl != null) {
        product.setImageUrl(imageUrl);
        }
        
        // 價格處理
        if (price != null) {
            product.setPrice(BigDecimal.valueOf(price));
        }
        
        // 促銷價處理（可能為 null）
        if (promotionPrice != null && promotionPrice > 0) {
            product.setPromotionPrice(BigDecimal.valueOf(promotionPrice));
        } else {
            product.setPromotionPrice(null);
        }
        
        // 庫存處理
        product.setStockQuantity(stockQuantity != null ? stockQuantity : 0);
        
        // 狀態處理
        product.setProductStatus(productStatus != null ? productStatus : "available");
        
        // 時間戳記
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        // 銷售量初始化
        product.setSalesCount(0);

        // 先存商品，取得 productId
        Product savedProduct = productRepository.save(product);

        // 如果有訂閱方案就存
        if (subscriptionPlans != null && !subscriptionPlans.isEmpty()) {
            List<SubscriptionPlan> plans = new ArrayList<>();
            for (SubscriptionPlanRequest planReq : subscriptionPlans) {
                SubscriptionPlan plan = new SubscriptionPlan();
                plan.setCycleType(planReq.getCycleType());
                plan.setCycleDays(planReq.getCycleDays());
                plan.setDiscountRate(planReq.getDiscountRate()!= null 
                    ? BigDecimal.valueOf(planReq.getDiscountRate()) 
                    : null);
                plan.setProductId(savedProduct.getProductId()); // 綁定關聯(綁定商品ID)
                plans.add(plan);
            }
            subscriptionPlanRepository.saveAll(plans);
        }
        return savedProduct;
    }
    // ===================== 更新 =====================
    /**
     * 更新商品（管理員用）
     */
    @Transactional
    public Product updateProduct(
        Integer productId,
        String productName,
        String category,
        Integer price,
        Double promotionPrice,
        Integer stockQuantity,
        String description,
        String imageUrl,
        String productStatus,
        List<SubscriptionPlanRequest> subscriptionPlans,
        MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("找不到商品，ID: " + productId));

        product.setProductName(productName);
        product.setCategory(category);
        product.setDescription(description);
        
        // 價格處理
        if (price != null) {
            product.setPrice(BigDecimal.valueOf(price));
        }
        
        // 促銷價處理
        if (promotionPrice != null && promotionPrice > 0) {
            product.setPromotionPrice(BigDecimal.valueOf(promotionPrice));
        } else {
            product.setPromotionPrice(null);
        }
        
        // 庫存處理
        if (stockQuantity != null) {
            product.setStockQuantity(stockQuantity);
        }
        
        // 狀態處理
        if (productStatus != null) {
            product.setProductStatus(productStatus);
        }
        
        // 圖片處理
        handleImageUpdate(product, imageUrl, file);
        
        // 更新時間
        product.setUpdatedAt(LocalDateTime.now());
        // 更新訂閱方案（先刪舊方案，再新增新方案）
        if (subscriptionPlans != null) {
            subscriptionPlanRepository.deleteByProductId(productId);

            List<SubscriptionPlan> plans = new ArrayList<>();
            for (SubscriptionPlanRequest planReq : subscriptionPlans) {
                SubscriptionPlan plan = new SubscriptionPlan();
                plan.setCycleType(planReq.getCycleType());
                plan.setCycleDays(planReq.getCycleDays());
                plan.setDiscountRate(planReq.getDiscountRate() != null
                        ? BigDecimal.valueOf(planReq.getDiscountRate())
                        : null);
                plan.setProductId(product.getProductId());
                plans.add(plan);
            }
            subscriptionPlanRepository.saveAll(plans);
        }

        return productRepository.save(product);
    }

    private void handleImageUpdate(Product product, String imageUrl, MultipartFile file) {
        try {
            // 有新圖片 → 刪舊 + 上傳新
            if (file != null && !file.isEmpty()) {
                if (product.getImageUrl() != null) {
                    fileUploadService.deleteProductImage(
                            Paths.get(product.getImageUrl()).getFileName().toString() //只會拿到最後的檔名轉成字串
                    );
                }

                String newFileName = fileUploadService.uploadProductImage(file);
                product.setImageUrl(newFileName);
                return;
            }

            // 沒新圖片 → 沿用前端傳來的 imageUrl
            if (imageUrl != null) {
                product.setImageUrl(imageUrl);
            }

            // 兩者都 null → 不動圖片

        } catch (IOException e) {
            throw new RuntimeException("圖片更新失敗", e);
        }
    }
     // ===================== 刪除 =====================
    /**
     * 刪除商品（管理員用）
     */
    @Transactional
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("找不到商品，ID: " + productId));
        
        // 軟刪除：改狀態而不是真的刪
        product.setProductStatus("deleted");
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    // ===================== 輔助方法 =====================

    /**
     * 將 Iterable 轉換為 List
     */
    private List<Product> iterableToList(Iterable<Product> iterable) {
        List<Product> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    /**
     * Entity 轉 DTO
     */
    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setPromotionPrice(product.getPromotionPrice());  
        response.setStockQuantity(product.getStockQuantity());
        response.setDescription(product.getDescription());
        response.setImageUrl(product.getImageUrl());
        response.setProductStatus(product.getProductStatus());
        
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findByProductId(product.getProductId());
        if (plans != null && !plans.isEmpty()) {
            response.setSubscriptionPlans(
                plans.stream().map(plan -> {
                    ProductResponse.SubscriptionPlanResponse planRes =
                            new ProductResponse.SubscriptionPlanResponse();
                    planRes.setPlanId(plan.getPlanId());
                    planRes.setCycleType(plan.getCycleType());
                    planRes.setCycleDays(plan.getCycleDays());
                    planRes.setDiscountRate(plan.getDiscountRate());
                    return planRes;
                }).collect(Collectors.toList())
            );
        }  
        return response;
    }
}