package com.purelife.repository;

import com.purelife.entity.Product;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

    // 查詢所有上架商品
    @Query("SELECT * FROM products WHERE product_status = 'available' ORDER BY product_id")
    List<Product> findAllAvailable();

    // 依分類查詢上架商品
    @Query("SELECT * FROM products WHERE product_status = 'available' AND category = :category ORDER BY product_id")
    List<Product> findByCategory(@Param("category") String category);

    // 查詢所有不重複的分類
    @Query("SELECT DISTINCT category FROM products WHERE product_status = 'available'")
    List<String> findAllCategories();

    // 查詢新品（按建立時間最新的 4 筆）
    @Query("SELECT * FROM products WHERE product_status = 'available' ORDER BY created_at DESC LIMIT 4")
    List<Product> findNewProducts();

    // 查詢熱銷商品（按銷量排序取前 4 筆）
    @Query("SELECT * FROM products WHERE product_status = 'available' ORDER BY sales_count DESC LIMIT 4")
    List<Product> findHotProducts();
}