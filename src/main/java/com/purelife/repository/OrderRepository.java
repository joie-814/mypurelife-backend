package com.purelife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.purelife.entity.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {

    // 取得會員的所有訂單（依時間倒序）
    @Query("SELECT * FROM orders WHERE member_id = :memberId ORDER BY order_time DESC")
    List<Order> findByMemberIdOrderByOrderTimeDesc(@Param("memberId") Integer memberId);

    // 根據訂單編號查詢
    Optional<Order> findByOrderNumber(String orderNumber);

    // 根據訂單ID和會員ID查詢（確保只能查自己的訂單）
    @Query("SELECT * FROM orders WHERE order_id = :orderId AND member_id = :memberId")
    Optional<Order> findByOrderIdAndMemberId(@Param("orderId") Integer orderId, 
                                              @Param("memberId") Integer memberId);
}