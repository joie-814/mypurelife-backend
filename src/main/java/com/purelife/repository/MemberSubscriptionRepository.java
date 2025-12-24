package com.purelife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.purelife.entity.MemberSubscription;

@Repository
public interface MemberSubscriptionRepository extends CrudRepository<MemberSubscription, Integer> {

    // 取得會員的所有訂閱
    List<MemberSubscription> findByMemberId(Integer memberId);

    // 取得會員的有效訂閱（active 或 paused）
    @Query("SELECT * FROM member_subscriptions WHERE member_id = :memberId AND subscription_status IN ('active', 'paused') ORDER BY created_at DESC")
    List<MemberSubscription> findActiveByMemberId(@Param("memberId") Integer memberId);

    // 檢查會員是否已訂閱某方案
    @Query("SELECT * FROM member_subscriptions WHERE member_id = :memberId AND plan_id = :planId AND subscription_status IN ('active', 'paused')")
    Optional<MemberSubscription> findByMemberIdAndPlanId(@Param("memberId") Integer memberId, @Param("planId") Integer planId);

    // 根據訂閱ID和會員ID查詢（確保只能操作自己的訂閱）
    @Query("SELECT * FROM member_subscriptions WHERE subscription_id = :subscriptionId AND member_id = :memberId")
    Optional<MemberSubscription> findBySubscriptionIdAndMemberId(@Param("subscriptionId") Integer subscriptionId, @Param("memberId") Integer memberId);
}