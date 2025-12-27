package com.purelife.controller;

import com.purelife.controller.dto.response.ApiResponse;
import com.purelife.controller.dto.response.ProductResponse;
import com.purelife.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    // 取得所有商品（可選分類篩選）(required = false)->category 不是必填
    // GET /api/products
    // GET /api/products?category=維他命
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String category) {
        
        List<ProductResponse> products = productService.getAllProducts(category);
        return ApiResponse.success(products);
    }

    // 取得單一商品
    // GET /api/products/1
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("id") Integer id) {
        ProductResponse product = productService.getProductById(id);
        return ApiResponse.success(product);
    }

    // 取得所有分類
    // GET /api/products/categories
    @GetMapping("/categories")
    public ApiResponse<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ApiResponse.success(categories);
    }

    // 取得新品
    // GET /api/products/new
    @GetMapping("/new")
    public ApiResponse<List<ProductResponse>> getNewProducts() {
        List<ProductResponse> products = productService.getNewProducts();
        return ApiResponse.success(products);
    }

    // 取得熱銷商品
    // GET /api/products/hot
    @GetMapping("/hot")
    public ApiResponse<List<ProductResponse>> getHotProducts() {
        List<ProductResponse> products = productService.getHotProducts();
        return ApiResponse.success(products);
    }
}
