package com.purelife.repository;

import com.purelife.entity.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends CrudRepository<Member, Integer> {
    
    // 根據帳號查詢會員
    Optional<Member> findByAccount(String account);
    
    // 根據 Email 查詢會員
    Optional<Member> findByEmail(String email);
    
    // 檢查帳號是否存在
    boolean existsByAccount(String account);
    
    // 檢查 Email 是否存在
    boolean existsByEmail(String email);
}

