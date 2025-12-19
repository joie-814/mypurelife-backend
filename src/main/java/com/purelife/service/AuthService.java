//檢查帳號是否重複、加密密碼、驗證登入
package com.purelife.service;

import com.purelife.controller.dto.request.LoginRequest;
import com.purelife.controller.dto.request.RegisterRequest;
import com.purelife.controller.dto.response.LoginResponse;
import com.purelife.entity.Member;
import com.purelife.exception.BusinessException;
import com.purelife.repository.MemberRepository;
import com.purelife.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    // 省略這段建構子，因為有 @RequiredArgsConstructor
    // public AuthService(MemberRepository memberRepository) {
    // this.memberRepository = memberRepository; //在這裡被賦值
    // }
    
    @Transactional
    public Member register(RegisterRequest request) {
        // 檢查 Email 是否已存在
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email已被使用");
        }
        
        // 生成 salt 並加密密碼
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(request.getPassword(), salt);
        
        // 建立新會員
        Member member = new Member();
        member.setAccount(request.getEmail()); 
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPasswordHash(salt + ":" + hashedPassword);  // 儲存格式：salt:hash
        member.setPhone(request.getPhone());
        member.setMemberLevel("general");
        member.setRegistrationTime(LocalDateTime.now());
        member.setIsActive(true);
        
        return memberRepository.save(member);
    }
    
    /**
     * 登入 - 回傳 LoginResponse（含 JWT Token）
     */
    public LoginResponse login(LoginRequest request) {
        // 查詢會員
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("帳號或密碼錯誤"));
        
        // 檢查帳號是否啟用
        if (!member.getIsActive()) {
            throw new BusinessException("帳號已被停用");
        }
        
        // 驗證密碼
        String[] parts = member.getPasswordHash().split(":");
        String salt = parts[0];
        String storedHash = parts[1];
        
        if (!PasswordUtil.verifyPassword(request.getPassword(), salt, storedHash)) {
            throw new BusinessException("帳號或密碼錯誤");
        }
        
        // 產生 JWT Token
        String token = jwtUtil.generateToken(member.getMemberId(), member.getAccount());
        
        // 回傳登入資訊
        return LoginResponse.builder()
                            .token(token)
                            .tokenType("Bearer")
                            .memberId(member.getMemberId())
                            .account(member.getAccount())
                            .name(member.getName())
                            .memberLevel(member.getMemberLevel())
                            .build();
    }
    
    /**
     * 根據 ID 取得會員（給 JWT Filter 用）
     */
    public Member getMemberById(Integer memberId) {
        return memberRepository.findById(memberId)
                               .orElseThrow(() -> new BusinessException("會員不存在"));
    }
}
