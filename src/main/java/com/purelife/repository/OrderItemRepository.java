package com.purelife.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.purelife.entity.OrderItem;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {

    // 取得訂單的所有明細
    List<OrderItem> findByOrderId(Integer orderId);
}