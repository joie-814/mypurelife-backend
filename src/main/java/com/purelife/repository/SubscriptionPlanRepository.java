package com.purelife.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.purelife.entity.SubscriptionPlan;

@Repository
public interface SubscriptionPlanRepository extends CrudRepository<SubscriptionPlan, Integer> {

    // 取得某商品的所有訂閱方案
    List<SubscriptionPlan> findByProductId(Integer productId);
}
