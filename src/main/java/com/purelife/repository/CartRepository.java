package com.purelife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.purelife.entity.Cart;

@Repository
public interface CartRepository extends CrudRepository<Cart, Integer> {
    // 根據會員ID查詢購物車
    List<Cart> findByMemberId(Integer memberId);

    // 檢查會員是否已有該商品在購物車
    Optional<Cart> findByMemberIdAndProductId(Integer memberId, Integer productId);

    // 刪除會員的所有購物車項目
    @Modifying
    @Query("DELETE FROM carts WHERE member_id = :memberId")
    void deleteByMemberId(Integer memberId);
    
} 
