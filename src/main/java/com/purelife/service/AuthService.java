//檢查帳號是否重複、加密密碼、驗證登入
package com.purelife.service;

import com.purelife.controller.dto.request.LoginRequest;
import com.purelife.controller.dto.request.RegisterRequest;
import com.purelife.entity.Member;
import com.purelife.exception.BusinessException;
import com.purelife.repository.MemberRepository;
import com.purelife.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor //Lombok自動生成建構子注入: 把 final 成員變數注入
public class AuthService {
    
    //宣告成員變數 + 建構子注入(不用每次都new)
    //final需要在建構子裡面被賦值
    private final MemberRepository memberRepository;

    // 省略這段建構子，因為有 @RequiredArgsConstructor
    // public AuthService(MemberRepository memberRepository) {
    // this.memberRepository = memberRepository; //在這裡被賦值
    // }
    
    @Transactional
    public Member register(RegisterRequest request) {
        // 1. 檢查帳號是否已存在
        if (memberRepository.existsByAccount(request.getAccount())) {
            throw new BusinessException("帳號已存在");
        }
        
        // 2. 檢查 Email 是否已存在
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email已被使用");
        }
        
        // 3. 生成 salt 並加密密碼
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(request.getPassword(), salt);
        
        // 4. 建立新會員
        Member member = new Member();
        member.setAccount(request.getAccount());
        member.setPasswordHash(salt + ":" + hashedPassword);  // 儲存格式：salt:hash
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setMemberLevel("general");
        member.setRegistrationTime(LocalDateTime.now());
        member.setIsActive(true);
        
        return memberRepository.save(member);
    }
    
    public Member login(LoginRequest request) {
        // 1. 查詢會員
        Member member = memberRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new BusinessException("帳號或密碼錯誤"));
        
        // 2. 檢查帳號是否啟用
        if (!member.getIsActive()) {
            throw new BusinessException("帳號已被停用");
        }
        
        // 3. 驗證密碼
        String[] parts = member.getPasswordHash().split(":");
        String salt = parts[0];
        String storedHash = parts[1];
        
        if (!PasswordUtil.verifyPassword(request.getPassword(), salt, storedHash)) {
            throw new BusinessException("帳號或密碼錯誤");
        }
        
        return member;
    }
}
