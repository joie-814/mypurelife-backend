package com.purelife.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.purelife.controller.dto.request.AdminLoginRequest;
import com.purelife.controller.dto.request.ProductRequest;
import com.purelife.controller.dto.response.AdminLoginResponse;
import com.purelife.controller.dto.response.ApiResponse;
import com.purelife.controller.dto.response.MemberResponse;
import com.purelife.controller.dto.response.OrderResponse;
import com.purelife.controller.dto.response.SubscriptionResponse;
import com.purelife.entity.Product;
import com.purelife.service.AdminService;
import com.purelife.service.FileUploadService;  
import com.purelife.service.ProductService;      

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final FileUploadService fileUploadService;  
    private final ProductService productService;       

    // ===== 管理員登入 =====
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AdminLoginResponse>> login(
            @Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminService.login(request);
        return ResponseEntity.ok(ApiResponse.success("登入成功", response));
    }

    // ===== 會員管理 =====
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {
        List<MemberResponse> members = adminService.getAllMembers();
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    // ===== 訂單管理 =====
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = adminService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {
        adminService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("訂單狀態已更新", null));
    }

    @PutMapping("/orders/{orderId}/payment")
    public ResponseEntity<ApiResponse<Void>> updatePaymentStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {
        adminService.updatePaymentStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("付款狀態已更新", null));
    }

    // ===== 訂閱管理 =====
    @GetMapping("/subscriptions")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getAllSubscriptions() {
        List<SubscriptionResponse> subscriptions = adminService.getAllSubscriptions();
        return ResponseEntity.ok(ApiResponse.success(subscriptions));
    }

    // ===== 商品管理 =====
    
    /**
     * 上傳商品圖片
     */
    @PostMapping("/upload/product-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileUploadService.uploadProductImage(file);
            return ResponseEntity.ok(ApiResponse.success("圖片上傳成功", new ImageUploadResponse(imageUrl)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("圖片上傳失敗"));
        }
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = adminService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * 使用 ProductRequest 而不是 Product
     */
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Product>> createProduct(
        @RequestParam String productName,
        @RequestParam String category,
        @RequestParam Integer price,
        @RequestParam Double promotionPrice,
        @RequestParam Integer stockQuantity,
        @RequestParam String description,
        @RequestParam String imageUrl,
        @RequestParam String productStatus,
        @RequestParam("file") MultipartFile file) {
        Product created = productService.createProduct(
            productName,
            category,
            price,
            promotionPrice,
            stockQuantity,
            description,
            imageUrl,
            productStatus,
            file );
        return ResponseEntity.ok(ApiResponse.success("商品新增成功", created));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
        @PathVariable Integer productId,
        @RequestParam String productName,
        @RequestParam String category,
        @RequestParam Integer price,
        @RequestParam Double promotionPrice,
        @RequestParam Integer stockQuantity,
        @RequestParam String description,
        @RequestParam String imageUrl,
        @RequestParam String productStatus,
        @RequestParam("file") MultipartFile file) {
        Product updated = productService.updateProduct(  
            productId,          
            productName,
            category,
            price,
            promotionPrice,
            stockQuantity,
            description,
            imageUrl,
            productStatus,
            file);
        return ResponseEntity.ok(ApiResponse.success("商品更新成功", updated));
    }

    @PutMapping("/products/{productId}/status")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(
            @PathVariable Integer productId,
            @RequestParam String status) {
        adminService.updateProductStatus(productId, status);
        return ResponseEntity.ok(ApiResponse.success("商品狀態已更新", null));
    }

    /**
     * 刪除商品時也刪除圖片
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer productId) {
        try {
            // 取得商品資訊
            Product product = productService.getProductEntityById(productId);
            
            // 刪除商品圖片
            if (product.getImageUrl() != null) {
                fileUploadService.deleteProductImage(product.getImageUrl());
            }
            
            // 刪除商品
            productService.deleteProduct(productId);
            return ResponseEntity.ok(ApiResponse.success("商品已刪除", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // DTO 類別
    record ImageUploadResponse(String imageUrl) {}
}